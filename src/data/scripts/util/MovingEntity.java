package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import org.lwjgl.util.vector.Vector2f;

/**
 * An implementation of {@code CombatEntityAPI} that moving.
 *
 * @author Deathfly
 */
public class MovingEntity implements CombatEntityAPI {

    protected Vector2f velocity, location;
    protected float timeStamp = 0;
    protected float TET = Global.getCombatEngine().getTotalElapsedTime(false);

    public MovingEntity(Vector2f location, Vector2f velocity) {
        this.location = location;
        this.velocity = velocity;
        timeStamp = TET;
    }

    public void setLocation(Vector2f location) {
        this.location = location;
        this.timeStamp = TET;
    }

    @Override
    public Vector2f getLocation() {
        if (velocity != null && velocity != new Vector2f(0, 0) && TET != timeStamp) {
            Vector2f variation = new Vector2f(velocity);
            variation.scale(TET - timeStamp);
            Vector2f.add(location, variation, location);
            timeStamp = TET;
        }
        return location;
    }

    public void setVelocity(Vector2f velocity) {
        if (timeStamp != 0 && TET != timeStamp) {
            getLocation();
        }
        this.velocity = velocity;
    }

    @Override
    public Vector2f getVelocity() {
        return velocity;
    }

    @Override
    public float getFacing() {
        return 0;
    }

    @Override
    public void setFacing(float facing) {
    }

    @Override
    public float getAngularVelocity() {
        return 0;
    }

    @Override
    public void setAngularVelocity(float angVel) {
    }

    @Override
    public int getOwner() {
        return 100;
    }

    @Override
    public void setOwner(int owner) {
    }

    @Override
    public float getCollisionRadius() {
        return 0;
    }

    @Override
    public CollisionClass getCollisionClass() {
        return CollisionClass.NONE;
    }

    @Override
    public void setCollisionClass(CollisionClass collisionClass) {
    }

    @Override
    public float getMass() {
        return 0f;
    }

    @Override
    public void setMass(float mass) {
    }

    @Override
    public BoundsAPI getExactBounds() {
        return null;
    }

    @Override
    public ShieldAPI getShield() {
        return null;
    }

    @Override
    public float getHullLevel() {
        return 1;
    }

    @Override
    public float getHitpoints() {
        return 1;
    }

    @Override
    public float getMaxHitpoints() {
        return 1;
    }

    @Override
    public void setCollisionRadius(float radius) {
    }

    @Override
    public Object getAI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
