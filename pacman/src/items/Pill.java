// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.items;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;

import java.awt.*;

public class Pill extends Item {
    private static final String NAME = "pill";
    private static final int POINTS = 1;
    public Pill(Location location) {
        super(NAME, location, POINTS);
    }
    @Override
    public void putItem(GGBackground bg, Game g) {
        bg.setPaintColor(Color.white);
        bg.fillCircle(g.toPoint(this.getLocation()), 5);
    }
}
