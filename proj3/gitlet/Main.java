package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Lisa Dunlap
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {

        if (args.length == 0) {
            Utils.problem("Please enter a command.");
        }
        Repository repo = new Repository(System.getProperty("user.dir"));
        if (args[0].equals("init")) {
            if (args.length > 1) {
                Utils.problem("Incorrect operands.");
            }
            repo.init();
            System.exit(0);
        }
        String[] commands = new String[]{"add", "rm", "commit", "merge"};
        String[] c2 = new String[]{"log", "status", "golobal-log", "find"};
        String[] c3 = new String[]{"rm-brnach", "checkout", "branch", "reset"};
        ArrayList<String> command = new ArrayList<>(Arrays.asList(commands));
        command.addAll(Arrays.asList(c2));
        command.addAll(Arrays.asList(c3));
        File f = new File(System.getProperty("user.dir") + "/.gitlet");
        if (!f.exists() && command.contains(args[0])) {
            Utils.problem("Not in an initialized Gitlet directory.");
        }
        CommandInterpreter ci = new CommandInterpreter(args, repo);
        ci.process();
    }

}
