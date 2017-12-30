package gitlet;

import java.io.File;

/**Command that adds a file to the stagin area.
 * @author Lisa Dunlap
 */
public class AddCommand {

    /**Name of file to be removed.*/
    private String _name;

    /**Working directory.*/
    private WorkingDir _dir;

    /**Creates command to add file NAME.*/
    AddCommand(String[] name) {
        if (name.length != 2) {
            Utils.problem("Incorrect operands.");
        }
        _name = name[1];
        _dir = FileCrap.getWorkingDir();
    }

    /**Adds the file NAME to the staging area.*/
    public void apply(String name) {
        File f = new File(name);
        if (f.isFile()) {
            Staging stage = FileCrap.getStaging();
            stage.stageFiles(name, _dir);
            FileCrap.saveStaging(stage);
            FileCrap.saveWorkingDir(_dir);
        } else {
            Utils.problem("File does not exist.");
        }
    }

    /**Adds all the files in the directory to the stagin area.*/
    public void apply2() {
        for (String s : Utils.plainFilenamesIn(_dir.path())) {
            apply(s);
        }
    }

}
