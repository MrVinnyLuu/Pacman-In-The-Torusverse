// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src;

import src.mapeditor.editor.Controller;

import java.io.File;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH =
            "properties/test.properties";

    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        if (args.length == 0) {
            new Controller(null);
        } else if (args.length == 1) {
            new Controller(new File(args[0]));
        } else {
            System.out.println("Invalid arguments.");
            System.exit(1);
        }
    }

}
