//By Deathfly
package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.MissileRenderDataAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class NeutrinoSigmaUpgrade extends BaseHullMod {

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        // These hullmods will automatically be removed
        // Not as elegant as blocking them in the first place, but
        // this method doesn't require editing every hullmod's script
        BLOCKED_HULLMODS.add("extendedshieldemitter");
        BLOCKED_HULLMODS.add("frontemitter");
        BLOCKED_HULLMODS.add("frontshield");
        BLOCKED_HULLMODS.add("adaptiveshields");
    }

    private static final Map<String, Float> factors = new HashMap<>(100);

    //private static final List allowedIds = new ArrayList();
    public static final float SHIELD_ARC_BONUS = 45f;
    public static final float SHIELD_BONUS_UNFOLD = 300f;

    public static final float GUIDANCE_IMPROVEMENT = 0.5f;

    public static final float AUTOFIRE_AIM_ACCURACY = 40f;

    public static final float WEAPON_HEALTH_BONUS = 25f;

    public static final float VENT_RATE_BONUS = 0.75f;
    public static final float DISSIPATION_PER_VENT = 12f;
    public static final float FLUX_PER_CAPACITOR = 160f;
    public static final String SO_ID = "safetyoverrides";
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id
    ) {
        //shield arc
//        stats.getShieldArcBonus().modifyFlat(id, SHIELD_ARC_BONUS);
//        stats.getShieldTurnRateMult().modifyMult(id, 0);
        //missile maneuverability
        stats.getMissileGuidance().modifyFlat(id, GUIDANCE_IMPROVEMENT);
        stats.getMissileMaxSpeedBonus().modifyPercent(id, 5f);
        stats.getMissileAccelerationBonus().modifyPercent(id, 25f);
        stats.getMissileMaxTurnRateBonus().modifyPercent(id, 10f);
        stats.getMissileTurnAccelerationBonus().modifyPercent(id, 25f);

        //weapon aim
        if (hullSize == HullSize.DEFAULT || hullSize == HullSize.FIGHTER) {
            stats.getAutofireAimAccuracy().modifyPercent(id, 100);
            stats.getWeaponTurnRateBonus().modifyFlat(id, 2);
        } else {
            stats.getAutofireAimAccuracy().modifyPercent(id, AUTOFIRE_AIM_ACCURACY);
        }

        //weapon hp
        stats.getWeaponHealthBonus().modifyPercent(id, WEAPON_HEALTH_BONUS);

        //shield expansion rate
        stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_BONUS_UNFOLD);
        stats.getShieldTurnRateMult().modifyMult(id, 0.5f);
        //flux vent rate
        stats.getVentRateMult().modifyMult(id, VENT_RATE_BONUS);

        // flux state mod for capacity and vent part 1. this will do the real thing.
        MutableStat capacity = stats.getFluxCapacity();
        if (capacity.getFlatStatMod("flux_capacitors") != null) {
            float capacitors = capacity.getFlatStatMod("flux_capacitors").getValue() / 200f;
            capacity.modifyFlat(id, capacitors * (FLUX_PER_CAPACITOR - 200));
        } else {
            capacity.unmodify(id);
        }
        MutableStat vent = stats.getFluxDissipation();
        if (vent.getFlatStatMod("flux_vents") != null) {
            float vents = vent.getFlatStatMod("flux_vents").getValue() / 10f;
            vent.modifyFlat(id, vents * (DISSIPATION_PER_VENT - 10));
        } else {
            vent.unmodify(id);
        }
        //fighter weapon trick
        if (hullSize == HullSize.FIGHTER || hullSize == HullSize.DEFAULT) {
            stats.getWeaponDamageTakenMult().modifyMult(id, 0);
        }
        
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize
    ) {
        if (index == 0) {
            return String.valueOf((int)SHIELD_ARC_BONUS);
        }
        if (index == 1) {
            return String.valueOf((int)WEAPON_HEALTH_BONUS);
        }
        if (index == 2) {
            return String.valueOf((int) (VENT_RATE_BONUS * 100f));
        }
        return null;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                ship.getVariant().removeMod(tmp);
            }
        }
        //A little nerf for SO
         if (ship.getVariant().getHullMods().contains(SO_ID)){		
		ship.getMutableStats().getCriticalMalfunctionChance().modifyFlat(id, 0.02f);
		ship.getMutableStats().getWeaponMalfunctionChance().modifyFlat(id,  0.02f);
         }
        // flux state mod for capacity and vent part 2. for refit UI only.
        MutableShipStatsAPI stats = ship.getMutableStats();
        MutableStat capacity = stats.getFluxCapacity();
        if (capacity.getFlatStatMod("flux_capacitors") != null) {
            float capacitors = capacity.getFlatStatMod("flux_capacitors").getValue() / 200f;
            capacity.modifyFlat(id, capacitors * (FLUX_PER_CAPACITOR - 200));
        } else {
            capacity.unmodify(id);
        }
        MutableStat vent = stats.getFluxDissipation();
        if (vent.getFlatStatMod("flux_vents") != null) {
            float vents = vent.getFlatStatMod("flux_vents").getValue() / 10f;
            vent.modifyFlat(id, vents * (DISSIPATION_PER_VENT - 10));
        } else {
            vent.unmodify(id);
        }
        // Try to hide the missiles in hidden slots
        List<WeaponAPI> weapons = ship.getAllWeapons();
        for (WeaponAPI weapon : weapons) {
            List<MissileRenderDataAPI> missilerenders = weapon.getMissileRenderData();
            if (missilerenders != null && weapon.getSlot().isHidden()) {
                for (MissileRenderDataAPI missilereander : missilerenders) {
                    missilereander.getSprite().setWidth(0);
                }
            }
        }
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        // Refit screen check,
        if (ship.getOriginalOwner() == -1) {
            return;
        }
        // the 'ellipse' omni shield things. 
        ShieldAPI shield = ship.getShield();

        if (shield != null&&shield.getType()==ShieldAPI.ShieldType.OMNI) {
            if (shield.isOn()) {
//                float radians = (float) Math.toRadians(Math.abs(MathUtils.getShortestRotation(ship.getFacing(), ship.getShield().getFacing())));
                // OK...let me find some easier way to do this math later.(if I can found any >_<)
//                float a = ship.getHullSpec().getShieldSpec().getRadius();
//                float b = getFactor(ship);
//                float x = (float) FastTrig.cos(radians) * a;
//                float y = (float) FastTrig.sin(radians) * b;
//                float radius = (float) Math.sqrt(x * x + y * y);
//                shield.setRadius(radius);
//                float arc = ship.getHullSpec().getShieldSpec().getArc();
//                arc *= a / radius;
//                shield.setArc(arc);

                float radians = (float) Math.toRadians(MathUtils.getShortestRotation(ship.getFacing(), ship.getShield().getFacing()));
                float a = ship.getHullSpec().getShieldSpec().getRadius();
                float b = getFactor(ship);
                float c = (float) FastTrig.cos(radians) * (a - b);
                float d = (float) FastTrig.sin(radians) * (a - b);
                c += ship.getHullSpec().getShieldSpec().getCenterX();
                d += ship.getHullSpec().getShieldSpec().getCenterY();
                //c *= 1.2f;
                d *= 0.2f;
                shield.setCenter(c, -d);
                shield.setRadius(b);
                float arc = ship.getHullSpec().getShieldSpec().getArc();
                arc *= a / b;
                shield.setArc(arc);

                // debug lines
//                Global.getCombatEngine().addSmoothParticle(h.getLocation(), new Vector2f(0, 0), 10, 1, 0.1f, Color.yellow);
//                factors.clear();
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(h.getLocation(), 200, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.yellow);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(h.getLocation(), 400, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.yellow);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(h.getLocation(), 600, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.yellow);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(h.getLocation(), 800, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.yellow);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(h.getLocation(), 1000, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.yellow);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(ship.getLocation(), 200, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.green);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(ship.getLocation(), 400, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.green);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(ship.getLocation(), 600, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.green);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(ship.getLocation(), 800, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.green);
//                Global.getCombatEngine().addSmoothParticle(MathUtils.getPointOnCircumference(ship.getLocation(), 1000, h.getFacing()), new Vector2f(0, 0), 10, 1, 0.1f, Color.green);
            }
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship
    ) {
        // Allows any ship with a Neutrino hull id  
        return ship.getHullSpec().getHullId().startsWith("neutrino_");
    }

    // Try to found a way to...let me say draw a ellipse to fit the ship?
    // this should runs once per ship to help the CPU don't gose boom >_<
    private static float getFactor(ShipAPI ship) {
        String hullID = ship.getHullSpec().getHullId();
        if (factors.containsKey(hullID)) {
            return factors.get(hullID);
        }
        ShieldAPI shield = ship.getShield();
        float a = ship.getHullSpec().getShieldSpec().getRadius();
        if (shield == null || a == 0) {
            return 0;
        }
        float b = 0;

        List<BoundsAPI.SegmentAPI> segmentsToCheck = ship.getExactBounds().getSegments();
        Vector2f loc = ship.getLocation();
        float facing = ship.getFacing();
        ship.getExactBounds().update(new Vector2f(-ship.getHullSpec().getShieldSpec().getCenterX(), -ship.getHullSpec().getShieldSpec().getCenterY()), 0);
        for (int i = 0; i < segmentsToCheck.size(); i++) {
            BoundsAPI.SegmentAPI segment = segmentsToCheck.get(i);
            float x = segment.getP1().x;
            float y = segment.getP1().y;
            float tmp = Math.abs(x) == a ? 0 : y * y / (1 - ((x * x) / (a * a)));
            b = tmp > b ? tmp : b;
            if (i == (segmentsToCheck.size() - 1)) {
                x = segment.getP2().x;
                y = segment.getP2().y;
                tmp = Math.abs(x) == a ? 0 : y * y / (1 - ((x * x) / (a * a)));
                b = tmp > b ? tmp : b;
            }
        }
        ship.getExactBounds().update(loc, facing);
        float factor = (float) Math.sqrt(b);
        if (factor < a * 0.618f) {
            factor = a * 0.618f;//Golden Ratio! it works great! WOW!
        }
        factor += ship.getHullSize().ordinal() * 4f;
        factors.put(hullID, factor);
        return factor;
    }
//
//    protected class ellipseShieldSpec {
//
//        protected float a;
//        protected float b;
//        protected float c;
//
//        ellipseShieldSpec(float a, float b, float c) {
//            this.a = a;
//            this.b = b;
//            this.c = c;
//        }
//    }
}
