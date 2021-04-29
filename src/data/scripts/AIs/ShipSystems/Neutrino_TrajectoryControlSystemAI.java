// By Deathfly
package data.scripts.AIs.ShipSystems;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import static com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import java.util.HashSet;
import java.util.Set;

import org.lazywizard.lazylib.combat.AIUtils;

public class Neutrino_TrajectoryControlSystemAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipSystemAPI system;
    private Set<WeaponAPI> weapons = new HashSet<>();

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.engine = engine;
        this.system = system;
        if (!ship.getAllWeapons().isEmpty()) {
            for (WeaponAPI weapon : ship.getAllWeapons()) {
                if (weapon.getType() == WeaponType.MISSILE && !weapon.hasAIHint(WeaponAPI.AIHints.PD)) {              
                    weapons.add(weapon);
                }
            }
        }
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        ////
        //Don't use IF
        //if can't Use System This Frame
        if (!AIUtils.canUseSystemThisFrame(ship) || system.isActive()) {
            return;
        }
        for (WeaponAPI weapon : weapons) {
            if(weapon.isFiring()){
                ship.useSystem();
            }
        }
    }
}
