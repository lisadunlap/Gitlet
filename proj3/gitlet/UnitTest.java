package gitlet;

import ucb.junit.textui;
import org.junit.Test;
import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Lisa Dunlap
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void commitTest() {
        Commit commit = new Commit("this is a commit", new HashMap<>(), "");
        commit.setID(Utils.sha1(Utils.serialize(commit)));
        assertEquals("this is a commit", commit.message());
        String m = "this is the next commit";
        Commit child = new Commit(m, new HashMap<>(), commit.getID());
        child.setID(Utils.sha1(Utils.serialize(child)));
        String s = "=== \ncommit " + commit.getID() + "\nDate: ";
        s += commit.getDate() + "\n" + commit.message();
        assertEquals(s, commit.toString());
        String i = commit.getID();
        Commit merge = new Commit(m, new HashMap<>(), i, child.getID());
        merge.setID(Utils.sha1(Utils.serialize(merge)));
        String ss = "=== \ncommit " + merge.getID();
        ss += "\nMerge: " + merge.getParent().substring(0, 7) + " ";
        ss += merge.getParent2().substring(0, 7);
        ss += "\nDate: " + merge.getDate() + "\n" + merge.message();
        assertEquals(ss, merge.toString());
    }

    @Test
    public void removeTest() {
        HashMap<String, String> blobs = new HashMap<>();
        blobs.put("file1.txt", "11111");
        blobs.put("file2.txt", "22222");
        blobs.put("file3.txt", "33333");
        Commit commit1 = new Commit("commit1", blobs, "");

    }

    @Test
    public void blobTest() {
        File file = new File("/Users/lisadunlap/repo/proj3/scrub.txt");
        String s = Utils.readContentsAsString(file);
        assertTrue(s.equals(Utils.readContentsAsString(file)));
        String ss = Utils.readContentsAsString(file);
        assertTrue(Utils.sha1(s).equals(Utils.sha1(ss)));
        assertTrue(Utils.sha1(s).equals(Utils.sha1(s)));
        Blob b1 = new Blob("/Users/lisadunlap/repo/proj3/scrub.txt");
        Blob b2 = new Blob("/Users/lisadunlap/repo/proj3/scrub.txt");
        assertTrue(b1.equals(b2));
    }

}


