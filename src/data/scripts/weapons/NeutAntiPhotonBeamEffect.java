// by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.combat.entities.BeamWeaponRay;
import data.scripts.plugins.Neutrino_LocalData;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;

public class NeutAntiPhotonBeamEffect implements BeamEffectPlugin {

    private final float maxAimOffset = 5f;
    private final float trunRate = 90f;
    private final IntervalUtil interval = new IntervalUtil(0.1f, 0.5f);
    private final separatelyAimBeam SAB;
    private static final String KEY = "Neutrino_LocalData";
    private final Color mockColor = new Color(0, 0, 0, 0);
    private final IntervalUtil fireInterval = new IntervalUtil(0.05f, 0.2f);
    private boolean wasZero = true;
    private float jitter = 0;
    private boolean runOnce = false;

    public NeutAntiPhotonBeamEffect() {
        interval.forceIntervalElapsed();
        SAB = new separatelyAimBeam(maxAimOffset, trunRate);
        SAB.setTargetAimOffset(0);
        SAB.setCurrAimOffset(0);
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        BeamWeaponRay b = (BeamWeaponRay) beam;
        if (!runOnce) {
            beam.getDamage().setMultiplier(0f);
            runOnce = true;
        }
        if (engine == null || engine.isPaused()) {
            return;
        }
        if (beam.getDamageTarget() != null) {
            CombatEntityAPI target = beam.getDamageTarget();
            if (target instanceof ShipAPI && beam.didDamageThisFrame()) {
                ShipAPI targetShip = (ShipAPI) target;
                targetShip.getMutableStats().getEffectiveArmorBonus().modifyMult("NeutAntiPhotonBeamEffect", 0.2f);
                engine.applyDamage(target, beam.getTo(), beam.getDamage().getDamage() * 0.01f * beam.getHitGlowBrightness(), DamageType.FRAGMENTATION, 0, false, true, beam.getSource());
                targetShip.getMutableStats().getEffectiveArmorBonus().unmodifyMult("NeutAntiPhotonBeamEffect");
            }
        }
        final Neutrino_LocalData.LocalData localData = (Neutrino_LocalData.LocalData) engine.getCustomData().get(KEY);

        if (beam.getBrightness() > 0) {
            interval.advance(amount);
            if (interval.intervalElapsed()) {
                // tarcking missiles
                WeaponAPI weapon = SAB.getWeapon();
                if (weapon == null) {
                    weapon = beam.getWeapon();
                    SAB.setWeapon(weapon);
                }
                float closestDist = Float.MAX_VALUE;

                MissileAPI target = (MissileAPI) SAB.getTarget();
                float aimOffset = 0;
                if (target != null) {
                    aimOffset = MathUtils.getShortestRotation(weapon.getCurrAngle(), VectorUtils.getAngle(beam.getFrom(), target.getLocation()));
                }
                if (target == null
                        || target.getCollisionClass() == CollisionClass.NONE || target.getOwner() == weapon.getShip().getOwner()
                        || MathUtils.getDistance(beam.getFrom(), target.getLocation()) > weapon.getRange()
                        || Math.abs(aimOffset) > maxAimOffset) {
                    aimOffset = Float.MAX_VALUE;
                    for (MissileAPI m : engine.getMissiles()) {
                        if (m.getCollisionClass() == CollisionClass.NONE || m.getOwner() == weapon.getShip().getOwner()) {
                            continue;
                        }
                        float dist = MathUtils.getDistance(beam.getFrom(), m.getLocation());
                        if (dist > weapon.getRange()) {
                            continue;
                        }
                        float offset = MathUtils.getShortestRotation(weapon.getCurrAngle(), VectorUtils.getAngle(beam.getFrom(), m.getLocation()));
                        if (Math.abs(offset) > maxAimOffset) {
                            continue;
                        }
//                    if (dist < closestDist) {
//                        closestDist = dist;
//                        aimOffset = offset;
//                        target = m;
//                    }
                        if (Math.abs(offset) < Math.abs(aimOffset)) {
                            closestDist = dist;
                            aimOffset = offset;
                            target = m;
                            if (Math.random() < 0.1f) {
                                break;
                            }
                        }
                    }
                }
                if (target == null || Math.abs(aimOffset) > maxAimOffset) {
                    aimOffset = 0;
                }
                jitter = MathUtils.getRandomNumberInRange(-0.1f, 0.1f);
                SAB.setTarget(target);
                SAB.setTargetAimOffset(aimOffset + jitter);
                SAB.setBeam(beam);

                // OK, we can't modify beam.getTo() in BeamPlugin so we have to pass this Data to somewhere else.
                localData.antiPhotonAimData.put(beam, SAB);
            }
            if (beam.getDamageTarget() != null && beam.getDamageTarget() instanceof ShipAPI && beam.getBrightness() >= 1f) {
                float dur = beam.getDamage().getDpsDuration();
                // needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
                if (!wasZero) {
                    dur = 0;
                }
                wasZero = beam.getDamage().getDpsDuration() <= 0;
                fireInterval.advance(dur);

                if (fireInterval.intervalElapsed()) {
                    ShipAPI ship = (ShipAPI) beam.getDamageTarget();
                    boolean hitShield = ship.getShield() != null && ship.getShield().isWithinArc(beam.getTo());
                    float pierceChance = (ship.getHardFluxLevel() - 0.1f) * 0.2f;
                    pierceChance *= ship.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT);

                    boolean piercedShield = hitShield && (float) Math.random() < pierceChance;
                    //piercedShield = true;

                    if ((!hitShield && Math.random() < 0.2f) || piercedShield) {
//                        Vector2f dir = Vector2f.sub(beam.getTo(), beam.getFrom(), new Vector2f());
//                        if (dir.lengthSquared() > 0) {
//                            dir.normalise();
//                        }
//                        dir.scale(50f);
//                        Vector2f point = Vector2f.sub(beam.getTo(), dir, new Vector2f());
                        float emp = beam.getDamage().getFluxComponent() * 0.2f;
                        float dam = beam.getDamage().getDamage() * 0.2f;
                        engine.spawnEmpArcPierceShields(
                                beam.getSource(), beam.getTo(), beam.getDamageTarget(), beam.getDamageTarget(),
                                DamageType.FRAGMENTATION,
                                dam, // damage
                                emp, // emp 
                                10000f, // max range 
                                null,
                                0.1f,
                                mockColor,
                                beam.getCoreColor()
                        );
                    }
                }
            }
        }
    }

    public class separatelyAimBeam {

        protected CombatEntityAPI target;
        protected float turnRate, currAimOffset, targetAimOffset, maxAimOffset, dither;
        protected BeamAPI beam;
        protected WeaponAPI weapon;
        protected boolean ditherDirct;

        public separatelyAimBeam(float maxAimOffset, float turnRate) {
            this.turnRate = turnRate;
            this.maxAimOffset = maxAimOffset;
            this.currAimOffset = 0;
            this.target = null;
        }

        public BeamAPI getBeam() {
            return beam;
        }

        public void setBeam(BeamAPI beam) {
            this.beam = beam;
        }

        public float getCurrAimOffset() {
            return currAimOffset;
        }

        public void setCurrAimOffset(float currAimOffset) {
            this.currAimOffset = currAimOffset;
        }

        public float getMaxAimOffset() {
            return maxAimOffset;
        }

        public void setMaxAimOffset(float maxAimOffset) {
            this.maxAimOffset = maxAimOffset;
        }

        public CombatEntityAPI getTarget() {
            return target;
        }

        public void setTarget(CombatEntityAPI target) {
            this.target = target;
        }

        public float getTargetAimOffset() {
            return targetAimOffset;
        }

        public void setTargetAimOffset(float targetAimOffset) {
            this.targetAimOffset = targetAimOffset;
        }

        public float getTurnRate() {
            return turnRate;
        }

        public void setTurnRate(float turnRate) {
            this.turnRate = turnRate;
        }

        public WeaponAPI getWeapon() {
            return weapon;
        }

        public void setWeapon(WeaponAPI weapon) {
            this.weapon = weapon;
        }
    }
}
