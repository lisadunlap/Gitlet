package gitlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**Command that finds the commit ids of a certain message.
 * @author Lisa Dunlap
 */
public class FindCommand {

    /**Commit message.*/
    private String _name;

    /**Creates find command with NAME.*/
    FindCommand(String[] name) {
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
    }

    /**Serches through commit history and finds any commits
     * with given message.
     */
    public void apply() {
        ArrayList<Commit> commits = new ArrayList<>();
        File f = new File(".gitlet/logs/HEAD");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                commits.add(FileCrap.getCommit(line));
            }
        } catch (IOException e) {
            throw Utils.error("Problem reading file");
        }

        boolean flag = false;
        for (Commit c : commits) {
            if (c.message().equals(_name)) {
                Utils.message(c.getID());
                flag = true;
            }
        }
        if (!flag) {
            Utils.problem("Found no commit with that message.");
        }
    }
}
