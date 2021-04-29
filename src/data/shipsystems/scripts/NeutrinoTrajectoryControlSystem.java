// By Deathfly
package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class NeutrinoTrajectoryControlSystem extends BaseShipSystemScript {

    private final float DAMAGE_PERCENT = 15f;
    private final float SPEED_PERCENT = 15f;
    private final float ROF_PERCENT = 100f;
    private final float ECCM_FLAT = 1f;
    private final float HEALTH_PERCENT = 50f;
    
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getMissileRoFMult().modifyPercent(id, ROF_PERCENT);
        stats.getMissileWeaponDamageMult().modifyPercent(id, DAMAGE_PERCENT);
        stats.getMissileAccelerationBonus().modifyPercent(id, SPEED_PERCENT);
        stats.getMissileTurnAccelerationBonus().modifyPercent(id, SPEED_PERCENT);
        stats.getMissileMaxSpeedBonus().modifyPercent(id, SPEED_PERCENT);
        stats.getMissileMaxTurnRateBonus().modifyPercent(id, SPEED_PERCENT);
        stats.getEccmChance().modifyFlat(id, ECCM_FLAT);
        stats.getMissileGuidance().modifyFlat(id, ECCM_FLAT);
        stats.getMissileHealthBonus().modifyPercent(id,HEALTH_PERCENT);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMissileRoFMult().unmodify(id);
        stats.getMissileWeaponDamageMult().unmodify(id);
        stats.getMissileAccelerationBonus().unmodify(id);
        stats.getMissileTurnAccelerationBonus().unmodify(id);
        stats.getMissileMaxSpeedBonus().unmodify(id);
        stats.getMissileMaxTurnRateBonus().unmodify(id);
        stats.getEccmChance().unmodify(id);
        stats.getMissileGuidance().unmodify(id);
        stats.getMissileHealthBonus().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        float damageBonusPercent = DAMAGE_PERCENT * effectLevel;
//        float ROFBonusPercent = ROF_PERCENT * effectLevel;
        float missileHealth = HEALTH_PERCENT * effectLevel;
//        float damageTakenPercent = EXTRA_DAMAGE_TAKEN_PERCENT * effectLevel;
        switch (index) {
            case 0:
                return new StatusData("+" + (int) damageBonusPercent + "% missile damage and max speed", false);
            case 1:
                return new StatusData("" + (int) missileHealth + "% missile durability", false);
            case 2:
                return new StatusData("double missile reload speed", false);
            default:
                break;
        }
        return null;
    }
}
