//by Deathfly
//Base on Tartiflette's SCY_laserTorpedoAI and take some refer form MShadowy's WindingMissileAI and Cycerin's SeekerAI. There are too many modifications had been done so it may hardly recognised but you still have my credits.
package data.scripts.AIs.Missiles;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.Missile;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;

import org.lwjgl.util.vector.Vector2f;

public final class Neutrino_AdvancedTorpedoAI implements MissileAIPlugin, GuidedMissileAI {

    // initialization variable 
    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private CombatEntityAPI target;
    private final Vector2f zero = new Vector2f(0f, 0f);
    private Vector2f evadingOffset = new Vector2f(0f, 0f);
    private Vector2f aimLeadOffset = new Vector2f(0f, 0f);
    private boolean chargeUp = false;
    private boolean shouldFire = false;
    private float chargeUoTimer = 0f;
    private final float flightSpeed;
    private final IntervalUtil OffsetsRCInterval;
    private final float guidefactor;
    private final static float damping = 0.1f;
    private float subLeadingMod = 0f;
    private float omniThrustTimer = 0f;
    private float overshootTimer = 0f;
//    private float accuracyCompensate = 0f;
    private boolean overshootFlag = false;
    private boolean launching = true;
    private boolean forceEvade = true;
//    private float speedCompensate = 0f;
    private Vector2f lead;
    private float dist;
    // adjustable DATA
    // MIRV behaviour
    private final float minTimeToSplit = 1.5f; // like the same thing in .proj file
    private final float maxArmedTime = 5f;
    private float fireTimeRandomer = 1.5f;
    private float fireTimer = fireTimeRandomer;
    private final IntervalUtil flashInterval = new IntervalUtil(0.1F, 0.1F); //for charge up VFX
    private final IntervalUtil firingCheckInterval = new IntervalUtil(0.05F, 0.1F);
    private final Color coler1 = new Color(86, 86, 255, 20);
    private final Color coler2 = new Color(0, 0, 255, 30);

    // all distances below WILL consider target's radius.
    private final float closeRange = 700; // distance to target under this will make missile aim for payload release.
    private final float chargeUpRange = 600;
    private final float releaseRange = 500; // distance to target under this will make missile fire if it aimed to target
//    private final float releaseRangeHullmodBonus = 20;// 
    private final int backOffRange = 250; //distance to target under this will make missile try to back off form target and lock the strafe control.

    private float payloadSpeed = 400; // use for aim ONLY, will NOT change the payload speed.
    private final float minAimOffset = 35f; // missile will aim to a Random Point On Circumference which radius equal to this instead of the center of target.

    private final float leadingMod = 0.2f; // 0 means missile will aim to target leading point.

    private final int payload1 = 1; //payload amount.adjustable
//    private final int payload2 = 0; //payload amount.adjustable
//    private final int payload3 = 0; //payload amount.adjustable

    // Targeting behaviour
    private final boolean FAF = true; // Fire and Forgot behaviour. set to false will lead a remote control behaviour.(not very good for MIRV)
    private final IntervalUtil guidanceInterval = new IntervalUtil(0.1F, 0.1F);
    private float targetPhasedTimer = 0;
    private final float targetPhasedDelay = 0.5f;
    // Maneuver behaviour 
    private final float launchingFloatTime = 0.5f;
    private final float forceEvadeTime = 1f;
    private final float overshoot = 60f; //Angle which the target beyond witch the missile will first turn around before accelerating again
    private final float overshootLag = 1f;
    private boolean omniThrust = false; // if this one is true, missile will always accelerate to target. regardless it's facing.
    private final boolean alwaysAcc = false; // as it's name.
    private final float omniThrustAccLimit = 0.5f;// Limit omni thrust's acc
    private float omniThrustMaxSpeedLimit = 1f;// Limit omni thrust's MaxSpeed

    //////////////////////
    //  DATA COLLECTING //
    //////////////////////
    public Neutrino_AdvancedTorpedoAI(MissileAPI missile, ShipAPI launchingShip) {
        this.missile = missile;
        missile.setEmpResistance(missile.getEmpResistance() + 1);
        flightSpeed = missile.getMaxSpeed();
        payloadSpeed = launchingShip.getMutableStats().getMissileMaxSpeedBonus().computeEffective(payloadSpeed);
        guidefactor = 1f + (0.5f * launchingShip.getMutableStats().getMissileGuidance().getModifiedValue());
        omniThrustMaxSpeedLimit = 1 - omniThrustMaxSpeedLimit;
        omniThrustMaxSpeedLimit *= flightSpeed;
        fireTimeRandomer *= 1 / launchingShip.getMutableStats().getMissileMaxSpeedBonus().computeEffective(1);
//        float rangeCompensate = fireTimeRandomer * 0.5f * flightSpeed * (launchingShip.getMutableStats().getMissileMaxSpeedBonus().computeEffective(1f) - 1f);
//        rangeCompensate += releaseRangeHullmodBonus * launchingShip.getMutableStats().getMissileGuidance().getModifiedValue();
//        releaseRange += rangeCompensate;
//        chargeUpRange += rangeCompensate;
//        closeRange += rangeCompensate;
//        speedCompensate = (payloadSpeed * rangeCompensate) / (releaseRange * fireTimeRandomer * 0.5f);
//        accuracyCompensate = (releaseRange - rangeCompensate) / releaseRange;
        // adjustable DATA
        OffsetsRCInterval = new IntervalUtil(1f, 2f); // missile "evading waving" interval
        if (missile.getSource().getVariant().getHullMods().contains("neutrino_sigmaupgrade")) {
            omniThrust = true;
        }
        // adjustable DATA END

        OffsetsRCInterval.forceIntervalElapsed(); //NPE protect
        guidanceInterval.forceIntervalElapsed();
        setTarget(assignCurrentTarget(missile));
        this.lead = new Vector2f();
        this.dist = Float.MAX_VALUE;
        overshootTimer = overshootLag;
//        if (Math.random()>0.5){
//            Missile m = (Missile) missile;
//            m.getActiveLayers().remove(com.fs.starfarer.combat.ooOO.valueOf("ABOVE_SHIPS_LAYER"));
//            m.getActiveLayers().add(com.fs.starfarer.combat.ooOO.valueOf("BELOW_SHIPS_LAYER"));
// 
//        }
    }

    //////////////////////
    //   MAIN AI LOOP   //
    //////////////////////
    @Override
    public void advance(float amount) {

        if (engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
        }

        ////
        //cancelling IF: skip the AI if the game is paused, the missile is engineless or fading
        if (Global.getCombatEngine().isPaused() || missile.isFading() || missile.isFizzling()) {
            return;
        }
        float elapsed = missile.getElapsed();
        ////
        launching = elapsed < launchingFloatTime;
        forceEvade = elapsed < forceEvadeTime;
        //once it fired up, it continuity charge up.
        if (chargeUp) {
            releasingPayload(missile, amount);
        }
        shouldFire = false;
        ////
        //Current Target Check
        if (!FAF) {
            setTarget(assignCurrentTarget(missile));
        } else {
            if (target instanceof ShipAPI && ((ShipAPI) target).isPhased()) {
                targetPhasedTimer += amount;
            } else {
                targetPhasedTimer = 0;
            }
            if (target == null // unset
                    || (target instanceof ShipAPI && !((ShipAPI) target).isAlive()) // dead
                    //                    || (target instanceof ShipAPI && ((ShipAPI) target).isDrone()) // is drone
                    || (missile.getOwner() == target.getOwner()) // friendly
                    || !Global.getCombatEngine().isEntityInPlay(target) // completely removed
                    || targetPhasedTimer > targetPhasedDelay) { // phased out
                setTarget(reAssignTarget(missile));
                overshootFlag = false;
                overshootTimer = overshootLag;
                OffsetsRCInterval.forceIntervalElapsed();// NPE protect
                guidanceInterval.forceIntervalElapsed();
                fireTimer = fireTimeRandomer;
                targetPhasedTimer = 0;
                return;
            }
        }
        Vector2f mLoc = missile.getLocation();
        Vector2f tLoc = target.getLocation();
        float mFacing = missile.getFacing();
        guidanceInterval.advance(amount);
        if (guidanceInterval.intervalElapsed() && tLoc != null) {

            ////  
            //Offsets calculate(TEST)
            OffsetsRCInterval.advance(amount);

            if (OffsetsRCInterval.intervalElapsed()) {
                evadingOffset = Vector2f.sub(mLoc, tLoc, null);
                evadingOffset = new Vector2f(-evadingOffset.y, evadingOffset.x);
                aimLeadOffset = MathUtils.getRandomPointOnCircumference(zero, minAimOffset);
                evadingOffset.scale(MathUtils.getRandomNumberInRange(-0.3f, 0.3f));
                if (overshootFlag) {
                    overshootTimer = 0f;
                }
                subLeadingMod = MathUtils.getRandomNumberInRange(1 - (leadingMod / guidefactor), 1 + (leadingMod / guidefactor));
            }

            ////
            // Target Lead Calculate 
            //public static Vector2f getBestInterceptPoint(Vector2f point, float speed,Vector2f tLoc, Vector2f targetVel)
            Vector2f aimLead = AIUtils.getBestInterceptPoint(mLoc, payloadSpeed * subLeadingMod, tLoc, target.getVelocity());
            Vector2f flightLead = AIUtils.getBestInterceptPoint(mLoc, flightSpeed * subLeadingMod, tLoc, target.getVelocity());
            //if lead can not be calculated. lead to target directly.
            if (flightLead == null) {
                flightLead = new Vector2f(tLoc);
            }
            if (aimLead == null) {
                aimLead = new Vector2f(tLoc);
            }
            //implant offsets
            Vector2f.add(flightLead, evadingOffset, flightLead);
            Vector2f.add(aimLead, aimLeadOffset, aimLead);

//        float dist = MathUtils.getDistance(missile, target);
            float aimDist = Misc.getDistance(mLoc, aimLead);
            float guideMod = forceEvade ? 1 : Math.min(1f, Math.max(0, (aimDist - closeRange) * 0.002f));
            Vector2f diff = Vector2f.sub(flightLead, aimLead, null);
            diff.scale(guideMod);
            lead = Vector2f.add(diff, aimLead, lead);
            dist = Math.min(aimDist, Misc.getDistance(mLoc, tLoc) - Misc.getTargetingRadius(missile.getLocation(), target, target.getShield() == null ? false : target.getShield().isWithinArc(missile.getLocation())));
//        engine.addSmoothParticle(lead, new Vector2f(0, 0), 5, 1f, 0.5f, Color.red);
        }
        // aimAngle = angle deviate from the lead direction
//        engine.addSmoothParticle(lead, new Vector2f(0, 0), 5, 1f, 0.5f, Color.yellow);
        float aimAngle = lead == null ? 0 : MathUtils.getShortestRotation(mFacing, VectorUtils.getAngle(mLoc, lead));
        ////

        ////
        // Universal Missile Attitude Control
        // 
        int Thrust = 0, Turn = 0, Strafe = 0;
        boolean backOff = dist < backOffRange;
        // Regular Missile Heading Control
        if (aimAngle < 0) {
            Turn = 1;
        }
        if (aimAngle > 0) {
            Turn = -1;
        }
        if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * damping) {
            missile.setAngularVelocity(aimAngle / damping);
        }

        // Regular Missile Course Correct
        float MFlightAng = VectorUtils.getAngle(new Vector2f(), missile.getVelocity());
        float MFlightCC = MathUtils.getShortestRotation(mFacing, MFlightAng);
        if (Math.abs(aimAngle) < 5) {
            if (MFlightCC < -20) {
                Strafe = 1;
            }
            if (MFlightCC > 20) {
                Strafe = -1;
            }
        }

        // Regular Missile ACCELERATE Control
//        if (Math.abs(aimAngle) < overshoot) Thrust = 1;{        
//            // Stop the missile if it getting too close
//            if (backOff) Thrust = -1;
//        }
        //Missile ACCELERATE Control with Evading Maneuver    
        if (Math.abs(aimAngle) < overshoot) {
            overshootTimer = 0f;
            overshootFlag = true;
        } else {
            overshootTimer += amount;
        }
        if (overshootTimer < overshootLag) {
            Thrust = 1;
        }
        if (backOff) {
            Thrust = -1;
        }

        // Omni Thrust Unit. for some carzy things.
        if (omniThrust && !launching) {
            // Omni Thrust acc limiter
            omniThrustTimer += omniThrustAccLimit;
            if (omniThrustTimer >= 1f) {
                omniThrustTimer -= 1f;
                // relative velocity deviation vector calculations
                Vector2f absMVDev = Vector2f.sub((Vector2f) VectorUtils.getDirectionalVector(mLoc, lead).scale(flightSpeed), missile.getVelocity(), null);
                Vector2f relMVDev = VectorUtils.rotate(absMVDev, -mFacing, new Vector2f());
                // closing in maneuver(with max speed limit TEST)
                if (!backOff) {
                    if (relMVDev.x > 2.5f + omniThrustMaxSpeedLimit) {
                        Thrust = 1;
                    }
                    if (relMVDev.x < -(2.5f + omniThrustMaxSpeedLimit)) {
                        Thrust = -1;
                    }
                    if (relMVDev.y > 2.5f + omniThrustMaxSpeedLimit) {
                        Strafe = 1;
                    }
                    if (relMVDev.y < -(2.5f + omniThrustMaxSpeedLimit)) {
                        Strafe = -1;
                    }
                }
                // back off maneuver
                if (backOff) {
                    if (relMVDev.x > 2.5f) {
                        Thrust = -1;
                    }
                    if (relMVDev.x < -2.5f) {
                        Thrust = 1;
                    }
                    if (relMVDev.y < 2.5f) {
                        Strafe = -1;
                    }
                    if (relMVDev.y > -2.5f) {
                        Strafe = 1;
                    }
                }
            }
        }

        if (alwaysAcc) {
            Thrust = 1;
        }

        if (Thrust == 1 && !launching) {
            missile.giveCommand(ShipCommand.ACCELERATE);
        }
        if (Thrust == -1 && !launching) {
            missile.giveCommand(ShipCommand.ACCELERATE_BACKWARDS);
        }
        if (Turn == 1) {
            missile.giveCommand(ShipCommand.TURN_RIGHT);
        }
        if (Turn == -1) {
            missile.giveCommand(ShipCommand.TURN_LEFT);
        }
        if (Strafe == 1 && !launching) {
            missile.giveCommand(ShipCommand.STRAFE_LEFT);
        }
        if (Strafe == -1 && !launching) {
            missile.giveCommand(ShipCommand.STRAFE_RIGHT);
        }

        ////
        ////
        // Payload Release Decide
        // if the missile is close enough and facing to target. release payload
        float absFacingToLead = Math.abs(MathUtils.getShortestRotation(mFacing, VectorUtils.getAngle(mLoc, lead)));
        if (elapsed > minTimeToSplit
                && absFacingToLead < 3f
                && (dist < chargeUpRange || MathUtils.getDistance(mLoc, lead) < chargeUpRange)) {
            chargeUp = true;
            if (dist < releaseRange) {
                firingCheckInterval.advance(amount);
                if (firingCheckInterval.intervalElapsed()) {
                    fireTimer -= firingCheckInterval.getElapsed();
                    shouldFire = fireTimer < MathUtils.getRandomNumberInRange(0, fireTimeRandomer);
                }
            }
        }
        ////
    }

    //////////////////////
    //    TARGETTING    //
    //////////////////////
    //will be called when firing
    public CombatEntityAPI assignCurrentTarget(MissileAPI missile) {
        ShipAPI source = missile.getSource();
        ShipAPI currentTarget = source.getShipTarget();

        // try "lock'n'fire" targeting behavior frist. 
        // get target form ship,
        if (currentTarget != null
                && currentTarget.isAlive()
                && currentTarget.getOwner() != missile.getOwner()) {
            //return the ship's target if it is valid
            return (CombatEntityAPI) currentTarget;
        } else {
            // If we don't got any valid target form ship, try "fire at target by clicking on them" behavior.
            // get nearest target form cursor
            List<ShipAPI> directTargets = CombatUtils.getShipsWithinRange(source.getMouseTarget(), 50f);
            if (!directTargets.isEmpty()) {
                Collections.sort(directTargets, new CollectionUtils.SortEntitiesByDistance(source.getMouseTarget()));
                for (ShipAPI tmp : directTargets) {
                    if (tmp.isAlive() && tmp.getOwner() != source.getOwner()) {
                        return (CombatEntityAPI) tmp;
                    }
                }
            }
            //still no target? OK, let's fake one for the remote control (FAF = false) missile.
            if (!FAF) {
                return new SimpleEntity(source.getMouseTarget());
            }
        }
        return null;
    }

    //will be called if current target is invalid
    public CombatEntityAPI reAssignTarget(MissileAPI missile) {
        ShipAPI newtarget = null;
        ShipAPI source = missile.getSource();
        int side = source.getOwner();

        float searchRange = chargeUp ? missile.getMaxSpeed() * (maxArmedTime - chargeUoTimer) : missile.getMaxSpeed() * 1.5f * (missile.getMaxFlightTime() - missile.getElapsed());
        float distance, closestDistance = Float.MAX_VALUE;

        for (ShipAPI tmp : AIUtils.getNearbyEnemies(missile, searchRange)) {
            float mod = 0f;
            if (!CombatUtils.isVisibleToSide(tmp, side)) {
                continue;
            }
            if (tmp.isFighter()) {
                mod = searchRange;
            }
            if (tmp.isDrone() || tmp.getCollisionClass() == CollisionClass.NONE) {
                mod = 2 * searchRange;
            }
            distance = MathUtils.getDistance(tmp, missile.getLocation()) + mod;
            if (distance < closestDistance) {
                newtarget = tmp;
                closestDistance = distance;
            }
        }
        if (newtarget == null) {
            newtarget = AIUtils.getNearestEnemy(missile);
            if (newtarget != null && !CombatUtils.isVisibleToSide(newtarget, side)) {
                newtarget = null;
            }
        }
        return newtarget;
    }

    ////////////////////////
    // RELEASING PAYLOAD  //
    ////////////////////////
    public void releasingPayload(MissileAPI missile, float amount) {

        Vector2f mLoc = missile.getLocation();
        ShipAPI mSource = missile.getSource();
        chargeUoTimer += amount;
        Global.getCombatEngine().getElapsedInLastFrame();
        Global.getCombatEngine().getCustomData();

        ////
        // charge up flashing FX
        if (chargeUoTimer < maxArmedTime) {
            flashInterval.advance(amount);
            if (flashInterval.intervalElapsed()) {
                Vector2f randomPoint = MathUtils.getRandomPointOnCircumference(mLoc, 5f);
                CombatEntityAPI anchor1 = new AnchoredEntity(missile, randomPoint);
                //            CombatEntityAPI anchor2 = new SimpleEntity(randomPoint);
                engine.spawnEmpArc(mSource, mLoc, anchor1, anchor1, DamageType.ENERGY, 0f, 0f, 50f, null, 2f,
                        coler1, coler2);
                //            engine.spawnEmpArc(mSource, mLoc, anchor2, anchor2, DamageType.ENERGY, 0f, 0f, 50f, null, 2f,
                //                                           new Color(86,86,255,100), new Color(0,0,255,150) );
            }
        } else {
            engine.applyDamage(missile, mLoc, missile.getHitpoints() * 4, DamageType.FRAGMENTATION, 0f, false, false, mSource);
        }

        ////
        //firing Payloads now        
        if (shouldFire) {
            Vector2f mVel = missile.getVelocity();
            float mFacing = missile.getFacing();
            //public CombatEntityAPI spawnProjectile(ShipAPI ship, WeaponAPI weapon, String weaponId, Vector2f point, float angle, Vector2f shipVelocity)
            for (int i = 0; i < payload1; i++) {
                float firingAng = mFacing;
                CombatEntityAPI tmp = engine.spawnProjectile(
                        mSource,
                        missile.getWeapon(),
                        "neutrino_adv_torpedo_1",
                        mLoc,
                        firingAng,
                        zero);
                Missile m = (Missile) tmp;
                m.advance(0);
            }
            ////
            //VFX & SFX
            //void spawnExplosion(Vector2f loc, Vector2f vel, Color color, float size, float maxDuration);
            engine.spawnExplosion(mLoc, mVel, new Color(86, 86, 255, 100), 30, 2f);
            //public void addHitParticle(Vector2f loc, Vector2f vel, float size, float brightness, float duration, Color color)  
            //engine.addHitParticle(mLoc, mVel, 200, 1, 2, new Color(86,86,255,100));
            Global.getSoundPlayer().playSound("neutrino_photon_firing", 0.8f, 0.7f, mLoc, zero);
            ////

            ////
            //leave something and remove something :)
            engine.spawnProjectile(
                    mSource,
                    missile.getWeapon(),
                    "neutrino_adv_torpedo_4",
                    mLoc,
                    mFacing,
                    mVel).setAngularVelocity(MathUtils.getRandomNumberInRange(-30, 30));
            engine.removeEntity(missile);
        }
    }

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target = target;
    }
}
