package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashMap;
import java.util.Map;

public class NeutrinoSensorDrone extends BaseHullMod {

    private final static Map<HullSize, Float> mag = new HashMap<>();

    static {
        mag.put(HullSize.FIGHTER, 50f);
        mag.put(HullSize.FRIGATE, 0f);
        mag.put(HullSize.DESTROYER, 0f);
        mag.put(HullSize.CRUISER, 0f);
        mag.put(HullSize.CAPITAL_SHIP, 0f);
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getSensorStrength().modifyFlat(id, 15);
        stats.getSightRadiusMod().modifyPercent(id, mag.get(hullSize));
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0){
            return "+15";
        }
        return null;
    }
}
