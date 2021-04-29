// By Deathfly
package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

public class NeutrinoSchwarmStats extends BaseShipSystemScript {

    private final IntervalUtil interval = new IntervalUtil(1f, 2f);
    private final Color color = new Color(100,165,255,155);
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        }
        if (ship != null) {
            if (ship.isPhased()) {
                ship.setPhased(false);
            }
            interval.advance(Global.getCombatEngine().getElapsedInLastFrame());
            if (interval.intervalElapsed()) {
                List<ShipAPI> drones = ship.getDeployedDrones();
                if (!drones.isEmpty()) {
                    int index = (int)(drones.size() * Math.random());
                    ShipAPI target = drones.get(index);
                    Vector2f tem = new Vector2f(ship.getLocation());
                    ship.getLocation().set(target.getLocation());
                    target.getLocation().set(tem);
                    Vector2f tem1 = new Vector2f(ship.getVelocity());
                    ship.getVelocity().set(target.getVelocity());
                    target.getVelocity().set(tem1);
                    ship.setJitter(id, color, 1, 2, 3);
                    target.setJitter(id, color, 1, 2, 3);
                    ship.setPhased(true);
                }
            }
        }
    }
}
