// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.checkers.levelChecker;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LevelChecker {
    // Note that this the levelChecks module uses ArrayList<Integer> instead of Location because it hashes easier

    // Variables for the level state
    protected final HashMap<String, ArrayList<ArrayList<Integer>>> portals = new HashMap<>();
    protected final HashMap<ArrayList<Integer>, ArrayList<Integer>> validPortals = new HashMap<>();
    protected final ArrayList<ArrayList<Integer>> pacLocations = new ArrayList<>();
    protected final ArrayList<ArrayList<Integer>> goldLocations = new ArrayList<>();
    protected final ArrayList<ArrayList<Integer>> pillLocations = new ArrayList<>();
    protected final ArrayList<ArrayList<Integer>> iceLocations = new ArrayList<>();
    protected final ArrayList<ArrayList<Integer>> pathLocations = new ArrayList<>();
    protected final HashSet<ArrayList<Integer>> wallLocations = new HashSet<>();
    protected int height, width;
    protected String filepath;
    protected String header;
    protected FileWriter fw;

    // Checkers that make up the composite object
    private final ArrayList<ILevelCheck> checkers = new ArrayList<>();

    public LevelChecker() {
        checkers.add(new StartingPointCheck());
        checkers.add(new PortalPairCheck());
        checkers.add(new MinItemsCheck());
        checkers.add(new AccessiblilityCheck());
    }

    private void reset() {
        portals.clear();
        validPortals.clear();
        pacLocations.clear();
        goldLocations.clear();
        pillLocations.clear();
        iceLocations.clear();
        pathLocations.clear();
        wallLocations.clear();
    }

    public boolean checkLevel(String filepath) throws IOException {

        reset();
        loadLevel(filepath);

        // Set up error log file
        filepath = filepath.replace(".xml","");
        fw = new FileWriter(filepath + "log.txt");
        String delim = filepath.contains("/") ? "/" : "\\\\";
        String[] tokens = filepath.split(delim);
        header = "Level " + tokens[tokens.length-1] + ".xml – ";

        // Perform checks
        for (ILevelCheck checker : checkers) {
            checker.check(this);
//            System.out.println("Checking " + filepath + " against " + checker);
        }

        // Delete the error log file if it's empty (no errors found)
        fw.close();
        File f = new File(filepath + "log.txt");
        if (f.length() == 0) {
            boolean deleted = f.delete();
            return true;
        } else {
            return false;
        }

    }

    private void loadLevel(String filepath) {

        this.filepath = filepath;

        SAXBuilder builder = new SAXBuilder();
        Document document;
        try {
            document = builder.build(filepath);
        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }

        Element level = document.getRootElement();

        Element size = (Element) level.getChildren("size").get(0);
        for (Object c : size.getChildren()) {
            Element e = (Element) c;
            if (e.getName().equals("width")) width = Integer.parseInt(e.getText());
            if (e.getName().equals("height")) height = Integer.parseInt(e.getText());
        }

        List<Element> rows = level.getChildren("row");
        for (int i = 0; i < height; i++) {

            List<Element> col = rows.get(i).getChildren("cell");
            for (int j = 0; j < width; j++) {

                String cellValue = col.get(j).getText();

                ArrayList<Integer> cell = new ArrayList<>();
                cell.add(j + 1);
                cell.add(i + 1);

                switch (cellValue) {
                    case "PathTile" -> pathLocations.add(cell);
                    case "WallTile" -> wallLocations.add(cell);
                    case "PillTile" -> pillLocations.add(cell);
                    case "GoldTile" -> goldLocations.add(cell);
                    case "IceTile" -> iceLocations.add(cell);
                    case "PacTile" -> pacLocations.add(cell);
                    case "PortalWhiteTile" -> {
                        portals.computeIfAbsent("White", k -> new ArrayList<>());
                        portals.get("White").add(cell);
                    }
                    case "PortalYellowTile" -> {
                        portals.computeIfAbsent("Yellow", k -> new ArrayList<>());
                        portals.get("Yellow").add(cell);
                    }
                    case "PortalDarkGoldTile" -> {
                        portals.computeIfAbsent("DarkGold", k -> new ArrayList<>());
                        portals.get("DarkGold").add(cell);
                    }
                    case "PortalDarkGrayTile" -> {
                        portals.computeIfAbsent("DarkGray", k -> new ArrayList<>());
                        portals.get("DarkGray").add(cell);
                    }
                }

            }

        }

    }

}
