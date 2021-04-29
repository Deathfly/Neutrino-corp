// By Deathfly
// This used to be an example to show how to use Fake Beam but end up way to complicated...
package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatAsteroidAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import static data.scripts.NCModPlugin.ShaderLibExists;
import data.scripts.hullmods.TEM_LatticeShield;
import static data.scripts.NCModPlugin.TemplarsExists;
import data.scripts.plugins.Neutrino_FakeBeamPlugin;
import data.scripts.util.Neutrino_CollisionUtilsEX;
import data.scripts.util.Neutrino_ParticlesEffectLib;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.WaveDistortion;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class NeutSilverLanceEffect implements BeamEffectPlugin {

    public NeutSilverLanceEffect() {
    }
    // for VFX
    private final SpriteAPI beamSprite = Global.getSettings().getSprite("graphics/fx/beamcore.png");
    private final Color beamColor = Color.white;
    private final Color arcColor = new Color(200, 150, 200, 150);
    private final Color arcFringeColor = new Color(200, 200, 200, 75);
    private final float beamWidth = 12f;
    private final float duration = 0.1f;
    private final float fadeIn = 0.01f;
    private final float fadeOut = 0.01f;
    private final float particlesDensity = 5f;
    private final float hitSize = 50f;

    // for dmg
    private final float shieldHitHardFulxRatio = 0.5f;
    private final float baseShieldHitDmg = 900f;
    private final float baseExtraHitDmg = 50f;
    private final float dmgMultAfterShieldHit = 0.1f;
    private final float baseHullHitDmg = 400f;
//    private final float onHitDmgMult = 0.12f;
    private final float subDmg = 50f;
    private final float piercingDmgInterval = 15f;
    private final float EMPRatio = 0.1f;
    private final float EMPRatioAfterShield = 5f;
//    private final DamageType damageType = DamageType.KINETIC;
    private int times = 5;
    private final IntervalUtil firingInterval = new IntervalUtil(0.1f, 0.1f);

    private final Vector2f zero = new Vector2f(0, 0);
    private final List<PiercedEntity> PiercedEntities = new ArrayList<>();

    private boolean firing = false;
    private boolean runOnce = false;

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        if (!runOnce) {
            firingInterval.forceIntervalElapsed();
            runOnce = true;
        }
        if (!firing && beam.getWeapon().getChargeLevel() >= 1) {
            firing = true;
        }
        if (times > 0 && firing) {
            firingInterval.advance(amount);
            if (firingInterval.intervalElapsed()) {
                final float mult = beam.getSource().getMutableStats().getBeamWeaponDamageMult().getModifiedValue() * beam.getSource().getMutableStats().getEnergyWeaponDamageMult().getModifiedValue();
                final float fullDmg = beam.getWeapon().getDerivedStats().getBurstDamage() * mult / times;
                float shieldHitDmg = baseShieldHitDmg * mult;
                float extraHitDmg = baseExtraHitDmg * mult;
                float hullHitDmg = baseHullHitDmg * mult;
                float dmg = fullDmg;
                ShipAPI source = beam.getSource();
                final float range = beam.getWeapon().getRange();
                float facing = beam.getWeapon().getCurrAngle();
                final Vector2f beamForm = beam.getWeapon().getLocation();
                Vector2f beamTo = MathUtils.getPointOnCircumference(beamForm, range, facing);
                final Vector2f midPoint = MathUtils.getMidpoint(beamForm, beamTo);
                DamageType damageType = beam.getWeapon().getDamageType();

                // Collect all entitise that may be hit by beam.
                for (CombatEntityAPI e : CombatUtils.getEntitiesWithinRange(midPoint, range / 2f)) {
                    // skip what can't be hit
                    if (e.getCollisionClass() == CollisionClass.NONE) {
                        continue;
                    }
                    // check for ship hit. find out shield hit point, hull hit point, and hull exit point.
                    if (e instanceof ShipAPI) {
                        ShipAPI s = (ShipAPI) e;
                        if (s == source || (s.isFighter() && s.getOwner() == source.getOwner()) || s.isPhased()) {
                            continue;
                        }
                        PiercedEntity piercedEntity = new PiercedEntity();
                        piercedEntity.entity = e;
                        if (TemplarsExists && s.getVariant().getHullMods().contains("tem_latticeshield") && TEM_LatticeShield.shieldLevel(s) > 0f) {
                            piercedEntity.isLatticeShield = true;
                            piercedEntity.hullHit = CollisionUtils.getCollisionPoint(beamForm, beamTo, e);
                            piercedEntity.shieldHit = piercedEntity.hullHit;
                        }
                        piercedEntity.shieldHit = Neutrino_CollisionUtilsEX.getShieldCollisionPoint(beamForm, beamTo, s, false);
                        piercedEntity.hullHit = CollisionUtils.getCollisionPoint(beamForm, beamTo, e);

                        if (piercedEntity.hullHit == null && piercedEntity.shieldHit == null) {
                            continue;
                        }
                        piercedEntity.exit = CollisionUtils.getCollisionPoint(beamTo, beamForm, e);
                        PiercedEntities.add(piercedEntity);
                    }
                    // check for asteroid hit. find out hit point and exit point.                
                    if (e instanceof CombatAsteroidAPI) {
                        PiercedEntity piercedEntity = new PiercedEntity();
                        piercedEntity.entity = e;
                        piercedEntity.hullHit = Neutrino_CollisionUtilsEX.getCollisionPointOnCircle(beamForm, beamTo, e.getLocation(), e.getCollisionRadius());
                        if (piercedEntity.hullHit == null) {
                            continue;
                        }
                        piercedEntity.exit = Neutrino_CollisionUtilsEX.getCollisionPointOnCircle(beamTo, beamForm, e.getLocation(), e.getCollisionRadius());
                        PiercedEntities.add(piercedEntity);
                    }
                    // check for missile hit, just find out the hit point.
                    if (e instanceof MissileAPI) {
                        PiercedEntity piercedEntity = new PiercedEntity();
                        piercedEntity.entity = e;
                        piercedEntity.hullHit = CollisionUtils.getCollisionPoint(beamForm, beamTo, e);
                        if (piercedEntity.hullHit == null) {
                            continue;
                        }
                        PiercedEntities.add(piercedEntity);
                    }
                }

                Vector2f tmp = new Vector2f(beamForm);
                boolean end = false;

                if (!PiercedEntities.isEmpty()) {
                    // sort all hit point by distance to beamForm.
                    if (PiercedEntities.size() > 1) {
                        Collections.sort(PiercedEntities, new SortByDistance(beamForm));
                    }
                    Vector2f beamDir = Vector2f.sub(beamTo, beamForm, null);
                    // ...and check it one by one.
                    for (PiercedEntity pe : PiercedEntities) {
                        // hit missile or asteroid? just apply damage and pass it.
                        if (pe.entity instanceof MissileAPI || pe.entity instanceof CombatAsteroidAPI) {
                            if (Vector2f.dot(beamDir, Vector2f.sub(pe.hullHit, tmp, null)) <= 0) {
                                continue;
                            }
                            hullHitDmg = pe.entity.getMaxHitpoints() < hullHitDmg ? pe.entity.getMaxHitpoints() : hullHitDmg;
                            engine.applyDamage(pe.entity, pe.hullHit, hullHitDmg, damageType, 0f, false, true, source);
                            dmg -= hullHitDmg;
                            continue;
                        }
                        if (pe.entity instanceof ShipAPI) {
                            ShipAPI ship = (ShipAPI) pe.entity;
                            float beamDmgMutl = ((ShipAPI) pe.entity).getMutableStats().getBeamDamageTakenMult().getModifiedValue();
                            // check for a shieldHit. 
                            boolean hitShield = false;
                            float targetShieldDmgFluxRaido = 1f;
                            if (pe.shieldHit != null) {
                                // hmm, just make sure this hit point is on the right direction.
                                if (Vector2f.dot(beamDir, Vector2f.sub(pe.shieldHit, tmp, null)) <= 0) {
                                    continue;
                                }
                                // draw a fake beam!
                                Neutrino_FakeBeamPlugin.renderFakeBeam(tmp, pe.shieldHit, beamWidth, duration, fadeIn, fadeOut, beamSprite, beamColor);
                                // apply a particles path effect!
                                Neutrino_ParticlesEffectLib.AddParticlesOnSegment(
                                        tmp, pe.shieldHit,
                                        particlesDensity, 2, false,
                                        1,
                                        30f,
                                        10, 50,
                                        2, 4,
                                        0.8f, 1f,
                                        0.3f, 1f,
                                        Color.WHITE);
                                // draw an on hit effect!
                                engine.addHitParticle(
                                        pe.shieldHit,
                                        zero,
                                        (float) Math.random() * hitSize / 2 + hitSize,
                                        1,
                                        (float) Math.random() * duration / 2 + duration,
                                        Color.WHITE
                                );
                                engine.addHitParticle(
                                        pe.shieldHit,
                                        zero,
                                        (float) Math.random() * hitSize / 4 + hitSize / 2,
                                        1,
                                        0.1f,
                                        Color.WHITE
                                );
                                // move the tmp point for next check
                                tmp = pe.shieldHit;
                                targetShieldDmgFluxRaido = pe.isLatticeShield
                                        ? 0.8f
                                        : ship.getShield().getFluxPerPointOfDamage();
                                float dmgOnshield = shieldHitDmg * (targetShieldDmgFluxRaido == 0 ? 100f : 1 / targetShieldDmgFluxRaido);
                                // check if we are run out of damage.
                                if (dmg <= dmgOnshield) {
                                    dmgOnshield = dmg;
                                    beamTo = pe.shieldHit;
                                    end = true;
                                }
                                // apply damage on shield!
                                engine.applyDamage(ship, pe.shieldHit, dmgOnshield * shieldHitHardFulxRatio * beamDmgMutl, damageType, 0f, false, false, source);
                                engine.applyDamage(ship, pe.shieldHit, dmgOnshield * (1f - shieldHitHardFulxRatio) * beamDmgMutl, damageType, 0f, false, true, source);
                                if (end == true) {
                                    break;
                                }
                                hitShield = true;
                                dmg -= dmgOnshield;
                                // extra RNG damage!

                                for (int j = 0; j < 4; j++) {
                                    float critChance = ship.getFluxTracker().getFluxLevel();
                                    if (Math.random() < critChance) {
                                        if (dmg <= extraHitDmg) {
                                            extraHitDmg = dmg;
                                            beamTo = pe.shieldHit;
                                            end = true;
                                        }
                                        SimpleEntity ramdon = new SimpleEntity(MathUtils.getRandomPointOnCircumference(pe.shieldHit, MathUtils.getRandomNumberInRange(0, 1)));
                                        engine.spawnEmpArc(source, pe.shieldHit, ramdon, ramdon, damageType, 0, 0, 0, null, 20, arcColor, arcFringeColor);
                                        engine.applyDamage(ship, ramdon.getLocation(), extraHitDmg, damageType, 0, false, false, source);
                                        if (end == true) {
                                            break;
                                        }
                                        dmg -= extraHitDmg;
                                    }
                                }
                                // if we out of damage, break here.
                                if (end == true) {
                                    break;
                                }
                            }
                            // check for a hull hit.
                            if (pe.hullHit != null) {
                                // ...just in case as above.
                                if (!pe.isLatticeShield && Vector2f.dot(beamDir, Vector2f.sub(pe.hullHit, tmp, null)) <= 0) {
                                    continue;
                                }
                                // VFXs
                                Neutrino_FakeBeamPlugin.renderFakeBeam(tmp, pe.hullHit, beamWidth, duration, fadeIn, fadeOut, beamSprite, beamColor);
                                Neutrino_ParticlesEffectLib.AddParticlesOnSegment(
                                        tmp, pe.hullHit,
                                        particlesDensity, 2, false,
                                        1,
                                        30f,
                                        10, 50,
                                        2, 4,
                                        0.8f, 1f,
                                        0.3f, 1f,
                                        Color.WHITE);
                                engine.addHitParticle(
                                        pe.hullHit,
                                        zero,
                                        (float) Math.random() * hitSize / 2 + hitSize,
                                        1,
                                        (float) Math.random() * duration / 2 + duration,
                                        Color.WHITE
                                );
                                engine.addHitParticle(
                                        pe.hullHit,
                                        zero,
                                        (float) Math.random() * hitSize / 4 + hitSize / 2,
                                        1,
                                        0.1f,
                                        Color.WHITE
                                );
                                float dmgOnHull = hullHitDmg;
                                if (hitShield) {
                                    dmgOnHull *= dmgMultAfterShieldHit;
                                }
                                if (dmg <= dmgOnHull) {
                                    dmgOnHull = dmg;
                                    beamTo = pe.hullHit;
                                    end = true;
                                }
                                engine.applyDamage(ship,
                                        pe.hullHit,
                                        dmgOnHull * beamDmgMutl,
                                        damageType,
                                        dmgOnHull,
                                        true, false, source);
                                dmg -= dmgOnHull;
                                if (end == true) {
                                    break;
                                }
                                float critChance = ship.getFluxTracker().getFluxLevel() * 0.5f + 0.2f;
                                for (int j = 0; j < 4; j++) {
                                    if (Math.random() < critChance) {
                                        if (dmg <= extraHitDmg) {
                                            extraHitDmg = dmg;
                                            beamTo = pe.shieldHit;
                                            end = true;
                                        }
                                        engine.spawnEmpArcPierceShields(source, pe.hullHit, ship, ship, damageType, extraHitDmg, extraHitDmg, 200, null, 15, arcColor, arcFringeColor);
                                        if (end == true) {
                                            break;
                                        }
                                        dmg -= extraHitDmg;
                                    }
                                }

                                // if we out of damage, break here.
                                if (end == true) {
                                    break;
                                }

                                if (Vector2f.dot(pe.exit, beamTo) <= 0) {
                                    pe.exit = beamTo;
                                    end = true;
                                }

                                float piercingDmg = subDmg * mult;
                                float piercingEMP = piercingDmg * EMPRatio;
                                if (hitShield) {
                                    piercingEMP *= EMPRatioAfterShield;
                                }
                                float piercingSubDmg = piercingDmg;
                                if (hitShield) {
                                    piercingSubDmg *= dmgMultAfterShieldHit;
                                    piercingEMP *= dmgMultAfterShieldHit * targetShieldDmgFluxRaido;
                                }
                                Vector2f dir = VectorUtils.getDirectionalVector(pe.hullHit, pe.exit);
                                dir.scale(piercingDmgInterval);
                                Vector2f pTmp = new Vector2f(pe.hullHit);
                                while (dmg > 0 && Vector2f.dot(dir, Vector2f.sub(pe.exit, pTmp, null)) > 0) {
                                    if (dmg <= piercingDmg) {
                                        piercingDmg = dmg;
                                        beamTo = pTmp;
                                        end = true;
                                    }
                                    if (!hitShield && Math.random() < critChance) {
                                        ship.getMutableStats().getEffectiveArmorBonus().modifyMult("NeutSilverLanceEffect", 0);
                                    }
                                    if (Math.random() < 0.05 + critChance * 0.1) {
                                        SimpleEntity ramdon = new SimpleEntity(MathUtils.getRandomPointOnCircumference(pTmp, MathUtils.getRandomNumberInRange(15, 20)));
                                        engine.spawnEmpArcPierceShields(source, ramdon.getLocation(), ramdon, ship, damageType, extraHitDmg, extraHitDmg, 130f, null, 20, arcColor, arcFringeColor);
                                        dmg -= extraHitDmg;
                                    }
                                    engine.applyDamage(pe.entity,
                                            pTmp,
                                            piercingSubDmg * beamDmgMutl,
                                            damageType,
                                            piercingEMP,
                                            true, false, source);
                                    Vector2f.add(pTmp, dir, pTmp);
                                    dmg -= piercingDmg;
                                    ship.getMutableStats().getEffectiveArmorBonus().unmodify("NeutSilverLanceEffect");
                                }
                                if (end == true) {
                                    break;
                                }
                                // seens like fighter can cause trouble in some case, so we should do some trick on it.
                                if (ship.isFighter()) {

                                } else if (PiercedEntities.iterator().hasNext() && !CollisionUtils.isPointWithinBounds(beamTo, ship)) {
                                    // do some beam exit point VFX
                                    tmp = pe.exit;
                                    engine.applyDamage(pe.entity,
                                            tmp,
                                            piercingSubDmg * beamDmgMutl,
                                            damageType,
                                            piercingEMP,
                                            true, false, source);
                                    Neutrino_ParticlesEffectLib.AddParticles(
                                            10, 1, false,
                                            tmp, 5f,
                                            facing, 30f,
                                            30, 200,
                                            3, 5,
                                            0.8f, 1f,
                                            0.5f, 2f,
                                            Color.WHITE);
                                }
                            }
                        }
                    }
                }
                if (!end) {
                    Neutrino_FakeBeamPlugin.renderFakeBeam(tmp, beamTo, beamWidth, duration, fadeIn, fadeOut, beamSprite, beamColor);
                    Neutrino_ParticlesEffectLib.AddParticlesOnSegment(
                            tmp, beamTo,
                            particlesDensity, 2, false,
                            1,
                            30f,
                            10, 50,
                            2, 4,
                            0.8f, 1f,
                            0.3f, 1f,
                            Color.WHITE);
                }
                PiercedEntities.clear();
                // SFX
                Global.getSoundPlayer().playSound("neutrino_silverlance_fire", 1, 1, beamForm, zero);
                // extra VFX
                if (ShaderLibExists) {
                    WaveDistortion wave = new WaveDistortion();
                    wave.setLocation(beamForm);
                    wave.setIntensity(5f);
                    wave.setLifetime(0.08f);
//                wave.fadeInIntensity(0.1f);
//                wave.setAutoFadeSizeTime(Float.MAX_VALUE);
//                wave.setAutoFadeIntensityTime(0.2f);
                    wave.setSize(MathUtils.getDistance(beamForm, beamTo));
                    wave.setArc(facing + 180 - 2.5f, facing + 180 + 2.5f);
                    wave.setArcAttenuationWidth(5f);
                    DistortionShader.addDistortion(wave);
                    StandardLight light = new StandardLight();
                    light.setType(1);
                    light.setLocation(beamForm);
                    light.setLocation2(beamTo);
                    light.setColor(new Color(255, 255, 255, 100));
                    light.setSize(25f);
                    light.setIntensity(1f);
                    light.fadeOut(0.3f);
                    LightShader.addLight(light);
                }
                times--;
            }
        }
    }

    public static class PiercedEntity {

        protected Vector2f shieldHit = null;
        protected Vector2f hullHit = null;
        protected Vector2f exit = null;
        protected CombatEntityAPI entity = null;
        protected boolean isLatticeShield = false;
    }

    public static class SortByDistance implements Comparator<PiercedEntity> {

        private final Vector2f location;

        public SortByDistance(Vector2f location) {
            this.location = location;
        }

        @Override
        public int compare(PiercedEntity o1, PiercedEntity o2) {

            return Float.compare(
                    MathUtils.getDistanceSquared(o1.shieldHit == null ? o1.hullHit : o1.shieldHit, location),
                    MathUtils.getDistanceSquared(o2.shieldHit == null ? o2.hullHit : o2.shieldHit, location));
        }
    }
}
