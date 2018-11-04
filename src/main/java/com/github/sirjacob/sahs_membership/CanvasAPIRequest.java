package com.github.sirjacob.sahs_membership;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class CanvasAPIRequest {

    private static final long course_id = 57030000000004505L;
    private static String access_token;
    private static final String urlPrefix = "https://canvas.instructure.com/api/v1/";

    public static void init() {
        if (access_token == null) {
            try {
                access_token = Files.readAllLines(new File("CanvasAccessToken.txt").toPath()).get(0);
            } catch (IOException ex) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*    public static String getUsers() {
    getUsers(new String[0]);
    return null;
    }
    
    public static String getUsers(String enrollment_type) {
    getUsers(new String[]{enrollment_type});
    return null;
    }
    
    public static Any getUsers(String[] enrollment_type) {
    final String urlRoot = "courses/" + course_id + "/users?access_token=" + access_token + "&per_page=999&include[]=avatar_url";
    String urlSuffix = "";
    for (String type : enrollment_type) {
    urlSuffix += "&enrollment_type[]=" + type;
    }
    String requestURL = urlPrefix + urlRoot + urlSuffix;
    System.out.println(requestURL);
    Any json = JsonIterator.deserialize(readWebpage(requestURL));
    /*        for (int i = 0; i < json.size(); i++) {
    System.out.println(json.get(i).get("name"));
    System.out.println(json.get(i).get("login_id"));
    System.out.println(json.get(i).get("avatar_url"));
    }*/
    //return json.get(0);
//}*/
    private static Any getMembers() {
        //https://canvas.instructure.com/api/v1/courses/57030000000004505/users?access_token=XXX&per_page=999&include[]=avatar_url&include[]=enrollments
        String requestURL = urlPrefix + "courses/" + course_id + "/users?access_token=" + access_token + "&per_page=999&include[]=avatar_url&include[]=enrollments";
        System.out.println(requestURL);
        Any json = JsonIterator.deserialize(readWebpage(requestURL));
        return json;
    }

    private static String readWebpage(String address) {
        InputStream in;
        try {
            URL url = new URL(address);
            URLConnection con = url.openConnection();
            in = con.getInputStream();
            String encoding = con.getContentType();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
            encoding = encoding == null ? "UTF-8" : encoding.substring(encoding.indexOf("charset=") + 8);
            String body = IOUtils.toString(in, encoding);
            in.close();
            return body;
        } catch (IOException ex) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void updateMembershipDatabase() {
        Any members = getMembers();
        System.out.println("Members: " + members);
        for (Any any : members) {
            String login_id = String.valueOf(any.get("login_id"));
            String name = String.valueOf(any.get("name"));

            //User.memberNames.add(name);
            String enrollment_type = String.valueOf(any.get("enrollments").get(0).get("type"));
            switch (enrollment_type) {
                case "StudentEnrollment":
                    // TODO: Check enrollment_state under enrollments for better verification
                    if (login_id.isEmpty()) {
                        enrollment_type = "pending";
                    } else {
                        enrollment_type = "student";
                    }
                    break;
                case "TaEnrollment":
                    enrollment_type = "ta";
                    break;
                case "TeacherEnrollment":
                    enrollment_type = "teacher";
                    break;
                default:
                    enrollment_type = null;
                    break;
            }
            String avatar_url = String.valueOf(any.get("avatar_url"));
            if ("https://canvas.instructure.com/images/messages/avatar-50.png".equals(avatar_url)) {
                avatar_url = ""; // TODO: Can't use null because String.format converts to "null"
            }

            String statement = String.format("INSERT INTO `members` (`login_id`, `name`, `enrollment_type`, `avatar_url`) VALUES ('%s', '%s', '%s', '%s') ON DUPLICATE KEY UPDATE `login_id` = '%s', `enrollment_type` = '%s', `avatar_url` = '%s';",
                    login_id, name, enrollment_type, avatar_url, login_id, enrollment_type, avatar_url);
            System.out.println(statement);
            MySQL.executeUpdate(statement);
        }

    }
}
