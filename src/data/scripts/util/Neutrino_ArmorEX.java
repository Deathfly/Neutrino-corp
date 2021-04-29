package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.combat.entities.Ship;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_ArmorEX {

    private static final Map<String, Integer> countedInBoundArmor = new HashMap<>(100);
    private static final Map<String, Integer> countedValidArmor = new HashMap<>(100);
    private static final Map<String, boolean[][]> inBoundArmorCells = new HashMap<>(100);
    private static final Map<String, boolean[][]> validArmorCells = new HashMap<>(100);

    public static int getValidArmorCellCount(ShipAPI ship) {
        String id = ship.getHullSpec().getHullId();
        if (countedValidArmor.containsKey(id)) {
            return countedValidArmor.get(id);
        } else {
            validArmorCellCheck(ship);
            return countedValidArmor.get(id);
        }
    }

    public static int getInBoundArmorCellCount(ShipAPI ship) {
        String id = ship.getHullSpec().getHullId();
        if (countedInBoundArmor.containsKey(id)) {
            return countedInBoundArmor.get(id);
        } else {
            inBoundArmorCellCheck(ship);
            return countedInBoundArmor.get(id);
        }
    }

    public static boolean[][] validArmorCellCheck(ShipAPI ship) {
        String id = ship.getHullSpec().getHullId();
        if (validArmorCells.containsKey(id)) {
            return validArmorCells.get(id);
        }
        ArmorGridAPI armorGrid = ship.getArmorGrid();
        if (armorGrid.getGrid() == null) {
            return null;
        }
        int count = 0;
        int x = armorGrid.getGrid().length;
        int y = armorGrid.getGrid()[0].length;
        boolean valid[][] = new boolean[x][y];
        boolean inBounds[][] = inBoundArmorCellCheck(ship);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                isValid:
                for (int a = -2; a < 3; a++) {
                    if (i + a < 0 || i + a > x - 1) {
                        continue;
                    }
                    for (int b = -2; b < 3; b++) {
                        if (Math.abs(a * b) == 4
                                || j + b < 0 || j + b > y - 1) {
                            continue;
                        }

                        if (inBounds[(i + a)][(j + b)]) {
                            valid[i][j] = true;
                            count++;
                            break isValid;
                        }
                    }
                }

            }
        }
        validArmorCells.put(id, valid);
        countedValidArmor.put(id, count);
        return valid;
    }

    public static boolean[][] inBoundArmorCellCheck(ShipAPI ship) {
        String id = ship.getHullSpec().getHullId();
        if (inBoundArmorCells.containsKey(id)) {
            return inBoundArmorCells.get(id);
        }
        ArmorGridAPI armorGrid = ship.getArmorGrid();
        if (armorGrid.getGrid() == null) {
            return null;
        }
        int count = 0;
        int x = armorGrid.getGrid().length;
        int y = armorGrid.getGrid()[0].length;
        boolean[][] inBound = new boolean[x][y];
        int left = armorGrid.getLeftOf();
        int below = armorGrid.getBelow();
        float size = armorGrid.getCellSize();
        List<BoundsAPI.SegmentAPI> segmentsToCheck = ship.getExactBounds().getSegments();
        Vector2f loc = ship.getLocation();
        float facing = ship.getFacing();
        ship.getExactBounds().update(new Vector2f(0, 0), 90);
        List<Vector2f> points = new ArrayList<>(segmentsToCheck.size() + 1);
        BoundsAPI.SegmentAPI segment;
        float xMin = 0, xMax = 0, yMin = 0, yMax = 0;
        for (int i = 0; i < segmentsToCheck.size(); i++) {
            segment = segmentsToCheck.get(i);
            Vector2f point = new Vector2f(segment.getP1());
            points.add(point);
            xMin = xMin < point.x ? xMin : point.x;
            xMax = xMax > point.x ? xMax : point.x;
            yMin = yMin < point.y ? yMin : point.y;
            yMax = yMax > point.y ? yMax : point.y;
            if (i == (segmentsToCheck.size() - 1)) {
                point = new Vector2f(segment.getP2());
                points.add(point);
                xMin = xMin < point.x ? xMin : point.x;
                xMax = xMax > point.x ? xMax : point.x;
                yMin = yMin < point.y ? yMin : point.y;
                yMax = yMax > point.y ? yMax : point.y;
            }
        }
        ship.getExactBounds().update(loc, facing);
        for (int i = 0; i < x; i++) {
            float xL = (i - left) * size;
            float xR = xL + size;
//            if (xR < xMin || xL > xMax) {
//                continue;
//            }
            for (int j = 0; j < y; j++) {
                float yB = (j - below) * size;
                float yA = yB + size;
//                if (yA < yMin || yB > yMax) {
//                    continue;
//                }
                int a, b;
                boolean result = false;
                Rectangle2D.Float rectangle = new Rectangle2D.Float();
                rectangle.setRect(xL, yB, size, size);
                float xC = (xL+xR)/2;
                float yC = (yA+yB)/2;
                for (a = 0, b = points.size() - 1; a < points.size(); b = a++) {
                    if (rectangle.intersectsLine(points.get(a).x, points.get(a).y, points.get(b).x, points.get(b).y)) {
                        result = true;
                        break;
                    }
                    if ((points.get(a).y > yC) != (points.get(b).y > yC)
                            && (xC < (points.get(b).x - points.get(a).x)
                            * (yC - points.get(a).y)
                            / (points.get(b).y - points.get(a).y) + points.get(b).x)) {
                        result = !result;
                    }
                }
                inBound[i][j] = result;
                if (inBound[i][j]) {
                    count++;
                }
            }
        }

        inBoundArmorCells.put(id, inBound);
        countedInBoundArmor.put(id, count);
        return inBound;
    }

    public static Vector2f getCellLocation(ShipAPI ship, float x, float y) {
        x -= ship.getArmorGrid().getLeftOf();
        y -= ship.getArmorGrid().getBelow();
        float cellSize = ship.getArmorGrid().getCellSize();
        Vector2f cellLoc = new Vector2f();
        float theta = (float) (((ship.getFacing() - 90) / 360f) * (Math.PI * 2));
        cellLoc.x = (float) (x * Math.cos(theta) - y * Math.sin(theta)) * cellSize + ship.getLocation().x;
        cellLoc.y = (float) (x * Math.sin(theta) + y * Math.cos(theta)) * cellSize + ship.getLocation().y;
        return cellLoc;
    }

    public static Vector2f getCellCentreLocation(ShipAPI ship, float x, float y) {
        return getCellLocation(ship, x + 0.5f, y + 0.5f);
    }

    public static void armorDebuging(ShipAPI ship, float amount) {
        ArmorGridAPI armorGrid = ship.getArmorGrid();
        int x = armorGrid.getGrid().length;
        int y = armorGrid.getGrid()[0].length;
        boolean[][] vc = validArmorCellCheck(ship);
        boolean[][] ib = inBoundArmorCellCheck(ship);
        Global.getCombatEngine().addSmoothParticle(ship.getLocation(), new Vector2f(0, 0), 15, 1, amount, Color.green);
        Ship s = (Ship) ship;
        s.setRenderBounds(true);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Vector2f point = getCellLocation(ship, i, j);
                if (ib[i][j]) {
                    Global.getCombatEngine().addSmoothParticle(point, new Vector2f(0, 0), 15, 1, amount, Color.cyan);
                } else if (vc[i][j]) {
                    Global.getCombatEngine().addSmoothParticle(point, new Vector2f(0, 0), 15, 1, amount, Color.yellow);
                } else {
                    Global.getCombatEngine().addSmoothParticle(point, new Vector2f(0, 0), 15, 1, amount, Color.RED);
                }
            }
        }
    }
}
