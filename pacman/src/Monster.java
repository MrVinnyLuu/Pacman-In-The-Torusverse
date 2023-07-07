// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

// Monster.java
// Used for PacMan

package src;

import ch.aplu.jgamegrid.Location;
import src.items.GoldPiece;
import src.items.Item;
import src.monsterTypes.MonsterType;

import java.util.*;

import static java.lang.Math.min;

public abstract class Monster extends Character {
    private final static int SECOND_TO_MILLISECONDS = 1000;
    private final MonsterType type;
    protected boolean frozen = false;
    private boolean reFrozen = false;
    protected boolean furious = false;
    private boolean reFurious = false;
    private final Timer timer = new Timer();

    public Monster(Game game, MonsterType type) {
        super(game, "sprites/m_" + type.getName().toLowerCase() + ".gif");
        this.type = type;
    }

    public void update(String effect, int duration) {
        if (effect.equals("stopMoving")) {
            stopMoving(duration);
        } else if (effect.equals("increaseSpeed")) {
            increaseSpeed(duration);
        }
    }

    public void stopMoving() {
        this.frozen = true;
    }
    public void stopMoving(int duration) {
        if (frozen) reFrozen = true;
        frozen = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!reFrozen) frozen = false;
                reFrozen = false;
            }
        }, (long) duration * SECOND_TO_MILLISECONDS);
    }

    public void increaseSpeed(int duration) {
        if (frozen) return;

        if (furious) reFurious = true;
        furious = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!reFurious) furious = false;
                reFurious = false;
            }
        }, (long) duration * SECOND_TO_MILLISECONDS);
    }

    public void act() {
        if (frozen || Objects.equals(getLocation(), new Location(-1, -1))) {
            return;
        }
        walkApproach();
        game.teleport(this);
        setHorzMirror(!(getDirection() > 150) || !(getDirection() < 210));
    }

    protected abstract void walkApproach();

    protected void randomWalk() {

        double oldDirection = getDirection();

        int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;

        setDirection(oldDirection);
        turn(sign * 90);  // Try to turn left/right
        Location next = getNextMoveLocation();
        if (furious && canMove(next)) next = next.getNeighbourLocation(getDirection());

        // If can't turn left/right
        if (!canMove(next)) {

            setDirection(oldDirection);
            next = getNextMoveLocation();
            if (furious && canMove(next)) next = next.getNeighbourLocation(getDirection());

            // If can't move forward
            if (!canMove(next)) {

                setDirection(oldDirection);
                turn(-sign * 90);  // Try to turn right/left
                next = getNextMoveLocation();
                if (furious && canMove(next)) next = next.getNeighbourLocation(getDirection());

                // If can't turn right/left
                if (!canMove(next)) {

                    setDirection(oldDirection);
                    turn(180);  // Turn backward
                    next = getNextMoveLocation();
                    // Furious: Try to move two steps back if possible. If not, keep as just one step back.
                    if (furious) {
                        Location nextNext = next.getNeighbourLocation(getDirection());
                        if (canMove(next) && canMove(nextNext)) {
                            next = nextNext;
                        }
                    }

                }

            }

        }

        if(!canMove(next)) return;

        setLocation(next);
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);

    }

    protected void walkTowards(Location target) {

        HashMap<Integer, ArrayList<Location.CompassDirection>> dists = new HashMap<>(8);
        int minDist = Integer.MAX_VALUE;

        for (Location.CompassDirection dir : Location.CompassDirection.values()) {

            Location move = getLocation().getNeighbourLocation(dir);
            if (furious && canMove(move)) move = move.getNeighbourLocation(dir);
            if (!canMove(move) || isVisited(move)) continue;

            int curDist = move.getDistanceTo(target);
            minDist = min(minDist, curDist);
            if (dists.get(curDist) != null) {
                dists.get(curDist).add(dir);
            } else {
                dists.put(curDist, new ArrayList<>(List.of(dir)));
            }

        }

        ArrayList<Location.CompassDirection> choices = dists.get(minDist);
        if (choices == null) {
            randomWalk();
            return;
        }

        Location.CompassDirection compassDir = choices.get(randomiser.nextInt(choices.size()));
        Location next = getLocation().getNeighbourLocation(compassDir);
        if (furious) next = next.getNeighbourLocation(compassDir);
        setDirection(compassDir);

        setLocation(next);
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);

    }

    public MonsterType getType() {
        return type;
    }

    protected Location getPacManLocation() {
        return game.getPacManLocation();
    }

    protected ArrayList<Location> getGoldLocations() {
         ArrayList<Location> goldLocations = new ArrayList<>();
         ArrayList<Item> items = game.getItems();
         for(Item item: items){
             if(item instanceof GoldPiece){
                 goldLocations.add(item.getLocation());
             }
         }
        return goldLocations;
    }
}