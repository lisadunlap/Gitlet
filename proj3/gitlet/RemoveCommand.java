package gitlet;

import java.io.File;

/**Command to remove a file if it has been previously tracked.
 * @author Lisa Dunlap.
 */
public class RemoveCommand {

    /**File to be removed.*/
    private String _name;

    /**Working directory.*/
    private WorkingDir _dir;

    /**Creates remove command for file NAME.*/
    RemoveCommand(String[] name) {
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
        _dir = FileCrap.getWorkingDir();
    }

    /**Removes the file from stages and possibly the directory.*/
    public void apply() {
        File file = new File(_name);
        if (!file.exists() && !_dir.isTracked(_name)) {
            Utils.problem("File does not exist.");
        }
        Staging stage = FileCrap.getStaging();
        _dir.remove(stage, _name);
        FileCrap.saveStaging(stage);
        FileCrap.saveWorkingDir(_dir);
    }

}
