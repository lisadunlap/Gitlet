package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/**Command that creates commit with user message and
 * tracked files.
 * @author Lisa Dunlap
 */
public class CommitCommand implements Serializable {

    /**Commit message.*/
    private String _name;

    /**Working directory.*/
    private WorkingDir _dir;

    /**Creates commit command given NAME.*/
    CommitCommand(String[] name) {
        if (name.length == 1) {
            Utils.problem("Please enter a commit message.");
        }
        if (name.length == 2 && name[1].equals("")) {
            Utils.problem("Please enter a commit message.");
        }
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
        _dir = FileCrap.getWorkingDir();
    }

    /**Commits files with user message.*/
    public void apply() {
        Staging stage = FileCrap.getStaging();
        boolean update = _dir.update();
        if (!stage.anyChanges() && !update) {
            Utils.problem("No changes added to the commit.");
            System.exit(0);
        }
        Commit last = FileCrap.getLastCommit();
        for (String s : stage.getStFiles().keySet()) {
            _dir.add(s, stage.getStFiles().get(s));
        }
        HashMap<String, String> blobs = new HashMap<>(_dir.getTracked());
        Commit commit = new Commit(_name, blobs, last.getID());
        commit.setID(Utils.sha1(Utils.serialize(commit)));
        FileCrap.saveCommit(commit);
        FileCrap.changeHead(commit);
        FileCrap.clearStaging();
        FileCrap.clearWorkingDir();
    }

    /**Commits files after merge with second parent SECOND.*/
    public void apply2(String second) {
        Staging stage = FileCrap.getStaging();
        boolean update = _dir.update();
        if (!stage.anyChanges() && !update) {
            Utils.problem("No changes added to the commit.");
            System.exit(0);
        }
        Commit last = FileCrap.getLastCommit();
        for (String s : stage.getStFiles().keySet()) {
            _dir.add(s, stage.getStFiles().get(s));
        }
        HashMap<String, String> blobs = new HashMap<>(_dir.getTracked());
        String sec = FileCrap.getBranchHead(second);
        Commit commit = new Commit(_name, blobs, last.getID(), sec);
        commit.setID(Utils.sha1(Utils.serialize(commit)));
        FileCrap.saveCommit(commit);
        FileCrap.changeHead(commit);
        FileCrap.changeHead(commit, second);
        FileCrap.clearStaging();
        FileCrap.clearWorkingDir();
    }
}
