package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class NeutrinoOmegaUpgrade extends BaseHullMod {

    //private static final List allowedIds = new ArrayList();
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        // These hullmods will automatically be removed
        // Not as elegant as blocking them in the first place, but
        // this method doesn't require editing every hullmod's script

    }

//    private static final Map<HullSize, Float> WEAPON_RANGE_BONUS = new HashMap<>();
//
//    static {
//        WEAPON_RANGE_BONUS.put(HullSize.FIGHTER, 0f);
//        WEAPON_RANGE_BONUS.put(HullSize.FRIGATE, 5f);
//        WEAPON_RANGE_BONUS.put(HullSize.DESTROYER, 15f);
//        WEAPON_RANGE_BONUS.put(HullSize.CRUISER, 30f);
//        WEAPON_RANGE_BONUS.put(HullSize.CAPITAL_SHIP, 45f);
//    }
//    private static final Map<HullSize, Float> WEAPON_RANGE_BONUS_SSP = new HashMap<>();
//
//    static {
//        WEAPON_RANGE_BONUS_SSP.put(HullSize.FIGHTER, 0f);
//        WEAPON_RANGE_BONUS_SSP.put(HullSize.FRIGATE, 15f);
//        WEAPON_RANGE_BONUS_SSP.put(HullSize.DESTROYER, 15f);
//        WEAPON_RANGE_BONUS_SSP.put(HullSize.CRUISER, 15f);
//        WEAPON_RANGE_BONUS_SSP.put(HullSize.CAPITAL_SHIP, 15f);
//    }

    private static final Map<HullSize, Float> ARMOR_BONUS = new HashMap<>();

    static {
        ARMOR_BONUS.put(HullSize.FRIGATE, 50f);
        ARMOR_BONUS.put(HullSize.DESTROYER, 100f);
        ARMOR_BONUS.put(HullSize.CRUISER, 150f);
        ARMOR_BONUS.put(HullSize.CAPITAL_SHIP, 200f);
    }

    public static final float HULL_BONUS = -25f;
    public static final float SHIELD_UPKEEP_BONUS = 50f;
    public static final float DAMAGE_TO_CAPITAL = 10f;
    public static final float DAMAGE_TO_CRUISERS = 5f;
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        //weapon aim
        stats.getAutofireAimAccuracy().modifyFlat(id, 60f);

        //weapon range
//        if (SSPExists) {
//            stats.getBallisticWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS_SSP.get(hullSize));
//            stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS_SSP.get(hullSize));
//        } else {
//            stats.getBallisticWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS.get(hullSize));
//            stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS.get(hullSize));
//        }
        //damage increas
        stats.getDamageToCapital().modifyPercent(id, DAMAGE_TO_CAPITAL);
        stats.getDamageToCruisers().modifyPercent(id,DAMAGE_TO_CRUISERS);
        //armor + hull
        stats.getArmorBonus().modifyFlat(id, ARMOR_BONUS.get(hullSize));
//        stats.getHullBonus().modifyPercent(id, HULL_BONUS);
//
//        //increase shield upkeep
//        stats.getShieldUpkeepMult().modifyMult(id, 1f + SHIELD_UPKEEP_BONUS * 0.01f);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {

        if (index == 0) {
            return "" + (int)DAMAGE_TO_CAPITAL +"%";
        }
        if (index == 1) {
            return "" + (int)DAMAGE_TO_CRUISERS + "%";
        }
        if (index == 2) {
            return "" + (ARMOR_BONUS.get(HullSize.FRIGATE)).intValue();
        }
        if (index == 3) {
            return "" + (ARMOR_BONUS.get(HullSize.DESTROYER)).intValue();
        }
        if (index == 4) {
            return "" + (ARMOR_BONUS.get(HullSize.CRUISER)).intValue();
        }
        if (index == 5) {
            return "" + (ARMOR_BONUS.get(HullSize.CAPITAL_SHIP)).intValue();
        }
//        if (index == 6) {
//            return "" + (int) -HULL_BONUS + "%";
//        }
//        if (index == 7) {
//            return "" + (int) SHIELD_UPKEEP_BONUS + "%";
//        }
        return null;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id
    ) {
        // seems like we only needs to do this in refit... 
        if (ship.getOriginalOwner() == -1) {
            for (String tmp : BLOCKED_HULLMODS) {
                if (ship.getVariant().getHullMods().contains(tmp)) {
                    ship.getVariant().removeMod(tmp);
                }
            }
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship
    ) {
        // Allows any ship with a Neutrino hull id  
        return ship.getHullSpec().getHullId().startsWith("neutrino_");
//                && !ship.getVariant().getHullMods().contains("dedicated_targeting_core")
//                && !ship.getVariant().getHullMods().contains("targetingunit")
//                && !ship.getVariant().getHullMods().contains("advancedcore");
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

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if(!ship.getHullSpec().getHullId().startsWith("neutrino_")) return "Can only be installed on Neutrino ships";
//	if (ship.getVariant().getHullMods().contains("dedicated_targeting_core")) return "Incompatible with Dedicated Targeting Core";
//	if (ship.getVariant().getHullMods().contains("targetingunit")) return "Incompatible with Integrated Targeting Unit";
//	if (ship.getVariant().getHullMods().contains("advancedcore")) return "Incompatible with Advanced Targeting Core";	
        return null;
    }
}
