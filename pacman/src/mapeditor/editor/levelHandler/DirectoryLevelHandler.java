// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.mapeditor.editor.levelHandler;

import src.mapeditor.editor.Controller;

import java.io.File;

public class DirectoryLevelHandler implements LevelHandler {
    public void handleLevel(Controller controller, File level) {
       controller.startGame();
    }

}
