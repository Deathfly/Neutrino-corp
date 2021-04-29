package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

/**
 * An implementation of {@code CombatEntityAPI} that follows and rotates with
 * another anchoring {@code CombatEntityAPI} and moving relatively. (All for the
 * beams! oh, I used to love beams until I hate them)
 *
 * @author Deathfly
 */
public class AnchoredMovingEntity implements CombatEntityAPI {

    protected Vector2f relativeVelocity, location, relativeLocation;
    protected CombatEntityAPI anchor;
    protected float timeStamp, relativeDistance, relativeAngle, angle;
    protected float TET = Global.getCombatEngine().getTotalElapsedTime(false);

    public AnchoredMovingEntity(CombatEntityAPI anchor, Vector2f location, Vector2f relativeVelocity) {
        reanchor(anchor, location, relativeVelocity);
    }

    public void reanchor(CombatEntityAPI newAnchor, Vector2f newLocation, Vector2f newRelativeVelocity) {
        relativeDistance = MathUtils.getDistance(newAnchor.getLocation(), newLocation);
        angle = newAnchor.getFacing();
        relativeAngle = MathUtils.clampAngle(VectorUtils.getAngle(newAnchor.getLocation(), newLocation) - newAnchor.getFacing());
        anchor = newAnchor;
        this.relativeVelocity = newRelativeVelocity;
        timeStamp = TET;
    }

    public void rotateRelativeVelocity() {
        if (anchor.getFacing() != angle) {
            VectorUtils.rotate(relativeVelocity, MathUtils.getShortestRotation(angle, anchor.getFacing()), relativeVelocity);
            angle = anchor.getFacing();
        }
    }

    public void setLocation(Vector2f location) {
        reanchor(this.anchor, location, this.relativeVelocity);
    }

    @Override
    public Vector2f getLocation() {
        Vector2f toReturn = new Vector2f(anchor.getLocation());
        if (timeStamp != TET) {
            rotateRelativeVelocity();
            Vector2f variation = new Vector2f(relativeVelocity);
            variation.scale(TET - timeStamp);
            Vector2f.add(toReturn, variation, toReturn);
        }
        return toReturn;
    }

    public void setVelocity(Vector2f velocity) {
        reanchor(this.anchor, this.location, velocity);
    }

    @Override
    public Vector2f getVelocity() {
        rotateRelativeVelocity();
        return Vector2f.add(anchor.getVelocity(), relativeVelocity, null);
    }

    @Override
    public float getFacing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFacing(float facing) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getAngularVelocity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAngularVelocity(float angVel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getOwner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOwner(int owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getCollisionRadius() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CollisionClass getCollisionClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCollisionClass(CollisionClass collisionClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getMass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMass(float mass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BoundsAPI getExactBounds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ShieldAPI getShield() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getHullLevel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getHitpoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getMaxHitpoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCollisionRadius(float radius) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getAI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
