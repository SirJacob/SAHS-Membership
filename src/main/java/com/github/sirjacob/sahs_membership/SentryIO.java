package com.github.sirjacob.sahs_membership;

import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class SentryIO {

    private static boolean init = false;

    public static void init() {
        if (!init) {
            try {
                Sentry.init(Files.readAllLines(new File("SentryDSN.txt").toPath()).get(0));
            } catch (IOException ex) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
            }
            init = true;
        }
    }

    public static void recordBreadcrumb(String message) {
        Sentry.getContext().recordBreadcrumb(
                new BreadcrumbBuilder().setMessage(message).build()
        );
    }

}
