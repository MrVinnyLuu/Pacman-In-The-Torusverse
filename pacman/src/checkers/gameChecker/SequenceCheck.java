// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.gameChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SequenceCheck implements IGameCheck {

    private final HashMap<Integer, ArrayList<String>> levelMaps = new HashMap<>();
    @Override
    public void check(GameChecker gc) throws IOException {

        // Get the numbers of the levels
        for (File file : gc.files) {

            String name = file.getName();

            if (!name.contains(".xml")) continue;

            StringBuilder prefix = new StringBuilder();
            for (char c : name.toCharArray()) {
                if (!Character.isDigit(c)) break;
                prefix.append(c);
            }

            if (prefix.isEmpty()) continue;

            Integer level = Integer.parseInt(String.valueOf(prefix));
            levelMaps.computeIfAbsent(level, k -> new ArrayList<>());
            levelMaps.get(level).add(name);

        }

        // Find any maps at same level
        for (Map.Entry<Integer, ArrayList<String>> levels : levelMaps.entrySet()) {
            if (levels.getValue().size() > 1) {
                StringBuilder line = new StringBuilder(String.format(gc.header + "multiple maps at the same level:"));
                for (String map : levels.getValue()) {
                    line.append(String.format(" %s;", map));
                }
                line.deleteCharAt(line.length()-1);
                line.append("\n");
                gc.fw.write(String.valueOf(line));
            }

        }

    }
}
