// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.grid;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import src.Character;

public abstract class PacManGameGrid extends GameGrid {
    private final static int NB_HORZ_CELLS = 20;
    private final static int NB_VERT_CELLS = 11;
    protected final static Color WALL_COLOR = Color.gray;
    protected final static Color EMPTY_COLOR = Color.lightGray;

    // Dict of portal locations of each color
    protected final HashMap<String, ArrayList<Location>> portalColors = new HashMap<>();
    // Connected portals
    protected final HashMap<ArrayList<Integer>, ArrayList<Integer>> portals = new HashMap<>();

    // List of locations for various game objects
    protected final ArrayList<Location> pacLocations = new ArrayList<>();
    protected final ArrayList<Location> TX5Locations = new ArrayList<>();
    protected final ArrayList<Location> trollLocations = new ArrayList<>();
    protected final ArrayList<Location> goldLocations = new ArrayList<>();
    protected final ArrayList<Location> pillLocations = new ArrayList<>();
    protected final ArrayList<Location> iceLocations = new ArrayList<>();
    protected final ArrayList<Location> pathLocations = new ArrayList<>();
    protected final ArrayList<Location> wallLocations = new ArrayList<>();

    public PacManGameGrid() {
        super(NB_HORZ_CELLS, NB_VERT_CELLS, 20, false);
    }

    protected void resetMap() {
        portalColors.clear();
        portals.clear();
        pacLocations.clear();
        TX5Locations.clear();
        trollLocations.clear();
        goldLocations.clear();
        pillLocations.clear();
        iceLocations.clear();
        pathLocations.clear();
        wallLocations.clear();
    }

    protected void loadMapFile(File f) {

        SAXBuilder builder = new SAXBuilder();
        Document document;
        try {
            document = builder.build(f);
        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }

        Element level = document.getRootElement();

        Element size = (Element) level.getChildren("size").get(0);
        int width = 0, height = 0;
        for (Object c : size.getChildren()) {
            Element e = (Element) c;
            if (e.getName().equals("width")) width = Integer.parseInt(e.getText());
            if (e.getName().equals("height")) height = Integer.parseInt(e.getText());
        }

        java.util.List<Element> rows = level.getChildren("row");
        for (int i = 0; i < height; i++) {

            List<Element> col = rows.get(i).getChildren("cell");
            for (int j = 0; j < width; j++) {

                String cellValue = col.get(j).getText();

                Location cell = new Location(j,i);

                switch (cellValue) {
                    case "PathTile" -> pathLocations.add(cell);
                    case "WallTile" -> wallLocations.add(cell);
                    case "PillTile" -> pillLocations.add(cell);
                    case "GoldTile" -> goldLocations.add(cell);
                    case "IceTile" -> iceLocations.add(cell);
                    case "PacTile" -> pacLocations.add(cell);
                    case "TX5Tile" -> TX5Locations.add(cell);
                    case "TrollTile" -> trollLocations.add(cell);
                    case "PortalWhiteTile" -> {
                        portalColors.computeIfAbsent("White", k -> new ArrayList<>());
                        portalColors.get("White").add(cell);
                    }
                    case "PortalYellowTile" -> {
                        portalColors.computeIfAbsent("Yellow", k -> new ArrayList<>());
                        portalColors.get("Yellow").add(cell);
                    }
                    case "PortalDarkGoldTile" -> {
                        portalColors.computeIfAbsent("DarkGold", k -> new ArrayList<>());
                        portalColors.get("DarkGold").add(cell);
                    }
                    case "PortalDarkGrayTile" -> {
                        portalColors.computeIfAbsent("DarkGray", k -> new ArrayList<>());
                        portalColors.get("DarkGray").add(cell);
                    }
                }

            }

        }

        // Map out portals
        for (ArrayList<Location> pair : portalColors.values()) {
            ArrayList<Integer> p1 = new ArrayList<>();
            p1.add(pair.get(0).x); p1.add(pair.get(0).y);

            ArrayList<Integer> p2 = new ArrayList<>();
            p2.add(pair.get(1).x); p2.add(pair.get(1).y);

            portals.put(p1, p2);
            portals.put(p2, p1);

        }

    }

    public void teleport(Character actor) {
        ArrayList<Integer> cell = new ArrayList<>();
        cell.add(actor.getX()); cell.add(actor.getY());
        if (portals.get(cell) != null) {
            Location newLocation = new Location(portals.get(cell).get(0), portals.get(cell).get(1));
            actor.setLocation(newLocation);
        }
    }

    public int getNumHorzCells(){
        return NB_HORZ_CELLS;
    }
    public int getNumVertCells(){
        return NB_VERT_CELLS;
    }

    public Color getEmptyColor() {
        return EMPTY_COLOR;
    }
    public Color getWallColor() {
        return WALL_COLOR;
    }

}
