package data.scripts.AIs.ShipSystems;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import static com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;

import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.MathUtils;

public class Neutrino_PhaseMissileSystemAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipSystemAPI system;

    private final IntervalUtil tracker = new IntervalUtil(0.3f, 0.5f);
    private float dmg = 0f;

    private float maxFiringRange = 2250f;
    private final static float minFiringRange = 0f;//If target is vulnerable, this will be ignored.
    private final static float selfFluxThreshold = 0.5f;
    private final static float selfFluxMaxThreshold = 0.9f;//Only if target is vulnerable.

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.engine = engine;
        this.system = system;
        List<WeaponAPI> weapons = ship.getAllWeapons();
        if (!weapons.isEmpty()) {
            for (WeaponAPI weapon : weapons) {
                if (weapon.getType() == WeaponType.SYSTEM) {
                    maxFiringRange = weapon.getRange() * 0.9f;
                    dmg += 325 * 5 * (ship.getMutableStats().getMissileWeaponDamageMult().getModifiedValue() + 1);
                }
            }
        }
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
			////
            //Don't Fire IF
            //if can't Use System This Frame
            if (!AIUtils.canUseSystemThisFrame(ship)) {
                return;
            }
            //if there is AIFlage the told this ship don't use flux or don't fire
            if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_FLUX) || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_AUTOFIRE_NON_ESSENTIAL_GROUPS)){
                return;
            }
            //if target invalid
            if (target == null || !target.isAlive() || target.isFighter() || target.isDrone() || target == ship || target.getOwner() == ship.getOwner() || (target.getPhaseCloak() != null && target.getPhaseCloak().isActive())) {
                return;
            }
            //if target too far away
            float targetDistance = MathUtils.getDistance(ship, target);
            if (targetDistance > maxFiringRange) {
                return;
            }
//            //if not facing target
//            float absTargetDirection = Math.abs(MathUtils.getShortestRotation(ship.getFacing(), VectorUtils.getAngle(ship.getLocation(), target.getLocation())));
//            if (absTargetDirection > 90F) {
//                return;
//            }
            ////
            ////
            //Checking Flux
            //check if target is vulnerable first
            boolean targetIsVulnerable = target.getFluxTracker().isOverloadedOrVenting()
                    && (target.getFluxTracker().getOverloadTimeRemaining() > 5f
                    || target.getFluxTracker().getTimeToVent() > 5f);
			////
            ////
            //Don't Fire IF continue
            //if target too close
            if (!targetIsVulnerable && targetDistance < minFiringRange) {
                return;
            }
            //if self flux too high
            float fluxThreshold = selfFluxThreshold;
            if (targetIsVulnerable) {
                fluxThreshold = selfFluxMaxThreshold;
            }
            float fluxLevel = ship.getFluxTracker().getFluxLevel();
            float fluxFractionPerUse = system.getFluxPerUse() / ship.getFluxTracker().getMaxFlux();
            float fluxLevelAfterUse = fluxLevel + fluxFractionPerUse;
            if (fluxLevelAfterUse > fluxThreshold) {
                return;
            }
			////
            ////
            //Fire If
            int ammo = system.getAmmo();
            if (ammo == 3) {
                ship.useSystem();
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT,0.2f);
                return;
            }            
            if (ammo == 2 && Math.random() < 0.1) {
                ship.useSystem();
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT,0.2f);
                return;
            }
            //If target in vulnerable, try to finish it with the last shot
            if (targetIsVulnerable || (target.getShield() == null && target.getHitpoints() < 2 * dmg)) {
                ship.useSystem();
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT,0.2f);
            }
            ////	
        }
    }
}
