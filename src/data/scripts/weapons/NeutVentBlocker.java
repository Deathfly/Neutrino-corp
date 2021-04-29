// by Deathfly
// hope this will work properly.
package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatAssignmentType;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;

public class NeutVentBlocker implements EveryFrameWeaponEffectPlugin {

    private final float ventBlockThreshold = 0.8f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        if (ship.getOriginalOwner() == -1 || ship.isHulk()) {
            return;
        }
        if (weapon.isFiring()
                && weapon.getChargeLevel() >= 1f
                && ship.getAIFlags() != null
                && ship.getFluxTracker().getFluxLevel() < ventBlockThreshold) {
            ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT);
        }
    }

    private boolean isCapturing(ShipAPI ship) {
        CombatFleetManagerAPI.AssignmentInfo aInfo = Global.getCombatEngine().getFleetManager(ship.getOwner()).getTaskManager(ship.isAlly()).getAssignmentFor(ship);
        if (aInfo != null) {
            return aInfo.getType() == CombatAssignmentType.CAPTURE;
        } else {
            return false;
        }
    }
}
