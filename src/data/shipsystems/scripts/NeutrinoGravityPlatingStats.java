package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class NeutrinoGravityPlatingStats implements ShipSystemStatsScript {


	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		
		//float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
		//stats.getEnergyWeaponDamageMult().modifyPercent(id, bonusPercent);
		//stats.getEnergyWeaponRangeBonus().modifyPercent(id, bonusPercent);
		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			stats.getMaxTurnRate().unmodify(id);
		} else {		
		stats.getArmorDamageTakenMult().modifyPercent(id, 2f * effectLevel);
		stats.getHullDamageTakenMult().modifyPercent(id, 10f * effectLevel);
//		stats.getShieldDamageTakenMult().modifyPercent(id, damageTakenPercent);
//		stats.getWeaponDamageTakenMult().modifyPercent(id, damageTakenPercent);
//		stats.getEngineDamageTakenMult().modifyPercent(id, damageTakenPercent);
			stats.getAcceleration().modifyPercent(id, 70f * effectLevel);
			stats.getDeceleration().modifyPercent(id, 70f * effectLevel);		
			stats.getTurnAcceleration().modifyPercent(id, 70f * effectLevel);			
		}		
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		//stats.getEnergyWeaponDamageMult().unmodify(id);
		//stats.getEnergyWeaponRangeBonus().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getShieldDamageTakenMult().unmodify(id);
		stats.getWeaponDamageTakenMult().unmodify(id);
		stats.getEngineDamageTakenMult().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);

		}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		//float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
		//float damageTakenPercent = EXTRA_DAMAGE_TAKEN_PERCENT * effectLevel;
		if (index == 0) {
			return new StatusData("-90% damage taken", false);
			//return null;
		} else if (index == 1) {
			//return new StatusData("shield damage taken +" + (int) damageTakenPercent + "%", true);
			return null;
		}
		return null;
	}

    @Override
    public float getActiveOverride(ShipAPI ship) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getInOverride(ShipAPI ship) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getOutOverride(ShipAPI ship) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getUsesOverride(ShipAPI ship) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getRegenOverride(ShipAPI ship) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
