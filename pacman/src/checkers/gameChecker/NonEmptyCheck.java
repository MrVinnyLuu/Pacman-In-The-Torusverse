// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.gameChecker;

import java.io.File;
import java.io.IOException;

public class NonEmptyCheck implements IGameCheck {
    @Override
    public void check(GameChecker gc) throws IOException {

        // Count number of correctly named maps (not total number of files)
        int maps = 0;
        for (File file : gc.files) {
            String filename = file.getName();
            if (Character.isDigit(filename.charAt(0)) && filename.contains(".xml")) maps++;
        }

        if (maps < 1) {
            gc.fw.write(gc.header + "no maps found");
        }

    }
}
