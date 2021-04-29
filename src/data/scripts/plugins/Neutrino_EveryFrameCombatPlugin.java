package data.scripts.plugins;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.loading.ProjectileSpawnType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.ai.GuidedProximityFuseAI;
import com.fs.starfarer.combat.entities.BeamWeaponRay;
import com.fs.starfarer.combat.entities.Missile;
import com.fs.starfarer.combat.entities.MovingRay;

import data.hullmods.NeutrinoNeutroniumPlating.PowerAromr;
import static data.scripts.NCModPlugin.ShaderLibExists;
import static data.scripts.plugins.Neutrino_CombatPluginCreator.createDERPOnRemovePlugin;
import static data.scripts.util.AdvForce.applyMomentum;
import data.scripts.util.Neutrino_CollisionUtilsEX;
import static data.scripts.util.Neutrino_CollisionUtilsEX.getShipCollisionPoint;
import data.scripts.weapons.NeutAntiPhotonBeamEffect;
import data.scripts.weapons.NeutAntiPhotonBeamEffect.separatelyAimBeam;

import java.util.List;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.lwjgl.util.vector.Vector2f;

import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lazywizard.lazylib.MathUtils;

import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.WaveDistortion;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;

public class Neutrino_EveryFrameCombatPlugin extends BaseEveryFrameCombatPlugin {

    private CombatEngineAPI engine;
    private final Vector2f zero = new Vector2f(0, 0);
    private static final String KEY = "Neutrino_LocalData";
    private static boolean paused = false;
    // DATA
    // For phaseMissileEffect
    private static final float phaseFadeTime = 2f;
    private final IntervalUtil flashInterval = new IntervalUtil(0.1F, 0.1F);
    //Projectile List
    private static final String neutrino_phase_missile_ID = "neutrino_phase_missile";
    private static final String neutrino_super_phase_missile_ID = "neutrino_super_phase_missile";
    private static final String neutrino_phase_missile_payload_ID = "neutrino_phase_missile2";
    private static final String neutrino_sapper_missile_ID = "neutrino_sapper_missile";
    private static final String neutrino_heavypulsar_shot_ID = "neutrino_heavypulsar_shot";
    private static final String neutrino_pulsar_shot_ID = "neutrino_pulsar_shot";
    private static final String neutrino_magnetar_shot_ID = "neutrino_magnetar_shot";
    private static final String neutrino_quasar_shot_ID = "neutrino_quasar_shot";
    private static final String neutrino_graviton_ID = "neutrino_graviton";
    private static final String NEUTRINO_DERP_ID = "neutrino_derpcharge";
    //drone list
    private static final String neutrino_guardianshield_ID = "neutrino_guardianshield";
    private static final String neutrino_aegis_ID = "neutrino_aegis";
    //ship list
    private static final String neutrino_colossus_ID = "neutrino_colossus";
    private List<ShipAPI> toCheck = new ArrayList<>();
    //some maps to optimize CPU loads
    private Map<DamagingProjectileAPI, Vector2f> neutrino_pulsar_shot_relative_velocity;
//    private Map<DamagingProjectileAPI, Vector2f> neutrino_pulsar_shot_fading_loc;

//    private CombatViewport mockViewport;
//    private Rectangle2D.Float vp = new Rectangle2D.Float();
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null) {
            return;
        }
        final Neutrino_LocalData.LocalData localData = (Neutrino_LocalData.LocalData) engine.getCustomData().get(KEY);
        ViewportAPI cv = engine.getViewport();
//        vp.setRect(cv.getLLX(), cv.getLLY(), cv.getVisibleWidth(), cv.getVisibleHeight());
        // we needs to do some tricks when the engine.isPaused() so make this check        
        if (!engine.isPaused()) {
            paused = false;
            // OK, do some shield drone check first
            List<ShipAPI> ships = engine.getShips();
//            List<ShipAPI> toCheck = new ArrayList<>();
            if (!ships.isEmpty()) {
                for (ShipAPI ship : ships) {
                    switch (ship.getHullSpec().getHullId()) {
                        case neutrino_aegis_ID:
                            toCheck.add(ship);
                            if (ship.isHulk() || ship.getShield().isOff()) {
                                ship.setCollisionClass(CollisionClass.FIGHTER);
                                ship.setCollisionRadius(45);
                            } else if (ship.getShield().isOn()) {
                                ship.setCollisionRadius(200);
                            }
                            break;
                        case neutrino_guardianshield_ID:
                            toCheck.add(ship);
                            if (ship.isHulk() || ship.getShield().isOff()) {
                                ship.setCollisionClass(CollisionClass.FIGHTER);
                                ship.setCollisionRadius(400);
//                                localData.guardianShieldRadiusMap.put(ship, 360f);
                            } else if (ship.getShield().isOn()) {
                                ship.getShield().setActiveArc(360);
                                float radius = ship.getCollisionRadius();
                                if (radius < 1500) {
                                    radius += 300 * amount;
                                } else {
                                    radius = 1500;
                                }
                                ship.getShield().setRadius(radius);
                                ship.setCollisionRadius(radius);
                            }
                            if (ship.getFluxTracker().isOverloaded() && Math.random() < 0.1) {
                                engine.applyDamage(ship, ship.getLocation(), MathUtils.getRandomNumberInRange(0F, 100F), DamageType.ENERGY, 0f, true, true, ship);
                                break;
                            }
                    }
                    //So let's to something to make the guardian shield push ships out.
                    for (ShipAPI shieldShip : toCheck) {
                        if (shieldShip.getId() != neutrino_guardianshield_ID
                                || shieldShip == ship
                                || (shieldShip.getParentStation() != null && shieldShip.getParentStation() == ship)
                                || ship.isFighter()
                                || ship.getCollisionClass() == CollisionClass.NONE) {
                            continue;
                        }
                        if (MathUtils.getDistance(ship.getLocation(), shieldShip.getLocation()) < ship.getCollisionRadius() + shieldShip.getCollisionRadius()) {//hmm, I needs to override the no negative result thing here.
                            Vector2f pointToTest = VectorUtils.clampLength(Vector2f.sub(shieldShip.getLocation(), ship.getLocation(), null), shieldShip.getShieldRadiusEvenIfNoShield());
                            Vector2f collisionPoint = getShipCollisionPoint(shieldShip.getLocation(), pointToTest, ship);
                            if (collisionPoint != null) {
//                                engine.addSmoothParticle(collisionPoint, zero, 10, 10, 0.001f, Color.yellow);//debuging
                                if (!ship.isStation() && !(ship.isStationModule() && ship.getParentStation().isStation())) {
                                    ship.getVelocity().set(shieldShip.getVelocity());
                                    applyMomentum(ship, collisionPoint, Vector2f.sub(shieldShip.getLocation(), ship.getLocation(), null), amount * 10f, true);
                                }
                                applyMomentum(shieldShip.getParentStation(), collisionPoint, Vector2f.sub(ship.getLocation(), shieldShip.getLocation(), null), amount * -0.5f, true);
                            }
                        }
                    }
                }
            }

            //shield drone check END
            //Damaging Projectiles check
            List<DamagingProjectileAPI> projectiles = engine.getProjectiles();

            if (!projectiles.isEmpty()) {
                for (DamagingProjectileAPI proj : projectiles) {
                    // make shield drone shield impenetrable
                    if (proj.didDamage()
                            && proj.getCollisionClass() != CollisionClass.NONE
                            && proj.getDamageTarget() instanceof ShipAPI
                            && toCheck.contains(proj.getDamageTarget())) {
                        engine.removeEntity(proj);
                        continue;
                    }
                    // END

                    // Projectile's EveryFrameCombatPlugin  
                    String projSpecId = proj.getProjectileSpecId();
                    flashInterval.advance(amount);
                    if (projSpecId != null) {
                        switch (projSpecId) {
                            // phase missile-ish things
                            case neutrino_phase_missile_ID:
                            case neutrino_phase_missile_payload_ID:
                            case neutrino_super_phase_missile_ID:
                                phaseMissileEffect(proj, amount);
                                break;
                            // sapper-ish things
                            case neutrino_sapper_missile_ID:
                                Vector2f vel = proj.getVelocity();
                                if (!vel.equals(zero)) {
                                    vel.normalise(vel);
                                    float mod = Math.max(50 + proj.getSource().getMutableStats().getMissileMaxSpeedBonus().computeEffective(700f)
                                            - (proj.getSource().getMutableStats().getMissileMaxSpeedBonus().computeEffective(300f) * proj.getElapsed()),
                                            50f);
                                    vel.scale(mod);
                                }
                                break;
                            // Graviton Inversion Device hack
                            case neutrino_graviton_ID:
                                Missile missile = (Missile) proj;

                                if (missile.getElapsed() == 0 && missile.getSource().getShipTarget() != null) {
                                    CombatEntityAPI target = missile.getSource().getShipTarget();
//                                    float dis = target.getCollisionRadius() + MathUtils.getRandomNumberInRange(50, 100);
//                                    float angle = VectorUtils.getAngle(target.getLocation(), missile.getLocation()) + MathUtils.getRandomNumberInRange(-45, 45);
//                                    Vector2f tem = MathUtils.getPointOnCircumference(target.getLocation(), dis, angle);
                                    Vector2f tem = MathUtils.getRandomPointOnCircumference(missile.getSource().getLocation(), MathUtils.getRandomNumberInRange(100, 150));
                                    missile.getLocation().set(tem);
                                    Vector2f lead = AIUtils.getBestInterceptPoint(missile.getLocation(), 125, target.getLocation(), target.getVelocity());
                                    if (lead != null) {
                                        missile.getVelocity().set(MathUtils.getPointOnCircumference(zero, missile.getVelocity().length(), VectorUtils.getAngle(missile.getLocation(), lead) + MathUtils.getRandomNumberInRange(-15, 15)));
                                    } else if (target.getLocation() != null) {
                                        missile.getVelocity().set(MathUtils.getPointOnCircumference(zero, missile.getVelocity().length(), VectorUtils.getAngle(missile.getLocation(), target.getLocation()) + MathUtils.getRandomNumberInRange(-15, 15)));
                                    }
                                    missile.fadeOutThenIn(0.2f);
                                    if (ShaderLibExists) {
                                        WaveDistortion wave = new WaveDistortion();
                                        wave.setLocation(missile.getLocation());
                                        wave.setIntensity(50f);
                                        wave.setLifetime(0.5f);
                                        wave.setSize(50f);
                                        wave.fadeOutIntensity(0.25f);
                                        DistortionShader.addDistortion(wave);
                                        StandardLight light = new StandardLight();
                                        light.attachTo(proj);
                                        light.setLocation(missile.getLocation());
                                        light.setColor(new Color(255, 175, 255, 50));
                                        light.setSize(30f);
                                        light.setIntensity(0.2f);
                                        light.fadeOut(0.3f);
                                        LightShader.addLight(light);
                                    }
                                    Vector2f randomPoint = MathUtils.getRandomPointOnCircumference(missile.getLocation(), 5f);
                                    CombatEntityAPI anchor1 = new AnchoredEntity(proj, randomPoint);
                                    engine.spawnEmpArc(proj.getSource(), missile.getLocation(), anchor1, anchor1, DamageType.ENERGY, 0f, 0f, 50f, null, 2f,
                                            new Color(51, 37, 51, 100), new Color(255, 0, 255, 0));
                                }
                                if (missile.getElapsed() < 0.3) {
                                    missile.setJitter(missile, new Color(51, 37, 51, 100), 5, 2, 2);
                                }

                                if (missile.isMinePrimed() && missile.getUntilMineExplosion() < 0.2f) {
                                    SimpleEntity se = new SimpleEntity(proj.getLocation());
                                    GuidedProximityFuseAI AI = (GuidedProximityFuseAI) missile.getAI();
                                    DamagingProjectileAPI explosion = AI.explode();

                                    if (ShaderLibExists) {
                                        RippleDistortion Rip = new RippleDistortion();
                                        Rip.setLocation(se.getLocation());
                                        Rip.setIntensity(100f);
                                        Rip.setLifetime(1f);
                                        Rip.setFrameRate(120);
                                        Rip.setCurrentFrame(0);
                                        Rip.setSize(175f);
                                        Rip.fadeInSize(0.15f);
                                        Rip.fadeOutIntensity(0.5f);
                                        Rip.flip(true);
                                        DistortionShader.addDistortion(Rip);
                                    }
                                    //SFX
                                    Global.getSoundPlayer().playSound("neutrino_gravitonexplosion", 1f, 1f, se.getLocation(), proj.getVelocity());

                                    List<CombatEntityAPI> entitis = CombatUtils.getEntitiesWithinRange(se.getLocation(), 200);
                                    for (Iterator<CombatEntityAPI> iterator = entitis.iterator(); iterator.hasNext();) {
                                        CombatEntityAPI e = iterator.next();
                                        Vector2f hitPoint;
                                        if (e instanceof DamagingProjectileAPI) {
                                            DamagingProjectileAPI p = (DamagingProjectileAPI) e;
                                            if ("neutrino_graviton".equals(p.getProjectileSpecId()) || !ProjectileSpawnType.MISSILE.equals(p.getSpawnType())) {
                                                explosion.addDamagedAlready(e);
                                                continue;
                                            }
                                        }
                                        if (e instanceof ShipAPI) {
                                            ShipAPI hitShip = (ShipAPI) e;
                                            if (hitShip.isFighter() || hitShip.isPhased() || hitShip.isStation() || hitShip.isStationModule()) {
                                                continue;
                                            }
                                            hitPoint = Neutrino_CollisionUtilsEX.getShipCollisionPoint(se.getLocation(), e.getLocation(), hitShip);
                                            for (int i = 0; i < 5; i++) {
                                                engine.spawnEmpArcPierceShields(
                                                        proj.getSource(),
                                                        se.getLocation(),
                                                        se,
                                                        hitShip,
                                                        DamageType.KINETIC,
                                                        0,
                                                        proj.getEmpAmount() * 0.2f,
                                                        10000,
                                                        null,
                                                        10,
                                                        new Color(193, 234, 255, 255),
                                                        new Color(54, 179, 200, 200));
                                            }
                                        } else {
                                            hitPoint = e.getLocation();
                                        }
                                        if (hitPoint != null && se.getLocation() != null) {//hmm, they should not be null but some I get NPE from this...so whatever, do a null check
                                            applyMomentum(e, hitPoint, Vector2f.sub(hitPoint, se.getLocation(), null), -proj.getDamageAmount() / 5f, true);
                                        }
                                    }
//                                  engine.removeEntity(proj);
//                                    engine.applyDamage(missile, missile.getLocation(), missile.getHitpoints() * 2, DamageType.OTHER, 0, true, true, proj);
                                }
                                break;
                            // TESTING deap trick
                            case NEUTRINO_DERP_ID:
                                if (proj.getElapsed() == 0) {
                                    createDERPOnRemovePlugin(proj);
                                }
                                break;
                            // pulsar weapon line ion-ish like VFX
                            case neutrino_heavypulsar_shot_ID:
                            case neutrino_pulsar_shot_ID:
                            case neutrino_magnetar_shot_ID:
                            case neutrino_quasar_shot_ID:
                                if (proj.didDamage()) {
                                    continue;
                                }
                                MovingRay m = (MovingRay) proj;

                                if (proj.isFading()) {
                                    Vector2f VVel;
                                    if (neutrino_pulsar_shot_relative_velocity.containsKey(proj)) {
                                        VVel = neutrino_pulsar_shot_relative_velocity.get(proj);
                                    } else {

                                        VVel = MathUtils.getPointOnCircumference(null, 1, proj.getFacing() + 90);
                                        VVel.scale(Vector2f.dot(proj.getVelocity(), VVel));
                                        neutrino_pulsar_shot_relative_velocity.put(proj, VVel);
                                    }
                                    float speed = proj.getVelocity().length();
                                    float bright = Math.min(proj.getDamageAmount() / proj.getBaseDamageAmount(), 1);
                                    if (projSpecId.equals(neutrino_magnetar_shot_ID)) {
                                        for (float particlesPerSec = 200f * amount; Math.random() < particlesPerSec; particlesPerSec--) {
                                            Vector2f random = MathUtils.getRandomPointOnLine(m.getFrom(), m.getTo());
                                            Neutrino_ExtraParticlePlugin.AddParticlesEx(
                                                    1, 1, true,
                                                    random, 0,
                                                    proj.getFacing(), 10,
                                                    VVel,
                                                    0, speed * 0.03f,
                                                    1, 2,
                                                    bright, bright,
                                                    0.4f, 0.6f,
                                                    new Color(1f, 1f, 1f, bright));
                                        }
                                    } else if (projSpecId.equals(neutrino_pulsar_shot_ID)) {
                                        for (float particlesPerSec = 1500f * amount; Math.random() < particlesPerSec; particlesPerSec--) {
                                            Vector2f random = MathUtils.getRandomPointOnLine(m.getFrom(), m.getTo());
                                            Neutrino_ExtraParticlePlugin.AddParticlesEx(
                                                    1, 1, true,
                                                    random, 1,
                                                    proj.getFacing(), 10,
                                                    VVel,
                                                    0, speed * 0.03f,
                                                    2, 3,
                                                    bright, bright,
                                                    0.5f, 0.8f,
                                                    new Color(1f, 1f, 1f, bright));
                                        }
                                    } else {
                                        for (float particlesPerSec = 3000f * amount; Math.random() < particlesPerSec; particlesPerSec--) {
                                            Vector2f random = MathUtils.getRandomPointOnLine(m.getFrom(), m.getTo());
                                            Neutrino_ExtraParticlePlugin.AddParticlesEx(
                                                    1, 1, true,
                                                    random, 2,
                                                    proj.getFacing(), 10,
                                                    VVel,
                                                    0, speed * 0.03f,
                                                    3, 5,
                                                    bright, bright,
                                                    0.5f, 0.8f,
                                                    new Color(1f, 1f, 1f, bright));
                                        }
                                    }
                                    break;
                                }
                        }
                        // Projectile's EveryFrameCombatPlugin END
                    }
                }
                // make shield drone shield impenetrable, beams part
                List<BeamAPI> beams = engine.getBeams();
                if (!toCheck.isEmpty() && !beams.isEmpty()) {
                    for (BeamAPI beam : beams) {
                        if (beam.getBrightness() > 0) {
                            Vector2f closest = null;
                            float closestDist = Float.MAX_VALUE;
                            for (ShipAPI shipToCheck : toCheck) {
                                if (beam.getSource().getOwner() == shipToCheck.getOwner()) {
                                    continue;
                                }
                                Vector2f tmp = getShipCollisionPoint(beam.getFrom(), beam.getTo(), shipToCheck);
                                if (closest == null) {
                                    closest = tmp;
                                } else if (tmp != null && MathUtils.getDistance(tmp, beam.getFrom()) < closestDist) {
                                    closest = tmp;
                                    closestDist = MathUtils.getDistance(tmp, beam.getFrom());
                                }
                            }
                            if (closest != null) {
                                beam.getTo().set(closest);
                            }
                        }
                    }
                }
                // make shield drone shield impenetrable, beams part END

                // anti-photon traking
                // just remind myself, I already tried to put this in beamEffect 2 times and it won't work due to the internal looping oreder.
                for (Iterator<Map.Entry<BeamAPI, NeutAntiPhotonBeamEffect.separatelyAimBeam>> it = localData.antiPhotonAimData.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<BeamAPI, NeutAntiPhotonBeamEffect.separatelyAimBeam> entry = it.next();
                    BeamWeaponRay beam = (BeamWeaponRay) entry.getKey();
                    if (beam.getBrightness() <= 0) {
                        it.remove();
                    } else {
                        separatelyAimBeam SAB = entry.getValue();
                        float aimOffset = SAB.getCurrAimOffset();
                        if (aimOffset == SAB.getTargetAimOffset()) {
//                    continue; 
                        } else {
                            float trun = SAB.getTurnRate() * amount;
                            float targetAimOffset = SAB.getTargetAimOffset();
                            float diff = aimOffset - targetAimOffset;
                            if (Math.abs(diff) < trun) {
                                aimOffset = targetAimOffset;
                            } else {
                                aimOffset += Math.copySign(trun, -diff);
                            }
                            SAB.setCurrAimOffset(aimOffset);
                        }
                        beam.getTo().set(MathUtils.getPointOnCircumference(beam.getFrom(), MathUtils.getDistance(beam.getFrom(), beam.getTo()), SAB.getWeapon().getCurrAngle() + aimOffset));
                    }
                }

                // anti-photon traking. END
                // we needs to make something run once when engine.isPaused()
                // for NeutrinoNeutroniumPlating after battle restroge trick
            } else if (engine.isPaused()
                    && !paused
                    && engine.getFleetManager(FleetSide.ENEMY).getTaskManager(false).isInFullRetreat()) {
                if (!localData.powerAromrState.isEmpty()) {
                    for (ShipAPI ship : localData.powerAromrState.keySet()) {
                        ArmorGridAPI armorGrid = ship.getArmorGrid();
                        PowerAromr powerAromr = localData.powerAromrState.get(ship);
                        if (!powerAromr.active) {
                            for (int i = 0; i < powerAromr.x; i++) {
                                for (int j = 0; j < powerAromr.y; j++) {
                                    powerAromr.armorValueWithoutPlating[i][j] = Math.min(armorGrid.getArmorValue(i, j), powerAromr.maxArmorPerCell - powerAromr.maxPowerArmorPerCell);
                                }
                            }
                        }
                        for (int i = 0; i < powerAromr.x; i++) {
                            for (int j = 0; j < powerAromr.y; j++) {
                                armorGrid.setArmorValue(i, j, Math.min(powerAromr.maxArmorPerCell, Math.max(0, powerAromr.armorValueWithoutPlating[i][j]) + powerAromr.maxPowerArmorPerCell));
                            }
                        }
                        powerAromr.justPaused = true;
                    }
                }
                paused = true;
            }
            // NeutrinoNeutroniumPlating after battle restroge trick END

            // something needs to be done all the times...well, maintainStatusForPlayerShip, for player ship only
            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();

            if (Global.getCurrentState()
                    == GameState.COMBAT && playerShip != null && !playerShip.isHulk()) {
                // Neutronium Plating
                PowerAromr powerAromr = localData.powerAromrState.get(playerShip);
                if (powerAromr != null) {
                    if (powerAromr.active) {
                        float armorPercent = (powerAromr.extarArmor / powerAromr.maxExtarArmor) * 100;
                        BigDecimal b = new BigDecimal(armorPercent);
                        String armorPercentS = b.setScale(1, RoundingMode.HALF_UP).toString();
                        if ("100.0".equals(armorPercentS)) {
                            armorPercentS = "100";
                        }
                        String data = "integrity at " + armorPercentS + "%";
                        engine.maintainStatusForPlayerShip("NeutroniumPlatingStatus1", "graphics/neut/icons/hullsys/neutrino_NeutroniumPlating_StatusIcon.png", "Neutronium Plating", data, false);
                        if (playerShip.getFluxTracker().isVenting()) {
                            engine.maintainStatusForPlayerShip("NeutroniumPlatingStatus2", "graphics/neut/icons/hullsys/neutrino_NeutroniumPlating_StatusIcon.png", "Neutronium Plating", "Plating Resistance lowered due to venting", true);
                        }
                    } else {
                        float timeRemain = (powerAromr.resetThreshold - powerAromr.extarArmor) / powerAromr.extarArmorRegenPerSec;
                        BigDecimal b = new BigDecimal(timeRemain);
                        String timeRemainS = b.setScale(2, RoundingMode.HALF_UP).toString();
                        String data = "restore in " + timeRemainS + " seconds";
                        engine.maintainStatusForPlayerShip("NeutroniumPlatingStatus1", "", "Neutronium Plating", data, true);
                        if (playerShip.getFluxTracker().isOverloaded()) {
                            engine.maintainStatusForPlayerShip("NeutroniumPlatingStatus2", "", "Neutronium Plating", "plating collapsed due to overloaded", true);
                        }
                    }
                }
                // Neutronium Plating END

//            // Guardian Shield
//            if (localData.guardianShieldMap.containsKey(playerShip)) {
//                ShipAPI drone = localData.guardianShieldMap.get(playerShip);
//                if (drone.isAlive()) {
//                    FluxTrackerAPI shieldFlux = drone.getFluxTracker();
//                    if (shieldFlux.isOverloaded()) {
//                        Global.getCombatEngine().maintainStatusForPlayerShip("GuardianShieldStatus", "graphics/icons/hullsys/fortress_shield.png", "Guardian Shield", "Warning! Shield Core Overloaded!", true);
//                    } else {
//                        String state;
//                        if (playerShip.getSystem().isOn()) {
//                            state = "Actived: Flux Capacity At ";
//                        } else {
//                            state = "Standby: Flux Capacity At ";
//                        }
//                        float fluxLevel = shieldFlux.getFluxLevel();
//                        fluxLevel *= 100f;
//                        BigDecimal b = new BigDecimal(fluxLevel);
//                        String fluxLevelS = b.setScale(2, RoundingMode.HALF_UP).toString();
//                        String data = state + fluxLevelS + "%";
//                        Global.getCombatEngine().maintainStatusForPlayerShip("GuardianShieldStatus", "graphics/icons/hullsys/fortress_shield.png", "Guardian Shield", data, false);
//                    }
//                } else {
//                    Global.getCombatEngine().maintainStatusForPlayerShip("GuardianShieldStatus", "graphics/icons/hullsys/fortress_shield.png", "Guardian Shield", "Warning! Shield Core Ejected!", true);
//                }
//            }
            }
            //
            toCheck.clear();
        }
    }

    private void phaseMissileEffect(DamagingProjectileAPI proj, float amount) {
        float elapsed = proj.getElapsed();
        Vector2f loc = proj.getLocation();
        MissileAPI missile = (MissileAPI) proj;
        switch (proj.getProjectileSpecId()) {
            case neutrino_phase_missile_ID:
            case neutrino_super_phase_missile_ID:
                missile.setJitter(missile, new Color(255, 175, 255, 100), 0.2f, 1, 0.1f, 0.2f);
                if (elapsed < amount) {
                    missile.fadeOutThenIn(30f);
                    missile.setMinePrimed(true);
                    missile.setShineBrightness(10);
                    Missile m = (Missile) missile;
                    float dis = proj.getSource().getCollisionRadius() + MathUtils.getRandomNumberInRange(50f, 200f);
                    Vector2f mov = MathUtils.getRandomPointOnCircumference(null, dis);
                    Vector2f.add(loc, mov, loc);
                    if (ShaderLibExists) {
                        WaveDistortion wave = new WaveDistortion();
                        wave.setLocation(loc);
                        wave.setIntensity(25f);
                        wave.setLifetime(0.5f);
                        wave.setSize(15f);
                        wave.fadeOutIntensity(0.25f);
                        DistortionShader.addDistortion(wave);
                        StandardLight light = new StandardLight();
                        light.setLocation(loc);
                        light.setColor(new Color(255, 175, 255, 50));
                        light.setSize(10f);
                        light.setIntensity(1f);
                        light.fadeOut(0.25f);
                        LightShader.addLight(light);
                    }
                }
//                if (maxFlightTime - elapsed < phaseFadeTime) {
//                    missile.getSpriteAPI().setAlphaMult(Math.min((Math.max(maxFlightTime - elapsed, 0f)) / phaseFadeTime * 0.1f, MathUtils.getRandomNumberInRange(0f, 0.1f)));
//                }
                break;
            case neutrino_phase_missile_payload_ID:
                if (elapsed < amount) {
                    Vector2f zero = new Vector2f();
                    if (ShaderLibExists) {
                        WaveDistortion wave = new WaveDistortion();
                        wave.setLocation(loc);
                        wave.setIntensity(50f);
                        wave.setLifetime(0.5f);
                        wave.setSize(50f);
                        wave.fadeOutIntensity(0.25f);
                        DistortionShader.addDistortion(wave);
                        StandardLight light = new StandardLight();
                        light.attachTo(proj);
                        light.setLocation(loc);
                        light.setColor(new Color(255, 175, 255, 50));
                        light.setSize(30f);
                        light.setIntensity(0.2f);
                        light.fadeOut(0.3f);
                        LightShader.addLight(light);
                    }
                    Global.getSoundPlayer().playSound("system_phase_cloak_collision", 1f, 1f, loc, zero);
                    Global.getSoundPlayer().playSound("neutrino_lightlaunch", 0.5f, 0.5f, loc, zero);
                    Vector2f randomPoint = MathUtils.getRandomPointOnCircumference(loc, 5f);
                    CombatEntityAPI anchor1 = new AnchoredEntity(proj, randomPoint);
                    engine.spawnEmpArc(proj.getSource(), loc, anchor1, anchor1, DamageType.ENERGY, 0f, 0f, 50f, null, 2f,
                            new Color(51, 37, 51, 100), new Color(255, 0, 255, 0));
                }
                if (elapsed <= 1f) {
                    missile.getSpriteAPI().setAlphaMult(elapsed);
                    if (flashInterval.intervalElapsed()) {
                        Vector2f randomPoint = MathUtils.getRandomPointOnCircumference(loc, 5f);
                        CombatEntityAPI anchor1 = new AnchoredEntity(proj, randomPoint);
                        engine.spawnEmpArc(proj.getSource(), loc, anchor1, anchor1, DamageType.ENERGY, 0f, 0f, 50f, null, 2f,
                                new Color(51, 37, 51, 100), new Color(255, 0, 255, 0));
                    }
                }
                break;
        }
    }

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
        this.neutrino_pulsar_shot_relative_velocity = new WeakHashMap<>(250);
//        this.neutrino_pulsar_shot_fading_loc = new WeakHashMap<>(250);
//        this.mockViewport = new CombatViewport(-100000000f, -100000000f, 200000000f, 200000000f);
    }
}
