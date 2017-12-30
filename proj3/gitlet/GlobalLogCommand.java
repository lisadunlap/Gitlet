package gitlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**Command that displays all previous commmands of all
 * branches in any order.
 * @author Lisa Dunlap
 */
public class GlobalLogCommand {

    /**Creates global log command with NAME.*/
    GlobalLogCommand(String[] name) {
        if (name.length != 1) {
            Utils.problem("Incorrect operands.");
        }
    }

    /**Prints all previous commits of all branches.*/
    public void apply() {
        ArrayList<Commit> commits = new ArrayList<>();
        File f = new File(".gitlet/logs/HEAD");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Commit c = FileCrap.getCommit(line);
                if (!commits.contains(c)) {
                    Utils.message(c.toString());
                    System.out.println();
                    commits.add(c);
                }
            }
        } catch (IOException e) {
            throw Utils.error("Problem reading file");
        }
    }
}
