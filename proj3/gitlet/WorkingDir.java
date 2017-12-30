package gitlet;

import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**The working directory that holds all the tracked
 * and untracked files.
 * @author Lisa Dunlap*/
public class WorkingDir implements Serializable {

    /**Creates working directory.*/
    WorkingDir() {
        _path = FileCrap.workingDir();
        Commit c = FileCrap.getLastCommit();
        _tracked = c.getBlobs();
        _untracked = Utils.plainFilenamesIn(_path);
        for (String s : _tracked.keySet()) {
            if (_untracked.contains(s)) {
                _untracked.remove(s);
            }
        }
        _modified = new HashMap<>();
    }

    /**Returns the path name of the directory.*/
    public String path() {
        return _path;
    }

    /**Returns whether it removed any deleted files
     * from tracked.*/
    public boolean update() {
        boolean flag = false;
        ArrayList<String> rem = new ArrayList<>();
        for (String s : _tracked.keySet()) {
            File f = new File(s);
            if (!f.exists()) {
                rem.add(s);
                flag = true;
            }
        }
        for (String ss : rem) {
            _tracked.remove(ss);
        }
        return flag;
    }

    /**Removes files from untracked if they were deleted.*/
    public void setUntracked() {
        _untracked = Utils.plainFilenamesIn(_path);
        for (String s : _tracked.keySet()) {
            if (_untracked.contains(s)) {
                _untracked.remove(s);
            }
        }
    }

    /**Adds FILE to tracked and removes from untracked given STAGE.*/
    public void add(Staging stage, String file) {
        Blob b = new Blob(file);
        if (!_tracked.containsValue(b.getID())) {
            _tracked.put(file, stage.getStFiles().get(file));
        }
        if (_untracked.contains(file)) {
            _untracked.remove(file);
        }
    }

    /**Adds FILE with blob ID to tracked and removes from untracked
     * given STAGE.*/
    public void add(String file, String id) {
        Blob b = new Blob(file);
        if (!_tracked.containsValue(b.getID())) {
            _tracked.put(file, id);
        }
        if (_untracked.contains(file)) {
            _untracked.remove(file);
        }
    }

    /**Removes FILE from untracked but does not add it to tracked.*/
    public void restrictedAdd(String file) {
        Blob b = new Blob(file);
        if (!_tracked.containsValue(b.getID())) {
            _tracked.put(file, b.getID());
        }
        if (_untracked.contains(file)) {
            _untracked.remove(file);
        }
    }

    /**Removes FILE from tracked given STAGE.*/
    public void remove(Staging stage, String file) {
        Commit last = FileCrap.getLastCommit();
        if (!last.getBlobs().containsKey(file)) {
            if (stage.getStFiles().containsKey(file)) {
                stage.removeFiles(file);
            } else {
                Utils.problem("No reason to remove the file.");
            }
        } else {
            stage.removeFiles(file);
            _tracked.remove(file);
            Utils.restrictedDelete(file);
        }
    }

    /**Returns whether the FILE is tracked.*/
    public boolean isTracked(String file) {
        return _tracked.keySet().contains(file);
    }

    /**Returns tracked files.*/
    public HashMap<String, String> getTracked() {
        return _tracked;
    }

    /**Returns untracked files.*/
    public List<String> getUntracked() {
        return _untracked;
    }

    /**Returns whether FILE has been modified since lst staging.*/
    public boolean modified(String file) {
        Blob blob = new Blob(file);
        Staging stage = FileCrap.getStaging();
        Commit c = FileCrap.getLastCommit();
        String id = _tracked.get(file);
        if (c.contains(file)) {
            id = c.getBlobs().get(file);
        }
        return !blob.getID().equals(id) || stage.modified(file);
    }

    /**Returns modified files from STAGE.*/
    public ArrayList<String> getModified(Staging stage) {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> files = new ArrayList<>();
        files.addAll(_untracked);
        files.addAll(_tracked.keySet());
        for (String s : files) {
            File f = new File(s);
            if (!f.exists()) {
                boolean ir = !stage.isRemoved(s);
                if ((isTracked(s) && ir) || stage.isStaged(s)) {
                    ret.add(s + " (deleted)");
                }
            } else {
                if (isTracked(s) && !stage.isStaged(s) && modified(s)) {
                    ret.add(s + " (modified)");
                }
                if (stage.isStaged(s) && stage.modified(s)) {
                    ret.add(s + " (modified)");
                }
            }
        }
        return ret;
    }

    /**Returns the modified file names given the STAGE.*/
    public String getModifiedNames(Staging stage) {
        ArrayList<String> modified = getModified(stage);
        String ret = "";
        Collections.sort(modified);
        for (String s : modified) {
            ret += s + "\n";
        }
        return ret;
    }

    /**Returns the untracked file names.*/
    public String getUnTrackedNames() {
        Staging stage = FileCrap.getStaging();
        Collections.sort(_untracked);
        String ret = "";
        for (String s : _untracked) {
            if (!stage.isStaged(s)) {
                ret += s + "\n";
            }
        }
        return ret;
    }

    /**Returns the tracked names for status.*/
    public String getTrackedNames() {
        ArrayList<String> tracked = new ArrayList<>(_tracked.keySet());
        Collections.sort(tracked);
        String ret = "";
        for (String s : tracked) {
            ret += s + "\n";
        }
        return ret;
    }

    /**Checks out file NAME with snapshot B.*/
    public void checkoutFile(String name, Blob b) {
        Staging stage = FileCrap.getStaging();
        if (_untracked.contains(name) && !stage.isStaged(name)) {
            String p = "There is an untracked file in the way; ";
            p += "delete it or add it first.";
            Utils.problem(p);
        }
        Utils.writeContents(new File(name), b.getContents());
        _tracked.put(name, b.getID());
        if (_untracked.contains(name)) {
            _untracked.remove(name);
        }
    }

    /**Checks out commit COMMIT.*/
    public void checkoutBranch(Commit commit) {
        Staging stage = FileCrap.getStaging();
        for (String s : _untracked) {
            Blob b = new Blob(s);
            String p = "There is an untracked file in the way; ";
            p += "delete it or add it first.";
            if (!stage.isStaged(s)) {
                if (commit.getBlobs().keySet().contains(s)) {
                    if (!b.getID().equals(commit.getBlobs().get(s))) {
                        Utils.problem(p);
                    }
                }
            }
        }
        for (String s : commit.getBlobs().keySet()) {
            checkoutFile(s, FileCrap.getBlob(commit.getBlobs().get(s)));
        }
        ArrayList<String> rem = new ArrayList<>();
        for (String s : _tracked.keySet()) {
            if (!commit.getBlobs().containsKey(s)) {
                File f = new File(s);
                f.delete();
                rem.add(s);
            }
        }
        for (String ss : rem) {
            _tracked.remove(ss);
        }
    }

    /**Removes file of name S from tracked files.*/
    public void untrack(String s) {
        if (_tracked.containsKey(s)) {
            _tracked.remove(s);
        }
    }

    /**Path name for the directory.*/
    private String _path;

    /**Name and blob id of the tracked files.*/
    private HashMap<String, String> _tracked;

    /**Names of the untracked files.*/
    private ArrayList<String> _untracked;

    /**Name and blob id of the modified files.*/
    private HashMap<String, String> _modified;

}
