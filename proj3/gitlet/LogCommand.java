package gitlet;

import java.util.ArrayList;

/**Commands that displays the commit history of the
 * first parent branch.
 * @author Lisa Dunlap
 */
public class LogCommand {

    /**Working directory.*/
    private WorkingDir _dir;

    /**Staging area.*/
    private Staging _stage;

    /**Creates log command with NAME.*/
    LogCommand(String[] name) {
        if (name.length != 1) {
            Utils.problem("Incorrect operands.");
        }
        _stage = FileCrap.getStaging();
        _dir = FileCrap.getWorkingDir();
    }

    /**Prints the commit history from current commit and
     * going through their parents.
     */
    public void apply() {
        ArrayList<Commit> commits = new ArrayList<>();
        Commit last = FileCrap.getLastCommit();
        commits.add(last);
        while (!last.getParent().equals("")) {
            Commit parent = FileCrap.getCommit(last.getParent());
            commits.add(parent);
            last = parent;
        }
        for (Commit c : commits) {
            System.out.println(c.toString());
            System.out.println();
        }
    }
}
