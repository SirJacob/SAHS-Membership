package com.github.sirjacob.sahs_membership;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.sentry.Sentry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class Main {

    //private static final Preferences pref = Preferences.userRoot();
    //private static final String prefUserLookupEpoch = "last_user_epoch";
    //private static final long UserLookupInterval = 60 * 60 * 24; // 24 hours
    //private static long userLookupEpoch = pref.getLong(prefUserLookupEpoch, 0);
    //private static final Timer timer = new Timer();
    private static final String FADING_POPUP_PREFIX = "SAHS Membership DB Update - ";

    public static void main(String[] args) {
        SentryIO.init();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).addHandler(new Handler() {
            @Override
            public void publish(LogRecord lr) {
                Sentry.capture(lr.getThrown());
                System.exit(0);
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });
        new TrayIcon();
        File mysqlYAML = new File("MySQLInfo.yaml");
        System.out.println(mysqlYAML.getAbsolutePath());
        try {
            YamlReader reader = new YamlReader(new FileReader(mysqlYAML));
            MySQL.init(reader.read(MySQLInfo.class));
        } catch (FileNotFoundException | YamlException ex) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
            /*            try {
            Files.write(databaseYAML.toPath(), ("--- \n"
            + "username: null\n"
            + "password: null\n"
            + "ip: 127.0.0.1\n"
            + "database: null\n").getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex1) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (IOException ex1) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            }*/
        }
        //FileManager.saveMemberNames();
        //FileManager.loadMemberNames();
        //new NewJFrame().setVisible(true);
        CanvasAPIRequest.init();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new FadingPopup(FADING_POPUP_PREFIX + "Started");
                if (CanvasAPIRequest.updateMembershipDatabase()) {
                    new FadingPopup(FADING_POPUP_PREFIX + "Finished");
                }
                new FadingPopup(FADING_POPUP_PREFIX + "Failed!");
            }
        }, 0, 1000 * 60 * 60 * 1); //every hour
    }

    /*    private static long getEpoch() {
    return System.currentTimeMillis() / 1000;
    }
    
    private static boolean shouldPerformUserLookup() {
    return getEpoch() >= userLookupEpoch + UserLookupInterval;
    }*/
}
