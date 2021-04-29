// By Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class NeutPACEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    private final float validFrames = 16;
//    private final float animeMAXchargelvl = 0.8f;
    private float chargeLevel = 0;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        AnimationAPI anime = weapon.getAnimation();
        
        if (anime == null){
            return;
        }
        if (!weapon.isFiring()) {
            chargeLevel = 0;
            anime.setFrameRate(-12);
            if (anime.getFrame() == 0) {
                anime.setFrameRate(0);
            }
        } else {
            if (weapon.getChargeLevel() >= chargeLevel) {
                chargeLevel = weapon.getChargeLevel();
                if (anime.getFrame() < validFrames) {
                    anime.setFrameRate(12);
                } else {
                    anime.setFrameRate(0);
                    anime.setFrame(16);
                }
            } else {
                chargeLevel = weapon.getChargeLevel();
                if (anime.getFrame() > 0) {
                    anime.setFrameRate(-12);
                } else {
                    anime.setFrameRate(0);
                    anime.setFrame(0);
                }
            }
            if (weapon.getShip().getFluxTracker().isOverloadedOrVenting()){
                weapon.setRemainingCooldownTo(weapon.getCooldown());
            }
        }
    }
}
