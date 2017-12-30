package gitlet;

import java.io.File;

/**Command that processes a checkout of a file, a commit
 * file, or a branch.
 * @author Lisa Dunlap
 */
public class CheckoutCommand {

    /**Name of things to checkout.*/
    private String[] _name;

    /**Staging area.*/
    private Staging _stage;

    /**Working directory.*/
    private WorkingDir _dir;

    /**Creates checkout commands with arguemnts NAME.*/
    CheckoutCommand(String... name) {
        _name = name;
        _stage = FileCrap.getStaging();
        _dir = FileCrap.getWorkingDir();
    }

    /**Decides which method to use based on the operands.*/
    public void apply() {
        if (_name[1].equals("--")) {
            apply1();
        } else if (_name.length == 2) {
            apply3();
        } else if (_name.length == 4 && _name[2].equals("--")) {
            apply2();
        } else {
            Utils.problem("Incorrect operands.");
        }
    }

    /**Just overwriting a file name.*/
    public void apply1() {
        overwrite(FileCrap.getLastCommitID(), _name[2]);
    }

    /**Checking out a commit and file.*/
    public void apply2() {

        if (_name[1].length() < Utils.UID_LENGTH) {
            _name[1] = FileCrap.getShortenedCommit(_name[1]);
        }
        overwrite(_name[1], _name[3]);
    }

    /**Checking out a branch.*/
    public void apply3() {
        String branch = _name[1];
        if (!FileCrap.getBranches().contains(branch)) {
            Utils.problem("No such branch exists.");
        }
        if (FileCrap.getCurrentBranch().equals(branch)) {
            Utils.problem("No need to check out the current branch.");
        }
        String id = FileCrap.getBranchHead(branch);
        Commit commit = FileCrap.getCommit(id);
        _dir.checkoutBranch(commit);
        String last = FileCrap.getLastCommitID();
        FileCrap.saveWorkingDir(_dir);
        Utils.writeContents(new File(".gitlet/HEAD"), "refs/heads/" + branch);
        Utils.writeContents(new File(".gitlet/ORIG_HEAD"), last);
        FileCrap.clearStaging();

    }

    /**Overwrites a file NAME in commit ID.*/
    public void overwrite(String id, String name) {
        Commit commit = FileCrap.getCommit(id);
        if (!commit.getBlobs().containsKey(name)) {
            Utils.problem("File does not exist in that commit.");
        } else {
            Blob b = FileCrap.getBlob(commit.getBlobs().get(name));
            _dir.checkoutFile(name, b);
        }
        FileCrap.saveWorkingDir(_dir);
    }
}
