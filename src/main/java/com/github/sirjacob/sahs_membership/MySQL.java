package com.github.sirjacob.sahs_membership;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class MySQL {

    private static final MysqlDataSource DATA_SOURCE = new MysqlDataSource();
    private static boolean init = false;

    public static void init(MySQLInfo mySQLInfo) {
        if (!init) {
            DATA_SOURCE.setUser(mySQLInfo.username);
            DATA_SOURCE.setPassword(mySQLInfo.password);
            DATA_SOURCE.setServerName(mySQLInfo.ip);
            DATA_SOURCE.setDatabaseName(mySQLInfo.database);
            init = true;
        }
        // TODO: else.. error
    }

    /**
     * INSERT, UPDATE, or DELETE
     *
     * @param statement
     */
    public static void executeUpdate(String statement) {
        /*        try (Connection conn = DATA_SOURCE.getConnection(); Statement stmt = conn.createStatement()) {
        stmt.executeUpdate(statement);
        
        } catch (SQLException ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
        }*/
    }

    /**
     * SELECT
     *
     * @param statement
     * @return
     */
    public static HashMap executeQuery(String statement) {
        return null;
        /*        HashMap hm = new HashMap();
        try {
        Connection conn = DATA_SOURCE.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(statement);
        
        hm.put("rs", rs);
        hm.put("stmt", stmt);
        hm.put("conn", conn);
        } catch (SQLException ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
        }
        return hm;*/
    }
}
