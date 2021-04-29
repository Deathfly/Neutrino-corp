package data.scripts.AILib;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class ManeuverModule {

    private final CombatEntityAPI entity;
    private final BaseManeuverModule m;
    private CombatEntityAPI target;
    private Vector2f targetLoc;
    private IntervalUtil interval;
    private ManeuverType maneuverType = null;
    private FacingType facingType = null;

    public ManeuverModule(ShipAPI ship) {
        entity = ship;
        m = new BaseManeuverModule(ship);
        this.interval = new IntervalUtil(0.1f, 0.1f);
        interval.forceIntervalElapsed();
    }

    public ManeuverModule(MissileAPI missile) {
        entity = missile;
        m = new BaseManeuverModule(missile);
        this.interval = new IntervalUtil(0.1f, 0.1f);
        interval.forceIntervalElapsed();
    }

    public void setInterval(IntervalUtil interval) {
        this.interval = interval;
        interval.forceIntervalElapsed();
    }

    public enum ManeuverType {

        CLOSE_IN,
        BACK_OFF,
        INTERCEPT,
        ORBIT
    }

    public ManeuverType getManeuverType() {
        return maneuverType;
    }

    private float desireFacing = 0;

    public enum FacingType {

        TO_TARGET,
        TO_LEADING,
        TO_GIVEN_FACING
    }

    public FacingType getFacingType() {
        return facingType;
    }

    public float getDesireFacing() {
        return desireFacing;
    }

    /**
     * should only be uesed when FacingType.TO_GIVEN_FACING
     *
     * @param desireFacing in degree
     */
    public void setDesireFacing(float desireFacing) {
        this.desireFacing = desireFacing;
    }

    private float leadingFactor = 0;

    public void interceptTarget(CombatEntityAPI target, float leadingFactor) {
        this.target = target;
        this.leadingFactor = leadingFactor;
        this.maneuverType = ManeuverType.INTERCEPT;
    }

    public Vector2f getInterceptPoint(CombatEntityAPI target, float leadingFactor) {
        return AIUtils.getBestInterceptPoint(entity.getLocation(), m.getMaxSpeed(), target.getLocation(), target.getVelocity());
    }

    public void advance(float amount) {

    }

}
