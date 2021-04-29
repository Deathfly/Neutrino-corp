// by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.Neutrino_ParticlesEffectLib;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class NeutSilverLanceEveryFrameWeaponEffect implements EveryFrameWeaponEffectPlugin {

    private float aim = 0;
    private final IntervalUtil interval = new IntervalUtil(0.05F, 0.1F);

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (weapon.getShip().getOriginalOwner() == -1 || weapon.getShip().isHulk()) {
            return;
        }
        if (weapon.isFiring()) {
            float diff = MathUtils.getShortestRotation(aim, weapon.getCurrAngle());
            if (Math.abs(diff) > weapon.getTurnRate() * amount) {
                diff = Math.copySign(weapon.getTurnRate() * amount, diff);
            }
            float aimTo = MathUtils.clampAngle(aim + diff);
            weapon.setCurrAngle(aimTo);
            float outOfArc = weapon.distanceFromArc(Vector2f.add(weapon.getLocation(), Misc.getUnitVectorAtDegreeAngle(aimTo), null));
            if (outOfArc != 0) {
                float newAngV = Math.abs(weapon.getShip().getAngularVelocity()) < weapon.getTurnRate()
                        ? Math.copySign(weapon.getShip().getAngularVelocity(), outOfArc)
                        : Math.copySign(weapon.getTurnRate(), outOfArc);
                weapon.getShip().setAngularVelocity(newAngV);
            }
            float chargeLevel = weapon.getChargeLevel();
            if (chargeLevel > 0.2) {
                interval.advance(amount);
                if (interval.intervalElapsed()) {
                    Neutrino_ParticlesEffectLib.ParticlesRay(
                            weapon.getLocation(),
                            MathUtils.getPointOnCircumference(weapon.getLocation(), 75, aim),
                            0.5f + chargeLevel,
                            2,
                            false,
                            3, 0,
                            weapon.getShip().getVelocity(), 0,
                            50 + chargeLevel * 100, 50 + chargeLevel * 200,
                            2, 5,
                            chargeLevel, 1f,
                            0.2f, 1f,
                            Color.white,
                            false);

                }
            }
        }
        aim = weapon.getCurrAngle();
    }

}
