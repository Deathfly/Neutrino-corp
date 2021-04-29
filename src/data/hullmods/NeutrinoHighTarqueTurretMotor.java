package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class NeutrinoHighTarqueTurretMotor extends BaseHullMod {

    private static final float WEAPON_TRUNRATE_FLAT = 2;
//    private static final float WEAPON_TRUNRATE_PERCENT = 25;
    private static final float WEAPON_RANGE_PERCENT = 5;
    private static final float WEAPON_RECOIL_PERCENT = 5;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getWeaponTurnRateBonus().modifyFlat(id, WEAPON_TRUNRATE_FLAT);
        stats.getBeamWeaponTurnRateBonus().modifyFlat(id, WEAPON_TRUNRATE_FLAT);
//        stats.getWeaponTurnRateBonus().modifyPercent(id, -WEAPON_TRUNRATE_PERCENT);
//        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, -WEAPON_TRUNRATE_PERCENT);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_PERCENT);
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_PERCENT);
        stats.getRecoilPerShotMult().modifyPercent(id, -WEAPON_RECOIL_PERCENT);
        stats.getRecoilDecayMult().modifyPercent(id, WEAPON_RECOIL_PERCENT);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {

        if (index == 0) {
            return "" + (int) (WEAPON_TRUNRATE_FLAT * 6);
        }
//        if (index == 1) {
//            return "" + (int) WEAPON_TRUNRATE_PERCENT + "%";
//        }
        if (index == 1) {
            return "" + (int) WEAPON_RANGE_PERCENT + "%";
        }
        if (index == 2) {
            return "" + (int) WEAPON_RECOIL_PERCENT + "%";
        }
        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return true;

    }
    /*
     @Override
     public boolean isApplicableToShip(ShipAPI ship)
     {
     // Allows any ship with a neutrino hull id
     return ( ( ship.getHullSpec().getHullId().startsWith("neutrino_") && !ship.getVariant().getHullMods().contains("dedicated_targeting_core")) ||
     ( ship.getHullSpec().getHullId().startsWith("neutrino_") && !ship.getVariant().getHullMods().contains("targetingunit")) );

        
        
     }
     */
}
