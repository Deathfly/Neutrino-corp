//by Deathfly
package data.scripts.AIs.Missiles;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.entities.Missile;
import static data.scripts.plugins.Neutrino_CombatPluginCreator.createPhontoVFXPlugin;
import data.scripts.plugins.Neutrino_LocalData.LocalData;
import java.awt.Color;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lwjgl.util.vector.Vector2f;

public final class Neutrino_UnstablePhotonAI implements MissileAIPlugin {

    private CombatEngineAPI engine;
    private final MissileAPI missile;
//    private CombatEntityAPI target;
//    private Vector2f lead = new Vector2f(0f, 0f);
//    private Vector2f offset = new Vector2f(0f, 0f);
    private static final String KEY = "Neutrino_LocalData";
    private boolean runOnce = true;
    private final static Vector2f zero = new Vector2f(0f, 0f);
    private final float flightSpeed;
    private final IntervalUtil flashInterval = new IntervalUtil(0.2F, 0.25F);
//    private final IntervalUtil guidanceInterval = new IntervalUtil(0.1F, 0.1F);
//    private final static float closeDist = 1000;
    private int flashSize = 4;
    private boolean flash = true;
    private boolean split = true;
    private String splitWeaponId = null;
    private String specId = null;
    private int payload = 0;
    private float splitDelay = 0f;
    private float splitArc = 0f;
    private int critCount = 0;
    private boolean isCrit = false;
    private Color color1;
    private Color color2;

    //damn, I hate impact.
    private float facing;
    private Vector2f vel;
    private int owner;
    private boolean flameOuted;

//    // Angle with the target beyond witch the missile AI will shutdown
//    private final static float overshoot = 45;
//    private final static float searchCone = 45;
//    private final static float damping = 0.1f;
////    private final static float splitSpeedRNG = 200f;
//    private final static float spreadRadius = 250f;
//    private final float RNG;
    //////////////////////
    //  DATA COLLECTING //
    //////////////////////
    public Neutrino_UnstablePhotonAI(MissileAPI missile, ShipAPI launchingShip) {
        this.missile = missile;
        flightSpeed = missile.getMaxSpeed();
        split = !isCrit;
        specId = missile.getProjectileSpecId();
        missile.setAngularVelocity(0);
        switch (specId) {
            case "neutrino_unstable_photon":
//                setTarget(assignCurrentTarget(missile)); // lock on target
                splitWeaponId = "neutrino_unstable_photon1";
                payload = 5;
                splitDelay = MathUtils.getRandomNumberInRange(0.3f, 0.6f);
                splitArc = 8;
                flash = true;
                flashSize = 6;
//                critCount = 1;
                color1 = new Color(227, 40, 26, 200);
                color2 = new Color(227, 40, 26, 200);
                createPhontoVFXPlugin(missile, "neutrino_derp_sub");
                break;
            case "neutrino_split_photon1":
                splitWeaponId = "neutrino_unstable_photon2";
                payload = 8;
                splitDelay = MathUtils.getRandomNumberInRange(0.1f, 0.3f);
                splitArc = 8;
                flash = true;
                flashSize = 6;
                critCount = 2;
                color1 = new Color(227, 40, 26, 200);
                color2 = new Color(227, 40, 26, 200);
                break;
            case "neutrino_split_photon2":
                splitWeaponId = "neutrino_unstable_photon3";
                payload = 2;
                splitDelay = MathUtils.getRandomNumberInRange(0.1f, 0.3f);
                splitArc = 8;
                flash = true;
                flashSize = 4;
//                critCount = 1;
                color1 = new Color(227, 40, 26, 200);
                color2 = new Color(227, 40, 26, 200);
                break;
            case "neutrino_split_final":
                split = false;
                flash = false;
                break;
            case "neutrino_adv_torpedo_payload1":
                splitWeaponId = "neutrino_adv_torpedo_2";
                payload = 6;
                splitDelay = MathUtils.getRandomNumberInRange(0.1f, 0.2f);
                splitArc = 8;
                flash = true;
                flashSize = 5;
                critCount = 3;
                color1 = new Color(86, 86, 255, 20);
                color2 = new Color(0, 0, 255, 30);
                break;
            case "neutrino_adv_torpedo_payload2":
                splitWeaponId = "neutrino_adv_torpedo_3";
                payload = 5;
                splitDelay = MathUtils.getRandomNumberInRange(0.1f, 0.2f);
                splitArc = 8;
                flash = true;
                flashSize = 4;
                color1 = new Color(86, 86, 255, 20);
                color2 = new Color(0, 0, 255, 30);
                break;
            case "neutrino_adv_torpedo_payload3":
                split = false;
                flash = false;
                break;
            default:
        }
        flashInterval.forceIntervalElapsed();
//        guidanceInterval.forceIntervalElapsed();
        missile.setEmpResistance(666);
        missile.setMass(0);
        facing = missile.getFacing();
        vel = new Vector2f(missile.getVelocity());
        owner = missile.getOwner();
        flameOuted = missile.isFizzling();
    }

    //////////////////////
    //   MAIN AI LOOP   //
    //////////////////////
    @Override
    public void advance(float amount) {
//        Missile m = (Missile) missile;
//        m.getActiveLayers().remove(com.fs.starfarer.combat.ooOO.valueOf("FF_INDICATORS_LAYER"));

        if (engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
        }

        //cancelling IF: skip the AI if the game is paused
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
//        //this missile always ACCELERATE
//        missile.giveCommand(ShipCommand.ACCELERATE);
        //crit check
        if (runOnce) {
            final LocalData localData = (LocalData) engine.getCustomData().get(KEY);
            final Set<DamagingProjectileAPI> critSet = localData.critSet;
            isCrit = critSet.contains(missile);
        }
        if (isCrit) {
            split = false;
        }
        if (missile.getOwner() != owner) {
            owner = missile.getOwner();
//            facing = missile.getFacing();
            vel = new Vector2f(missile.getVelocity());
        } else if (missile.isFizzling() != flameOuted) {
            flameOuted = missile.isFizzling();
//            facing = missile.getFacing();
            vel = new Vector2f(missile.getVelocity());
        } else {
            missile.setAngularVelocity(0);
//         missile.setFacing(facing);
            missile.getVelocity().set(vel);
        }
        //
        Vector2f mLoc = missile.getLocation();
        // some VFX
        if (flash) {
//            missile.getSpriteAPI().setAlphaMult(0);
            flashInterval.advance(amount);
            if (flashInterval.intervalElapsed()) {
                Vector2f randomPoint = MathUtils.getRandomPointOnCircumference(mLoc, flashSize);
                CombatEntityAPI anchor = new AnchoredEntity(missile, randomPoint);
//                CombatEntityAPI anchor2 = new SimpleEntity(randomPoint);
                engine.spawnEmpArc(missile.getSource(), mLoc, anchor, anchor, DamageType.ENERGY, 0f, 0f, 50f, null, 2f,
                        color1, color2);
            }
        }

        // this missile will split
        if (split && missile.getElapsed() > splitDelay) {
            releasingPayload(missile);
        }
//
//        //cancelling IF: skip the AI the missile is engineless or fading
//        if (missile.isFading() || missile.isFizzling()) {
////            missile.setAngularVelocity(missile.getAngularVelocity()*0.95f);
//            return;
//        }
//        ////
//        //Current Target Check
//        //this missile only acquire target when firing.
//        if (target == null // unset
//                || (missile.getOwner() == target.getOwner()) // friendly
//                || !Global.getCombatEngine().isEntityInPlay(target)) { // completely removed
//            missile.setAngularVelocity(missile.getAngularVelocity() * 0.1f);
//            return;
//        }
//        //// 
//
//        ////
//        // Target Lead Calculate
//        guidanceInterval.advance(amount);
//        Vector2f tLoc = target.getLocation();
//        float mFacing = missile.getFacing();
//        if (guidanceInterval.intervalElapsed() && tLoc != null) {
//            float dist = MathUtils.getDistance(missile, target);
//            //public static Vector2f getBestInterceptPoint(Vector2f point, float speed,Vector2f tLoc, Vector2f targetVel)
//            lead = AIUtils.getBestInterceptPoint(mLoc, flightSpeed, tLoc, target.getVelocity());
//            //if lead can not be calculated. lead to target directly.
//            if (lead == null) {
//                lead = Vector2f.add(zero, tLoc, lead);
//            }
//            Vector2f.sub(lead, tLoc, lead);
//            lead.scale(0.5f);
//            Vector2f.add(lead, tLoc, lead);
////             implant offset
//            if (runOnce) {
//                offset = MathUtils.getRandomPointInCircle(zero, spreadRadius);
//                runOnce = false;
//            }
//            Vector2f.add(lead, offset, lead);
////            Vector2f currentOffset = new Vector2f(offset);
////            currentOffset.scale(Math.min(Math.max(dist / closeDist, 4), 0.25f));
////            Vector2f.add(lead, currentOffset, lead);
//
//        }
//        // aimAngle = angle deviate from the lead direction
//        float aimAngle = lead == null? 0:MathUtils.getShortestRotation(mFacing, VectorUtils.getAngle(mLoc, lead));
//        float dist = MathUtils.getDistance(mLoc, lead);
//        ////
//
//        if (dist < 200 || aimAngle > overshoot) {
//            setTarget(null);
//            return;
//        }
//
//        ////
//        // Missile Attitude Control
//        if (Math.abs(aimAngle) > 5) {
//            missile.giveCommand(aimAngle < 0 ? ShipCommand.TURN_RIGHT : ShipCommand.TURN_LEFT);
//        }
////         Course correct for missile velocity vector 
//        float MFlightAng = VectorUtils.getAngle(new Vector2f(), missile.getVelocity());
//        float MFlightCC = MathUtils.getShortestRotation(mFacing, MFlightAng);
//        if (Math.abs(MFlightCC) > 5f) {
//            missile.giveCommand(MFlightCC < 0f ? ShipCommand.STRAFE_LEFT : ShipCommand.STRAFE_RIGHT);
//        }
//        // Damp angular velocity if the missile aim is getting close to the targeted angle
////        if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * damping)
////        {
////            missile.setAngularVelocity(aimAngle / damping);
////        } 
        if (missile.getArmingTime() > 0) {
            engine.applyDamage(missile, mLoc, 9999, DamageType.FRAGMENTATION, 0, true, true, missile);
        }
    }

    //////////////////////
    //    TARGETTING    //
    //////////////////////
    //will be called when firing
//    public CombatEntityAPI assignCurrentTarget(MissileAPI missile) {
//        ShipAPI source = missile.getSource();
//        ShipAPI currentTarget = source.getShipTarget();
//
//        // try "lock'n'fire" targeting behavior frist. 
//        // get target form ship,
//        if (currentTarget != null
//                && currentTarget.isAlive()
//                && currentTarget.getOwner() != missile.getOwner()) {
//            //return the ship's target if it's valid
//            return (CombatEntityAPI) currentTarget;
//        } else {
//            // If we don't got any valid target form ship, try "fire at target by clicking on them" behavior.
//            // get nearest target form cursor
//            List<ShipAPI> directTargets = CombatUtils.getShipsWithinRange(source.getMouseTarget(), 50f);
//            if (!directTargets.isEmpty()) {
//                Collections.sort(directTargets, new CollectionUtils.SortEntitiesByDistance(source.getMouseTarget()));
//                for (ShipAPI tmp : directTargets) {
//                    if (tmp.isAlive() && tmp.getOwner() != source.getOwner() && CombatUtils.isVisibleToSide(tmp, tmp.getOwner())) {
//                        return (CombatEntityAPI) tmp;
//                    }
//                }
//            }
//            // Still no target? OK, let's got the closest one in search cone.
//            List<ShipAPI> targetsInArc = CombatUtils.getShipsWithinRange(missile.getLocation(), 3000);
//            if (!targetsInArc.isEmpty()) {
//                Collections.sort(targetsInArc, new CollectionUtils.SortEntitiesByDistance(missile.getLocation()));
//                for (ShipAPI tmp : targetsInArc) {
//                    if (tmp.isAlive() && tmp.getOwner() != source.getOwner() && Math.abs(MathUtils.getShortestRotation(missile.getFacing(), VectorUtils.getAngle(missile.getLocation(), tmp.getLocation()))) < searchCone && CombatUtils.isVisibleToSide(tmp, tmp.getOwner())) {
//                        return (CombatEntityAPI) tmp;
//                    }
//                }
//            }
//        }
//        // well, no target   
//        return null;
//    }
    ////////////////////////
    // RELEASING PAYLOAD  //
    ////////////////////////
    public void releasingPayload(MissileAPI missile) {

        Vector2f mLoc = missile.getLocation();
        ShipAPI mSource = missile.getSource();
        Vector2f mVel = missile.getVelocity();
        float mFacing = missile.getFacing();
        //public CombatEntityAPI spawnProjectile(ShipAPI ship, WeaponAPI weapon, String weaponId, Vector2f point, float angle, Vector2f shipVelocity)
        // releasing payloads!
        int j = 0;
        for (int i = 0; i < payload; i++) {
            Vector2f random1 = MathUtils.getRandomPointOnCircumference(zero, 15);
            float firingAng = MathUtils.clampAngle(mFacing + MathUtils.getRandomNumberInRange(-splitArc * 0.5f, splitArc * 0.5f));
            Vector2f random2 = MathUtils.getPointOnCircumference(zero, MathUtils.getRandomNumberInRange(-flightSpeed * 0.2f, flightSpeed * 0.2f), firingAng);
            CombatEntityAPI entity = engine.spawnProjectile(
                    mSource,
                    missile.getWeapon(),
                    splitWeaponId,
                    Vector2f.add(mLoc, random1, null),
                    firingAng,
                    random2);
            Missile m = (Missile) entity;
            m.advance(0);
            // pass the target data to payloads
//            if (((MissileAPI) entity).getMissileAI() instanceof GuidedMissileAI) {
//                ((GuidedMissileAI) ((MissileAPI) entity).getMissileAI()).setTarget(getTarget());
//            }
            if (j < critCount) {
                j++;
                final LocalData localData = (LocalData) engine.getCustomData().get(KEY);
                final Set<DamagingProjectileAPI> critSet = localData.critSet;
                critSet.add((MissileAPI) entity);
                createPhontoVFXPlugin((MissileAPI) entity, "neutrino_plasma_DECO");
//                CombatEntityAPI anchor = new AnchoredEntity(entity, entity.getLocation());
//                engine.spawnEmpArc(missile.getSource(), mLoc, null, anchor, DamageType.ENERGY, 0f, 0f, 50f, null, 10f,
//                                   new Color(227,40,26,50), new Color(227,40,26,50));
//                engine.spawnEmpArc(missile.getSource(), mLoc, null, anchor, DamageType.ENERGY, 0f, 0f, 50f, null, 10f,
//                                   new Color(227,40,26,50), new Color(227,40,26,50));
//                engine.spawnEmpArc(missile.getSource(), mLoc, null, anchor, DamageType.ENERGY, 0f, 0f, 50f, null, 10f,
//                                   new Color(227,40,26,50), new Color(227,40,26,50));
            }
        }
        engine.applyDamage(missile, mLoc, missile.getMaxHitpoints(), DamageType.OTHER, 0f, false, false, mSource);
    }

//    @Override
//    public CombatEntityAPI getTarget() {
//        return null;
//    }
//
//    @Override
//    public void setTarget(CombatEntityAPI target) {
//    }
}
