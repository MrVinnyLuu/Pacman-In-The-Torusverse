// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.levelChecker;

import java.io.IOException;

public class MinItemsCheck implements ILevelCheck {
    @Override
    public void check(LevelChecker lc) throws IOException {
        if (lc.goldLocations.size() + lc.pillLocations.size() < 2) {
            lc.fw.write(lc.header + "less than 2 Gold and Pill");
        }
    }

}
