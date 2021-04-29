//by Deathfly
package data.scripts.weapons;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.combat.entities.MovingRay;

public class NeutPulsarOnHitEffect implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
            Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (target instanceof MissileAPI) {
            Vector2f zero = new Vector2f(0, 0);
            if (projectile.getDamageAmount() > target.getHitpoints()) {
                MovingRay proj = (MovingRay) projectile;
                Vector2f projVel = new Vector2f(proj.getVelocity());
                MovingRay newProj = (MovingRay) engine.spawnProjectile(
                        proj.getSource(),
                        proj.getWeapon(),
                        proj.getWeapon().getId(),
                        point,
                        proj.getFacing(),
                        zero);
//                engine.removeEntity(target);
                newProj.getVelocity().set(zero);
                newProj.advance(proj.getElapsed());
                newProj.getVelocity().set(projVel);
                newProj.getFrom().set(proj.getFrom());
                newProj.getTo().set(proj.getTo());
                newProj.setDamageAmount(projectile.getDamageAmount() - target.getHitpoints());
                newProj.advance(0);
            }
        }
    }
}
