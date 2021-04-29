//by Deathfly
package data.scripts.AIs.ShipSystems;

import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;

public class Neutrino_PhaseBombReleaseAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipSystemAPI system;
    private ShipwideAIFlags flags;
    private WeaponAPI weapon;
    private final IntervalUtil tracker = new IntervalUtil(0.1f, 0.2f);

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.engine = engine;
        this.system = system;
        this.flags = flags;
        List<WeaponAPI> weapons = ship.getAllWeapons();
        for (WeaponAPI weapon : weapons) {
            if (weapon.getId().equals("neutrino_graviton_inverter")) {
                this.weapon = weapon;
            }
        }
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir,
            Vector2f collisionDangerDir, ShipAPI target
    ) {
        if (engine == null) {
            return;
        }
        if (engine.isPaused()) {
            return;
        }
        if (system.getAmmo() == 0) {
            return;
        }
        if (ship.isHoldFire()) {
            return;
        }
        tracker.advance(amount);
        if (tracker.intervalElapsed()
                && ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.IN_ATTACK_RUN)
                && target != null
                && engine.isEntityInPlay(target)
                && MathUtils.getDistance(ship, target) <= weapon.getRange()) {
            ship.useSystem();
        }
    }
}
