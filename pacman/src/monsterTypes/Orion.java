// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.monsterTypes;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.Monster;

import java.util.ArrayList;
import java.util.Collections;

public class Orion extends Monster {

    private final ArrayList<Location> ogGoldLocations;
    private final ArrayList<Location> visitedGoldLocations = new ArrayList<>();
    private Location curTarget;

    public Orion(Game game) {
        super(game, MonsterType.Orion);
        ogGoldLocations = getGoldLocations();
        curTarget = ogGoldLocations.get(randomiser.nextInt(ogGoldLocations.size()));
    }

    protected void walkApproach() {
        if (this.getLocation().equals(curTarget)) updateTarget();
        walkTowards(curTarget);
    }

    private void updateTarget() {

        if (visitedGoldLocations.size() == ogGoldLocations.size()-1) visitedGoldLocations.clear();

        visitedGoldLocations.add(curTarget);

        ArrayList<Location> curGoldLocations = getGoldLocations();
        Collections.shuffle(curGoldLocations, randomiser);

        Location targetGold = null;

        // Try target a non-visited, non-eaten gold
        for (Location gold : curGoldLocations) {
            targetGold = gold;
            if (!visitedGoldLocations.contains(targetGold)) break;
        }

        // Try no non-visited non-eaten gold, target a non-visited eaten gold
        if (targetGold == null) {
            // Find eaten gold
            ogGoldLocations.removeAll(curGoldLocations);

            for (Location gold : ogGoldLocations) {
                targetGold = gold;
                if (!visitedGoldLocations.contains(targetGold)) break;
            }

            // Revert ogGoldLocations
            ogGoldLocations.addAll(curGoldLocations);
        }

        curTarget = targetGold;

    }

}
