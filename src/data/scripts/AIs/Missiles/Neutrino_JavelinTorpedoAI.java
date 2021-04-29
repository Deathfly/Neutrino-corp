// by Deathfly
package data.scripts.AIs.Missiles;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;

import java.util.Collections;
import java.util.List;

import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;

import org.lwjgl.util.vector.Vector2f;

public final class Neutrino_JavelinTorpedoAI implements MissileAIPlugin, GuidedMissileAI {

    //initialization variable
    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private CombatEntityAPI target;
    private Vector2f lead;
    private final Vector2f zero = new Vector2f(0f, 0f);
    private Vector2f evadingOffset = new Vector2f(0f, 0f);
    private Vector2f leadingOffset = new Vector2f(0f, 0f);
    private final float flightSpeed;
    private final IntervalUtil evadingInterval;
    private final float guidefactor;
    private float omniThrustTimer = 0f;
    private boolean acc;
    private float overshootTimer = 0;
    private boolean overshootFlag = false;
    private float subLeadingMod = 0f;
    private final IntervalUtil guidanceInterval = new IntervalUtil(0.1F, 0.1F);

    //adjustable DATA
    private final int EMParcResistance = 3;
    private float targetPhasedTimer = 0;
    private final float targetPhasedDelay = 0.5f;
    // Distance under witch the missile cease to lead the target aim directly for it
    private final float bestLeadingRange = 700;
    // Angle with the target beyond witch the missile will first turn around before accelerating again
    private final float overshoot = 30f;
    private final float overshootLag = 0.5f;
    private final float damping = 0.1f;
    private final boolean FAF = true;
    private boolean omniThrust = false;
    private final boolean alwaysAcc = false;
    private final float omniThrustAccLimit = 0.15f;// Limit omni thrust's acc
    private float omniThrustMaxSpeedLimit = 1f;// Limit omni thrust's MaxSpeed
    private final float leadingMod = 0.8f;

    //////////////////////
    //  DATA COLLECTING //
    //////////////////////
    public Neutrino_JavelinTorpedoAI(MissileAPI missile, ShipAPI launchingShip) {
        this.missile = missile;
        flightSpeed = missile.getMaxSpeed();
        omniThrustMaxSpeedLimit = 1 - omniThrustMaxSpeedLimit;
        omniThrustMaxSpeedLimit *= flightSpeed;
        guidefactor = 1f + launchingShip.getMutableStats().getMissileGuidance().getModifiedValue();
        evadingInterval = new IntervalUtil(1f, 2f);
        setTarget(assignCurrentTarget(missile));
        evadingInterval.forceIntervalElapsed(); //NPE protect
        guidanceInterval.forceIntervalElapsed();
        this.lead = new Vector2f();
        missile.setEmpResistance(EMParcResistance);
        overshootTimer = overshootLag;
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
        ////

        ////
        //Current Target Check
        if (!FAF) {
            setTarget(assignCurrentTarget(missile));
        } else {
            if (target != null && target.getCollisionClass() == CollisionClass.NONE) {
                targetPhasedTimer += amount;
            } else {
                targetPhasedTimer = 0;
            }
            if (target == null // unset
                    || (target instanceof ShipAPI && !((ShipAPI) target).isAlive()) // dead
                    || (target instanceof ShipAPI && ((ShipAPI) target).isDrone()) // is drone
                    || (missile.getOwner() == target.getOwner()) // friendly
                    || !Global.getCombatEngine().isEntityInPlay(target) // completely removed
                    || targetPhasedTimer > targetPhasedDelay) { // phased out
                setTarget(reAssignTarget(missile));
                evadingInterval.forceIntervalElapsed();// NPE protect
                guidanceInterval.forceIntervalElapsed();
                overshootFlag = false;
                overshootTimer = overshootLag;
                targetPhasedTimer = 0;
                return;
            }
        }
        ////
        Vector2f mLoc = missile.getLocation();
        Vector2f tLoc = target.getLocation();
        float mFacing = missile.getFacing();
        guidanceInterval.advance(amount);
        if (guidanceInterval.intervalElapsed() && tLoc != null) {

            ////
            //Evading Maneuver(TEST)
            evadingInterval.advance(guidanceInterval.getElapsed());
            float dist = MathUtils.getDistance(missile, target);

            if (evadingInterval.intervalElapsed()) {
                float targetR = target.getCollisionRadius();
                leadingOffset = MathUtils.getRandomPointOnCircumference(zero, Math.max(0.25f * targetR, 1));
                evadingOffset = Vector2f.sub(mLoc, tLoc, null);
                evadingOffset = new Vector2f(-evadingOffset.y, evadingOffset.x);
                evadingOffset.scale(MathUtils.getRandomNumberInRange(-0.2f, 0.2f));
                if (overshootFlag) {
                    overshootTimer = -1.5f;
                }
                subLeadingMod = MathUtils.getRandomNumberInRange(leadingMod, 2f - leadingMod);
            }

            ////
            ////
            // Target Lead Calculate (whit Evading Maneuver)
            //public static Vector2f getBestInterceptPoint(Vector2f point, float speed,Vector2f tLoc, Vector2f targetVel)
            lead = AIUtils.getBestInterceptPoint(mLoc, flightSpeed * subLeadingMod, tLoc, target.getVelocity());
            //if lead can not be calculated. lead to target directly.
            if (lead == null) {
                lead = Vector2f.add(zero, tLoc, lead);
            }
            // dynamic calculate lead. the closer the better.
            float guideMod = Math.min(0.3f, guidefactor * bestLeadingRange / (dist + 1));
            Vector2f.sub(lead, tLoc, lead);
            lead.scale(guideMod);
            Vector2f.add(lead, tLoc, lead);
            //implant offsets 
            if (dist >= bestLeadingRange) {
                if (evadingOffset != null) {
                    Vector2f.add(lead, evadingOffset, lead);
                }
            } else {
                if (leadingOffset != null) {
                    Vector2f.add(lead, leadingOffset, lead);
                }
            }
            if (dist > 50) {
                missile.setArmingTime(0.2f);
            } else {
                missile.setArmingTime(0);
            }
//        engine.addSmoothParticle(lead, new Vector2f(0, 0), 5, 1f, 0.5f, Color.red);
        }
        // aimAngle = angle deviate from the lead direction
//        engine.addSmoothParticle(lead, new Vector2f(0, 0), 15, 1f, 0.5f, Color.yellow);
        float aimAngle = lead == null ? 0 : MathUtils.getShortestRotation(mFacing, VectorUtils.getAngle(mLoc, lead));
        ////

        ////
        // Universal Missile Attitude Control (for test purpose ONLY）
        // 
        if (missile.getSource().getVariant().getHullMods().contains("neutrino_sigmaupgrade")) {
            omniThrust = true;
        }
        int Thrust = 0, Turn = 0, Strafe = 0;
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
//        if (Math.abs(aimAngle) < overshoot) Thrust = 1;
        //Missile ACCELERATE Control with Evading Maneuver
        if (Math.abs(aimAngle) < overshoot) {
            overshootFlag = true;
            overshootTimer = 0f;
        }

        if (overshootFlag && Math.abs(aimAngle) > 90) {
            overshootTimer = 0;
            overshootFlag = false;
        } else {
            overshootTimer += amount;
        }
        if (overshootTimer < overshootLag) {
            Thrust = 1;
        }

        // Resemble vanilla Missile ACCELERATE behaviour
        if (Math.abs(aimAngle) < overshoot) {
            acc = true;
        }

        // Omni Thrust Unit. for some carzy things.
        if (omniThrust) {
            omniThrustTimer += omniThrustAccLimit;
            if (omniThrustTimer >= 1f) {
                omniThrustTimer -= 1f;
                // relative velocity deviation vector calculations
                Vector2f absMVDev = Vector2f.sub((Vector2f) VectorUtils.getDirectionalVector(mLoc, lead).scale(flightSpeed), missile.getVelocity(), null);
                Vector2f relMVDev = VectorUtils.rotate(absMVDev, -mFacing, new Vector2f());
                // closing in maneuver(with max speed limit TEST)

                if (relMVDev.x > 2.5f) {
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
        }
        if (alwaysAcc) {
            Thrust = 1;
        }
        if (acc && !omniThrust) {
            Thrust = 1;
        }

        if (Thrust == 1) {
            missile.giveCommand(ShipCommand.ACCELERATE);
        }
        if (Thrust == -1) {
            missile.giveCommand(ShipCommand.ACCELERATE_BACKWARDS);
        }
        if (Turn == 1) {
            missile.giveCommand(ShipCommand.TURN_RIGHT);
        }
        if (Turn == -1) {
            missile.giveCommand(ShipCommand.TURN_LEFT);
        }
        if (Strafe == 1) {
            missile.giveCommand(ShipCommand.STRAFE_LEFT);
        }
        if (Strafe == -1) {
            missile.giveCommand(ShipCommand.STRAFE_RIGHT);
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
            //return the ship's target if it's valid
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
        float searchRange = missile.getMaxSpeed() * 2 * (missile.getMaxFlightTime() - missile.getElapsed());
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

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target = target;
    }
}
