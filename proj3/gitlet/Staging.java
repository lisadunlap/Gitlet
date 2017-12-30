package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**Creates the staging area to stages and removes files.
 * @author Lisa Dunlap/
 */
public class Staging implements Serializable {

    /**Creates staging area.*/
    Staging() {
        _stFiles = new HashMap<>();
        _rmFiles = new ArrayList<>();
    }

    /**Returns the map of file names and blob ID's that
     * have been staged for commit.*/
    public HashMap<String, String> getStFiles() {
        return _stFiles;
    }

    /**Returns the map of file names and blob ID's that have been removed.*/
    public ArrayList<String> getRmFiles() {

        return _rmFiles;
    }

    /**Adds FILE to the list of files to be staged with working directory DIR.
     * If it is already staged, replace the old blob with the one at the time
     * of the most recent add.
     */
    public void stageFiles(String file, WorkingDir dir) {
        Blob blob = new Blob(file);
        boolean tracked = dir.getTracked().containsValue(blob.getID());
        if (_stFiles.containsValue(blob.getID()) || tracked) {
            return;
        } else if (_stFiles.containsKey(file)) {
            String id = _stFiles.get(file);
            File f = new File(".gitlet/Objects/" + id);
            File f2 = new File(".gitlet/Objects/" + id + "/" + id);
            f2.delete();
            f.delete();
        }
        if (_rmFiles.contains(file)) {
            _rmFiles.remove(file);
            dir.restrictedAdd(file);
            FileCrap.saveWorkingDir(dir);
            FileCrap.saveBlob(blob);
            return;
        }
        _stFiles.put(file, blob.getID());
        FileCrap.saveBlob(blob);
    }

    /**Adds FILE with blob ID to the list of files to be staged.
     * If it is already staged, replace the old blob with the one
     * at the time of the most recent add.
     */
    public void stageFiles(String file, String id) {
        Blob b = FileCrap.getBlob(id);
        if (_stFiles.containsValue(b.getID())) {
            return;
        }
        if (_rmFiles.contains(file)) {
            _rmFiles.remove(file);
            return;
        }
        _stFiles.put(file, b.getID());
    }

    /**Returns whether file FILE has been staged.*/
    public boolean isStaged(String file) {

        return _stFiles.keySet().contains(file);
    }

    /**Returns whether file FILE has been removed.*/
    public boolean isRemoved(String file) {
        return _rmFiles.contains(file);
    }

    /**Adds FILES to list of files to be staged.*/
    public void stageFiles(List<String> files) {
        for (String s : files) {
            WorkingDir dir = FileCrap.getWorkingDir();
            stageFiles(s, dir);
        }
    }

    /**Adds FILE to the list of removed files, assuimng
     * it has been removed.*/
    public void removeFiles(String file) {
        WorkingDir dir = FileCrap.getWorkingDir();
        if (_stFiles.containsKey(file) && dir.getUntracked().contains(file)) {
            _stFiles.remove(file);
            return;
        }
        if (!_rmFiles.contains(file)) {
            _rmFiles.add(file);
        }
        if (_stFiles.containsKey(file)) {
            _stFiles.remove(file);
        }
    }

    /**Adds FILES to the list of removed files, assuimng
     * they have been removed.*/
    public void removeFiles(List<String> files) {
        for (String s : files) {
            removeFiles(s);
        }
    }

    /**Returns whether anything has been staged or
     * removed since last commit.*/
    public boolean anyChanges() {

        return !_stFiles.isEmpty() || !_rmFiles.isEmpty();
    }

    /**Returns a string containing the names of the files
     * currently staged for commit.*/
    public String getStFileNames() {
        ArrayList<String> staged = new ArrayList<>(_stFiles.keySet());
        Collections.sort(staged);
        String ret = "";
        for (String s : staged) {
            ret += s + "\n";
        }
        return ret;
    }

    /**Returns a string containing the names of the files removed.*/
    public String getRmFileNames() {
        Collections.sort(_rmFiles);
        String ret = "";
        for (String s : _rmFiles) {
            ret += s + "\n";
        }
        return ret;
    }

    /**Return whether the FILE has been modified since the last staging.*/
    public boolean modified(String file) {
        Blob b = new Blob(file);
        if (_stFiles.containsKey(file)) {
            if (!_stFiles.get(file).equals(b.getID())) {
                return true;
            }
        }
        return false;
    }

    /**Map of file names and corresponding blob ID's to
     * snapshots of files staged
     * for commit.*/
    private HashMap<String, String> _stFiles;

    /**Map of file names and corresponding blob ID's to
     * snapshots of removed files.*/
    private ArrayList<String> _rmFiles;

}
