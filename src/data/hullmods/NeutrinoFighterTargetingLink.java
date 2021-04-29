package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;

public class NeutrinoFighterTargetingLink extends BaseHullMod {

    public static final IntervalUtil INTER = new IntervalUtil(0.2f, 0.2f);

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
//        if (ship.getWing() != null && ship.getWing().getSourceShip() != null && ship.getWing().getSourceShip().getMutableStats() != null) {
//            MutableShipStatsAPI stats = ship.getMutableStats();
//            MutableShipStatsAPI motherShipStats = ship.getWing().getSourceShip().getMutableStats();
//            stats.getEnergyWeaponRangeBonus().applyMods(motherShipStats.getEnergyWeaponRangeBonus());
//            stats.getBeamPDWeaponRangeBonus().applyMods(motherShipStats.getBeamPDWeaponRangeBonus());
//            stats.getBallisticWeaponRangeBonus().applyMods(motherShipStats.getBallisticWeaponRangeBonus());
//            //er,lets skip missiles
//            stats.getNonBeamPDWeaponRangeBonus().applyMods(motherShipStats.getNonBeamPDWeaponRangeBonus());
//            stats.getBeamWeaponRangeBonus().applyMods(motherShipStats.getBeamWeaponRangeBonus());
//            stats.getBeamPDWeaponRangeBonus().applyMods(motherShipStats.getBeamPDWeaponRangeBonus());
//        }
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
//        if (s.getSinceLaunch() == 0) {
        INTER.advance(amount);
        if (INTER.intervalElapsed()) {
            MutableShipStatsAPI stats = ship.getMutableStats();
            if (ship.getWing().getSourceShip() != null) {   //this should not be null. but just in case of testing 
                MutableShipStatsAPI motherShipStats = ship.getWing().getSourceShip().getMutableStats();
                stats.getEnergyWeaponRangeBonus().applyMods(motherShipStats.getEnergyWeaponRangeBonus());
                stats.getBeamPDWeaponRangeBonus().applyMods(motherShipStats.getBeamPDWeaponRangeBonus());
                stats.getBallisticWeaponRangeBonus().applyMods(motherShipStats.getBallisticWeaponRangeBonus());
                //er,lets skip missiles
                stats.getNonBeamPDWeaponRangeBonus().applyMods(motherShipStats.getNonBeamPDWeaponRangeBonus());
                stats.getBeamWeaponRangeBonus().applyMods(motherShipStats.getBeamWeaponRangeBonus());
                stats.getBeamPDWeaponRangeBonus().applyMods(motherShipStats.getBeamPDWeaponRangeBonus());
            }
        }
//        }
    }
}
