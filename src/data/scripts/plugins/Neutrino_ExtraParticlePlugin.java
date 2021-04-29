package data.scripts.plugins;

import com.fs.graphics.particle.DynamicParticleGroup;
import com.fs.graphics.particle.GenericTextureParticle;
import com.fs.graphics.particle.SmoothParticle;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.combat.CombatEngine;
import com.fs.starfarer.renderers.fx.DetailedSmokeParticle;
import static data.scripts.NCModPlugin.ShaderLibExists;
import java.awt.Color;
import java.util.List;
import org.dark.shaders.util.ShaderLib;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_ExtraParticlePlugin extends BaseEveryFrameCombatPlugin {

    public static DynamicParticleGroup ExtraParticleGroup = new DynamicParticleGroup(100000);

    private final static Vector2f zero = new Vector2f(0, 0);
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
     * @param brightness from 0 to 1
     * @param duration in seconds
     */
    public static void addExtraSmoothParticle(Vector2f loc, Vector2f vel, float size, float brightness, float duration, Color color) {
        SmoothParticle smoothparticle = new SmoothParticle(color, size);
        smoothparticle.setMaxAge(duration);
        smoothparticle.setPos(loc.x, loc.y);
        smoothparticle.setVel(vel.x, vel.y);
        ExtraParticleGroup.add(smoothparticle);
    }

    /**
     * Opaque smoke particle.
     *
     * @param brightness from 0 to 1
     * @param duration in seconds
     */
    public static void addExtraSmokeParticle(Vector2f loc, Vector2f vel, float size, float opacity, float duration, Color color) {
        DetailedSmokeParticle detailedsmokeparticle = new DetailedSmokeParticle(color, size, size * 1.25F);
        detailedsmokeparticle.setAngle((float) Math.random() * 360F);
        detailedsmokeparticle.setRotationSpeed((float) Math.random() * 140F - 70F);
        detailedsmokeparticle.setMaxAge(duration);
        detailedsmokeparticle.setPos(loc.x, loc.y);
        detailedsmokeparticle.setVel(vel.x, vel.y);
        detailedsmokeparticle.setRampUpPeriod(0.5F * (float) Math.random());
        ExtraParticleGroup.add(detailedsmokeparticle);
    }

    public static void AddParticlesEx(
            int hitParticleCount, int smoothParticleCount, int smokeParticleCount, boolean randomCounts,
            Vector2f loc, float radius,
            float facing, float spread,
            Vector2f relativeVel, float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
//        CombatEngineAPI engine = Global.getCombatEngine();
        if (!ShaderLibExists||!ShaderLib.isOnScreen(loc, offScreenDist)) {
            return;
        }
        if (randomCounts) {
            hitParticleCount = MathUtils.getRandomNumberInRange(0, hitParticleCount);
            smoothParticleCount = MathUtils.getRandomNumberInRange(0, smoothParticleCount);
            smokeParticleCount = MathUtils.getRandomNumberInRange(0, smokeParticleCount);
        }
        minBrightness = Math.max(minBrightness, 0);
        maxBrightness = Math.min(1, maxBrightness);
        for (int i = 0; i < hitParticleCount; i++) {
            Vector2f randomLoc;
            if (radius == 0) {
                randomLoc = loc;
            } else {
                randomLoc = MathUtils.getRandomPointInCircle(loc, radius);
            }
            Vector2f vel = MathUtils.getPointOnCircumference(relativeVel, MathUtils.getRandomNumberInRange(minSpeed, maxSpeed), MathUtils.clampAngle(MathUtils.getRandomNumberInRange(facing - spread, facing + spread)));
            int size = MathUtils.getRandomNumberInRange(minSize, maxSize);
            float brightness = MathUtils.getRandomNumberInRange(minBrightness, maxBrightness);
            float duration = MathUtils.getRandomNumberInRange(minDuration, maxDuration);
            GenericTextureParticle generictextureparticle = new GenericTextureParticle("graphics/fx/hit_glow.png", new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (brightness * 255f)), size, size, 0.0F);
            generictextureparticle.setBlendFunc(770, 1);
            generictextureparticle.setPos(randomLoc.x, randomLoc.y);
            generictextureparticle.setVel(vel.x, vel.y);
            generictextureparticle.setMaxAge(duration);
            ExtraParticleGroup.add(generictextureparticle);
        }
        for (int i = 0; i < smoothParticleCount; i++) {
            Vector2f randomLoc;
            if (radius == 0) {
                randomLoc = loc;
            } else {
                randomLoc = MathUtils.getRandomPointInCircle(loc, radius);
            }
            Vector2f vel = MathUtils.getPointOnCircumference(relativeVel, MathUtils.getRandomNumberInRange(minSpeed, maxSpeed), MathUtils.clampAngle(MathUtils.getRandomNumberInRange(facing - spread, facing + spread)));
            int size = MathUtils.getRandomNumberInRange(minSize, maxSize);
            float brightness = MathUtils.getRandomNumberInRange(minBrightness, maxBrightness);
            float duration = MathUtils.getRandomNumberInRange(minDuration, maxDuration);
            SmoothParticle smoothparticle = new SmoothParticle(color, size);
            smoothparticle.setMaxAge(duration);
            smoothparticle.setPos(randomLoc.x, randomLoc.y);
            smoothparticle.setVel(vel.x, vel.y);
            ExtraParticleGroup.add(smoothparticle);
        }
        for (int i = 0; i < smokeParticleCount; i++) {
            Vector2f randomLoc;
            if (radius == 0) {
                randomLoc = loc;
            } else {
                randomLoc = MathUtils.getRandomPointInCircle(loc, radius);
            }
            Vector2f vel = MathUtils.getPointOnCircumference(relativeVel, MathUtils.getRandomNumberInRange(minSpeed, maxSpeed), MathUtils.clampAngle(MathUtils.getRandomNumberInRange(facing - spread, facing + spread)));
            int size = MathUtils.getRandomNumberInRange(minSize, maxSize);
            float brightness = MathUtils.getRandomNumberInRange(minBrightness, maxBrightness);
            float duration = MathUtils.getRandomNumberInRange(minDuration, maxDuration);
            DetailedSmokeParticle detailedsmokeparticle = new DetailedSmokeParticle(color, size, size * 1.25F);
            detailedsmokeparticle.setAngle((float) Math.random() * 360F);
            detailedsmokeparticle.setRotationSpeed((float) Math.random() * 140F - 70F);
            detailedsmokeparticle.setMaxAge(duration);
            detailedsmokeparticle.setPos(randomLoc.x, randomLoc.y);
            detailedsmokeparticle.setVel(vel.x, vel.y);
            detailedsmokeparticle.setRampUpPeriod(0.5F * (float) Math.random());
            ExtraParticleGroup.add(detailedsmokeparticle);
        }
    }

    public static void AddParticlesEx(
            int count, int type, boolean randomCounts,
            Vector2f loc, float radius,
            float facing, float spread,
            Vector2f relativeVel, float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        int hitParticleCount = 0, smoothParticleCount = 0, smokeParticleCount = 0;
        switch (type) {
            case 1:
                hitParticleCount = count;
                break;
            case 2:
                smoothParticleCount = count;
                break;
            case 3:
                smokeParticleCount = count;
                break;
        }
        AddParticlesEx(hitParticleCount, smoothParticleCount, smokeParticleCount, randomCounts, loc, radius, facing, spread, relativeVel, minSpeed, maxSpeed, minSize, maxSize, minBrightness, maxBrightness, minDuration, maxDuration, color);
    }

    public static void AddParticlesEx(
            int count, int type, boolean randomCounts,
            Vector2f loc, float radius,
            float facing, float spread,
            float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        AddParticlesEx(
                count, type, randomCounts,
                loc, radius,
                facing, spread,
                zero, minSpeed, maxSpeed,
                minSize, maxSize,
                minBrightness, maxBrightness,
                minDuration, maxDuration,
                color);
    }

    public static void AddParticlesOnSegmentEx(
            Vector2f start, Vector2f end,
            float density, int type, boolean randomDensity,
            float radius,
            float spread,
            Vector2f relativeVel, float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        if (!ShaderLibExists||!ShaderLib.isOnScreen(start, end, offScreenDist)) {
            return;
        }
        float dist = MathUtils.getDistance(start, end);
        float times = dist * density / 100;
        float facing = VectorUtils.getAngle(start, end);
        for (int i = 0; i < times; i++) {
            AddParticlesEx(
                    1, type, randomDensity,
                    MathUtils.getRandomPointOnLine(start, end), radius,
                    facing, spread,
                    relativeVel, minSpeed, maxSpeed,
                    minSize, maxSize,
                    minBrightness, maxBrightness,
                    minDuration, maxDuration,
                    color);
        }
    }

    public static void AddParticlesOnSegmentEx(
            Vector2f start, Vector2f end,
            float density, int type, boolean randomDensity,
            float radius,
            float spread,
            float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        AddParticlesOnSegmentEx(
                start, end,
                density, type, randomDensity,
                radius,
                spread,
                zero, minSpeed, maxSpeed,
                minSize, maxSize,
                minBrightness, maxBrightness,
                minDuration, maxDuration,
                color);
    }

    public static void ParticleBeamPathEx(
            BeamAPI beam,
            float density, int type, boolean randomDensity,
            float radius,
            float spread,
            Vector2f relativeVel, float angularVel,
            float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {

        Vector2f to = beam.getTo();
        Vector2f from = beam.getFrom();

        ParticlesRayEx(
                from, to,
                density, type, randomDensity,
                radius,
                spread,
                relativeVel, angularVel,
                minSpeed, maxSpeed,
                minSize, maxSize,
                minBrightness, maxBrightness,
                minDuration, maxDuration,
                color,
                true
        );
    }

    public static void ParticlesRayEx(
            Vector2f start, Vector2f end,
            float density, int type, boolean randomDensity,
            float radius,
            float spread,
            Vector2f relativeVel, float angularVel,
            float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color,
            boolean sureEnd) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (!ShaderLibExists||!ShaderLib.isOnScreen(start, end, offScreenDist)) {
            return;
        }
        Vector2f startToEnd = Vector2f.sub(end, start, null);
        float dist = startToEnd.length();
        if(dist==0)return;
        float times = dist * density / 100;
        float facing = VectorUtils.getFacing(startToEnd);
        Vector2f AVModule = new Vector2f();
        startToEnd.normalise();
        if (angularVel != 0) {
            AVModule = MathUtils.getPointOnCircumference(null, 1, facing + 90 + angularVel);
//            AVModule = new Vector2f(-startToEnd.y, startToEnd.x);
//            AVModule.normalise();
            AVModule.scale((float) FastTrig.sin(Math.toRadians(angularVel)) / Global.getCombatEngine().getElapsedInLastFrame());
        }
        for (int i = 0; i < times; i++) {
            float randomDist = MathUtils.getRandomNumberInRange(0, dist);
            Vector2f point = MathUtils.getPointOnCircumference(start, randomDist, facing);
            if (radius > 0) {
                point = MathUtils.getRandomPointInCircle(point, radius);
            }
            Vector2f vel = new Vector2f(relativeVel);
            if (angularVel != 0) {
                Vector2f angVel = new Vector2f(AVModule);
                angVel.scale(randomDist);
                Vector2f.add(vel, angVel, vel);
            }
            vel = MathUtils.getPointOnCircumference(vel, MathUtils.getRandomNumberInRange(minSpeed, maxSpeed), MathUtils.clampAngle(MathUtils.getRandomNumberInRange(facing - spread, facing + spread)));
            int size = MathUtils.getRandomNumberInRange(minSize, maxSize);
            float brightness = MathUtils.getRandomNumberInRange(minBrightness, maxBrightness);
            float duration = MathUtils.getRandomNumberInRange(minDuration, maxDuration);
            if (sureEnd) {
                float speedModule = Vector2f.dot(vel, startToEnd);
                if (speedModule > 0) {
                    duration = Math.min(duration, (dist - randomDist) / speedModule);
                } else if (speedModule < 0) {
                    duration = Math.min(duration, (randomDist) / -speedModule);
                }
            }
            switch (type) {
                case 1:
                    engine.addHitParticle(point, vel, size, brightness, duration, color);
                    break;
                case 2:
                    engine.addSmoothParticle(point, vel, size, brightness, duration, color);
                    break;
                case 3:
                    engine.addSmokeParticle(point, vel, size, brightness, duration, color);
                    break;
            }
        }
    }
}
