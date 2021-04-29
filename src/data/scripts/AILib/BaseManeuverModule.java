// By Deathfly
package data.scripts.AILib;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class BaseManeuverModule {

    private final CombatEntityAPI entity;
    private final float StrafeAccMod;
    private final Vector2f zero = new Vector2f(0, 0);
    // prevent the engines looks twisty.
    public final float angleError = 0.1f;
    public final float speedError = 0.1f;
    public final float locError = 0.1f;

    public BaseManeuverModule(ShipAPI ship) {
        this.entity = ship;
        if (ship.isFighter()) {
            StrafeAccMod = 0.75f;
        } else if (ship.isFrigate()) {
            StrafeAccMod = 1;
        } else if (ship.isDestroyer()) {
            StrafeAccMod = 0.75f;
        } else if (ship.isCruiser()) {
            StrafeAccMod = 0.5f;
        } else if (ship.isCapital()) {
            StrafeAccMod = 0.25f;
        } else {
            StrafeAccMod = 1;
        }
    }

    public BaseManeuverModule(MissileAPI missile) {
        this.entity = missile;
        StrafeAccMod = 1;
    }

    private void giveCommand(ShipCommand Command, Vector2f point, int weaponGroup) {
        if (entity instanceof ShipAPI) {
            ((ShipAPI) entity).giveCommand(Command, point, weaponGroup);
        }
        if (entity instanceof MissileAPI) {
            ((MissileAPI) entity).giveCommand(Command);
        }
    }

    private void giveCommand(ShipCommand Command) {
        if (entity instanceof ShipAPI) {
            ((ShipAPI) entity).giveCommand(Command, null, 0);
        }
        if (entity instanceof MissileAPI) {
            ((MissileAPI) entity).giveCommand(Command);
        }
    }

    private boolean inErrorMargin(float toTest, float error) {
        return (toTest >= -error && toTest <= error);
    }

    public float getMaxSpeed() {
        if (entity instanceof ShipAPI) {
            return ((ShipAPI) entity).getMutableStats().getMaxSpeed().getModifiedValue();
        } else if (entity instanceof MissileAPI) {
            ((MissileAPI) entity).getMaxSpeed();
        }
        return 0;
    }

    public float getMaxTurnRate() {
        if (entity instanceof ShipAPI) {
            return ((ShipAPI) entity).getMutableStats().getMaxTurnRate().getModifiedValue();
        } else if (entity instanceof MissileAPI) {
            ((MissileAPI) entity).getMaxTurnRate();
        }
        return 0;
    }

    public float getAcceleration() {
        if (entity instanceof ShipAPI) {
            return ((ShipAPI) entity).getMutableStats().getAcceleration().getModifiedValue();
        } else if (entity instanceof MissileAPI) {
            ((MissileAPI) entity).getAcceleration();
        }
        return 0;
    }

    public float getStrafeAcceleration() {
        return getAcceleration() * StrafeAccMod;
    }

    public float getTurnAcceleration() {
        if (entity instanceof ShipAPI) {
            return ((ShipAPI) entity).getMutableStats().getTurnAcceleration().getModifiedValue();
        } else if (entity instanceof MissileAPI) {
            ((MissileAPI) entity).getTurnAcceleration();
        }
        return 0;
    }

    public void turnLeft() {
        giveCommand(ShipCommand.TURN_LEFT);
    }

    public void turnRight() {
        giveCommand(ShipCommand.TURN_RIGHT);
    }

    public void accelerate() {
        giveCommand(ShipCommand.ACCELERATE);
    }

    public void deccelerate() {
        giveCommand(ShipCommand.DECELERATE);
    }

    public void accelerateBack() {
        giveCommand(ShipCommand.ACCELERATE_BACKWARDS);
    }

    public void strafeLeft() {
        giveCommand(ShipCommand.STRAFE_LEFT);
    }

    public void strafeRight() {
        giveCommand(ShipCommand.STRAFE_RIGHT);
    }

    // slope should >0 
    private float getIntegralArea(float from, float to, float slope) {
        return (to + from) * 0.5f * ((to - from) / slope);
    }

    private float getOverTurnAndDirToChangeTurnRateTo(float targetTrunRate) {
        targetTrunRate = targetTrunRate > getMaxTurnRate() ? getMaxTurnRate() : targetTrunRate;
        return getMaxTurnRate() == 0 || getTurnAcceleration() == 0 ? Float.MAX_VALUE : getIntegralArea(entity.getAngularVelocity(), targetTrunRate, getTurnAcceleration());
    }

    public CombatEntityAPI getEntity() {
        return entity;
    }

    public void faceTo(float faceTo, float targetTrunRate) {
        float diff = MathUtils.getShortestRotation(entity.getFacing(), faceTo);
        diff -= getOverTurnAndDirToChangeTurnRateTo(targetTrunRate);
        if (inErrorMargin(diff, angleError)) {
        } else if (diff < 0) {
            giveCommand(ShipCommand.TURN_RIGHT);
        } else if (diff > 0) {
            giveCommand(ShipCommand.TURN_LEFT);
        }
    }

    public void faceTo(float faceTo) {
        faceTo(faceTo, 0);
    }

    public void faceTo(Vector2f faceToV2f, Vector2f targetVel) {
        Vector2f relativeLoc = Vector2f.sub(entity.getLocation(), faceToV2f, null);
        float faceTo = VectorUtils.getFacing(relativeLoc);
        float targetTrunRate;
        if (targetVel == null) {
            targetTrunRate = 0;
        } else {
            Vector2f relativeVel = Vector2f.sub(targetVel, entity.getVelocity(), null);
            targetTrunRate = VectorUtils.getAngle(relativeLoc, Vector2f.add(relativeLoc, relativeVel, null));
        }
        faceTo(faceTo, targetTrunRate);
    }

    public void matchVelTo(Vector2f targetVel, boolean canAccBackward) {
        Vector2f velDiff = Vector2f.sub(targetVel, entity.getVelocity(), null);
        VectorUtils.rotate(velDiff, -entity.getFacing(), velDiff);
        if (inErrorMargin(velDiff.x, speedError)) {
        } else if (velDiff.x < 0) {
            if (canAccBackward) {
                giveCommand(ShipCommand.ACCELERATE_BACKWARDS);
            } else {
                giveCommand(ShipCommand.DECELERATE);
            }
        } else if (velDiff.x > 0) {
            giveCommand(ShipCommand.ACCELERATE);
        }
        if (inErrorMargin(velDiff.y, speedError)) {
        } else if (velDiff.y < 0) {
            giveCommand(ShipCommand.STRAFE_LEFT);
        } else if (velDiff.y > 0) {
            giveCommand(ShipCommand.STRAFE_RIGHT);
        }
    }

    public void strafeToLoc(Vector2f targetLoc, Vector2f targetVel, boolean canAccBackward) {
        Vector2f velDiff = Vector2f.sub(targetVel, entity.getVelocity(), null);
        VectorUtils.rotate(velDiff, -entity.getFacing(), velDiff);
        Vector2f currVel = new Vector2f(entity.getVelocity());
        VectorUtils.rotate(currVel, -entity.getFacing(), currVel);
        Vector2f locDiff = Vector2f.sub(targetLoc, entity.getLocation(), null);
        VectorUtils.rotate(locDiff, -entity.getFacing(), locDiff);
        if (Math.abs(getIntegralArea(currVel.x, currVel.x + velDiff.x, getAcceleration())) >= Math.abs(locDiff.x)) {
            if (inErrorMargin(velDiff.x, speedError)) {
            } else if (velDiff.x < 0) {
                if (canAccBackward) {
                    giveCommand(ShipCommand.ACCELERATE_BACKWARDS);
                } else {
                    giveCommand(ShipCommand.DECELERATE);
                }
            } else if (velDiff.x > 0) {
                giveCommand(ShipCommand.ACCELERATE);
            }
        } else {
            if (inErrorMargin(locDiff.x, speedError)) {
            } else if (locDiff.x < 0) {
                if (canAccBackward) {
                    giveCommand(ShipCommand.ACCELERATE_BACKWARDS);
                } else {
                    giveCommand(ShipCommand.DECELERATE);
                }
            } else if (locDiff.x > 0) {
                giveCommand(ShipCommand.ACCELERATE);
            }
        }
        if (Math.abs(getIntegralArea(currVel.y, currVel.y + velDiff.y, getAcceleration())) >= Math.abs(locDiff.y)) {
            if (inErrorMargin(velDiff.y, locError)) {
            } else if (velDiff.y < 0) {
                giveCommand(ShipCommand.STRAFE_LEFT);
            } else if (velDiff.y > 0) {
                giveCommand(ShipCommand.STRAFE_RIGHT);
            }
        } else {
            if (inErrorMargin(locDiff.y, locError)) {
            } else if (locDiff.y < 0) {
                giveCommand(ShipCommand.STRAFE_LEFT);
            } else if (locDiff.y > 0) {
                giveCommand(ShipCommand.STRAFE_RIGHT);
            }
        }
    }

    public void strafeToLoc(Vector2f targetLoc, Vector2f targetVel) {
        strafeToLoc(targetLoc, targetVel, false);
    }

    public void strafeToLoc(Vector2f targetLoc, boolean stopAtLoc) {
        if (stopAtLoc) {
            strafeToLoc(targetLoc, zero);
        } else {
            Vector2f locDiff = Vector2f.sub(targetLoc, entity.getLocation(), null);
            VectorUtils.rotate(locDiff, -entity.getFacing(), locDiff);
            if (locDiff.x < 0) {
                giveCommand(ShipCommand.DECELERATE);
            } else if (locDiff.x > 0) {
                giveCommand(ShipCommand.ACCELERATE);
            }
            if (locDiff.y < 0) {
                giveCommand(ShipCommand.STRAFE_LEFT);
            } else if (locDiff.y > 0) {
                giveCommand(ShipCommand.STRAFE_RIGHT);
            }
        }
    }

    public void strafeToLoc(Vector2f targetLoc) {
        strafeToLoc(targetLoc, false);
    }
}
