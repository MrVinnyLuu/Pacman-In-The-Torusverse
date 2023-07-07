// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.GGKeyRepeatListener;
import ch.aplu.jgamegrid.Location;
import src.autoplayer.Autoplayer;
import src.autoplayer.AutoplayerStrategyFactory;
import src.autoplayer.IAutoplayerStrategy;
import src.items.Item;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PacActor extends Character implements GGKeyRepeatListener {
    private static final int NB_SPRITES = 4;
    private int idSprite = 0;
    private int nbPills = 0;
    private int score = 0;
    private Autoplayer autoplayer = null;
    private AutoplayerStrategyFactory autoplayerStrategyFactory = null;
    public PacActor(Game game) {
        super(game, "sprites/pacpix.gif", NB_SPRITES);  // Rotatable
        game.addKeyRepeatListener(this);
    }

    public void setAuto(boolean auto) {
        if (auto) {
            autoplayerStrategyFactory = AutoplayerStrategyFactory.getInstance();
            autoplayer = Autoplayer.Directed;
        }
    }

    public void keyRepeated(int keyCode) {
        if (autoplayer != null) {
            return;
        }
        if (isRemoved())  // Already removed
            return;
        Location next = null;
        switch (keyCode) {
            case KeyEvent.VK_LEFT -> {
                next = getLocation().getNeighbourLocation(Location.WEST);
                setDirection(Location.WEST);
            }
            case KeyEvent.VK_UP -> {
                next = getLocation().getNeighbourLocation(Location.NORTH);
                setDirection(Location.NORTH);
            }
            case KeyEvent.VK_RIGHT -> {
                next = getLocation().getNeighbourLocation(Location.EAST);
                setDirection(Location.EAST);
            }
            case KeyEvent.VK_DOWN -> {
                next = getLocation().getNeighbourLocation(Location.SOUTH);
                setDirection(Location.SOUTH);
            }
        }
        if (next != null && canMove(next))
        {
            setLocation(next);
            eat(next);
            game.teleport(this);
        }
    }

    public void act() {

        show(idSprite);
        idSprite = (idSprite+1)%NB_SPRITES;

        if (autoplayer != null) {
            IAutoplayerStrategy autoplayerStrategy = autoplayerStrategyFactory.getAutoplayerStrategy(autoplayer);
            Location nextLocation = autoplayerStrategy.moveInAutoMode(this);
            eat(nextLocation);
        }

        this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
        game.teleport(this);

    }

    public ArrayList<Item> getItems() {
        return game.getItems();
    }
    public ArrayList<Monster> getMonsters() {
        return game.getMonsters();
    }

    public HashMap<ArrayList<Integer>, ArrayList<Integer>> getPortals() {
        return game.getPortals();
    }

    public void resetScore() {
        score = 0;
    }

    public int score() {
        return score;
    }

    private void eat(Location location) {

        if (getBackground().getColor(location).equals(game.getEmptyColor())) return;

        for (Iterator<Item> iterator = game.getItems().iterator(); iterator.hasNext();) {

            Item item = iterator.next();

            if (item.getLocation().equals(location)) {
                nbPills++;
                iterator.remove(); // remove the item from the ArrayList
                item.hide();

                score += item.getPoints();

                game.getGameCallback().pacManEatPillsAndItems(location, item.getName());
                getBackground().fillCell(location, game.getEmptyColor());

                game.notifyMonsters(item.getEffect(), item.getEffectDuration());

            }

        }

        clearVisitedList();
        String title = Game.STANDARD_TITLE + "Current score: " + score;
        gameGrid.setTitle(title);

    }


}