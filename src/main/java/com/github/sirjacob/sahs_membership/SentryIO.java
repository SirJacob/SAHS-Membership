package com.github.sirjacob.sahs_membership;

import io.sentry.Sentry;
import io.sentry.event.Breadcrumb;
import io.sentry.event.BreadcrumbBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class SentryIO {

    private static boolean init = false;

    //<editor-fold defaultstate="collapsed" desc="Breadcrumb.Level">
    public static final Breadcrumb.Level DEBUG = Breadcrumb.Level.DEBUG;
    public static final Breadcrumb.Level INFO = Breadcrumb.Level.INFO;
    public static final Breadcrumb.Level WARNING = Breadcrumb.Level.WARNING;
    public static final Breadcrumb.Level ERROR = Breadcrumb.Level.ERROR;
    public static final Breadcrumb.Level CRITICAL = Breadcrumb.Level.CRITICAL;
    //</editor-fold>

    public static void init() {
        if (!init) {
            try {
                Sentry.init(Files.readAllLines(new File("SentryDSN.txt").toPath()).get(0));
            } catch (IOException ex) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(java.util.logging.Level.SEVERE, null, ex);
            }
            init = true;
        }
    }

    public static void recordBreadcrumb(String message) {
        recordBreadcrumb(message, INFO);
    }

    public static void recordBreadcrumb(String message, Breadcrumb.Level level) {
        Sentry.getContext().recordBreadcrumb(
                new BreadcrumbBuilder().setMessage(message).setLevel(level).build()
        );
    }

}
