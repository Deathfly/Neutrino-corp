package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class NeutrinoDrone extends BaseHullMod {

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getCrewLossMult().modifyMult(id, 0f);
        stats.getProjectileSpeedMult().modifyPercent(id, 50);
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if(ship.isHulk() && ship.getHullSpec().getHullId().startsWith("neutrino_schwarm")){
            Global.getCombatEngine().applyDamage(ship, ship.getLocation(), ship.getMaxHitpoints()*3, DamageType.ENERGY, 0, true, true, ship);
        }
    }
}
