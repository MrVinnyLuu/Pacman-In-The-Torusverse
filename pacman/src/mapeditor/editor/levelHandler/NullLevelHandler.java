// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.mapeditor.editor.levelHandler;

import src.mapeditor.editor.Constants;
import src.mapeditor.editor.Controller;

import java.io.File;

public class NullLevelHandler implements LevelHandler {
    public void handleLevel(Controller controller, File level) {
        controller.init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

}