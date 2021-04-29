// By Deathfly. For those who want more particles. This will add a new DynamicParticleGroup with new calling method that can hold up to 1 million particles independent of vanilla particle system.
// Already have a viewpoint check built in.
// Use some...well gray way to invoke something we should not use ordinary. So it need to add fs.common_obf.jar and starfarer_obf.jar as library.
// You can increase the particles limit but...just don't make too many of them flying at the same time.
package data.scripts.plugins;

import com.fs.graphics.particle.DynamicParticleGroup;
import com.fs.graphics.particle.GenericTextureParticle;
import com.fs.graphics.particle.SmoothParticle;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.combat.CombatEngine;
import com.fs.starfarer.renderers.fx.DetailedSmokeParticle;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class ExtraParticlePlugin extends BaseEveryFrameCombatPlugin {

    public static DynamicParticleGroup ExtraParticleGroup = new DynamicParticleGroup(1000000);
    private final static float offScreenDist = 200f;

    @Override
    public void init(CombatEngineAPI engine) {
        CombatEngine e = (CombatEngine) engine;
        e.addAnimation(ExtraParticleGroup);
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {
        ExtraParticleGroup.render(0.0f, 0.0f);
    }

    /**
     * Particle with a somewhat brighter middle.
     *
     * @param brightness from 0 to 1
     * @param duration in seconds
     */
    public static void addExtraHitParticle(Vector2f loc, Vector2f vel, float size, float brightness, float duration, Color color) {
        if(!Global.getCombatEngine().getViewport().isNearViewport(loc, offScreenDist)) return;
        GenericTextureParticle generictextureparticle = new GenericTextureParticle("graphics/fx/hit_glow.png", new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (brightness * 255f)), size, size, 0.0F);
        generictextureparticle.setBlendFunc(770, 1);
        generictextureparticle.setPos(loc.x, loc.y);
        generictextureparticle.setVel(vel.x, vel.y);
        generictextureparticle.setMaxAge(duration);
        ExtraParticleGroup.add(generictextureparticle);        
    }

    /**
     * Standard glowy particle.
     *
     * @param brightness don't do a thing in face
     * @param duration in seconds
     */
    public static void addExtraSmoothParticle(Vector2f loc, Vector2f vel, float size, float brightness, float duration, Color color) {
        if(!Global.getCombatEngine().getViewport().isNearViewport(loc, offScreenDist)) return;
        SmoothParticle smoothparticle = new SmoothParticle(color, size);
        smoothparticle.setMaxAge(duration);
        smoothparticle.setPos(loc.x, loc.y);
        smoothparticle.setVel(vel.x, vel.y);
        ExtraParticleGroup.add(smoothparticle);
    }

    /**
     * Opaque smoke particle.
     *
     * @param opacity don't do a thing in fact
     * @param duration in seconds
     */
    public static void addExtraSmokeParticle(Vector2f loc, Vector2f vel, float size, float opacity, float duration, Color color) {
        if(!Global.getCombatEngine().getViewport().isNearViewport(loc, offScreenDist)) return;
        DetailedSmokeParticle detailedsmokeparticle = new DetailedSmokeParticle(color, size, size * 1.25F);
        detailedsmokeparticle.setAngle((float) Math.random() * 360F);
        detailedsmokeparticle.setRotationSpeed((float) Math.random() * 140F - 70F);
        detailedsmokeparticle.setMaxAge(duration);
        detailedsmokeparticle.setPos(loc.x, loc.y);
        detailedsmokeparticle.setVel(vel.x, vel.y);
        detailedsmokeparticle.setRampUpPeriod(0.5F * (float) Math.random());
        ExtraParticleGroup.add(detailedsmokeparticle);
    }   
}
