package com.github.sirjacob.sahs_membership;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class MySQL {

    private static final MysqlDataSource dataSource = new MysqlDataSource();
    private static boolean init = false;

    public static void init(MySQLInfo mySQLInfo) {
        if (!init) {
            dataSource.setUser(mySQLInfo.username);
            dataSource.setPassword(mySQLInfo.password);
            dataSource.setServerName(mySQLInfo.ip);
            dataSource.setDatabaseName(mySQLInfo.database);
            init = true;
        }
        // TODO: else.. error
    }

    /**
     * INSERT, UPDATE, or DELETE
     */
    public static void executeUpdate(String statement) {
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(statement);

            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
