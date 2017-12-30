package gitlet;

import java.io.File;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**All the random file statements that may be used more than once.
 * @author Lisa Dunlap*/
public class FileCrap implements Serializable {

    /**Creates a file of PATH if it doesn't already exist
     * and writes CONTENTS into it.*/
    static void createFile(String path, String contents) {
        try {
            File file = new File(path);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(contents);

            bw.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**Returns the pathname of the users directory.*/
    static String workingDir() {
        File file = new File(".gitlet/PATH");
        return new String(Utils.readContents(file));
    }

    /**Saves COMMIT to Objects folder in .gitlet directory.*/
    static void saveCommit(Commit commit) {

        String id = commit.getID();
        File outFile = new File(".gitlet/Objects/" + id);
        outFile.mkdir();
        File outFile2 = new File(".gitlet/Objects/" + id + "/" + id);
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(outFile2));
            out.writeObject(commit);
            out.close();
        } catch (IOException excp) {
            System.out.println("problem wrting commit");
        }
    }

    /**Returns commit from Objects folder given its ID.*/
    static Commit getCommit(String id) {
        Commit commit;
        File inFile = new File(".gitlet/Objects/" + id + "/" + id);
        if (!inFile.exists()) {
            Utils.problem("No commit with that id exists.");
        } else {
            try {
                ObjectInputStream in =
                        new ObjectInputStream(new FileInputStream(inFile));
                commit = (Commit) in.readObject();
                in.close();
            } catch (IOException | ClassCastException
                    | ClassNotFoundException excp) {
                throw new IllegalArgumentException(excp.getMessage());
            }
            return commit;
        }
        return null;

    }

    /**Changes ORIG_HEAD file to reflect the new working head of the tree
     * pointing at COMMIT as well as adding the new head to the logs.
     */
    static void changeHead(Commit commit) {
        String id = commit.getID();
        String current = getCurrentBranch();
        Utils.writeContents(new File(".gitlet/refs/heads/" + current), id);
        Utils.appendContents(".gitlet/logs/refs/heads/" + current, id);
        File log = new File(".gitlet/logs/HEAD");
        Utils.appendContents(".gitlet/logs/HEAD", commit.getID());
    }

    /**Changes ORIG_HEAD file to reflect the new working head of the tree
     * pointing at COMMIT and BRANCH as well as adding the new head to
     * the logs.
     */
    static void changeHead(Commit commit, String branch) {
        String id = commit.getID();
        Utils.writeContents(new File(".gitlet/refs/heads/" + branch), id);
        Utils.appendContents(".gitlet/logs/refs/heads/" + branch, id);
    }

    /**Returns the last commit made using the logs folder in .gitlet.*/
    static Commit getLastCommit() {
        String path = Utils.readContentsAsString(new File(".gitlet/HEAD"));
        String id = Utils.readContentsAsString(new File(".gitlet/" + path));
        return getCommit(id);
    }

    /**Returns the id of the current commit.*/
    static String getLastCommitID() {
        String path = Utils.readContentsAsString(new File(".gitlet/HEAD"));
        String id = Utils.readContentsAsString(new File(".gitlet/" + path));
        return id;
    }

    /**Saves a list of BLOBS to Objects directory in the folder containing the
     * COMMIT it is a part of.
     */
    static void saveBlobs(List<Blob> blobs) {
        for (Blob b : blobs) {
            saveBlob(b);
        }
    }

    /**Saves BLOB to Objects directory in the folder containing the
     * COMMIT it is a part of.
     */
    static void saveBlob(Blob blob) {
        String id = blob.getID();
        File outFile = new File(".gitlet/Objects/" + id);
        outFile.mkdir();
        File outFile2 = new File(".gitlet/Objects/" + id + "/" + id);
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(outFile2));
            out.writeObject(blob);
            out.close();
        } catch (IOException excp) {
            System.out.println("problem wrting blob");
        }
    }

    /**Returns blob from Objects folder given its ID.*/
    static Blob getBlob(String id) {
        Blob blob;
        File inFile = new File(".gitlet/Objects/" + id + "/" + id);
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
            blob = (Blob) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            blob = null;
            System.out.println("trouble getting blob");
        }
        return blob;
    }

    /**Saves staging STAGE to staging file in Objects folder.*/
    static void saveStaging(Staging stage) {
        File outFile = new File(".gitlet/Objects/staging");
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(stage);
            out.close();
        } catch (IOException excp) {
            System.out.println("problem writing staging");
        }
    }

    /**Returns the current staging directory from Objects folder.*/
    static Staging getStaging() {
        Staging stage;
        File inFile = new File(".gitlet/Objects/staging");
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
            stage = (Staging) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            stage = null;
            System.out.println("trouble getting staging");
        }
        return stage;
    }

    /**Clears the staging file after a commit is made.*/
    static void clearStaging() {
        saveStaging(new Staging());
    }

    /**Saves working dir DIR.*/
    static void saveWorkingDir(WorkingDir dir) {
        File outFile = new File(".gitlet/DIR");
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(dir);
            out.close();
        } catch (IOException excp) {
            System.out.println("problem writing working directory");
        }
    }

    /**Returns the current staging directory from Objects folder.*/
    static WorkingDir getWorkingDir() {
        WorkingDir dir;
        File inFile = new File(".gitlet/DIR");
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
            dir = (WorkingDir) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            dir = null;
            System.out.println("trouble getting working directory");
        }
        dir.setUntracked();
        return dir;
    }

    /**Clears the staging file after a commit is made.*/
    static void clearWorkingDir() {
        saveWorkingDir(new WorkingDir());
    }

    /**Creates a branch with name NAME.*/
    static void createBranch(String name) {
        String s = getLastCommitID();
        createFile(".gitlet/refs/heads/" + name, s);
        createFile(".gitlet/logs/refs/heads/" + name, s);
    }

    /**Returns the head commit of the BRANCH.*/
    static String getBranchHead(String branch) {
        String s = ".gitlet/refs/heads/" + branch;
        return Utils.readContentsAsString(new File(s));
    }

    /**Returns a list of all the current branches.*/
    static ArrayList<String> getBranches() {
        File file = new File(".gitlet/refs/heads");
        return Utils.plainFilenamesIn(file);
    }

    /**Returns the current branches name.*/
    static String getCurrentBranch() {
        String path = Utils.readContentsAsString(new File(".gitlet/HEAD"));
        return path.substring(11);
    }

    /**Returns the history of all the commits on a certain BRANCH
     * from the split point.*/
    static ArrayList<String> getBranchHistory(String branch) {

        ArrayList<String> commits = new ArrayList<>();
        File f = new File(".gitlet/logs/refs/heads/" + branch);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                commits.add(line);
            }
        } catch (IOException e) {
            throw Utils.error("Problem reading file");
        }
        return commits;
    }

    /**Returns split commit of current branch and GIVEN.*/
    static String getSplit(String given) {
        ArrayList<String> currHist = getBranchHistory(getCurrentBranch());
        ArrayList<String> givenHist = getBranchHistory(given);
        String split = "";
        for (String s : currHist) {
            if (givenHist.contains(s)) {
                split = s;
            }
        }
        if (split.equals("")) {
            System.out.println("didnt find split point");
        }
        return split;
    }

    /**Return the history of all commits made.*/
    static ArrayList<String> commitHistory() {
        ArrayList<String> commits = new ArrayList<>();
        File f = new File(".gitlet/logs/HEAD");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!commits.contains(line)) {
                    commits.add(line);
                }
            }
        } catch (IOException e) {
            throw Utils.error("Problem reading file");
        }
        return commits;
    }

    /**Returns the unique commit correspondnig to the ID.*/
    static String getShortenedCommit(String id) {
        for (String l : commitHistory()) {
            if (l.substring(0, id.length()).equals(id)) {
                return l;
            }
        }
        return "";
    }

    /**Returns whether branch S exists.*/
    static boolean branchExists(String s) {
        return getBranches().contains(s);
    }

}
