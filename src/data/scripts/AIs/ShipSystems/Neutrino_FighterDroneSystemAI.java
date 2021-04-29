//by Deathfly
//damn you Alex, why we cann't have a way to get the DroneOrders?
package data.scripts.AIs.ShipSystems;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.entities.Ship;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;

public class Neutrino_FighterDroneSystemAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipSystemAPI system;
    private ShipwideAIFlags flags;
    private final IntervalUtil tracker = new IntervalUtil(0.1f, 0.2f);
    private int droneOrders = 0; // 0 = RECALL, 1 = DEPLOY, 2 = ATTACK

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.engine = engine;
        this.system = system;
        this.flags = flags;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null) {
            return;
        }
        if (engine.isPaused()) {
            return;
        }

        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            // Some how the fighter low HP retrun behavior is broken. So there is a fix.
            float HP = ship.getHullLevel();
            if (ship.getWing() != null && !ship.getWing().isReturning(ship) && engine.isEntityInPlay(ship.getWing().getSourceShip())) {
                if (ship.getWing().getSourceShip().isPullBackFighters() && HP < 0.9 && MathUtils.getDistance(ship, ship.getWing().getSourceShip()) < 500) {
                    ship.getWing().orderReturn(ship);
                } else if (HP < 0.2) {
                    ship.getWing().orderReturn(ship);
                }
                if (!AIUtils.canUseSystemThisFrame(ship)) {
                    return;
                }
                if (flags.hasFlag(ShipwideAIFlags.AIFlags.IN_ATTACK_RUN)
                        || flags.hasFlag(ShipwideAIFlags.AIFlags.HARASS_MOVE_IN)
                        || flags.hasFlag(ShipwideAIFlags.AIFlags.PURSUING)
                        || (target != null && MathUtils.getDistance(ship, target) < 500)) {
                    setToATTACK();
                } else if (ship.getWing() != null && ship.getWing().isReturning(ship) && MathUtils.getDistance(ship, ship.getWing().getSourceShip()) < 500) {
                    setToRECALL();
                } else {
                    setToDEPLOY();
                }

            }
        }
    }

    private void setToRECALL() {
        while (droneOrders != 0) {
            toggle();
        }
    }

    private void setToDEPLOY() {
        while (droneOrders != 1) {
            toggle();
        }
    }

    private void setToATTACK() {
        while (droneOrders != 2) {
            toggle();
        }
    }

    //so a hax for Alex's double-click poof hax
    private void toggle() {
        ((Ship) ship).getSystem().fire(new Vector2f(0, 0));
        ((Ship) ship).getSystem().advanceEvenIfPaused();
        ((Ship) ship).getSystem().advanceEvenIfPaused();
        droneOrders += 1;
        while (droneOrders > 2) {
            droneOrders -= 3;
        }
    }
}
