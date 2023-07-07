// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.autoplayer;

import ch.aplu.jgamegrid.Location;
import src.PacActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.min;

public class DirectedAutoplayerStrategy implements IAutoplayerStrategy {

    /**
     * Takes into account different "sub-areas" created by portals.
     * Tries to move towards the closest item in the same "subarea" (pill and gold equally).
     * Moves by choosing a valid neighbouring cell (N/S/E/W) that would bring it closest to the closest item.
     * Doesn't take into account monsters.
     */
    @Override
    public Location moveInAutoMode(PacActor pac) {

        ArrayList<Location> accessibleItems = getSubareaItems(pac);
        Location target;
        if (accessibleItems.size() > 0) {
            target = closestCell(pac, accessibleItems);
        } else {
            target = closestCell(pac, getSubareaPortals(pac));
        }

        HashMap<Integer, ArrayList<Location.CompassDirection>> dists = new HashMap<>(4);
        int minDist = Integer.MAX_VALUE;

        for (Location.CompassDirection dir : compassDir) {

            Location move = pac.getLocation().getNeighbourLocation(dir);

            if (!pac.canMove(move) || pac.isVisited(move)) continue;

            int curDist = move.getDistanceTo(target);
            minDist = min(minDist, curDist);
            if (dists.get(curDist) != null) {
                dists.get(curDist).add(dir);
            } else {
                dists.put(curDist, new ArrayList<>(List.of(dir)));
            }

        }

        ArrayList<Location.CompassDirection> choices = dists.get(minDist);
        Location next;
        if (choices != null) {
            Location.CompassDirection compassDir = choices.get(pac.randomiser.nextInt(choices.size()));
            next = pac.getLocation().getNeighbourLocation(compassDir);
            pac.setDirection(compassDir);
        } else {
            next = pac.getLocation();
            pac.clearVisitedList();
        }

        pac.setLocation(next);
        pac.addVisitedList(next);

        return next;

    }
}
