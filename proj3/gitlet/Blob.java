package gitlet;

import java.io.File;
import java.io.Serializable;

/**Blob object; can read in wither a file name or byte[] contents.
 * @author Lisa Dunlap*/
public class Blob implements Serializable {

    /**Creates blob with contents of file named NAME.*/
    Blob(String name) {
        File file = new File(name);
        _contents = Utils.readContents(file);
        _id = Utils.sha1(_contents, name);
        _name = name;
    }

    /**Returns the id of the blob.*/
    public String getID() {
        return _id;
    }

    /**Returns whether this blob is equal to B.*/
    public boolean equals(Blob b) {
        return _id.equals(b.getID());
    }

    /**Returns the contents of the blob.*/
    public byte[] getContents() {
        return _contents;
    }

    /**Returns the contents of the blob as a string.*/
    public String getStringContents() {
        return new String(_contents);
    }

    /**Returns whether B1 and B2 are the same.*/
    public static boolean equals(Blob b1, Blob b2) {

        return b1._id.equals(b2._id);
    }

    /**Returns bs hashcode.*/
    public int hashCode() {
        return -1;
    }

    /**Sha-1 id.*/
    private String _id;

    /**Contents of the file.*/
    private byte[] _contents;

    /**Name of the file.*/
    private String _name;
}
