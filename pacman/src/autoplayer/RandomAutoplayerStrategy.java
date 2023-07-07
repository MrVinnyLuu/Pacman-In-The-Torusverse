// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.autoplayer;

import ch.aplu.jgamegrid.Location;
import src.items.Item;
import src.PacActor;

import java.util.ArrayList;

public class RandomAutoplayerStrategy implements IAutoplayerStrategy {
    /**
     * Tries to move directly towards the closest item (pill, gold, ice equally).
     * If it can't move directly to the closest item, it moves randomly.
     * Doesn't take into account portals or monsters.
     */
    @Override
    public Location moveInAutoMode(PacActor pac) {

        ArrayList<Location> itemLocations = new ArrayList<>();
        for (Item item : pac.getItems()) itemLocations.add(item.getLocation());

        Location closestItem = closestCell(pac, itemLocations);
        double oldDirection = pac.getDirection();
        Location.CompassDirection compassDir = pac.getLocation().get4CompassDirectionTo(closestItem);
        Location next = pac.getLocation().getNeighbourLocation(compassDir);
        pac.setDirection(compassDir);

        if (pac.isVisited(next) || !pac.canMove(next)) {

            // normal movement
            int sign = pac.randomiser.nextDouble() < 0.5 ? 1 : -1;
            pac.setDirection(oldDirection);
            pac.turn(sign * 90);  // Try to turn left/right
            next = pac.getNextMoveLocation();

            // If can't turn left/right
            if (!pac.canMove(next)) {

                pac.setDirection(oldDirection); // Try forwards
                next = pac.getNextMoveLocation();

                // If can't move forward
                if (!pac.canMove(next)) {

                    pac.setDirection(oldDirection);
                    pac.turn(-sign * 90);  // Try to turn right/left
                    next = pac.getNextMoveLocation();

                    // If can't turn right/left
                    if (!pac.canMove(next)) {
                        pac.setDirection(oldDirection);
                        pac.turn(180);  // Turn backward
                        next = pac.getNextMoveLocation();
                        pac.setLocation(next);
                    }

                }

            }

        }

        pac.setLocation(next);
        pac.addVisitedList(next);
        return next;

    }

}
