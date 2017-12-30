package gitlet;

import java.io.IOException;

/**processes unser input into appropriate commands.
 * @author Lisa Dunlap.
 */
public class CommandInterpreter {

    /**Creates command interpretor with user input ARGS and repo REPO.*/
    CommandInterpreter(String[] args, Repository repo) {
        _args = args;
        _repo = repo;
    }

    /**Processes the commands.*/
    public void process() throws IOException {
        int length = _args.length;
        String command = _args[0];
        switch (command) {
        case "add":
            AddCommand ac = new AddCommand(_args);
            if (_args[1].equals(".")) {
                ac.apply2();
            } else {
                ac.apply(_args[1]);
            }
            break;
        case "commit":
            CommitCommand cc = new CommitCommand(_args);
            cc.apply();
            break;
        case "rm":
            RemoveCommand rc = new RemoveCommand(_args);
            rc.apply();
            break;
        case "status":
            StatusCommand sc = new StatusCommand(_args);
            sc.apply();
            break;
        case "log":
            LogCommand lc = new LogCommand(_args);
            lc.apply();
            break;
        case "branch":
            BranchCommand bc = new BranchCommand(_args);
            bc.apply();
            break;
        case "checkout":
            CheckoutCommand chc = new CheckoutCommand(_args);
            chc.apply();
            break;
        case "global-log":
            GlobalLogCommand glc = new GlobalLogCommand(_args);
            glc.apply();
            break;
        case "find":
            FindCommand fc = new FindCommand(_args);
            fc.apply();
            break;
        case "rm-branch":
            RmBranchCommand rbc = new RmBranchCommand(_args);
            rbc.apply();
            break;
        case "reset":
            ResetCommand rec = new ResetCommand(_args);
            rec.apply();
            break;
        case "merge":
            MergeCommand mc = new MergeCommand(_args);
            mc.apply();
            break;
        default:
            Utils.problem("No command with that name exists.");
        }
    }



    /**Arguemnts given by the user.*/
    private String[] _args;

    /**Repository.*/
    private Repository _repo;
}
