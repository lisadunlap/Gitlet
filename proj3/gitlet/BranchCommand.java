package gitlet;

/**Command that creates a branch and its current commit.
 * @author Lisa Dunlap
 */
public class BranchCommand {

    /**Name of the branch.*/
    private String _name;

    /**Creates a branch command to create branch named NAME.*/
    BranchCommand(String[] name) {
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
    }

    /**Creates a branch and sets its current commit.*/
    public void apply() {
        if (FileCrap.getBranches().contains(_name)) {
            Utils.problem("A branch with that name already exists.");
        }
        FileCrap.createBranch(_name);
    }
}
