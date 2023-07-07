// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.items;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;

public abstract class Item extends Actor {
    private final String name;
    private final Location location; // Under the assumption it does not change for src.items
    private final String effect;
    private final int effectDuration;
    private final int points;

    public Item(String name, Location location, int points) {
        this.location = location;
        this.points = points;
        this.effect = "doNothing";
        this.effectDuration = 0;
        this.name = name;
    }

    public Item(String name, Location location, String effect, int effectDuration, int points) {
        super("sprites/" + name + ".png");
        this.location = location;
        this.name = name;
        this.effect = effect;
        this.effectDuration = effectDuration;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public String getEffect() {
        return effect;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    public abstract void putItem(GGBackground bg, Game g);

    public int getPoints() {
        return points;
    }
}
