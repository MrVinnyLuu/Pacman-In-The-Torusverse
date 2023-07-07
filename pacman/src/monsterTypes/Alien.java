// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.monsterTypes;

import src.Game;
import src.Monster;

public class Alien extends Monster {

    public Alien(Game game) {
        super(game, MonsterType.Alien);
    }

    protected void walkApproach() {

        walkTowards(getPacManLocation());

    }

}
