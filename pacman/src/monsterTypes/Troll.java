// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.monsterTypes;

import src.Game;
import src.Monster;

public class Troll extends Monster {

    public Troll(Game game) {
        super(game, MonsterType.Troll);
    }

    protected void walkApproach() {
        randomWalk();
    }

}
