// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.monsterTypes;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.Monster;

public class TX5 extends Monster {

    private static final int INITIAL_WAIT = 5;

    public TX5(Game game) {
        super(game, MonsterType.TX5);
        this.stopMoving(INITIAL_WAIT);
        this.frozen = true;
    }

    protected void walkApproach() {

        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(getPacManLocation());

        Location next = getLocation().getNeighbourLocation(compassDir);
        if (furious && canMove(next)) next = next.getNeighbourLocation(compassDir);

        // Determine direction to pacActor and try to move in that direction. Otherwise, random walk.
        if (!isVisited(next) && canMove(next)) {
            setDirection(compassDir);
            setLocation(next);
            game.getGameCallback().monsterLocationChanged(this);
            addVisitedList(next);
        } else {
            randomWalk();
        }

    }

}
