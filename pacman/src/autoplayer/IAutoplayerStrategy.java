// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.autoplayer;

import ch.aplu.jgamegrid.Location;
import src.items.Item;
import src.PacActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public interface IAutoplayerStrategy {

    Location moveInAutoMode(PacActor pac);


    /* Helper functions */
    Location.CompassDirection[] compassDir = new Location.CompassDirection[]{Location.CompassDirection.NORTH,
            Location.CompassDirection.SOUTH, Location.CompassDirection.EAST, Location.CompassDirection.WEST};
    int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    HashMap<ArrayList<Integer>, Integer> subareaMapping = new HashMap<>(); // Key: location, value: subarea
    HashMap<Integer, ArrayList<Location>> subareaToItems = new HashMap<>(); // Key: subarea, value: Items in subarea
    HashMap<Integer, ArrayList<Location>> subareaToPortals = new HashMap<>(); // Key: subarea, value: Portals in subarea

    default Location closestCell(PacActor pac, ArrayList<Location> cells) {
        int currentDistance = 1000;
        Location currentLocation = null;
        for (Location cell : cells) {
            int distanceToPill = cell.getDistanceTo(pac.getLocation());
            if (distanceToPill < currentDistance) {
                currentLocation = cell;
                currentDistance = distanceToPill;
            }
        }
        return currentLocation;
    }

    default ArrayList<Location> getSubareaItems(PacActor pac) {

        // Create the mapping the first time called
        if (subareaMapping.isEmpty()) {
            createSubareaMap(pac, pac.getLocation());
        }

        // Items need to be reassigned since they could've been eaten
        assignSubareaItems(pac);

        ArrayList<Integer> pacLocation = new ArrayList<>();
        pacLocation.add(pac.getX()); pacLocation.add(pac.getY());
        return subareaToItems.get(subareaMapping.get(pacLocation));

    }

    default ArrayList<Location> getSubareaPortals(PacActor pac) {

        // Assign portals the first time called
        if (subareaToPortals.isEmpty()) {
            assignSubareaPortals(pac);
        }

        ArrayList<Integer> pacLocation = new ArrayList<>();
        pacLocation.add(pac.getX()); pacLocation.add(pac.getY());
        return subareaToPortals.get(subareaMapping.get(pacLocation));

    }

    default void createSubareaMap(PacActor pac, Location start) {

        ArrayList<Integer> startLocation = new ArrayList<>();
        startLocation.add(start.x); startLocation.add(start.y);

        LinkedList<ArrayList<Integer>> queue = new LinkedList<>();
        queue.add(startLocation);

        // Flood fill the subarea
        Integer curSubarea = Math.toIntExact(subareaMapping.values().stream().distinct().count());
        while (!queue.isEmpty()) {

            ArrayList<Integer> cur = queue.removeLast();

            if (subareaMapping.get(cur) != null) {
                continue;
            }
            subareaMapping.put(cur, curSubarea);

            for (int[] dir : dirs) {
                ArrayList<Integer> next = new ArrayList<>();
                next.add(cur.get(0)+dir[0]); next.add(cur.get(1)+dir[1]);
                if (pac.canMove(new Location(next.get(0), next.get(1)))) {
                    queue.add(next);
                }
            }

        }

        // Check for other subareas
        for (int i = 0; i < pac.game.getNumVertCells(); i++) {
            for (int j = 0; j < pac.game.getNumHorzCells(); j++) {
                ArrayList<Integer> cell = new ArrayList<>();
                cell.add(j); cell.add(i);
                Location cellLocation = new Location(cell.get(0), cell.get(1));
                if (pac.canMove(cellLocation) && subareaMapping.get(cell) == null) {
                    createSubareaMap(pac, cellLocation);
                }
            }
        }

    }

    default void assignSubareaItems(PacActor pac) {

        subareaToItems.clear();

        ArrayList<Item> items = pac.getItems();

        for (Item item: items) {
            // Don't go for items that aren't need for the win
            if (item.getPoints() <= 0) continue;

            ArrayList<Integer> cell = new ArrayList<>();
            cell.add(item.getLocation().x); cell.add(item.getLocation().y);
            Integer subarea = subareaMapping.get(cell);
            subareaToItems.computeIfAbsent(subarea, k -> new ArrayList<>());
            subareaToItems.get(subarea).add(item.getLocation());

        }

    }

    default void assignSubareaPortals(PacActor pac) {

        Set<ArrayList<Integer>> portals = pac.getPortals().keySet();

        for (ArrayList<Integer> portal: portals) {
            Integer subarea = subareaMapping.get(portal);
            subareaToPortals.computeIfAbsent(subarea, k -> new ArrayList<>());
            subareaToPortals.get(subarea).add(new Location(portal.get(0), portal.get(1)));
        }

    }

}
