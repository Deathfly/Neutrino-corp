//by Deathfly
package data.scripts.AIs.ShipSystems;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.entities.Ship;
import java.awt.Color;
import java.util.Iterator;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_ReactionControlSystemAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private ShipSystemAPI system;
    private ShipwideAIFlags flags;
    private CombatEngineAPI engine;
    private final IntervalUtil tracker = new IntervalUtil(0.25f, 0.5f);

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
        this.flags = flags;
        this.engine = engine;
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
            //Don't Use IF
            //if can't Use System This Frame
            FluxTrackerAPI flux = ship.getFluxTracker();
            if (!(system == null || flux.isOverloadedOrVenting() || system.isOutOfAmmo()
                    // In use but can't be toggled off right away
                    || (system.isOn() && system.getCooldownRemaining() > 0f)
                    // In chargedown, in cooldown
                    || (system.isActive() && !system.isOn()) || system.getCooldownRemaining() > 0f
                    // Not enough flux
                    || system.getFluxPerUse() > (flux.getMaxFlux() - flux.getCurrFlux()))) {
                return;
            }
            if (flags.hasFlag(ShipwideAIFlags.AIFlags.SAFE_VENT)) {
                return;
            }
                        ShipEngineControllerAPI e = ship.getEngineController();
            if (e.isDisabled()||e.isFlamingOut()){
                return;
            }
            //debug thing
//            String flagsString = "";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.HARASS_MOVE_IN)) flagsString += "HARASS_MOVE_IN" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.HARASS_MOVE_IN_COOLDOWN)) flagsString += "HARASS_MOVE_IN_COOLDOWN" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET)) flagsString += "MANEUVER_TARGET" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET)) flagsString += "CARRIER_FIGHTER_TARGET" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.MAINTAINING_STRIKE_RANGE)) flagsString += "MAINTAINING_STRIKE_RANGE" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DRONE_MOTHERSHIP)) flagsString += "DRONE_MOTHERSHIP" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS)) flagsString += "DO_NOT_USE_SHIELDS" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_FLUX)) flagsString += "DO_NOT_USE_FLUX" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT)) flagsString += "DO_NOT_VENT" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_AUTOFIRE_NON_ESSENTIAL_GROUPS)) flagsString += "DO_NOT_AUTOFIRE_NON_ESSENTIAL_GROUPS" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)) flagsString += "RUN_QUICKLY" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.TURN_QUICKLY)) flagsString += "TURN_QUICKLY" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.PURSUING)) flagsString += "PURSUING" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)) flagsString += "HAS_INCOMING_DAMAGE" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.KEEP_SHIELDS_ON)) flagsString += "KEEP_SHIELDS_ON" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF)) flagsString += "DO_NOT_BACK_OFF" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.BACK_OFF)) flagsString += "BACK_OFF" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE)) flagsString += "BACK_OFF_MIN_RANGE" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.STANDING_OFF_VS_SHIP_ON_MAP_BORDER)) flagsString += "STANDING_OFF_VS_SHIP_ON_MAP_BORDER" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)) flagsString += "BACKING_OFF" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.SAFE_VENT)) flagsString += "SAFE_VENT" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.OK_TO_CANCEL_SYSTEM_USE_TO_VENT)) flagsString += "OK_TO_CANCEL_SYSTEM_USE_TO_VENT" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.SAFE_FROM_DANGER_TIME)) flagsString += "SAFE_FROM_DANGER_TIME" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.PREFER_LEFT_BROADSIDE)) flagsString += "PREFER_LEFT_BROADSIDE" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.PREFER_RIGHT_BROADSIDE)) flagsString += "PREFER_RIGHT_BROADSIDE" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.AUTO_FIRING_AT_PHASE_SHIP)) flagsString += "AUTO_FIRING_AT_PHASE_SHIP" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.AUTO_BEAM_FIRING_AT_PHASE_SHIP)) flagsString += "AUTO_BEAM_FIRING_AT_PHASE_SHIP" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DELAY_STRIKE_FIRE)) flagsString += "DELAY_STRIKE_FIRE" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.PHASE_ATTACK_RUN)) flagsString += "PHASE_ATTACK_RUN" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.PHASE_ATTACK_RUN_FROM_BEHIND_DIST_CRITICAL)) flagsString += "PHASE_ATTACK_RUN_FROM_BEHIND_DIST_CRITICAL" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.PHASE_ATTACK_RUN_IN_GOOD_SPOT)) flagsString += "PHASE_ATTACK_RUN_IN_GOOD_SPOT" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.PHASE_ATTACK_RUN_TIMEOUT)) flagsString += "PHASE_ATTACK_RUN_TIMEOUT" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_PURSUE)) flagsString += "DO_NOT_PURSUE" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.IN_CRITICAL_DPS_DANGER)) flagsString += "IN_CRITICAL_DPS_DANGER" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.IN_ATTACK_RUN)) flagsString += "IN_ATTACK_RUN" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.POST_ATTACK_RUN)) flagsString += "POST_ATTACK_RUN" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.WANTED_TO_SLOW_DOWN)) flagsString += "WANTED_TO_SLOW_DOWN" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.FINISHED_SPREADING)) flagsString += "FINISHED_SPREADING" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.REACHED_WAYPOINT)) flagsString += "REACHED_WAYPOINT" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP)) flagsString += "NEEDS_HELP" + "/n";
//            if(flags.hasFlag(ShipwideAIFlags.AIFlags.SYSTEM_TARGET_COORDS)) flagsString += "SYSTEM_TARGET_COORDS" + "/n";      
//            engine.addFloatingText(ship.getLocation(), flagsString, 15, Color.yellow, ship, 0, 0);
            ////
            //Use If
            float hardFlux = ship.getFluxTracker().getHardFlux();
            

            boolean wantToMove = e.isAccelerating()||e.isAcceleratingBackwards()||e.isDecelerating()||e.isStrafingLeft()||e.isStrafingRight()||e.isTurningLeft()||e.isTurningRight();
            if(hardFlux<0.25 && wantToMove){
                ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, ship.getLocation(), 0);
                flags.setFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT, 0.5f);
                flags.setFlag(ShipwideAIFlags.AIFlags.DELAY_STRIKE_FIRE, 0.2f);
            }else if (hardFlux < 0.5
                    && (flags.hasFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET)
                    || flags.hasFlag(ShipwideAIFlags.AIFlags.MAINTAINING_STRIKE_RANGE))) {
                ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, ship.getLocation(), 0);
                flags.setFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT, 0.5f);
                flags.setFlag(ShipwideAIFlags.AIFlags.DELAY_STRIKE_FIRE, 0.2f);
            } else if (
                    flags.hasFlag(ShipwideAIFlags.AIFlags.HARASS_MOVE_IN)
                    ||(flags.hasFlag(ShipwideAIFlags.AIFlags.BACK_OFF))
                    || flags.hasFlag(ShipwideAIFlags.AIFlags.PURSUING)
                    || flags.hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)
                    || flags.hasFlag(ShipwideAIFlags.AIFlags.TURN_QUICKLY)
                    || flags.hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)
                    || flags.hasFlag(ShipwideAIFlags.AIFlags.IN_CRITICAL_DPS_DANGER)) {
                ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, ship.getLocation(), 0);
                flags.setFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT, 0.5f);
                flags.setFlag(ShipwideAIFlags.AIFlags.DELAY_STRIKE_FIRE, 0.2f);
            }
            ////	
        }

    }

}
