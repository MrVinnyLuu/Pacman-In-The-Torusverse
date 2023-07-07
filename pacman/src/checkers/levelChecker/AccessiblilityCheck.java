// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.levelChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class AccessiblilityCheck implements ILevelCheck {
    @Override
    public void check(LevelChecker lc) throws IOException {
        if (lc.pacLocations.size() == 0) {
            lc.fw.close();
            return;
        }

        // Find accessible cells
        HashSet<ArrayList<Integer>> accessible = new HashSet<>();
        ArrayList<Integer> defaultPacLocation = new ArrayList<>();
        defaultPacLocation.add(1); defaultPacLocation.add(1);
        ArrayList<Integer> pacLocation = lc.pacLocations.size() == 1 ? lc.pacLocations.get(0) : defaultPacLocation;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        LinkedList<ArrayList<Integer>> queue = new LinkedList<>();
        queue.add(pacLocation);

        while (!queue.isEmpty()) {
            ArrayList<Integer> cur = queue.removeLast();

            if (accessible.contains(cur)) continue;
            accessible.add(cur);

            for (int[] dir : dirs) {
                ArrayList<Integer> next = new ArrayList<>();
                next.add(cur.get(0)+dir[0]); next.add(cur.get(1)+dir[1]);
                if (lc.wallLocations.contains(cur) || cur.get(0)+dir[0] <= 0 || cur.get(0)+dir[0] > lc.width
                        || cur.get(1)+dir[1] <= 0 || cur.get(1)+dir[1] > lc.height) continue;
                if (lc.validPortals.containsKey(cur)) {
                    queue.add(lc.validPortals.get(cur));
                }
                queue.add(next);
            }
        }

        // Check for inaccessible gold pieces
        ArrayList<ArrayList<Integer>> inaccessGold = new ArrayList<>();
        for (ArrayList<Integer> location : lc.goldLocations) {
            if (!accessible.contains(location)) inaccessGold.add(location);
        }
        if (inaccessGold.size() > 0) {
            StringBuilder line = new StringBuilder(lc.header + "Gold not accessible:");
            for (ArrayList<Integer> location : inaccessGold) {
                line.append(String.format(" (%d,%d);", location.get(0), location.get(1)));
            }
            line.deleteCharAt(line.length()-1);
            line.append("\n");
            lc.fw.write(String.valueOf(line));
        }


        // Check for inaccessible pills
        ArrayList<ArrayList<Integer>> inaccessPills = new ArrayList<>();
        for (ArrayList<Integer> location : lc.pillLocations) {
            if (!accessible.contains(location)) inaccessPills.add(location);
        }
        if (inaccessPills.size() > 0) {
            StringBuilder line = new StringBuilder(lc.header + "Pill not accessible:");
            for (ArrayList<Integer> location : inaccessPills) {
                line.append(String.format(" (%d,%d);", location.get(0), location.get(1)));
            }
            line.deleteCharAt(line.length()-1);
            line.append("\n");
            lc.fw.write(String.valueOf(line));
        }
    }
}
