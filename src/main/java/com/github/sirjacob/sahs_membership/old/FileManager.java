package com.github.sirjacob.sahs_membership.old;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SirJacob <https://github.com/SirJacob>
 */
public class FileManager {

    private static final Path pathMemberNames = Paths.get("member_names.txt");

    public static void saveMemberNames() {
        try {
            Files.write(pathMemberNames, User.memberNames, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadMemberNames() {
        try {
            Files.lines(pathMemberNames).forEach(((name) -> {
                System.out.println(name);
            }));
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
