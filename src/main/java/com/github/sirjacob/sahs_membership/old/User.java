package com.github.sirjacob.sahs_membership.old;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class User {

    public static List<String> memberNames = new ArrayList();
    //public static final HashMap<User> users = new ArrayList();
    private final String login_id;
    private final String name;
    private final String avatar_url; // default avatar: https://canvas.instructure.com/images/messages/avatar-50.png
    private final String enrollment_type;

    public User(String login_id, String name, String avatar_url, String enrollment_type) {
        this.login_id = login_id;
        this.name = name;
        this.avatar_url = avatar_url;
        this.enrollment_type = enrollment_type;
    }

    public String getLogin_id() {
        return login_id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getEnrollment_type() {
        return enrollment_type;
    }

    public static List<String> searchMemberNames(String query) {
        List<String> result = new ArrayList();
        memberNames.stream().filter((memberName) -> (memberName.contains(query))).forEachOrdered((memberName) -> {
            result.add(memberName);
        });
        return result;
    }
}
