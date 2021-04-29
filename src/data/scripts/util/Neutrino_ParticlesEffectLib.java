package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.util.Misc;
import static data.scripts.NCModPlugin.ShaderLibExists;
import org.dark.shaders.util.ShaderLib;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;

public class Neutrino_ParticlesEffectLib {

    private final static Vector2f zero = new Vector2f(0, 0);
    private final static float offScreenDist = 500f;

    public static void AddParticles(
            int hitParticleCount, int smoothParticleCount, int smokeParticleCount, boolean randomCounts,
            Vector2f loc, float radius,
            float facing, float spread,
            Vector2f relativeVel, float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (!ShaderLibExists||!ShaderLib.isOnScreen(loc, offScreenDist)) {
            return;
        }
        if (randomCounts) {
            hitParticleCount = MathUtils.getRandomNumberInRange(0, hitParticleCount);
            smoothParticleCount = MathUtils.getRandomNumberInRange(0, smoothParticleCount);
            smokeParticleCount = MathUtils.getRandomNumberInRange(0, smokeParticleCount);
        }
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
            engine.addHitParticle(randomLoc, vel, size, brightness, duration, color);
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
            engine.addSmoothParticle(randomLoc, vel, size, brightness, duration, color);
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
            engine.addSmokeParticle(randomLoc, vel, size, brightness, duration, color);
        }
    }

    public static void AddParticles(
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
        AddParticles(hitParticleCount, smoothParticleCount, smokeParticleCount, randomCounts, loc, radius, facing, spread, relativeVel, minSpeed, maxSpeed, minSize, maxSize, minBrightness, maxBrightness, minDuration, maxDuration, color);
    }

    public static void AddParticles(
            int count, int type, boolean randomCounts,
            Vector2f loc, float radius,
            float facing, float spread,
            float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        AddParticles(
                count, type, randomCounts,
                loc, radius,
                facing, spread,
                zero, minSpeed, maxSpeed,
                minSize, maxSize,
                minBrightness, maxBrightness,
                minDuration, maxDuration,
                color);
    }

    public static void AddParticlesOnSegment(
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
            AddParticles(
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

    public static void AddParticlesOnSegment(
            Vector2f start, Vector2f end,
            float density, int type, boolean randomDensity,
            float radius,
            float spread,
            float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        AddParticlesOnSegment(
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

    public static void ParticleBeamPath(
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

        ParticlesRay(
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

    public static void ParticlesRay(
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

    public static void addEMPArcOnSegment(
            Vector2f start, Vector2f end,
            int times,
            float radius,
            float spread,
            Vector2f relativeVel, float minSpeed, float maxSpeed,
            int minSize, int maxSize,
            float minBrightness, float maxBrightness,
            float minDuration, float maxDuration,
            Color color) {
        /**
         * @param damageSource Ship that's ultimately responsible for dealing
         * the damage of this EMP arc. Can be null.
         * @param point starting point of the EMP arc, in absolute engine
         * coordinates.
         * @param pointAnchor The entity the starting point should move together
         * with, if any.
         * @param empTargetEntity Target of the EMP arc. If it's a ship, it will
         * randomly pick an engine nozzle/weapon to arc to. Can also pass in a
         * custom class implementing CombatEntityAPI to visually target the EMP
         * at a specific location (and not do any damage).
         * @param damageType
         * @param damAmount
         * @param empDamAmount
         * @param maxRange Maximum range the arc can reach (useful for confining
         * EMP arc targets to the area near point)
         * @param impactSoundId Can be null.
         * @param thickness Thickness of the arc (visual).
         * @param fringe
         * @param core
         * @return
         */
        Global.getCombatEngine().spawnEmpArcPierceShields(
                null, 
                end, null, null, DamageType.OTHER, radius, maxDuration, maxSpeed, null, minSpeed, color, color);

    }
}
