package gitlet;

import java.util.ArrayList;
import java.util.Collections;

/**Command that gives the status of the staging are and working directory.
 * @author Lisa Dunlap
 */
public class StatusCommand {

    /**Working directory.*/
    private WorkingDir _dir;

    /**Staging area.*/
    private Staging _stage;

    /**Creates staging command with NAME.*/
    StatusCommand(String[] name) {
        if (name.length != 1) {
            Utils.problem("Incorrect operands.");
        }
        _dir = FileCrap.getWorkingDir();
        _stage = FileCrap.getStaging();
    }

    /**Prints the status of the staging area and the working directory.*/
    public void apply() {
        String modified = _dir.getModifiedNames(_stage);
        String status = "";
        status += "=== Branches ===\n" + branches();
        status += "\n=== Staged Files ===\n" + _stage.getStFileNames();
        status += "\n=== Removed Files ===\n" + _stage.getRmFileNames();
        status += "\n=== Modifications Not Staged For Commit ===\n" + modified;
        status += "\n=== Untracked Files ===\n" + _dir.getUnTrackedNames();
        System.out.println(status);
    }

    /**Returns all the current branches.*/
    public String branches() {
        String ret = "";
        ArrayList<String> branches = FileCrap.getBranches();
        Collections.sort(branches);
        String curr = FileCrap.getCurrentBranch();
        ret += "*" + curr + "\n";
        for (String s : branches) {
            if (!s.equals(curr)) {
                ret += s + "\n";
            }
        }
        return ret;

    }
}
