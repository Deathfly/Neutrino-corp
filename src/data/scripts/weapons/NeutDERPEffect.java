// by Deathfly
// just a simple onHit that forced to trigger a GuidedProximityFuseAI.explode() to make sure the proximity AOE happen in case of a direct hit. 
package data.scripts.weapons;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.combat.ai.GuidedProximityFuseAI;

public class NeutDERPEffect implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        GuidedProximityFuseAI AI = (GuidedProximityFuseAI) projectile.getAI();
        AI.explode();
    }
}
