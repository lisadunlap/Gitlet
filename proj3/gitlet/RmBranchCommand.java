package gitlet;

import java.io.File;

/**Command the removes branch if it exists and is not
 * the current branch.
 * @author Lisa Dunlap
 */
public class RmBranchCommand {

    /**Name of branch to be removed.*/
    private String _name;

    /**Creates remove brnach command with name NAME.*/
    RmBranchCommand(String[] name) {
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
    }

    /**Removes branch name if it exists.*/
    public void apply() {
        if (_name.equals(FileCrap.getCurrentBranch())) {
            Utils.problem("Cannot remove the current branch.");
        }
        File f = new File(".gitlet/refs/heads/" + _name);
        File f2 = new File(".gitlet/logs/refs/" + _name);
        if (!f.exists()) {
            Utils.problem(" A branch with that name does not exist.");
        }
        f2.delete();
        f.delete();
    }
}
