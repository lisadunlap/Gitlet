package gitlet;

import java.io.File;
import java.io.IOException;

/**Resposory for gitlet.
 * @author Lisa Dunlap*/
public class Repository {

    /**Creates repository with path name PATH.*/
    Repository(String path) {
        _path = path;
        _currentDir = null;
    }

    /**Initialized the gitlet directory.*/
    public void init() throws IOException {
        String path = _path + "/.gitlet/";
        File theDir = new File(_path + "/.gitlet");

        if (!theDir.exists()) {
            boolean result = false;

            try {
                theDir.mkdir();
                File objs = new File(path + "Objects");
                File refs = new File(path + "refs");
                File heads = new File(path + "refs/heads");
                File logs = new File(path + "logs");
                File logsrefs = new File(path + "logs/refs");
                File logsrefsheads = new File(path + "logs/refs/heads");
                File staging = new File(path + "Objects/staging");
                File dir = new File(path + "DIR");
                objs.mkdir();
                refs.mkdir();
                logs.mkdir();
                logsrefs.mkdir();
                logsrefsheads.mkdir();
                heads.mkdir();
                staging.createNewFile();
                dir.createNewFile();
                Commit commit = new Commit();
                commit.setID(Utils.sha1(Utils.serialize(commit)));
                String id = commit.getID();
                FileCrap.createFile(path + "refs/heads/master", id);
                FileCrap.createFile(path + "logs/refs/heads/master", id);
                FileCrap.createFile(path + "HEAD", "refs/heads/master");
                FileCrap.createFile(path + "logs/HEAD", id);
                FileCrap.createFile(path + "ORIG_HEAD", id);
                FileCrap.createFile(path + "PATH", _path);
                FileCrap.saveCommit(commit);
                Staging stage = new Staging();
                FileCrap.saveStaging(stage);
                FileCrap.clearWorkingDir();
                result = true;
            } catch (SecurityException se) {
                System.out.println(se);
            }
        } else {
            String m;
            m = "A Gitlet version-control system ";
            m += "already exists in the current directory.";
            Utils.message(m);
        }
    }

    /**Returns the working directory.*/
    public WorkingDir getWorkingDir() {
        return _currentDir;
    }

    /**Working directory.*/
    private WorkingDir _currentDir;

    /**File path to the directory.*/
    private String _path;
}
