package gitlet;

import java.io.File;

/**Command to reset current branch to commit id if
 * it exists and no untracked files will be removed.
 * @author Lisa Dunlap.
 */
public class ResetCommand {

    /**Name of commit to reset to.*/
    private String _name;

    /**Working directory.*/
    private WorkingDir _dir;

    /**Stagin area.*/
    private Staging _stage;

    /**Creates resert command with string NAME.*/
    ResetCommand(String[] name) {
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
        _dir = FileCrap.getWorkingDir();
        _stage = FileCrap.getStaging();
    }

    /**Checks out files from given commit and moves current
     * brnaches head to commit node.*/
    public void apply() {
        if (_name.length() < Utils.UID_LENGTH) {
            _name = FileCrap.getShortenedCommit(_name);
        }
        String current = FileCrap.getCurrentBranch();
        Commit c = FileCrap.getCommit(_name);
        File f1 = new File(".gitlet/refs/heads/" + current);
        _dir.checkoutBranch(c);
        Utils.writeContents(f1, c.getID());
        Utils.appendContents(".gitlet/logs/refs/heads/" + current, c.getID());
        FileCrap.clearStaging();
        FileCrap.saveWorkingDir(_dir);
    }
}
