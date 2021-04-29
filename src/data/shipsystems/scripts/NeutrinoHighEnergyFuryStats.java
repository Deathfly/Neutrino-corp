package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import java.awt.Color;

public class NeutrinoHighEnergyFuryStats extends BaseShipSystemScript {

    public static final float DAMAGE_BONUS_PERCENT = 25f;
    public static final float ROF_BONUS_PERCENT = 25f;
    public static final float FLUX_REDUCTION = 15f;
//    public static final float EXTRA_DAMAGE_TAKEN_PERCENT = 100f;
    public static final float SPEED_BONUS = 100;
    public static final Color COLOR1 = new Color(255, 0, 100, 200);
    public static final float FLAME_EXTEND = 1.5f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        float damageBonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
        stats.getEnergyWeaponDamageMult().modifyPercent(id, damageBonusPercent);
        float ROFBonusPercent = ROF_BONUS_PERCENT * effectLevel;
        stats.getEnergyRoFMult().modifyPercent(id, ROFBonusPercent);
        float fluxReduction = -FLUX_REDUCTION * effectLevel;
        stats.getEnergyWeaponFluxCostMod().modifyPercent(id, fluxReduction);
//        float damageTakenPercent = EXTRA_DAMAGE_TAKEN_PERCENT * effectLevel;
//        stats.getShieldAbsorptionMult().modifyPercent(id, damageTakenPercent);
        if (state == State.IN) {
            ShipAPI ship = null;
            if (effectLevel == 0) {
                if (stats.getEntity() instanceof ShipAPI) {
                    ship = (ShipAPI) stats.getEntity();
                    ship.getEngineController().extendFlame(id, FLAME_EXTEND, FLAME_EXTEND, 0);
                    ship.getEngineController().fadeToOtherColor(id, COLOR1, new Color(0, 0, 0, 0), 1, 1f);
                    ship.giveCommand(ShipCommand.ACCELERATE, null, 0);

                }
            } else if (effectLevel < 0.5f) {
                stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
                stats.getAcceleration().modifyPercent(id, SPEED_BONUS * 10f * effectLevel);
//                stats.getDeceleration().modifyPercent(id, SPEED_BONUS * 1f * effectLevel);
//                stats.getTurnAcceleration().modifyMult(id, 0);
                if (stats.getEntity() instanceof ShipAPI) {
                    ship = (ShipAPI) stats.getEntity();
                    ship.getEngineController().extendFlame(id, FLAME_EXTEND, FLAME_EXTEND, 0);
                    ship.getEngineController().fadeToOtherColor(id, COLOR1, new Color(0, 0, 0, 0), 1, 1f);
                    ship.giveCommand(ShipCommand.ACCELERATE, null, 0);
                    if (!ship.getEngineController().getShipEngines().isEmpty()) {
                        for (ShipEngineControllerAPI.ShipEngineAPI slot : ship.getEngineController().getShipEngines()) {
                            ship.getEngineController().setFlameLevel(slot.getEngineSlot(), 1);
                        }
                    }
                }
            } else if (effectLevel < 0.9f) {

            } else {
                stats.getMaxSpeed().unmodify(id);
//                stats.getTurnAcceleration().unmodify(id);
                stats.getAcceleration().unmodify(id);
                stats.getDeceleration().unmodify(id);
            }
        }
        stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 1);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponDamageMult().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getShieldDamageTakenMult().unmodify(id);
        stats.getZeroFluxMinimumFluxLevel().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        float damageBonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
        float ROFBonusPercent = ROF_BONUS_PERCENT * effectLevel;
        float fluxReduction = -FLUX_REDUCTION * effectLevel;
//        float damageTakenPercent = EXTRA_DAMAGE_TAKEN_PERCENT * effectLevel;
        switch (index) {
            case 0:
                return new StatusData("+" + (int) damageBonusPercent + "% energy weapon damage and " + (int) ROFBonusPercent + "% rate of fire", false);
            case 1:
                return new StatusData("" + (int) fluxReduction + "% energy weapon flux cost", false);
            case 2:
                return new StatusData("enable engine boost", false);
            default:
                break;
        }
        return null;
    }
}
