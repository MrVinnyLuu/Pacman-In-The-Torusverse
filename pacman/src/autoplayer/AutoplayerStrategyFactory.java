// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.autoplayer;

import java.util.HashMap;

public class AutoplayerStrategyFactory {

    private static AutoplayerStrategyFactory singleton = null;
    private final HashMap<Autoplayer, IAutoplayerStrategy> autoplayerStrategies = new HashMap<>();

    public AutoplayerStrategyFactory() {
        autoplayerStrategies.put(Autoplayer.Random, new RandomAutoplayerStrategy());
        autoplayerStrategies.put(Autoplayer.Smart, new SmartAutoplayerStrategy());
        autoplayerStrategies.put(Autoplayer.Directed, new DirectedAutoplayerStrategy());
    }

    public static AutoplayerStrategyFactory getInstance() {
        if (singleton == null) {
            singleton = new AutoplayerStrategyFactory();
        }
        return singleton;
    }

    public IAutoplayerStrategy getAutoplayerStrategy(Autoplayer type) {
        return autoplayerStrategies.get(type);
    }

}
