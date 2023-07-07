// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.monsterTypes;

import src.Game;

public enum MonsterType {
    Troll,
    TX5,
    Orion,
    Alien,
    Wizard;

    public static MonsterType[] getValues(String version) {
        if (version.equals(Game.SIMPLE_VERSION)) {
            return new MonsterType[]{Troll, TX5};
        } else if (version.equals(Game.EXTENDED_VERSION)) {
            return values(); // All monsters
        } else {
            assert false;
            return new MonsterType[]{}; // Empty array
        }
    }

    public String getName() {
        switch (this) {
            case Troll -> {
                return "Troll";
            }
            case TX5 -> {
                return "TX5";
            }
            case Orion -> {
                return "Orion";
            }
            case Alien -> {
                return "Alien";
            }
            case Wizard -> {
                return "Wizard";
            }
            default -> {
                assert false;
                return "";
            }
        }
    }

}