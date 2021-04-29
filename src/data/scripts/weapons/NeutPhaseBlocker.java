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

public class NeutPhaseBlocker implements EveryFrameWeaponEffectPlugin {


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        if (ship.getOriginalOwner() == -1 || ship.isHulk()) {
            return;
        }
        if (weapon.isFiring()
                && weapon.getChargeLevel() >= 1f
                && ship.getAIFlags() != null) {
            ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS,amount);
        }
    }   
}
