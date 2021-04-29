package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lwjgl.util.vector.Vector2f;

public class NeutSiegeFusorEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    private final String chargeSound_ID = "neutrino_siegefusor_charge";
    private final float pitchStart = 0.8f;
    private final float pitchMax = 1.8f;
    private final float pitchEnd = 1.2f;
    private final float volumeStart = 0.2f;
    private final float volumeMax = 1f;
    private final float volumeEnd = 0.3f;

    private float chargeLevel = 0;
    private float pitch = 0;
    private float volume = 0;
    private final Vector2f zero = new Vector2f(0, 0);

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (weapon.isFiring()) {
            if (!weapon.isFiring()) {
                chargeLevel = 0;
            } else {
                if (weapon.getChargeLevel() >= chargeLevel) {
                    chargeLevel = weapon.getChargeLevel();
                    pitch = pitchStart + chargeLevel * (pitchMax - pitchStart);
                    volume = volumeStart + chargeLevel * (volumeMax - volumeStart);
                    Global.getSoundPlayer().playLoop(chargeSound_ID, weapon, pitch, volume, weapon.getLocation(), zero);
                } else {
                    chargeLevel = weapon.getChargeLevel();
//                    pitch = pitchEnd + chargeLevel * (pitchMax-pitchEnd);
//                    volume = volumeEnd + chargeLevel * (volumeMax-volumeEnd);
//                    Global.getSoundPlayer().playLoop(chargeSound_ID, weapon, pitch, volume, weapon.getLocation(), zero);
                }
            }
        }
    }
}
