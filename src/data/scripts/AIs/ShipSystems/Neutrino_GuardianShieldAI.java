//by Deathfly
package data.scripts.AIs.ShipSystems;

import com.fs.starfarer.api.combat.BeamAPI;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.plugins.Neutrino_LocalData;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;

public class Neutrino_GuardianShieldAI implements ShipSystemAIScript {

    private static final String KEY = "Neutrino_LocalData";
    CombatEngineAPI engine;
    private ShipAPI ship;
    private ShipAPI drone;
    private ShipSystemAPI system;
    private ShipwideAIFlags flags;
    private final IntervalUtil tracker = new IntervalUtil(0.3f, 0.5f);

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.engine = engine;
        this.ship = ship;
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
        if (drone == null) {
            if (!ship.getChildModulesCopy().isEmpty()) {
                drone = ship.getChildModulesCopy().get(0);
            }
            return;
        }
        if (!drone.isAlive() && ship.isAlive()) {
            return;
        }
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            boolean shipInDanger = ship.getHullLevel() < 0.15f;
            if (drone.getFluxTracker().getFluxLevel() > 0.96f && !shipInDanger) {
                systemOff();
                return;
            }
            List<ShipAPI> allies = AIUtils.getNearbyAllies(ship, 1500);
            for (ShipAPI ally : allies) {
                if (ally.getAIFlags() != null
                        && (ally.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)
                        || ally.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP))) {
                    systemOn();
                    return;
                }
            }
            List<ShipAPI> enemies = AIUtils.getNearbyEnemies(ship, 2000);
            for (ShipAPI enemy : enemies) {
                if ((enemy.getAIFlags() != null && enemy.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP))
                        || enemy.isRetreating()) {
                    continue;
                }
                systemOn();
                return;
            }
            List<DamagingProjectileAPI> projectiles = engine.getProjectiles();
            for (DamagingProjectileAPI proj : projectiles) {
                if (proj.getOwner() != ship.getOwner() && MathUtils.getDistance(ship, proj) > 1500) {
                    systemOn();
                    return;
                }
            }
            List<BeamAPI> beams = engine.getBeams();
            for (BeamAPI beam : beams) {
                if (beam.getSource().getOwner() != ship.getOwner() && MathUtils.isWithinRange(ship, beam.getTo(), 1500)) {
                    systemOn();
                    return;
                }
            }
            systemOff();
        }
    }

    private void systemOn() {
        if (AIUtils.canUseSystemThisFrame(ship) && !system.isOn()) {
            ship.useSystem();
        }
    }

    private void systemOff() {
        if (system.isOn()) {
            ship.useSystem();
        }
    }
}
