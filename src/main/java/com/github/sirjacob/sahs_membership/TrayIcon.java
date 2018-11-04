package com.github.sirjacob.sahs_membership;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class TrayIcon {

    public TrayIcon() {
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported.");
            return;
        }
        try {
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(new ImageIcon(getClass().getResource("/icon.png")).getImage());
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("SAHS Membership DB Updater");

            final JPopupMenu menu = new JPopupMenu();
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent me) {

                }

                @Override
                public void mousePressed(MouseEvent me) {

                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    System.exit(0);
                }

                @Override
                public void mouseEntered(MouseEvent me) {

                }

                @Override
                public void mouseExited(MouseEvent me) {

                }
            });
            menu.add(exitItem);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        menu.setLocation(e.getX(), e.getY());
                        menu.setInvoker(menu);
                        menu.setVisible(true);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                menu.setVisible(false);
                            }
                        }, 750);
                    }
                }
            });

            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException ex) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
        }
    }
}
