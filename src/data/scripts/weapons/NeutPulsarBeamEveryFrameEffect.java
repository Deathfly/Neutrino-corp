// By Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import java.util.HashMap;
import java.util.Map;

public class NeutPulsarBeamEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    private static final Map<WeaponSize, Integer> TIMER = new HashMap<>(3);

    static {
        TIMER.put(WeaponSize.SMALL, 6);
        TIMER.put(WeaponSize.MEDIUM, 24);
        TIMER.put(WeaponSize.LARGE, 3);
    }
//    private static final Map<WeaponSize, Float> FACTOR = new HashMap<>(3);
//
//    static {
//        FACTOR.put(WeaponSize.SMALL, 1f / (0.8f * 3f));
//        FACTOR.put(WeaponSize.MEDIUM, 0.4f / (0.2f * 3f));
//        FACTOR.put(WeaponSize.LARGE, 3f / (2f * 3f));
//    }
    private static final Map<WeaponSize, Float> FACTOR = new HashMap<>(3);

    static {
        FACTOR.put(WeaponSize.SMALL, 0.8f / (1 / 1.5f - 0.2f) - 1);
        FACTOR.put(WeaponSize.MEDIUM, 0.2f / (0.4f / 1.5f - 0.2f) - 1);
        FACTOR.put(WeaponSize.LARGE, 2f / (3 / 1.5f - 1f) - 1);
    }
//    private static final Map<WeaponSize, Float> extraCooldown = new HashMap<>(3);
//
//    static {
//        factor.put(WeaponSize.SMALL, 2f);
//        factor.put(WeaponSize.MEDIUM, 3f);
//        factor.put(WeaponSize.LARGE, 5f);
//    }   
//    private final float ROF_MULT = 0.5f; // 1+0.5 = 1.5 times faster
    private float cooldownReduceLimit = 0;
    private float cooldownReduce = 0;
    private int counterLimit = 0;
    private int counter = 0;
    private boolean fired = false;
    private boolean skip = false;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        // refit screen skip.
        if (weapon.getShip().getOriginalOwner() == -1 || weapon.getShip().isHulk()) {
            return;
        }
        if (cooldownReduceLimit == 0 && counterLimit == 0) {
            counterLimit = TIMER.get(weapon.getSize());
            cooldownReduceLimit = FACTOR.get(weapon.getSize());
        }
        if (!weapon.isFiring() && weapon.getChargeLevel() <= 0) {
            if (skip || weapon.getCooldownRemaining() > 0) {
                skip = false;
            } else {
                counter = 0;
                cooldownReduce = 0;
                fired = false;
            }
        } else {
            skip = true;
            if (fired) {
                if (cooldownReduce > 0) {
                    float cooldownRemaining = weapon.getCooldownRemaining();
                    weapon.setRemainingCooldownTo(cooldownRemaining - (cooldownReduce * amount));
                }
                if (weapon.getCooldownRemaining() <= 0) {//will fired in next frame
                    counter = counter >= counterLimit ? counterLimit : counter + 1;
                    cooldownReduce = cooldownReduceLimit * counter / counterLimit;
                    fired = false;
                }
            } else if (weapon.getChargeLevel() >= 1) {
                fired = true;
            }
        }
//        if (!weapon.isFiring() && weapon.getChargeLevel() <= 0) {
//            if (skip || weapon.getCooldownRemaining() > 0) {
//                skip = false;
//            } else {
//                counter = 0;
//                cooldownReduce = 0;
//                fired = false;
//            }
//        } else {
//            if (weapon.getChargeLevel() > 0f) {
//                skip = true;
//                if (!fired && weapon.getCooldownRemaining() > 0) {
//                    counter = 1;
//                    cooldownReduce = weapon.getCooldown() * ((float) counter / TIMER.get(weapon.getSize())) * FACTOR.get(weapon.getSize());
//                    fired = true;
//                } else if (fired && weapon.getCooldownRemaining() != 0 && weapon.getCooldownRemaining() <= cooldownReduce + amount) {
//                    weapon.setRemainingCooldownTo(0);
//                    if (counter < timer.get(weapon.getSize())) {
//                        counter++;
//                        cooldownReduce = weapon.getCooldown() * ((float) counter / TIMER.get(weapon.getSize())) * FACTOR.get(weapon.getSize());
//                    }
//                }
//            }
//        }
    }
}
