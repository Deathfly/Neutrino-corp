// By Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.awt.Color;

public class NeutGravitonEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();

        if (ship == null || !ship.isAlive()) {
            return;
        }
        if (weapon.isFiring()) {
            ship.setJitterShields(false);
            ship.setJitter(weapon,  new Color(255,175,255,255), weapon.getChargeLevel()*2, 1, 2);
            ship.setJitterUnder(weapon,  new Color(255,175,255,255), weapon.getChargeLevel()*3, 6, 7);
            ship.setJitterShields(true);
        }
    }
}
