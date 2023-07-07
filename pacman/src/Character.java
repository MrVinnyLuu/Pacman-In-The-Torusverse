// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class Character extends Actor {
    private final static int VISITED_LIST_LEN = 10;
    public final Game game;
    private final ArrayList<Location> visitedList = new ArrayList<>(VISITED_LIST_LEN);
    public final Random randomiser = new Random();

    public Character(Game game, String spriteName) {
        super(spriteName);
        this.game = game;
    }

    public Character(Game game, String spriteName, int nbSprites) {
        super(true, spriteName, nbSprites); // Rotatable
        this.game = game;
    }

    protected void setSeed(int seed) {
        randomiser.setSeed(seed);
    }

    public abstract void act();

    public void addVisitedList(Location location) {
        visitedList.add(location);
        if (visitedList.size() == VISITED_LIST_LEN) visitedList.remove(0);
    }

    public void clearVisitedList() {
        visitedList.clear();
    }

    public boolean isVisited(Location location) {
        return visitedList.contains(location);
    }

    public boolean canMove(Location location) {
        Color c = getBackground().getColor(location);
        return !c.equals(game.getWallColor()) && location.getX() < game.getNumHorzCells()
                && location.getX() >= 0 && location.getY() < game.getNumVertCells() && location.getY() >= 0;
    }

}