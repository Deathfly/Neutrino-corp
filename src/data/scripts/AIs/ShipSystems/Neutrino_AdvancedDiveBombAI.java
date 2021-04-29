package data.scripts.AIs.ShipSystems;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.Misc;

import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.MathUtils;

public class Neutrino_AdvancedDiveBombAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipSystemAPI system;
    private ShipwideAIFlags flags;
    private float timer = 5f;
    private boolean isFiring = false;
    private boolean isBombing = false;
    private boolean missed = false;
    private float bombingTime,
            bombingTimer,
            bombingRange,
            firingRange,
            payloadSpeed,
            bombingSpeed,
            missedTimer,
            missedTime;
    private final static float flighSpeedError = 5f;
    private final static float aimingError = 1f;
    private final static float damping = 0.1f;
    private boolean alwaysAcc = false;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.engine = engine;
        this.system = system;
        this.flags = flags;
        switch (ship.getHullSpec().getHullId()) {
            case "neutrino_schwarzgeist":
                payloadSpeed = ship.getMutableStats().getMissileMaxSpeedBonus().computeEffective(450f);
                bombingTime = bombingTimer = 1f;
                bombingRange = 2000f;
                firingRange = 800f;
                bombingSpeed = ship.getMutableStats().getMaxSpeed().getModifiedValue();
                alwaysAcc = true;
                break;
            case "neutrino_drache":
                payloadSpeed = ship.getMutableStats().getMissileMaxSpeedBonus().computeEffective(50f);
                payloadSpeed += ship.getMutableStats().getMaxSpeed().getModifiedValue();
                bombingTime = bombingTimer = 0.5f;
                bombingRange = 2000f;
                firingRange = 400f;
                bombingSpeed = ship.getMutableStats().getMaxSpeed().getModifiedValue();
                alwaysAcc = true;
                break;
            default:
        }
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null) {
            return;
        }
        if (engine.isPaused()) {
            return;
        }
        if (!ship.getShipAI().needsRefit()
                && target != null && target.isAlive() && !target.isHulk()
                && Misc.getDistance(ship.getLocation(), target.getLocation()) < bombingRange) {
            isBombing = true;
        }
        if (!system.isActive() && isBombing) {
            ship.useSystem();
        } else if (system.isActive() && !isBombing) {
            timer -= amount;
            if (timer <= 0) {
                ship.useSystem();
            }
        } else {
            timer = 5f;
        }

        if (isBombing) {
            Vector2f shipLoc = ship.getLocation();
            if (target == null || !target.isAlive() || target.isHulk()) {
                isBombing = false;
            }
            Vector2f targetLoc = target.getLocation();
            Vector2f lead = AIUtils.getBestInterceptPoint(shipLoc, payloadSpeed, targetLoc, target.getVelocity());
            if (lead == null) {
                isBombing = false;
            } else {
                ship.giveCommand(ShipCommand.SELECT_GROUP, lead, 0);
                ship.getMouseTarget().set(new Vector2f(lead));
                float aimAngle = MathUtils.getShortestRotation(ship.getFacing(), VectorUtils.getAngle(shipLoc, lead));
                int Thrust = 0, Turn = 0, Strafe = 0;
                float angularVel = ((CombatEntityAPI) ship).getAngularVelocity();
                float angularAcc = ship.getMutableStats().getTurnAcceleration().getModifiedValue();
                if (angularAcc != 0) {
                    float TTS = Math.abs(angularVel) / angularAcc;
                    if (Math.abs(aimAngle) < aimingError) {
                        if (aimAngle < 0) {
                            Turn = aimAngle + 0.5f * angularVel * TTS < 0 ? 1 : -1;
                        }
                        if (aimAngle > 0) {
                            Turn = aimAngle + 0.5f * angularVel * TTS > 0 ? -1 : 1;
                        }
                    } else {
                        Turn = (int) Math.signum(-aimAngle);
                        if (Math.abs(angularVel) < angularAcc * damping) {
                            Turn = 0;
                            ((CombatEntityAPI) ship).setAngularVelocity(angularVel * damping);
                        }
                    }
                }
                Vector2f absMVDev = Vector2f.sub((Vector2f) VectorUtils.getDirectionalVector(shipLoc, lead).scale(bombingSpeed), ship.getVelocity(), null);
                Vector2f relMVDev = VectorUtils.rotate(absMVDev, -ship.getFacing(), new Vector2f());
                if (relMVDev.y > flighSpeedError) {
                    Strafe = 1;
                }
                if (relMVDev.y < -flighSpeedError) {
                    Strafe = -1;
                }
                if (alwaysAcc) {
                    Thrust = 1;
                } else {
                    if (relMVDev.x > flighSpeedError) {
                        Thrust = 1;
                    }
                    if (relMVDev.x < -flighSpeedError) {
                        Thrust = -1;
                    }
                }
                if (Thrust == 1) {
                    ship.giveCommand(ShipCommand.ACCELERATE, lead, 0);
                }
                if (Thrust == -1) {
                    ship.giveCommand(ShipCommand.ACCELERATE_BACKWARDS, lead, 0);
                }
                if (Turn
                        == 1) {
                    ship.giveCommand(ShipCommand.TURN_RIGHT, lead, 0);
                }
                if (Turn
                        == -1) {
                    ship.giveCommand(ShipCommand.TURN_LEFT, lead, 0);
                }
                if (Strafe == 1) {
                    ship.giveCommand(ShipCommand.STRAFE_LEFT, lead, 0);
                }
                if (Strafe == -1) {
                    ship.giveCommand(ShipCommand.STRAFE_RIGHT, lead, 0);
                }
                if (Math.abs(aimAngle) < aimingError && Math.abs(relMVDev.y) < flighSpeedError
                        && Misc.getDistance(shipLoc, targetLoc) - Misc.getTargetingRadius(shipLoc, target, false) <= firingRange) {
                    isFiring = true;
                }
                if (isFiring) {
//                    ship.giveCommand(ShipCommand.SELECT_GROUP, lead, 0);
//                    ship.giveCommand(ShipCommand.FIRE, lead, 0);
                    bombingTimer -= amount;
                    if (bombingTimer < 0) {
                        bombingTimer = bombingTime;
                        isBombing = false;
                        isFiring = false;
                        ship.getShipAI().forceCircumstanceEvaluation();
                    }
                }
            }
        }
    }
}
