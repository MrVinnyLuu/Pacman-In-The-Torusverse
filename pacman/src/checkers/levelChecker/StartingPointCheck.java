// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.levelChecker;

import java.io.IOException;
import java.util.ArrayList;

public class StartingPointCheck implements ILevelCheck {
    @Override
    public void check(LevelChecker lc) throws IOException {
        // Check pacman validity
        if (lc.pacLocations.size() == 0) {
            lc.fw.write(lc.header + "no start for PacMan\n");
        } else if (lc.pacLocations.size() > 1) {
            StringBuilder line = new StringBuilder(lc.header + "more than one start for Pacman:");
            for (ArrayList<Integer> location : lc.pacLocations) {
                line.append(String.format(" (%d,%d);", location.get(0), location.get(1)));
            }
            line.deleteCharAt(line.length()-1);
            line.append("\n");
            lc.fw.write(String.valueOf(line));
        }
    }

}
