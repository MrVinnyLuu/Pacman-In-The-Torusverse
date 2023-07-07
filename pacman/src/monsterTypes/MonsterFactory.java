// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.monsterTypes;

import src.Game;
import src.Monster;

public class MonsterFactory {

    public static Monster createMonster(Game game, MonsterType type) {
        switch (type) {
            case Troll -> {
                return new Troll(game);
            }
            case TX5 -> {
                return new TX5(game);
            }
            case Orion -> {
                return new Orion(game);
            }
            case Alien -> {
                return new Alien(game);
            }
            case Wizard -> {
                return new Wizard(game);
            }
            default -> {
                assert false;
                return null;
            }
        }
    }

}