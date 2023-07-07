// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.levelChecker;

import java.io.IOException;
import java.util.ArrayList;

public class PortalPairCheck implements ILevelCheck {
    @Override
    public void check(LevelChecker lc) throws IOException {

        ArrayList<String> invalidPortals = new ArrayList<>();

        for (String portal : lc.portals.keySet()) {
            if (lc.portals.get(portal).size() != 2) {
                StringBuilder line = new StringBuilder(String.format(lc.header + "portal %s count is not 2:", portal));
                for (ArrayList<Integer> location : lc.portals.get(portal)) {
                    line.append(String.format(" (%d,%d);", location.get(0), location.get(1)));
                }
                line.deleteCharAt(line.length()-1);
                line.append("\n");
                lc.fw.write(String.valueOf(line));
                // Mark the invalid portal
                invalidPortals.add(portal);
            }
        }

        // Remove invalid portals
        for (String portal : invalidPortals) {
            lc.portals.remove(portal);
        }

        // Make a mapping of valid portals
        for (ArrayList<ArrayList<Integer>> pair : lc.portals.values()) {
            lc.validPortals.put(pair.get(0), pair.get(1));
            lc.validPortals.put(pair.get(1), pair.get(0));
        }

    }

}
