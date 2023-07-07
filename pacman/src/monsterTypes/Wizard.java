// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.monsterTypes;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.Monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wizard extends Monster {

    public Wizard(Game game) {
        super(game, MonsterType.Wizard);
    }

    protected void walkApproach() {

        ArrayList<Location.CompassDirection> directions = new ArrayList<>(List.of(Location.CompassDirection.values()));
        Collections.shuffle(directions, randomiser);

        Location.CompassDirection tryDirection = null;
        Location tryLocation = null;

        for (Location.CompassDirection dir : directions) {
            tryDirection = dir;

            // If location is empty, move there
            tryLocation = getLocation().getNeighbourLocation(tryDirection);
            if (furious) tryLocation = tryLocation.getNeighbourLocation(tryDirection);
            if (canMove(tryLocation)) break;

            // If next location over is empty, move there
            tryLocation = tryLocation.getNeighbourLocation(tryDirection);
            if (canMove(tryLocation)) break;

        }

        if (!canMove(tryLocation)) {
            randomWalk();
            return;
        }

        setDirection(tryDirection);
        setLocation(tryLocation);
        game.getGameCallback().monsterLocationChanged(this);

    }

}
