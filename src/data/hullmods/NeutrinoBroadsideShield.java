//by Deathfly
package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashMap;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;

public class NeutrinoBroadsideShield extends BaseHullMod {

    private final int ARC_EXPAND_TO = 360;
    private final float SHIELD_EFFICIENCY = 0.75f;
    private final float WEAPON_RANGE_MUTL = 0.9f;
    private static final Map<HullSize, Integer> SPEED_BONUS = new HashMap<>();
    private static final Map<HullSize, Integer> ZERO_FLUX_SPEED_BONUS = new HashMap<>();

    static {
        SPEED_BONUS.put(HullSize.DEFAULT, 15);
        SPEED_BONUS.put(HullSize.FIGHTER, 15);
        SPEED_BONUS.put(HullSize.FRIGATE, 15);
        SPEED_BONUS.put(HullSize.DESTROYER, 15);
        SPEED_BONUS.put(HullSize.CRUISER, 10);
        SPEED_BONUS.put(HullSize.CAPITAL_SHIP, 10);
        ZERO_FLUX_SPEED_BONUS.put(HullSize.DEFAULT, 15);
        ZERO_FLUX_SPEED_BONUS.put(HullSize.FIGHTER, 15);
        ZERO_FLUX_SPEED_BONUS.put(HullSize.FRIGATE, 15);
        ZERO_FLUX_SPEED_BONUS.put(HullSize.DESTROYER, 10);
        ZERO_FLUX_SPEED_BONUS.put(HullSize.CRUISER, 10);
        ZERO_FLUX_SPEED_BONUS.put(HullSize.CAPITAL_SHIP, 5);
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldAbsorptionMult().modifyMult(id, 1 / SHIELD_EFFICIENCY);
        stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS.get(hullSize));
        stats.getZeroFluxSpeedBoost().modifyFlat(id, ZERO_FLUX_SPEED_BONUS.get(hullSize));
        stats.getEnergyWeaponRangeBonus().modifyMult(id, WEAPON_RANGE_MUTL);
        stats.getBallisticWeaponRangeBonus().modifyMult(id, WEAPON_RANGE_MUTL);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
//        ship.getShield().setType(ShieldAPI.ShieldType.FRONT);
        ship.getShield().setArc(0);
        ship.getShield().setType(ShieldAPI.ShieldType.PHASE);
    }

//    @Override
//    public void advanceInCombat(ShipAPI ship, float amount) {
//        // Refit screen check,
//        if (ship.getOriginalOwner() == -1) {
//            return;
//        }
//        ShieldAPI shield = ship.getShield();
//        if (shield != null) {
//            if (shield.isOn()) {
//                float arcToExpand = ARC_EXPAND_TO - ship.getHullSpec().getShieldSpec().getArc();
////                double radians = Math.toRadians(Math.abs(MathUtils.getShortestRotation(ship.getFacing(), ship.getShield().getFacing())));
////                float arc = (float) FastTrig.sin(radians) * arcToExpand + ship.getHullSpec().getShieldSpec().getArc();     
//                float deg = Math.abs(MathUtils.getShortestRotation(ship.getFacing(), ship.getShield().getFacing()));
//                float arc = deg > 90 ? (165 - deg) / 60 : (deg - 15) / 60;
//                arc = Math.max(0, Math.min(1, arc));
//                arc *= arcToExpand;
//                arc += ship.getHullSpec().getShieldSpec().getArc();
//                shield.setArc(arc);
//            }
//        }
//    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        // Allows ship with a Neutrino hull id  
        return ship.getHullSpec().getHullId().startsWith("neutrino_") && ship.getShield() != null;//    
        // && ship.getShield().getType() == ShieldAPI.ShieldType.OMNI;
        //&& (ship.getHullSize() == HullSize.CAPITAL_SHIP || ship.getHullSize() == HullSize.CRUISER);

    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize
    ) {
        if (index == 0) {
            return String.valueOf(ARC_EXPAND_TO);
        }
        if (index == 1) {
            return String.valueOf((int) ((1f - SHIELD_EFFICIENCY) * 100f) + "%");
        }
        if (index == 2) {
            return String.valueOf(SPEED_BONUS.get(HullSize.FRIGATE) + "/" + SPEED_BONUS.get(HullSize.DESTROYER) + "/" + SPEED_BONUS.get(HullSize.CRUISER) + "/" + SPEED_BONUS.get(HullSize.CAPITAL_SHIP));
        }
        if (index == 3) {
            return String.valueOf(ZERO_FLUX_SPEED_BONUS.get(HullSize.FRIGATE) + "/" + ZERO_FLUX_SPEED_BONUS.get(HullSize.DESTROYER) + "/" + ZERO_FLUX_SPEED_BONUS.get(HullSize.CRUISER) + "/" + ZERO_FLUX_SPEED_BONUS.get(HullSize.CAPITAL_SHIP));
        }
        if (index == 4) {
            return String.valueOf((int) ((1f - WEAPON_RANGE_MUTL) * 100f) + "%");
        }
        return null;
    }
}
