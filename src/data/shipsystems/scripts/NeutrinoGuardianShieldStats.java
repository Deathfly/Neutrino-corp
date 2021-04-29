// by Deathfly
package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NeutrinoGuardianShieldStats extends BaseShipSystemScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getZeroFluxMinimumFluxLevel().modifyMult(id, 0f);
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship != null && !ship.getChildModulesCopy().isEmpty()) {
            ShipAPI sheild = ship.getChildModulesCopy().get(0);
            if (sheild.isAlive()) {
                sheild.getShield().toggleOn();
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getZeroFluxMinimumFluxLevel().unmodify();
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship != null && !ship.getChildModulesCopy().isEmpty()) {
            ShipAPI sheild = ship.getChildModulesCopy().get(0);
            if (sheild.isAlive()) {
                sheild.getShield().toggleOff();
            }
        }
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {

        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null || ship != engine.getPlayerShip()) {
            return null;
        }
        if (ship != null && !ship.getChildModulesCopy().isEmpty()) {
            ShipAPI sheild = ship.getChildModulesCopy().get(0);
            if (sheild.isAlive()) {
                FluxTrackerAPI shieldFlux = sheild.getFluxTracker();
                if (shieldFlux.isOverloaded()) {
                    return "Warning! Shield Core Overloaded!";
                } else {
                    String state;
                    if (system.isOn()) {
                        state = "Actived: Flux Capacity At ";
                    } else {
                        state = "Standby: Flux Capacity At ";
                    }
                    float fluxLevel = shieldFlux.getFluxLevel();
                    fluxLevel *= 100f;
                    BigDecimal b = new BigDecimal(fluxLevel);
                    String fluxLevelS = b.setScale(2, RoundingMode.HALF_UP).toString();
                    String data = state + fluxLevelS + "%";
                    return data;
                }
            } else {
                return "Warning! Shield Core Ejected!";
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship
    ) {
        if (ship != null && !ship.getChildModulesCopy().isEmpty()) {
            ShipAPI sheild = ship.getChildModulesCopy().get(0);
            return sheild.isAlive();
        }
        return false;
    }
}
