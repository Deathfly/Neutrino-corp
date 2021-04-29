//by Deathfly
package data.scripts.AIs.ShipSystems;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import org.lazywizard.lazylib.combat.AIUtils;

public class Neutrino_AlwaysOn implements ShipSystemAIScript {

    CombatEngineAPI engine;
    private ShipAPI ship;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.engine = engine;
        this.ship = ship;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null) {
            return;
        }
        if (engine.isPaused()) {
            return;
        }
        if (AIUtils.canUseSystemThisFrame(ship)){
            ship.useSystem();
        }
    }  
}
