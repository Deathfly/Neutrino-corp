//by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.plugins.Neutrino_ExtraParticlePlugin;

import data.scripts.util.Neutrino_ParticlesEffectLib;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class NeutBeamParticleEffect implements BeamEffectPlugin {

    private Vector2f lastWeaponLoc = new Vector2f();
    private float lastWeaponFacing = Float.NaN;
    private float amountReciprocal = 0;

    public NeutBeamParticleEffect() {
    }
    private final IntervalUtil interval = new IntervalUtil(0.05f, 0.05f);

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        if (engine.isPaused()) {
            return;
        }
        WeaponAPI weapon = beam.getWeapon();
        if (lastWeaponFacing != Float.NaN) {
            amountReciprocal = 1 / amount;
            Vector2f vel = Vector2f.sub(weapon.getLocation(), lastWeaponLoc, null);
            vel.scale(amountReciprocal);
            float angularVel = MathUtils.getShortestRotation(lastWeaponFacing, weapon.getCurrAngle());
//            angularVel *= amountReciprocal;
            float brightness = beam.getBrightness();            
            if (brightness >= 0.5f) {
                interval.advance(amount);
                if (interval.intervalElapsed()) {
                    float width = beam.getWidth();
                    Neutrino_ExtraParticlePlugin.ParticleBeamPathEx(
                            beam,
                            3f, 1, false,
                            width * 0.3f,
                            2f,
                            vel, angularVel,
                            200, 300,
                            (int) (width * 0.1f), (int) (width * 0.2f),
                            0.8f, 1f,
                            0.2f, 0.6f,
                            beam.getCoreColor());

                }
            }
        }
        lastWeaponLoc = new Vector2f(weapon.getLocation());
        lastWeaponFacing = weapon.getCurrAngle();
    }
}
