//by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.entities.Missile;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.util.vector.Vector2f;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;

;

public class NeutDisruptorEffect implements BeamEffectPlugin {

    private final IntervalUtil disruptInterval = new IntervalUtil(0.05F, 0.05F);
    private final float disruptChance = 0.15f;
//    private final float pushForcemax = 2f;
//    private final float pushForcemin = 1f;
    private final float minTurn = 10f;
    private final float slowRate = 0.95f;
    private float timer = 0f;
    private final float opeRangeSeq = 302500f;//Seq of 

    public NeutDisruptorEffect() {
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        if (engine.isPaused()) {
            return;
        }

        CombatEntityAPI target = beam.getDamageTarget();
        ShipAPI source = beam.getSource();
        //check if the beam fired up and hit missile?
        if (beam.getBrightness() >= 0.5f && target != null && target instanceof MissileAPI) {
            ////
            //get some data for the disrupted missile
            MissileAPI hitMissile = (MissileAPI) target;
            Vector2f beamV2F = Vector2f.sub(beam.getTo(), beam.getFrom(), null);
            float distSeq = beamV2F.lengthSquared();
            if (distSeq == 0) {
                distSeq = 1;
            }
            if (Math.random() > opeRangeSeq / distSeq) {
                return;
            }
            float missileTurnAcc = hitMissile.getTurnAcceleration();
            boolean noguided = false;
            if (missileTurnAcc == 0 || hitMissile.isFading()) {
                noguided = true;
            }
            float TurnAcc = Math.max(missileTurnAcc, minTurn);
            float MaxTurn = Math.max((hitMissile.getMaxTurnRate()), minTurn);
            float AngularV = target.getAngularVelocity();
            Vector2f targetVel = target.getVelocity();
            float missileAimAngle = MathUtils.getShortestRotation(VectorUtils.getFacing(targetVel), VectorUtils.getAngle(target.getLocation(), source.getLocation()));
            ////

            ////
            //slow target missile 
            targetVel.scale(slowRate);
            ////

            ////
            //push missile aside (comment out for now)
//            float puchAngle = MathUtils.clampAngle(VectorUtils.getAngle(beam.getFrom(),beam.getTo()) + Math.copySign(MathUtils.getRandomNumberInRange(0f, 90f) , missileAimAngle));
//            CombatUtils.applyForce(target, puchAngle, MathUtils.getRandomNumberInRange(pushForcemin,pushForcemax));
            ////
            ////
            //kick the missile off course a bit(for the customized missile AI and some slow turning missile)
            if (!noguided && Math.abs(missileAimAngle) < 7f) {
                target.setFacing(MathUtils.clampAngle(target.getFacing() + Math.copySign(1f, -missileAimAngle)));
            }
            ////

            ////
            //turn the missile away form beam, with some random factor if missile off coursed enough
            //oh, and do not let it spin faster than it can		
            float missiletobeamAng = MathUtils.getShortestRotation(target.getFacing(), VectorUtils.getAngle(beam.getTo(), beam.getFrom()));
            if (MathUtils.getRandomNumberInRange(30f, 120f) - Math.abs(missiletobeamAng) > 0f) {
                target.setAngularVelocity(Math.copySign(Math.min(Math.abs(AngularV) + TurnAcc * amount, MaxTurn), -missileAimAngle));
                //for the customized missile AI
                hitMissile.giveCommand(ShipCommand.ACCELERATE);
                hitMissile.giveCommand(-missileAimAngle < 0 ? ShipCommand.TURN_RIGHT : ShipCommand.TURN_LEFT);
            }
            ////

            ////
            //add some malfunction chance effect
            disruptInterval.advance(beam.getDamage().getDpsDuration());
            timer += beam.getDamage().getDpsDuration();
            float finalDisruptChance = (timer / beam.getWeapon().getDerivedStats().getBurstFireDuration()) * disruptChance;
            if (disruptInterval.intervalElapsed()) {
                if (Math.random() <= finalDisruptChance) {
                    if (hitMissile.getEmpResistance() == 0) {
                        engine.applyDamage(target,
                                target.getLocation(),
                                hitMissile.getMaxHitpoints() * 0.25f,
                                DamageType.FRAGMENTATION,
                                0f,
                                false, false, source);
                        // fuse malfunction
                        if (Math.random() > 0.5) {
                            hitMissile.setArmedWhileFizzling(false);
                            hitMissile.setArmingTime(99999);
                        } else { // or thruster malfunction
                            hitMissile.flameOut();
                        }
                    } else {
                        hitMissile.decrEMPResistance();
                    }
                }
            }
        }
    }
}
