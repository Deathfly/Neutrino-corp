package data.scripts.world.systems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.world.NeutrinoAddMarket;
import java.util.ArrayList;
import java.util.Arrays;

public class CoronaAustralis {

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Corona Australis");
        system.getLocation().set(-15200, -3400);
        HyperspaceTerrainPlugin hyper = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(hyper);
        editor.regenNoise();
        editor.clearArc(-15200, -3400, 0, 500, 0, 360);
        system.setBackgroundTextureFilename("graphics/neut/backgrounds/CoronaAustralis.jpg");

        // create the star and generate the hyperspace anchor for this system
        PlanetAPI star = system.initStar("neutronstar", "star_neutron", 48f,
                400, // extent of corona outside star
                10f, // solar wind burn level
                5f, // flare probability
                5f); // CR loss multiplier, good values are in the range of 1-5
        system.setLightColor(new Color(255, 255, 255)); // light color in entire system, affects all entities

        /*
         * addPlanet() parameters:
         * 1. Unique Id
         * 2. What the planet orbits (orbit is always circular)
         * 3. Name
         * 4. Planet type id in planets.json
         * 5. Starting angle in orbit, i.e. 0 = to the right of the star
         * 6. Planet radius, pixels at default zoom
         * 7. Orbit radius, pixels at default zoom
         * 8. Days it takes to complete an orbit. 1 day = 10 seconds.
         1.    2.       3.           4.              5.  6.   7.    8.       */
        PlanetAPI p1 = system.addPlanet("is7", star, "IS7", "rocky_unstable", 10, 150, 4700, 100);

        SectorEntityToken relay = system.addCustomEntity("CoronaAustralis_relay", // unique id
                "Data Storage Complex", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay", // type of object, defined in custom_entities.json
                "neutrinocorp"); // faction
        relay.setCircularOrbit(p1, 90, 650, 80);

        p1.getSpec().setPlanetColor(new Color(255, 215, 190, 255));
        p1.getSpec().setAtmosphereColor(new Color(160, 110, 45, 140));
        p1.getSpec().setCloudColor(new Color(255, 164, 96, 200));
        p1.setCircularOrbitPointingDown(star, 10, 4700, 100);
        p1.getSpec().setTilt(10);
        p1.applySpecChanges();

        /*
         * addAsteroidBelt() parameters:
         * 1. What the belt orbits
         * 2. Number of asteroids
         * 3. Orbit radius
         * 4. Belt width
         * 6/7. Range of days to complete one orbit. Value picked randomly for each asteroid.
         */
        system.addAsteroidBelt(p1, 50, 1100, 128, 40, 80);

        /*
         * addRingBand() parameters:
         * 1. What it orbits
         * 2. Category under "graphics" in settings.json
         * 3. Key in category
         * 4. Width of band within the texture
         * 5. Index of band
         * 6. Color to apply to band
         * 7. Width of band (in the game)
         * 8. Orbit radius (of the middle of the band)
         * 9. Orbital period, in days
                 
         The sun
         1.              2.      3.      4.   5.         6.              7.              8.        9.                                                            */
        system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 400, 70f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 475, -60f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 550, 80f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 475, -60f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 375, 50f);
//                system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 225, 40f);              
//                system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 256f, 175, -50f);

        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 700, 110f);

        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 875, -70f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 950, 90f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 1025, 110f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 950, -90f);
        //               system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 256f, 875, -70f);

        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 1200, -110f);

        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 1375, 90f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 1400, -50f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 1450, 70f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 1500, 80f);
//                system.addRingBand(star, "misc", "rings1", 256f, 1, Color.white, 256f, 1550, -90f);              

        system.addAsteroidBelt(star, 50, 500, 200, 10, -35);
        system.addAsteroidBelt(star, 50, 900, 200, 10, 55);
        system.addAsteroidBelt(star, 50, 1200, 200, 10, 45);
        system.addAsteroidBelt(star, 50, 1500, 200, 10, -35);

        // planet 1
        system.addRingBand(p1, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 250, 60f);
        system.addRingBand(p1, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 325, 40f);
        system.addRingBand(p1, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 400, 60f);
//                system.addRingBand(p1, "misc", "rings1", 256f, 2, Color.white, 256f, 475, 80f);

        system.addRingBand(p1, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 550, 70f);
        system.addRingBand(p1, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 650, 90f);
        system.addRingBand(p1, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 750, 110f);

        system.addAsteroidBelt(p1, 50, 400, 200, 10, -70);
        system.addAsteroidBelt(p1, 50, 800, 200, 10, 90);

        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("stalin", "Jump Point Alpha");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 55, 4700, 100);
        jumpPoint.setOrbit(orbit);
        jumpPoint.setRelatedPlanet(p1);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        /* 
         *      1. Unique station id
         *      2. Display name
         *      3. Type of station from Custom Entities
         *      4. FactionId for who this entity belongs to */
        SectorEntityToken NCstation1 = system.addCustomEntity("neut_smalldrill", "Small Extraction Drill", "neutrino_station_smalldrill", "neutrinocorp");
        NCstation1.setCircularOrbitPointingDown(star, 200, 700, 90);
        NCstation1.setCustomDescriptionId("neut_station_smalldrill");

        SectorEntityToken NCstation2 = system.addCustomEntity("neut_largeprocessing", "Large Processing Dock", "neutrino_station_largeprocessing", "neutrinocorp");
        NCstation2.setCircularOrbitPointingDown(star, 200, 1750, 90);
        NCstation2.setCustomDescriptionId("neut_station_largeprocessing");

        SectorEntityToken NCstation3 = system.addCustomEntity("neut_experimental", "Small Research Station", "neutrino_station_experimental", "neutrinocorp");
        NCstation3.setCircularOrbitPointingDown(p1, 10, 500, 100);
        NCstation3.setCustomDescriptionId("neut_station_experimental");

        MarketAPI NCSM1 = NeutrinoAddMarket.addMarketplace(
                "neutrinocorp",
                null,
                NCstation1,
                null,
                "Small Extraction Drill",
                2,
                new ArrayList<>(Arrays.asList(
                                Conditions.ORE_ULTRARICH,
                                Conditions.RARE_ORE_RICH,
                                Conditions.LOW_GRAVITY,
                                Conditions.NO_ATMOSPHERE,
                                Conditions.POPULATION_3)),
                new ArrayList<>(Arrays.asList(
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.MINING,
                                Industries.HEAVYBATTERIES
                                        )),
                new ArrayList<>(Arrays.asList(
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_STORAGE)),
                0.3f);
        
        MarketAPI NCSM2 = NeutrinoAddMarket.addMarketplace(
                "neutrinocorp",
                null,
                NCstation2,
                null,
                "Large Processing Dock",
                6,
                new ArrayList<>(Arrays.asList(
                                Conditions.URBANIZED_POLITY,
                                Conditions.REGIONAL_CAPITAL,
                                Conditions.LOW_GRAVITY,
                                Conditions.POPULATION_6)),
                new ArrayList<>(Arrays.asList(
                                Industries.POPULATION,
                                Industries.REFINING,
                                Industries.ORBITALWORKS,
                                Industries.LIGHTINDUSTRY,
                                Industries.MEGAPORT,
                                Industries.FUELPROD,
                                Industries.HEAVYBATTERIES,
                                Industries.STARFORTRESS_HIGH,
                                Industries.MILITARYBASE)),                           
                new ArrayList<>(Arrays.asList(
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_STORAGE)),
                0.3f);
        NCSM2.getIndustry(Industries.ORBITALWORKS).initWithParams(new ArrayList<>(Arrays.asList(Items.PRISTINE_NANOFORGE)));
        MarketAPI NCSM3 = NeutrinoAddMarket.addMarketplace(
                "neutrinocorp",
                null,
                NCstation3,
                null,
                "Small Research Station",
                3,
                new ArrayList<>(Arrays.asList(
                                Conditions.TECTONIC_ACTIVITY,
                                Conditions.FREE_PORT,
                                Conditions.OUTPOST,
                                Conditions.VICE_DEMAND,
                                Conditions.VOLATILES_TRACE,
                                Conditions.ORGANICS_TRACE,
                                Conditions.ORE_MODERATE,
                                Conditions.RARE_ORE_SPARSE,
                                "neutrino_small_hydroponics",
                                Conditions.FARMLAND_POOR,
                                Conditions.POPULATION_4)),
                new ArrayList<>(Arrays.asList(
                                Industries.POPULATION,
                                Industries.MINING,
                                Industries.FARMING,
                                Industries.LIGHTINDUSTRY,
                                Industries.ORBITALSTATION_HIGH,
                                Industries.PATROLHQ,
                                Industries.WAYSTATION,
                                Industries.SPACEPORT
                )),
                new ArrayList<>(Arrays.asList(
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_STORAGE)),
                0.3f);
//        initStationCargo1(NCstation1);
//        initStationCargo2(NCstation2);
//        initStationCargo3(NCstation3);
        // example of using custom visuals below
//              a1.setCustomInteractionDialogImageVisual(new InteractionDialogImageVisual("illustrations", "hull_breach", 800, 800));
//              jumpPoint.setCustomInteractionDialogImageVisual(new InteractionDialogImageVisual("illustrations", "space_wreckage", 1200, 1200));
//              station.setCustomInteractionDialogImageVisual(new InteractionDialogImageVisual("illustrations", "cargo_loading", 1200, 1200));
        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);

        // herp derp not working convoys
        //      LocationAPI hyper = Global.getSector().getHyperspace();
        //      system.addSpawnPoint(new neutrinoConvoySpawnPoint3(sector, hyper, 9, 1, hyper.createToken(3650, -3550), NCstation3));
        //neutrinoSpawnPoint1 NCSpawn1 = new neutrinoSpawnPoint1(sector, system, 9, 1, NCstation1);                      
        //neutrinoSpawnPoint2 NCSpawn2 = new neutrinoSpawnPoint2(sector, system, 6, 2, NCstation2);      
        //neutrinoSpawnPoint3 NCSpawn3 = new neutrinoSpawnPoint3(sector, system, 12, 1, NCstation3);     
        //PirateSpawnPoint1 pirateSpawn1 = new PirateSpawnPoint1(sector, system, 7, 4, system.getEntityByName("Jump Point Alpha"));
        //PirateSpawnPoint pirateSpawn = new PirateSpawnPoint(sector, system, 1f, 50, system.getEntityByName("Corvus IIIA"));
        //neutrinoAutomatedConvoySpawnPoint NCSpawn4 = new neutrinoAutomatedConvoySpawnPoint(sector, system, 3, 2, NCstation1, system.getEntityByName("Large Processing Dock")); 
        //system.addSpawnPoint(NCSpawn1);
        //for (int i = 0; i < 1; i++) NCSpawn1.spawnFleet();             
        //system.addSpawnPoint(NCSpawn2);
        //for (int i = 0; i < 2; i++) NCSpawn2.spawnFleet();
        //system.addSpawnPoint(NCSpawn3);
        //for (int i = 0; i < 1; i++) NCSpawn3.spawnFleet();
        //system.addSpawnPoint(NCSpawn4);
        //for (int i = 0; i < 2; i++) NCSpawn4.spawnFleet();             
        //system.addScript(pirateSpawn1);
        //for (int i = 0; i < 4; i++)
        //        pirateSpawn1.spawnFleet();
        //system.addScript(new neutrinoAverageConvoySpawnPoint(sector, hyper, 14, 1, hyper.createToken(-4000, 4000), NCstation2));
        //system.addScript(new neutrinoEliteConvoySpawnPoint(sector, hyper, 25, 1, hyper.createToken(-2000, 5000), NCstation3)); 
    }
//
//    //Small Extraction Drill Station
//    private void initStationCargo1(SectorEntityToken NCstation1) {
//        CargoAPI cargo = NCstation1.getCargo();
//        addRandomWeapons(cargo, 2);
//
//        /*
//         cargo.addWeapons("neutrino_pulsar", 4);        
//         cargo.addWeapons("neutrino_dualpulsar", 2);            
//         cargo.addWeapons("neutrino_antiproton", 2);
//         cargo.addWeapons("neutrino_lightphoton", 5);           
//         cargo.addWeapons("neutrino_darkmatterbeamcannon", 3);  
//         cargo.addWeapons("neutrino_derp_launcher", 5);                 
//         cargo.addWeapons("neutrino_neutronpulseheavy", 4);             
//         cargo.addWeapons("neutrino_dualpulsebeam", 3);         
// 
//               
//         cargo.addCrew(CrewXPLevel.VETERAN, 30);
//         cargo.addCrew(CrewXPLevel.REGULAR, 200);
//         cargo.addMarines(60);
//         cargo.addSupplies(1500);
//         cargo.addFuel(600);
// 
//               
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_maul_assault"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_hacksaw_assault"));            
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_singularity_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_polarity_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_polarity_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_polarity_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_polarity_standard"));          
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_drohne_wing"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_gepard1_wing"));        
//         */
//    }
//
//    //Large Processing Dock      
//    private void initStationCargo2(SectorEntityToken NCstation2) {
//        CargoAPI cargo = NCstation2.getCargo();
//        addRandomWeapons(cargo, 2);
//        /*               
//         cargo.addWeapons("neutrino_sapper", 4);
//         cargo.addWeapons("neutrino_advancedtorpedosingle", 4);
//         cargo.addWeapons("neutrino_disruptor", 2);                             
//         cargo.addWeapons("neutrino_advancedtorpedo", 4);                       
//         cargo.addWeapons("neutrino_photontorpedo", 2);
//         cargo.addWeapons("neutrino_antiproton", 2);
//         cargo.addWeapons("neutrino_darkmatterbeamcannon", 2);  
//         cargo.addWeapons("neutrino_derp_launcher", 4);                 
//         cargo.addWeapons("neutrino_pulsebeam", 4);                                     
//         cargo.addWeapons("neutrino_misery", 6);        
//         cargo.addWeapons("neutrino_neutronpulse", 10);         
//         cargo.addWeapons("neutrino_particlecannonarray", 2);                   
//         cargo.addWeapons("neutrino_goliath", 1);       
//               
//         cargo.addCrew(CrewXPLevel.VETERAN, 20);
//         cargo.addCrew(CrewXPLevel.REGULAR, 500);
//         cargo.addMarines(80);
//         cargo.addSupplies(2500);
//         cargo.addFuel(1500);
//               
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_banshee_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_nausicaa2_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_nirvash_standard"));           
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_grinder_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_sledgehammer_standard"));              
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_polarity_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_polarity_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_criticality_standard"));               
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_criticality_standard"));               
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_criticality_standard"));                       
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_schwarzgeist_wing"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_drohne_wing"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_drohne_wing"));        
//         */
//    }
//
//    //Small Research Station
//    private void initStationCargo3(SectorEntityToken NCstation3) {
//        CargoAPI cargo = NCstation3.getCargo();
//        addRandomWeapons(cargo, 4);
//        /*               
//         cargo.addWeapons("neutrino_sapper", 3);
//         cargo.addWeapons("neutrino_advancedtorpedosingle", 2);
//         cargo.addWeapons("neutrino_disruptor", 8);                     
//         cargo.addWeapons("neutrino_pulsar", 8);        
//         cargo.addWeapons("neutrino_dualpulsar", 4);    
//         cargo.addWeapons("neutrino_antiproton", 2);
//         cargo.addWeapons("neutrino_pulsebeam", 6);             
//         cargo.addWeapons("neutrino_neutronpulse", 9);  
//         cargo.addWeapons("neutrino_misery", 6);                
//         cargo.addWeapons("neutrino_neutronpulseheavy", 3);             
//         cargo.addWeapons("neutrino_neutronpulsebattery", 2);                           
//         cargo.addWeapons("neutrino_dualpulsebeam", 2);         
//         cargo.addWeapons("neutrino_heavypulsar", 4);                                                   
//         cargo.addWeapons("neutrino_XLadvancedtorpedo", 2);
//         cargo.addWeapons("neutrino_bane", 4);          
//         cargo.addWeapons("neutrino_goliath", 2);       
//               
//         cargo.addCrew(CrewXPLevel.VETERAN, 69);
//         cargo.addCrew(CrewXPLevel.ELITE, 25);          
//         cargo.addMarines(200);
//         cargo.addSupplies(600);
//         cargo.addFuel(200);
//               
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_jackhammer2_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_bansheenorn_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_lathe_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_theend_standard"));
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "neutrino_criticality_standard"));                       
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_schwarm_wing"));               
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_floh_wing"));          
//         cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_gepard1_wing"));               
//         //cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, "neutrino_gepard2_wing"));             
//         */
//    }
//
//    private void addRandomWeapons(CargoAPI cargo, int count) {
//        List weaponIds = Global.getSector().getAllWeaponIds();
//        for (int i = 0; i < count; i++) {
//            String weaponId = (String) weaponIds.get((int) (weaponIds.size() * Math.random()));
//            int quantity = (int) (Math.random() * 4f + 2f);
//            cargo.addWeapons(weaponId, quantity);
//        }
//    }

}
