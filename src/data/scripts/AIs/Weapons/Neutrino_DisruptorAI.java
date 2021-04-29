// By Deathfly
//WIP
package data.scripts.AIs.Weapons;

import com.fs.starfarer.api.combat.AutofireAIPlugin;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.ai.missile.MissileAI;
import static data.scripts.util.Neutrino_CollisionUtilsEX.getCollisionPointOnCircle;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_DisruptorAI implements AutofireAIPlugin {

    public WeaponAPI weapon;
    private Vector2f targetV2f = null;
    private CombatEntityAPI target = null;
    private IntervalUtil checkInterval = new IntervalUtil(0.1f, 0.1f);

    @Override
    public MissileAPI getTargetMissile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class incomingMissiles {

        private ShipAPI ship;
        private Map<MissileAI, Float> threatSet = new WeakHashMap<>(300);
        private Set<MissileAI> nearbyMissiles = new HashSet<>(300);
        private float timeStamp;
        private IntervalUtil checkInterval = new IntervalUtil(0.1f, 0.1f);
        
        public incomingMissiles(ShipAPI ship) {
            this.ship = ship;
        }
        
        
    }

    public static class incomingMissile {

        private final ShipAPI ship;
        private final MissileAPI missile;
        private boolean canHit;
        private float timeToHit;

        public incomingMissile(ShipAPI ship, MissileAPI missile) {
            this.ship = ship;
            this.missile = missile;
            if (missile.isGuided() && !missile.isFizzling()) {
                // well, most missiles have a really fast acc and trun acc so let me do it roughly.
                float timeToAimAt = Math.abs(MathUtils.clampAngle(missile.getFacing() - VectorUtils.getAngle(missile.getLocation(), ship.getLocation())) / missile.getMaxTurnRate());
                float dist = MathUtils.getDistance(ship, missile);
                timeToHit = timeToAimAt + (dist - (timeToAimAt * missile.getMaxSpeed()) / missile.getMaxSpeed());
                canHit = true;
            } else {
                Vector2f pathEnd = new Vector2f(missile.getVelocity());
                pathEnd.scale(missile.getMaxFlightTime()-missile.getFlightTime()+2);
                Vector2f lineEnd = new Vector2f();
                Vector2f.add(missile.getLocation(),pathEnd,lineEnd);
                Vector2f cp = getCollisionPointOnCircle(missile.getLocation(),lineEnd,ship.getLocation(),ship.getCollisionRadius());
                if (cp !=null){
                    canHit = true;
                    timeToHit = MathUtils.getDistance(missile.getLocation(), cp)/missile.getVelocity().length();
                }                
            }
        }

        public float getTimeToHit() {
            return timeToHit;
        }

        public MissileAPI getMissile() {
            return missile;
        }

        public ShipAPI getShip() {
            return ship;
        }

        public boolean isCanHit() {
            return canHit;
        }    

    }

    @Override
    public void advance(float amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean shouldFire() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void forceOff() {
        target = null;
        targetV2f = null;
        checkInterval.forceIntervalElapsed();
    }

    @Override
    public Vector2f getTarget() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ShipAPI getTargetShip() {
        if (target instanceof ShipAPI) {
            return (ShipAPI) target;
        } else {
            return null;
        }
    }

    @Override
    public WeaponAPI getWeapon() {
        return weapon;
    }
}
