// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.items;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;

import java.awt.*;

public class GoldPiece extends Item {
    private final static String NAME = "gold";
    private final static int POINTS = 5;
    private final static String EFFECT = "increaseSpeed";
    private static final int EFFECT_DURATION = 3;

    public GoldPiece(Location location) {
        super(NAME, location, EFFECT, EFFECT_DURATION, POINTS);
    }

    @Override
    public void putItem(GGBackground bg, Game g) {
        bg.setPaintColor(Color.yellow);
        bg.fillCircle(g.toPoint(this.getLocation()), 5);
        g.addActor(this, this.getLocation());
    }
}
