package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.combat.entities.Ship;

public class NeutrinoSiegeModeStats extends BaseShipSystemScript {

//    public static final float DAMAGE_BONUS_PERCENT = 25F;
//    public static final float ROF_PENALTY_PERCENT = -25f;
    public static final float WEAPON_FLUX_COST_REDUCE = -20f;

    public static final float WEAPON_RANGE_BONUS_MULT = 1F;//so the final weapon range should be 100% + 100%. Stack with skill and hull mod.
    public static final float TURN_PENALTY_MULT = 0.80F; //means the weapon turn rate will be 1-95%=5%. Stack with hull mod.
    public static final float PROJECTILE_SPEED_BONUS_PERCENT = 25F;

    public static final float BEAM_RANGE_PENALTY_MULT = 0.25F;//OK, beam is OP on this one. try to make them only got 50% extra Range.
    public static final float BEAM_TURN_PENALTY = 0.70F;//And lease penalty.

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        float effectLevelSquare = effectLevel * effectLevel;
        if (state == State.OUT) {
            stats.getMaxSpeed().unmodify(id);
            stats.getMaxTurnRate().unmodify(id);
        } else {
            stats.getMaxSpeed().modifyMult(id, 0.01F);
            stats.getMaxTurnRate().modifyMult(id, 0.01F);
        }
        if (state == State.ACTIVE) {
//            stats.getEnergyRoFMult().modifyPercent(id, ROF_PENALTY_PERCENT);
            stats.getWeaponTurnRateBonus().modifyMult(id, 1 - TURN_PENALTY_MULT);
        } else {
            ((Ship) stats.getEntity()).blockCommandForOneFrame(com.fs.starfarer.combat.entities.Ship.oo.valueOf("USE_SELECTED_GROUP"));
            ((Ship) stats.getEntity()).setHoldFireOneFrame(true);
        }
//        float damageBonusPercent = DAMAGE_BONUS_PERCENT * effectLevelSquare;
//        stats.getEnergyWeaponDamageMult().modifyPercent(id, damageBonusPercent);
        stats.getEnergyWeaponFluxCostMod().modifyPercent(id, WEAPON_FLUX_COST_REDUCE);
        float weaponRangePercent = 1F + WEAPON_RANGE_BONUS_MULT * effectLevel;
        stats.getEnergyWeaponRangeBonus().modifyMult(id, weaponRangePercent);
        float projectileSpeedBonus = PROJECTILE_SPEED_BONUS_PERCENT * effectLevelSquare;
        stats.getProjectileSpeedMult().modifyPercent(id, projectileSpeedBonus);
        //neaf for beams.
        float beamRangePenalty = 1F / (1F + BEAM_RANGE_PENALTY_MULT * effectLevelSquare);
        stats.getBeamWeaponRangeBonus().modifyMult(id, beamRangePenalty);
        float beamTurnMult = 1F - BEAM_TURN_PENALTY * effectLevelSquare;
        stats.getBeamWeaponTurnRateBonus().modifyMult(id, beamTurnMult);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
//        stats.getEnergyWeaponDamageMult().unmodify(id);
//        stats.getEnergyRoFMult().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponRangeBonus().unmodify(id);
        stats.getBeamWeaponRangeBonus().unmodify(id);
        stats.getWeaponTurnRateBonus().unmodify(id);
        stats.getBeamWeaponTurnRateBonus().unmodify(id);
        stats.getProjectileSpeedMult().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);

    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        switch (index) {
            case 0:
                if (state == State.ACTIVE) {
                    return new StatusData((int) (WEAPON_FLUX_COST_REDUCE) + "% energy weapon flux cost", false);
                } else {
                    return new StatusData("re-routing weapon energy distribution", true);
                }
            case 1:
                return new StatusData("+" + (int) (effectLevel * 200) + "% energy projectile weapons range and " + (int) (PROJECTILE_SPEED_BONUS_PERCENT * effectLevel) + "% projectile speed", false);
            case 2:
                return new StatusData("+" + (int) (effectLevel * 50) + "% energy beam weapons range", false);
            case 3:
                return new StatusData("weapon turn speed greatly slowed", true);
            default:
                break;
        }
        return null;
    }

}
