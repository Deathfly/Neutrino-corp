package data.scripts.world.systems;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import data.scripts.world.NeutrinoAddMarket;
import java.util.ArrayList;
import java.util.Arrays;
//import data.scripts.world.neutrinoSpawnPoint;

public class CorvusAddon {

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.getStarSystem("Corvus");

        SectorEntityToken sol = system.getStar();

        SectorEntityToken NCstation = system.addCustomEntity("neut_solar", "Solar Powerplant", "neutrino_station_powerplant", "neutrinocorp");
        NCstation.setCircularOrbitPointingDown(sol, 270, 1500, 75);
        NCstation.setCustomDescriptionId("neut_station_powerplant");

//        SectorEntityToken token = system.createToken(2000, 15000);
//        system.addSpawnPoint( new neutrinoConvoySpawnPoint(sector, system, 7, 1, token, NCstation));
//        neutrinoSpawnPoint NCSpawn = new neutrinoSpawnPoint(sector, system, 5, 1, NCstation);
        NeutrinoAddMarket.addMarketplace(
                "neutrinocorp",
                null,
                NCstation,
                null,
                "Solar Powerplant",
                3,
                new ArrayList<>(Arrays.asList(
                                Conditions.OUTPOST,
                                Conditions.POPULATION_2,
                                Conditions.VOLATILES_DIFFUSE,
                                Conditions.LOW_GRAVITY,
                                Conditions.NO_ATMOSPHERE,
                                Conditions.HOT)),
                new ArrayList<>(Arrays.asList(
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.WAYSTATION,
                                Industries.FUELPROD,
                                Industries.MINING,
                                Industries.PATROLHQ)),
                new ArrayList<>(Arrays.asList(
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_STORAGE)),
                0.3f);
    }
}
