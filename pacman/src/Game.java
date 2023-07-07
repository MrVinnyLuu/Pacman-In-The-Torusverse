// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.checkers.gameChecker.GameChecker;
import src.checkers.levelChecker.LevelChecker;
import src.grid.PacManGameGrid;
import src.items.GoldPiece;
import src.items.IceCube;
import src.items.Item;
import src.items.Pill;
import src.monsterTypes.TX5;
import src.monsterTypes.Troll;
import src.utility.GameCallback;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Game extends PacManGameGrid{
    public final static String STANDARD_TITLE = "[PacMan in the TorusVerse]";
    private final static String WIN_TITLE = "YOU WIN";
    private final static String LOSE_TITLE = "GAME OVER";
    private final static String EXPLOSION_SPRITE = "sprites/explosion3.gif";
    public final static String SIMPLE_VERSION = "simple";
    public final static String EXTENDED_VERSION = "multiverse";
    private final static int SLOW_DOWN = 3;
    private final static int SIM_PERIOD = 100;
    private final static int KEY_REPEAT_PERIOD = 150;
    private final static int STATE_CHECK_DELAY = 10;
    private final static int END_DELAY = 10;

    private final GameCallback gameCallback;
    private final GameChecker gameChecker = new GameChecker();
    private final LevelChecker levelChecker = new LevelChecker();
    private String version = SIMPLE_VERSION; // Default to simple
    private int seed = 30006; // Default to 30006
    private int totalPoints = 0;
    private final PacActor pacActor = new PacActor(this);
    private final ArrayList<Monster> monsters = new ArrayList<>();
    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<File> gameMaps = new ArrayList<>();
    private final File gameFile;
    private File curFile = null;

    private volatile boolean running;

    public Game(GameCallback gameCallback, Properties properties, File gameFile) throws IOException {
        super();
        this.gameCallback = gameCallback;
        this.running = true;
        this.gameFile = gameFile;

        initializeGame(properties);
        // To run a specific map
        loadMaps(gameFile);
        run();
    }

    private void initializeGame(Properties properties) {
        setSimulationPeriod(SIM_PERIOD);
        setTitle(STANDARD_TITLE);

        // Get relevant values from properties file
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
        seed = Integer.parseInt(properties.getProperty("seed"));
    }

    /**
     * Plays every game in gameMaps. In order to only play 1 map, ensure gameMaps only contains 1 map
     * I think game_checking should happen in this function before we for loop
     * If it fails, call the function stop() -> This should automatically work with 2.2.2.3 but I'm not sure
     *
     * @return
     **/
    private void run() throws IOException {
        if(!(gameCheckHandler() & levelsCheckHandler())){
            stop();
            return;
        }
        System.out.println("Passed all game checks...");
        for (File level : gameMaps) {
            // Perform level check
            loadLevel(level);
            doRun();
            show();
            while (pacActor.score() < totalPoints) {
                for (Monster m : monsters) {
                    if (m.getLocation().equals(pacActor.getLocation())) {
                        gameLose();
                        stop();
                        // End thread if it is running on thread
                        return;
                    }
                }
                delay(STATE_CHECK_DELAY);
            }
            resetGame();

            delay(END_DELAY);
            for (Monster m : monsters) {
                m.stopMoving();
            }
            pacActor.removeSelf();

            setTitle(WIN_TITLE);
            gameCallback.endOfGame(WIN_TITLE);
            doPause();
        }
        stop();
    }

    public boolean gameCheckHandler() throws IOException {
        System.out.println("Testing game folder: " + gameFile.toString());
        if(!gameFile.isDirectory()){
            return true;
        }
        if (!gameChecker.checkGame(gameFile.getPath())) {
            System.out.println("Failed game check, will return to editor after level checks...");
            return false;
        }
        return true;
    }

    public boolean levelsCheckHandler() throws IOException {
        boolean ret = true;
        int fails = 0;

        for (File level : gameMaps) {
            System.out.println("Checking level: " + level);
            if (!levelChecker.checkLevel(level.getPath())) {
                System.out.println("Failed level check on: " + level.getName());
                if (fails == 0) curFile = level;
                ret = false;
                fails++;
            }
        }
        if (ret) {
            System.out.println("Passed all level checks...");
        } else if (fails > 1) {
            System.out.format("Failed level checks on %d levels, returning to editor on first failed level...\n", fails);
        } else {
            System.out.println("Failed a level check, returning to editor on failed level...");
        }
        return ret;

    }

    public void stop() {
        running = false;
    }
    private void gameLose() {
        delay(END_DELAY);
        Location loc = pacActor.getLocation();
        for (Monster m : monsters) m.stopMoving();
        pacActor.removeSelf();
        addActor(new Actor(EXPLOSION_SPRITE), loc);
        setTitle(LOSE_TITLE);
        gameCallback.endOfGame(LOSE_TITLE);
        doPause();
    }

    private void loadMaps(File game) {
        if (game.isDirectory()) {
            File[] files = game.listFiles();
            // Make sure the files are sorted
            Arrays.sort(files);

            for (File file : files) {
                if (file.getName().contains(".xml")) {
                    gameMaps.add(file);
                }
            }
        } else {
            // Singular map
            gameMaps.add(game);
        }
    }

    private void loadLevel(File game) {
        // Load in map, item locations and actor locations
        loadMapFile(game);
        setupPacActor();
        setupMonsters();
        setupItems();

        // Draw map
        GGBackground bg = getBg();
        drawGrid(bg);
    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }

    private void resetGame() {
        removeAllActors();
        resetMap();
        pacActor.resetScore();
        monsters.clear();
        items.clear();
        totalPoints = 0;
        running = true;
    }

    private void setupPacActor() {
        addActor(pacActor, pacLocations.get(0));
        pacActor.setSeed(seed);
        pacActor.setSlowDown(SLOW_DOWN);
        setKeyRepeatPeriod(KEY_REPEAT_PERIOD);
    }

    private void setupMonsters() {
        for (Location location : TX5Locations) {
            Monster TX5 = new TX5(this);
            addActor(TX5, location);
            monsters.add(TX5);
        }

        for (Location location : trollLocations) {
            Monster troll = new Troll(this);
            addActor(troll, location);
            monsters.add(troll);
        }

        for (Monster m : monsters) {
            m.setSeed(seed);
            m.setSlowDown(SLOW_DOWN);
        }

    }

    private void setupItems() {
        for (Location location : pillLocations) items.add(new Pill(location));
        for (Location location : goldLocations) items.add(new GoldPiece(location));
        for (Location location : iceLocations) items.add(new IceCube(location));
        for (Item i : items) totalPoints += i.getPoints();
    }

    private void drawGrid(GGBackground bg) {
        bg.clear(EMPTY_COLOR);

        for (Location cell : wallLocations) {
            bg.fillCell(cell, WALL_COLOR);
        }

        for (Item item: items){
            item.putItem(bg, this);
        }

        for (Map.Entry<String, ArrayList<Location>> pair : portalColors.entrySet()) {
            addActor(new Actor("sprites/portal"+ pair.getKey() +"Tile.png"), pair.getValue().get(0));
            addActor(new Actor("sprites/portal"+ pair.getKey() +"Tile.png"), pair.getValue().get(1));
        }

    }

    public void notifyMonsters(String effect, int duration) {

        if (version.equals(EXTENDED_VERSION)) {
            for (Monster m : monsters) m.update(effect,duration);
        }

    }

    public Location getPacManLocation() {
        return pacActor.getLocation();
    }

    public ArrayList<Item> getItems(){
        return this.items;
    }
    public ArrayList<Monster> getMonsters(){
        return this.monsters;
    }
    public HashMap<ArrayList<Integer>, ArrayList<Integer>> getPortals() {
        return this.portals;
    }
    public boolean running() {
        return running;
    }
    public File curFile() {
        return curFile;
    }


}