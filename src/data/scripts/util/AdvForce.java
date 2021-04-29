package data.scripts.util;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class AdvForce {

    /**
     * Apply momentum to an object.
     *
     * Momentum is multiplied by 100 to avoid requiring ridiculous momentum
     * amounts.
     *
     * @param entity The {@link CombatEntityAPI} to apply the momentum to.
     * @param direction The directional vector of the momentum (this will
     * automatically be normalized).
     * @param pointOfImpact Where the momentum should apply to.
     * @param momentum How much momentum to apply. Unit is how much it takes to
     * modify a 100 weight object's velocity by 1 su/sec.
     */
    public static void applyMomentum(CombatEntityAPI entity, Vector2f pointOfImpact, Vector2f direction, float momentum, boolean elasticCollision) {
        if(entity==null){
            return;
        }
        // Filter out forces without a direction
        if (direction.lengthSquared() == 0) {
            return;
        }
        // Avoid divide-by-zero errors...
        float mass = Math.max(1f, entity.getMass());
        // We should not move stations, right?
        if (entity instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) entity;
            if (ship.isStation() || (ship.isStationModule() && ship.getParentStation().isStation())) {
                return;
            }
            if (ship.isStationModule() && ship.isShipWithModules()) {
                entity = ship.getParentStation();
                mass = Math.max(1f, ship.getMassWithModules());
            }
        }
        // Momentum is far too weak otherwise
        momentum *= 100f;
        // Doing some vector calculate
        Vector2f BPtoMC = Vector2f.sub(entity.getLocation(), pointOfImpact, null);
        Vector2f forceV = new Vector2f();
        direction.normalise(forceV);
        forceV.scale(momentum);
        // get force vector
        BPtoMC.normalise(BPtoMC);
        // calculate acceleration
        BPtoMC.scale(Vector2f.dot(forceV, BPtoMC) / mass);
        if (elasticCollision) {
            // Apply velocity change
            Vector2f.add(BPtoMC, entity.getVelocity(), entity.getVelocity());
        } else {
            // Apply velocity change
            direction = new Vector2f(forceV);
            direction.scale(1 / mass);
            Vector2f.add(direction, entity.getVelocity(), entity.getVelocity());
        }
        // calculate moment change 
        float angularAcc = VectorUtils.getCrossProduct(forceV, BPtoMC) / (0.5f * mass * entity.getCollisionRadius() * entity.getCollisionRadius());
        angularAcc = (float) Math.toDegrees(angularAcc);
        // Apply angular velocity change
        if (elasticCollision) {
            entity.setAngularVelocity(entity.getAngularVelocity() + angularAcc);
        } else {
            entity.setAngularVelocity(entity.getAngularVelocity() - angularAcc);
        }
    }
}
