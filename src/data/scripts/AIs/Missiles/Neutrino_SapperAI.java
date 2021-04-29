//by Deathfly
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
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.entities.Missile;
import java.util.Collections;
import java.util.List;
import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public final class Neutrino_SapperAI implements MissileAIPlugin, GuidedMissileAI {

    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private CombatEntityAPI target;
    private Vector2f lead;
    private final Vector2f zero = new Vector2f(0f, 0f);
    private final float guidefactor = 0.7f;
    private final float flightSpeed;
    // Angle with the target beyond witch the missile AI will shutdown
    private final float overshoot = 45;
    private final float searchCone = 25;
    private final float damping = 0.1f;
    private final IntervalUtil guidanceInterval = new IntervalUtil(0.1F, 0.1F);
    private float targetPhasedTimer = 0;
    private final float targetPhasedDelay = 0.5f;

    //////////////////////
    //  DATA COLLECTING //
    //////////////////////
    public Neutrino_SapperAI(MissileAPI missile, ShipAPI launchingShip) {
        this.missile = missile;
        flightSpeed = missile.getMaxSpeed();
        setTarget(assignCurrentTarget(missile));
        guidanceInterval.forceIntervalElapsed();
        missile.setEmpResistance(666);
        missile.setMass(0);
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

        //this missile always ACCELERATE
        missile.giveCommand(ShipCommand.ACCELERATE);

        ////
        //Current Target Check
        if (target instanceof ShipAPI && ((ShipAPI) target).isPhased()) {
            targetPhasedTimer += amount;
        } else {
            targetPhasedTimer = 0;
        }
        if (target == null // unset
                || (missile.getOwner() == target.getOwner()) // friendly
                || (target instanceof ShipAPI && !((ShipAPI) target).isAlive()) // dead
                || !Global.getCombatEngine().isEntityInPlay(target) // completely removed
                || targetPhasedTimer > targetPhasedDelay) { // phased out
            missile.setAngularVelocity(missile.getAngularVelocity() * 0.99f);
            target = null;
            return;
        }
        //// 

        ////
        // Target Lead Calculate
        Vector2f mLoc = missile.getLocation();
        Vector2f tLoc = target.getLocation();
        guidanceInterval.advance(amount);
        if (guidanceInterval.intervalElapsed() && tLoc != null) {

            //public static Vector2f getBestInterceptPoint(Vector2f point, float speed,Vector2f tLoc, Vector2f targetVel)
            lead = AIUtils.getBestInterceptPoint(mLoc, flightSpeed, tLoc, target.getVelocity());
            //if lead can not be calculated. lead to target directly.
            if (lead == null) {
                Vector2f.add(zero, tLoc, lead);
            }
            // implant offset.
            Vector2f leadingOffset = MathUtils.getRandomPointOnCircumference(zero, Math.max(0.25f * target.getCollisionRadius(), 1));
            if (leadingOffset != null) {
                if (lead == null) {
                    return;
                }
                Vector2f.add(lead, leadingOffset, lead);
            }
            Vector2f.sub(lead, tLoc, lead);
            lead.scale(guidefactor);
            Vector2f.add(lead, tLoc, lead);
        }
        // aimAngle = angle deviate from the lead direction
        float mFacing = missile.getFacing();
        float aimAngle = lead == null ? 0 : MathUtils.getShortestRotation(mFacing, VectorUtils.getAngle(mLoc, lead));
        ////
        if (aimAngle > overshoot) {
            setTarget(null);
            return;
        }

        ////
        // Missile Attitude Control
        missile.giveCommand(aimAngle < 0 ? ShipCommand.TURN_RIGHT : ShipCommand.TURN_LEFT);
        // Course correct for missile velocity vector 
        float MFlightAng = VectorUtils.getAngle(new Vector2f(), missile.getVelocity());
        float MFlightCC = MathUtils.getShortestRotation(mFacing, MFlightAng);
        if (Math.abs(MFlightCC) > 10f) {
            missile.giveCommand(MFlightCC < 0f ? ShipCommand.STRAFE_LEFT : ShipCommand.STRAFE_RIGHT);
        }
        // Damp angular velocity if the missile aim is getting close to the targeted angle
        if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * damping) {
            missile.setAngularVelocity(aimAngle / damping);
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
            // Still no target? OK, let's got the closest one in search cone.
            List<ShipAPI> targetsInArc = CombatUtils.getShipsWithinRange(missile.getLocation(), missile.getMaxFlightTime() * missile.getMaxFlightTime() * 4);
            if (!targetsInArc.isEmpty()) {
                Collections.sort(targetsInArc, new CollectionUtils.SortEntitiesByDistance(missile.getLocation()));
                for (ShipAPI tmp : targetsInArc) {
                    if (tmp.isAlive() && tmp.getOwner() != source.getOwner() && Math.abs(MathUtils.getShortestRotation(missile.getFacing(), VectorUtils.getAngle(missile.getLocation(), tmp.getLocation()))) < searchCone) {
                        return (CombatEntityAPI) tmp;
                    }
                }
            }
        }
        // well, no target. return null.
        return null;
    }

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        if (target instanceof MissileAPI && ((MissileAPI) target).isFlare()) {

        } else {
            this.target = target;
        }
    }
}
