package com.github.sirjacob.sahs_membership;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class MySQLInfo {

    public String username, password, ip, database;

    @Override
    public String toString() {
        return String.format(
                "Username: %s\n"
                + "Password: %s\n"
                + "IP: %s\n"
                + "Database: %s",
                username, password, ip, database);
    }
}
