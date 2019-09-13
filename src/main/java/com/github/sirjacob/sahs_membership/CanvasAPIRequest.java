package com.github.sirjacob.sahs_membership;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import io.sentry.Sentry;
import io.sentry.event.Breadcrumb;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class CanvasAPIRequest {

    private static final long COURSE_ID = 57030000000004505L;
    static String access_token;
    private static final String URL_PREFIX = "https://canvas.instructure.com/api/v1/";
    public static ArrayList<String> names = new ArrayList();

    private static final ArrayList<String> LOGIN_IDS = new ArrayList();
    private static int page = 1;

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
        String requestURL = URL_PREFIX + "courses/" + COURSE_ID + "/users?access_token=" + access_token + "&per_page=100&page=" + page++ + "&include[]=avatar_url&include[]=enrollments";
        System.out.println(requestURL);
        String webpage = readWebpage(requestURL);
        if (webpage != null) {
            Any json = JsonIterator.deserialize(webpage);
            if (json.toString().equals("[]")) {
                page = -1;
            }
            return json;
        } else {
            return null;
        }
    }

    private static String readWebpage(String address) {
        InputStream in;
        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            int responseCode = con.getResponseCode();
            if (responseCode == 200) { // Ensure reponse from API is served. 200 OK
                in = con.getInputStream();
                String encoding = con.getContentType();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
                encoding = encoding == null ? "UTF-8" : encoding.substring(encoding.indexOf("charset=") + 8);
                String body = IOUtils.toString(in, encoding);
                in.close();
                return body;
            } else {
                Breadcrumb.Level level;
                if (responseCode == 503) { // 503 Service Unavailable
                    level = SentryIO.INFO;
                    /* We expect that from time to time the
                    API service will be unavailable. */
                } else {
                    level = SentryIO.WARNING;
                }
                SentryIO.recordBreadcrumb(String.format("Server returned HTTP response code: %s for URL: %s", responseCode, address), level);
            }
        } catch (IOException ex) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean updateMembershipDatabase() {
        /* method that updates terminating var must be outside of terminating
        condition */
        Any members = getMembers();
        if (members == null) {
            Sentry.capture("updateMembershipDatabase() call skipped.");
            return false;
        } else if (page == -1) { // terminating condition
            /* Remove members from the DB that are no longer in SAHS */
            String loginIDsString = LOGIN_IDS.toString();
            loginIDsString = loginIDsString.substring(1, loginIDsString.length() - 1);
            String statement = String.format("DELETE FROM `members` WHERE NOT `login_id` IN (%s)", loginIDsString);
            System.out.println(statement);
            MySQL.executeUpdate(statement);

            /* Reset vars for next timer call */
            page = 1;
            LOGIN_IDS.clear();
        } else {
            System.out.println("Members: " + members);
            for (Any any : members) {
                String login_id = String.valueOf(any.get("login_id"));
                String name = String.valueOf(any.get("name"));

                String enrollment_type = String.valueOf(any.get("enrollments").get(0).get("type"));
                switch (enrollment_type) {
                    case "StudentEnrollment":
                        // TODO: Check enrollment_state under enrollments for better verification
                        if (login_id.isEmpty()) {
                            //enrollment_type = "pending";
                            continue;
                            /* The DB doesn't store pending members
                        their login_id cannot be retrieved. */
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
                    default: // Whoops, something went wrong! Wasn't expecting that.
                        //enrollment_type = null;
                        continue;
                }
                LOGIN_IDS.add("'" + login_id + "'"); // Help prepare for DELETE stmt 
                /* If the member's avatar is the default canvas one,
            set the avatar_url to "". The DB will convert this to NULL on its
            end. */
                String avatar_url = String.valueOf(any.get("avatar_url"));
                if ("https://canvas.instructure.com/images/messages/avatar-50.png".equals(avatar_url)) {
                    avatar_url = ""; // TODO: Can't use null because String.format converts to "null"
                }
                /* INSERT member into DB.
            If already exists, update enrollment_type & avatar_url */
                String statement = String.format("INSERT INTO `members` (`login_id`, `name`, `enrollment_type`, `avatar_url`) VALUES ('%s', '%s', '%s', '%s') ON DUPLICATE KEY UPDATE `enrollment_type` = '%s', `avatar_url` = '%s';",
                        login_id, name, enrollment_type, avatar_url, enrollment_type, avatar_url);
                System.out.println(statement);
                if(!"student".equals(enrollment_type)){
                    name = "**" + name;
                }
                names.add(name);
                MySQL.executeUpdate(statement);
            }

            updateMembershipDatabase(); // recurse
        }
        return true;
    }
}
