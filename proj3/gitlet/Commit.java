package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**Commit class that holds the parents, message, and timestamp.
 * @author Lisa Dunlap
 */
public class Commit implements Serializable {

    /**Creates initial commit.*/
    Commit() {
        _message = "initial commit";
        Date date = new Date(0);
        String form = "EEE MMM d HH:mm:ss y Z";
        SimpleDateFormat format = new SimpleDateFormat(form);
        _timestamp = format.format(date);
        _parent = "";
        _parent2 = "";
        _blobs = new HashMap<>();
    }

    /**Creates normal commit with MESSAGE, BLOBS, and PARENT.*/
    Commit(String message, HashMap<String, String> blobs, String parent) {
        _message = message;
        Date date = new Date();
        String form = "EEE MMM d HH:mm:ss y Z";
        SimpleDateFormat format = new SimpleDateFormat(form);
        _timestamp = format.format(date);
        _blobs = blobs;
        _parent = parent;
        _parent2 = "";
        String b = "";
        for (String s: blobs.values()) {
            b += s;
        }
    }

    /**Creates merge commit with MESSAGE, BLOBS, parent P, and parent P2.*/
    Commit(String message, HashMap<String, String> blobs, String p, String p2) {
        _message = message;
        Date date = new Date();
        String form = "EEE MMM d HH:mm:ss y Z";
        SimpleDateFormat format = new SimpleDateFormat(form);
        _timestamp = format.format(date);
        _blobs = blobs;
        _parent = p;
        _parent2 = p2;
        String b = "";
        for (String s: blobs.values()) {
            b += s;
        }
    }

    /**Returns the ID of the commit.*/
    public String getID() {
        return _id;
    }

    /**Returns whether the commit contains a file S.*/
    public boolean contains(String s) {
        return _blobs.containsKey(s);
    }

    /**Returns the Sha-1 of the blob with the name S.*/
    public String getID(String s) {
        return _blobs.get(s);
    }

    /**Sets id to ID.*/
    public void setID(String id) {
        _id = id;
    }

    /**Returns the commit message.*/
    public String message() {
        return _message;
    }

    /**Returns the date the commit was made.*/
    public String getDate() {
        return _timestamp;
    }

    /**Returns the strign representation of the commit for log.*/
    public String toString() {
        if (!_parent2.equals("")) {
            String p1 = _parent.substring(0, 7);
            String p2 = _parent2.substring(0, 7);
            return "=== \ncommit " + _id
                    + "\nMerge: " + p1 + " " + p2
                    + "\nDate: " + _timestamp
                    + "\n" + _message;
        }
        return "=== \ncommit " + _id
                + "\nDate: " + _timestamp
                + "\n" + _message;
    }

    /**Returns the first parent of the commit.*/
    public String getParent() {
        return _parent;
    }

    /**Returns the second parent of the commit.*/
    public String getParent2() {
        return _parent2;
    }

    /**Returns the blob names and IDs of teh commit.*/
    public HashMap<String, String> getBlobs() {
        return _blobs;
    }

    /**Returns the file names tracked by the commits.*/
    public String trackedFiles() {
        String ret = "";
        for (String s : _blobs.keySet()) {
            ret += s + "\n";
        }
        return ret;
    }

    /**Message of the commit.*/
    private String _message;

    /**Time that the commit was made.*/
    private String _timestamp;

    /**The names and ids of the blobs tracked
     * by the commit.*/
    private HashMap<String, String> _blobs;

    /**First parent of the commit.*/
    private String _parent;

    /**Sha-1 id of the commit.*/
    private String _id;

    /**Second parent of the commit.*/
    private String _parent2;
}
