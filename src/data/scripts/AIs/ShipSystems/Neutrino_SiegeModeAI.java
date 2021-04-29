// By Deathfly
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
import org.lazywizard.lazylib.CollisionUtils;

import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.WeaponUtils;

public class Neutrino_SiegeModeAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipSystemAPI system;

    private final IntervalUtil tracker = new IntervalUtil(0.2f, 0.6f);

    private float maxWeaponRange = 0;
    private float minWeaponRange = Float.MAX_VALUE;
    private float closeRange = Float.MAX_VALUE;;
    private final float weaponCDCheck = 2f;
    private final float weaponRangeMult = 2f;
    private final float beamWeaponRangeMult = 1.75f;

    private boolean preferMaxRange = false;
    private boolean preferMinRange = false;
    private boolean backOffForMaxRange = false;
    private final float backOffRange = 200f;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.engine = engine;
        this.system = system;
        List<WeaponAPI> weapons = ship.getAllWeapons();
        if (!weapons.isEmpty()) {
            for (WeaponAPI weapon : weapons) {
                if ((weapon.getType() == WeaponType.ENERGY || weapon.getType() == WeaponType.BUILT_IN) && (weapon.hasAIHint(WeaponAPI.AIHints.PD_ALSO) || !weapon.hasAIHint(WeaponAPI.AIHints.PD))) {
                    maxWeaponRange = weapon.isBeam() ? Math.max(weapon.getRange() * beamWeaponRangeMult, maxWeaponRange) : Math.max(weapon.getRange() * weaponRangeMult, maxWeaponRange);
                    minWeaponRange = weapon.isBeam() ? Math.min(weapon.getRange() * beamWeaponRangeMult, minWeaponRange) : Math.min(weapon.getRange() * weaponRangeMult, minWeaponRange);
                    closeRange = Math.min(weapon.getRange()+50, closeRange);
                }
            }
        }
        ////
        //And let's check the officer character
        if (ship.getCaptain() != null) {
            switch (ship.getCaptain().getPersonalityAPI().getId()) {
                case "timid":
                    backOffForMaxRange = true;
                    preferMaxRange = true;
                    break;
                case "cautious":
                    preferMaxRange = true;
                    break;
                case "aggressive":
                case "reckless":
                    preferMinRange = true;
                    break;
            }
        }
        ////	
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            ////
            //Don't use IF
            //if can't Use System This Frame
            if (!AIUtils.canUseSystemThisFrame(ship) || system.isActive()) {
                return;
            }
            //if ship is in a run or the flux state is not good.
            if (ship.isRetreating()
                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.AVOIDING_BORDER)
                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)
                    //                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACK_OFF)
                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_FLUX)
                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS)
                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_AUTOFIRE_NON_ESSENTIAL_GROUPS)
                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)
                    || ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TURN_QUICKLY)) {
                return;
            }
            //if target invalid
            if (target == null
                    || !target.isAlive()
                    || target.isFighter()
                    || target.isDrone()
                    || target == ship
                    || target.getOwner() == ship.getOwner()
                    || (target.getPhaseCloak() != null && target.getPhaseCloak().isActive())) {
                return;
            }
            //if target too far away or just too close
            float targetDistance = MathUtils.getDistance(ship, target);
            if (targetDistance > maxWeaponRange || targetDistance < closeRange) {
                return;
            }
            //so we are in range. backoff a little if we are timed
            if (backOffForMaxRange && targetDistance + backOffRange < maxWeaponRange) {
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF, 0.5f);
            }
            //And we should check if we hava a clear firing line, don't hit the allies.
            List<ShipAPI> allies = AIUtils.getNearbyAllies(ship, maxWeaponRange);
            for(ShipAPI ally:allies){
                if(CollisionUtils.getCollides(ship.getLocation(), target.getLocation(), ally.getLocation(), ally.getCollisionRadius())){
                    return;
                }
            }
            ////
            //check if target is vulnerable
            boolean targetIsVulnerable = target.getFluxTracker().isOverloadedOrVenting()
                    && (target.getFluxTracker().getOverloadTimeRemaining() > 5f
                    || target.getFluxTracker().getTimeToVent() > 5f);
            float totalWeaponWeight = 0;
            float availableWeaponWeight = 0;
            List<WeaponAPI> weapons = ship.getAllWeapons();
            if (!weapons.isEmpty()) {
                for (WeaponAPI weapon : weapons) {
                    if ((weapon.getType() == WeaponType.ENERGY || weapon.getType() == WeaponType.BUILT_IN) && (weapon.hasAIHint(WeaponAPI.AIHints.PD_ALSO) || !weapon.hasAIHint(WeaponAPI.AIHints.PD))) {
                        float weight = 0;
                        if (null != weapon.getSize()) switch (weapon.getSize()) {
                            case SMALL:
                                weight = 1;
                                break;
                            case MEDIUM:
                                weight = 2;
                                break;
                            case LARGE:
                                weight = 4;
                                break;
                            default:
                                break;
                        }
                        totalWeaponWeight += weight;
                        if (weapon.isFiring() || weapon.isDisabled() || weapon.getCooldownRemaining() > weaponCDCheck || weapon.distanceFromArc(target.getLocation()) > 0) {
                            continue;
                        }
                        float timeToAim = WeaponUtils.getTimeToAim(weapon, target.getLocation());
                        if (timeToAim > 2) {
                            continue;
                        }
                        float range = weapon.isBeam() ? weapon.getRange() * beamWeaponRangeMult : weapon.getRange() * weaponRangeMult;
                        float distToWeapon = MathUtils.getDistance(weapon.getLocation(), target.getLocation());
                        if (distToWeapon > range) {
                            continue;
                        }
                        if (preferMaxRange) {
                            weight *= 2;
                        } else if (preferMinRange && distToWeapon > minWeaponRange + 50f) {
                            weight *= -((maxWeaponRange - distToWeapon) / (maxWeaponRange - minWeaponRange));
                        }
                        availableWeaponWeight += weight;
                    }
                }
            }
            if (targetIsVulnerable) {
                availableWeaponWeight *= 2;
            }
            totalWeaponWeight *= 2;
            if (totalWeaponWeight > 0 && Math.random() < availableWeaponWeight / totalWeaponWeight) {
                ship.useSystem();
            }
        }
    }
}
