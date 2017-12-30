package gitlet;

import java.io.File;
import java.util.HashMap;

/**Command that merges two branches.
 * @author Lisa Dunlap
 */
public class MergeCommand {

    /**Name of given branch.*/
    private String _name;

    /**Staging srea.*/
    private Staging _stage;

    /**Working directory.*/
    private WorkingDir _dir;

    /**Creates command with branch NAME.*/
    MergeCommand(String[] name) {
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
        _stage = FileCrap.getStaging();
        _dir = FileCrap.getWorkingDir();
    }

    /**Catches all errors and completes the merge.*/
    public void apply() {
        boolean conflict = false;
        if (!FileCrap.branchExists(_name)) {
            Utils.problem("A branch with that name does not exist.");
        }
        String splitID = FileCrap.getSplit(_name);
        String f = ".gitlet/refs/heads/" + _name;
        String given = Utils.readContentsAsString(new File(f));
        File c = new File(".gitlet/refs/heads/" + FileCrap.getCurrentBranch());
        String current = Utils.readContentsAsString(c);
        Commit split = FileCrap.getCommit(splitID);
        Commit givenCommit = FileCrap.getCommit(given);
        Commit currentCommit = FileCrap.getLastCommit();
        untracked(givenCommit, currentCommit);
        if (current.equals(given)) {
            Utils.problem("Cannot merge a branch with itself.");
        }
        if (splitID.equals(given)) {
            String s = "Given branch is an ancestor of the current branch.";
            Utils.problem(s);
        }
        if (splitID.equals(current)) {
            Utils.writeContents(c, given);
            String cu = FileCrap.getCurrentBranch();
            String file = ".gitlet/logs/refs/heads/" + cu;
            Utils.appendContents(file, given);
            Utils.problem("Current branch fast-forwarded.");
        }

        if (_stage.anyChanges()) {
            Utils.problem("You have uncommited changes.");
        }

        untracked(givenCommit, currentCommit);
        merge1(split, givenCommit, currentCommit);
        merge2(split, givenCommit, currentCommit);
        merge3(split, givenCommit, currentCommit);
        conflict(splitID, given, current);
        FileCrap.clearStaging();
        FileCrap.clearWorkingDir();
    }

    /**Sees if there are any untracked files that would be
     * modified or deleted by CURR or GIVEN.
     */
    public void untracked(Commit given, Commit curr) {
        for (String s : _dir.getUntracked()) {
            boolean staged = !_stage.isStaged(s);
            if ((given.contains(s) || curr.contains(s)) && staged) {
                String mess = "There is an untracked file in the way; ";
                mess += "delete it or add it first.";
                Utils.problem(mess);
            }
        }
    }

    /**Determines whether there is a conflict between GIVEN,
     * CURRENT, and SPLITID.*/
    public void conflict(String splitID, String given, String current) {
        boolean ret = false;
        Commit split = FileCrap.getCommit(splitID);
        Commit givenCommit = FileCrap.getCommit(given);
        Commit currentCommit = FileCrap.getCommit(current);
        ret = ret || conflict1(split, givenCommit, currentCommit);
        ret = ret || conflict2(split, givenCommit, currentCommit);
        ret = ret || conflict3(split, givenCommit, currentCommit);
        String cu = FileCrap.getCurrentBranch();
        String mess = "Merged " + _name + " into " + cu + ".";
        FileCrap.saveStaging(_stage);
        FileCrap.saveWorkingDir(_dir);
        CommitCommand cc = new CommitCommand(new String[]{"commit", mess});
        cc.apply2(_name);
        if (ret) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**Any files that have been modified in the GIVEN branch since the
     * SPLIT point, but not modified in the CURR branch since the split
     * point should be changed to their versions in the given branch.
     */
    public void merge1(Commit split, Commit given, Commit curr) {
        for (String s : split.getBlobs().keySet()) {
            if (given.contains(s)) {
                if (!given.getID(s).equals(split.getID(s))) {
                    if (curr.contains(s)) {
                        if (curr.getID(s).equals(split.getID(s))) {
                            Blob b = FileCrap.getBlob(given.getID(s));
                            _dir.checkoutFile(s, b);
                            _stage.stageFiles(s, given.getID(s));
                        }
                    }
                }
            }
        }
    }

    /**Any files that were not present at the SPLIT point and are
     * present in the GIVEN branch but not the CURR branch
     * should be checked out and staged.
     */
    public void merge2(Commit split, Commit given, Commit curr) {
        for (String s : given.getBlobs().keySet()) {
            if (!split.contains(s) && !curr.contains(s)) {
                _dir.checkoutFile(s, FileCrap.getBlob(given.getID(s)));
                _stage.stageFiles(s, given.getID(s));
            }
        }
    }

    /**Any files present at the SPLIT point, unmodified in
     * the CURR branch, and absent in the GIVEN branch should
     * be removed (and untracked).
     */
    public void merge3(Commit split, Commit given, Commit curr) {
        for (String s : split.getBlobs().keySet()) {
            if (curr.contains(s)) {
                if (curr.getID(s).equals(split.getID(s))) {
                    if (!given.contains(s)) {
                        File f = new File(s);
                        f.delete();
                    }
                }
            }
        }
    }

    /**Returns if GIVEN and CURR have been modified differently since SPLIT.*/
    public boolean conflict1(Commit split, Commit given, Commit curr) {
        HashMap<String, String> givenBlobs = given.getBlobs();
        for (String s : split.getBlobs().keySet()) {
            if (given.contains(s)) {
                if (!given.getID(s).equals(split.getID(s))) {
                    if (curr.contains(s)) {
                        if (!curr.getID(s).equals(split.getID(s))) {
                            if (!given.getID(s).equals(curr.getID(s))) {
                                String c = curr.getID(s);
                                String g = given.getID(s);
                                Blob currentBlob = FileCrap.getBlob(c);
                                Blob givenBlob = FileCrap.getBlob(g);
                                String newContents = "<<<<<<< HEAD\n"
                                         + currentBlob.getStringContents()
                                         + "=======\n"
                                         + givenBlob.getStringContents()
                                         + ">>>>>>>\n";
                                File file = new File(s);
                                Utils.writeContents(file, newContents);
                                Blob b = new Blob(s);
                                FileCrap.saveBlob(b);
                                _stage.stageFiles(s, b.getID());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**Returns whether a File was abset in the SPLIT point but has diff
     * contents in GIVEN and CURR.
     */
    public boolean conflict2(Commit split, Commit given, Commit curr) {
        for (String s : given.getBlobs().keySet()) {
            if (!split.contains(s)) {
                if (curr.contains(s)) {
                    if (!given.getID(s).equals(curr.getID(s))) {
                        Blob currentBlob = FileCrap.getBlob(curr.getID(s));
                        Blob givenBlob = FileCrap.getBlob(given.getID(s));
                        String newContents = "<<<<<<< HEAD\n"
                                 + currentBlob.getStringContents()
                                 + "=======\n"
                                 + givenBlob.getStringContents()
                                 + ">>>>>>>\n";
                        File file = new File(s);
                        Utils.writeContents(file, newContents);
                        Blob b = new Blob(s);
                        FileCrap.saveBlob(b);
                        _stage.stageFiles(s, b.getID());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**Returns if either GIVEN or CURR is modified from SPLIT and the
     * other is deleted.
     */
    public boolean conflict3(Commit split, Commit given, Commit curr) {
        HashMap<String, String> givenBlobs = given.getBlobs();
        for (String s : split.getBlobs().keySet()) {
            if (given.contains(s)) {
                if (!given.getID(s).equals(split.getID(s))) {
                    if (!curr.contains(s)) {
                        Blob givenBlob = FileCrap.getBlob(given.getID(s));
                        String newContents = "<<<<<<< HEAD\n"
                                 + "=======\n"
                                 + givenBlob.getStringContents()
                                 + ">>>>>>>\n";
                        File file = new File(s);
                        Utils.writeContents(file, newContents);
                        Blob b = new Blob(s);
                        FileCrap.saveBlob(b);
                        _stage.stageFiles(s, b.getID());
                        return true;
                    }
                }
            } else {
                if (curr.contains(s)) {
                    if (!curr.getID(s).equals(split.getID(s))) {
                        Blob currentBlob = FileCrap.getBlob(curr.getID(s));
                        String newContents = "<<<<<<< HEAD\n"
                                 + currentBlob.getStringContents()
                                 + "=======\n"
                                 + ">>>>>>>\n";
                        File file = new File(s);
                        Utils.writeContents(file, newContents);
                        Blob b = new Blob(s);
                        FileCrap.saveBlob(b);
                        _stage.stageFiles(s, b.getID());
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
