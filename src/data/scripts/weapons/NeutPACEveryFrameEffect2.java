// By Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.combat.entities.Ship;

public class NeutPACEveryFrameEffect2 implements EveryFrameWeaponEffectPlugin {

    private final int keyFrame1 = 4;
    private final int keyFrame2 = 20;
    private final int keyFrame3 = 21;
    private final int frameRate = 12;
//    private final float animeMAXchargelvl = 0.8f;
    private float chargeLevel = 0;
    private boolean fired = false;
    private boolean blocked = false;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        AnimationAPI anime = weapon.getAnimation();
        if (anime == null) {
            return;
        }
        int currFrame = anime.getFrame();
        if (weapon.isFiring()) {
             fired = true;
            if (weapon.getChargeLevel() >= chargeLevel) {
                chargeLevel = weapon.getChargeLevel();
                if (currFrame < keyFrame2) {
                    anime.setFrameRate(frameRate);
                } else if (currFrame > keyFrame2) {
                    anime.setFrameRate(frameRate);
                    anime.setFrame(keyFrame1);
                } else {
                    anime.setFrameRate(0);
                }
            } else {
                blocked = true;
                chargeLevel = weapon.getChargeLevel();
                if (currFrame < keyFrame1) {
                    anime.setFrameRate(frameRate);
                } else if (currFrame > keyFrame1) {
                    anime.setFrameRate(-frameRate);
                } else {
                    anime.setFrameRate(0);
                }
            }
        } else {
            chargeLevel = 0;
            if (fired) {
                if (blocked) {
                    if (weapon.getCooldownRemaining() > 0) {
                        blocked = false;
                    } else {
                        chargeLevel = weapon.getChargeLevel();
                        if (currFrame < keyFrame1) {
                            anime.setFrameRate(frameRate);
                        } else if (currFrame > keyFrame1) {
                            anime.setFrameRate(-frameRate);
                        } else {
                            anime.setFrameRate(0);
                        }
                    }
                } else if (weapon.getCooldownRemaining() > 0.2f) {
                    if (currFrame > keyFrame2) {
                        anime.setFrame(keyFrame2);
                        anime.setFrameRate(-frameRate);
                    } else if (currFrame > keyFrame1) {
                        anime.setFrameRate(-frameRate);
                    } else {
                        anime.setFrame(keyFrame1);
                        anime.setFrameRate(0);
                    }
                } else {
                    if (currFrame > keyFrame2) {
                        anime.setFrameRate(frameRate);
                    } else if (currFrame > keyFrame1) {
                        anime.setFrameRate(-frameRate);
                    } else if (currFrame == keyFrame1) {
                        anime.setFrameRate(frameRate);
                        anime.setFrame(keyFrame3);
                    } else if (currFrame == 0) {
                        anime.setFrameRate(0);
                        if (weapon.getCooldownRemaining() == 0) {
                            fired = false;
                        }
                    } else {
                        anime.setFrameRate(frameRate);
                    }
                }
            }
        }
    }
}
