// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.autoplayer;

import ch.aplu.jgamegrid.Location;
import src.Monster;
import src.PacActor;

import java.util.ArrayList;

public class SmartAutoplayerStrategy implements IAutoplayerStrategy{
    /**
     * Hypothetical use of the strategy pattern to implement a smarter autoplayer
     */
    @Override
    public Location moveInAutoMode(PacActor pac) {

        ArrayList<Location> accessibleItems = getSubareaItems(pac);
        ArrayList<Location> accessiblePortals = getSubareaPortals(pac);
        ArrayList<Monster> monsters = pac.getMonsters();
        Location next = new Location();

        /**
        * Do something with this information
        */

        pac.setLocation(next);
        pac.addVisitedList(next);

        return next;
    }
}
