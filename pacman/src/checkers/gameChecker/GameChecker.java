// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.gameChecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GameChecker {

    // Variables for the game state
    protected File[] files;
    protected FileWriter fw;
    protected String header;

    // Checkers that make up the composite object
    private final ArrayList<IGameCheck> checkers = new ArrayList<>();

    public GameChecker() {
        checkers.add(new NonEmptyCheck());
        checkers.add(new SequenceCheck());
    }

    public boolean checkGame(String folderpath) throws IOException {

        // Load files
        files = (new File(folderpath)).listFiles();

        // Set up error log file
        String delim = folderpath.contains("/") ? "/" : "\\\\";
        String[] tokens = folderpath.split(delim);
        String foldername = tokens[tokens.length-1];

        fw = new FileWriter(folderpath + delim + foldername + "log.txt");
        header = "Game " + foldername + " – ";

        // Perform checks
        for (IGameCheck checker : checkers) {
            checker.check(this);
//            System.out.println("Checking " + folderpath + " against " + checker);
        }

        // Delete the error log file if it's empty (no errors found)
        fw.close();
        File f = new File(folderpath + delim + foldername + "log.txt");
        if (f.length() == 0) {
            f.delete();
            return true;
        } else {
            return false;
        }

    }

}
