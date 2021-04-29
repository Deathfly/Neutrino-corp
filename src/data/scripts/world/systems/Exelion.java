package data.scripts.world.systems;

import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.CharacterCreationData;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class Exelion {

//    private static final List<Vector2f> locList = new ArrayList<>();
//    private static final boolean randomLoc = true;
//
//    static {
//        locList.add(new Vector2f(-16000, -12000));
//        locList.add(new Vector2f(-18000, -12000));
//        locList.add(new Vector2f(-20000, -10000));
//        locList.add(new Vector2f(-10000, -14000));
//        locList.add(new Vector2f(-14000, -14000));
//        locList.add(new Vector2f(-16000, -14000));
//    }
    public void generate(SectorAPI sector) {
        Random random = new Random();
        Long seed = (long) sector.getSeedString().hashCode();
        random.setSeed(seed);
        List<StarSystemAPI> systems = sector.getStarSystems();
        List<StarSystemAPI> tagets = new ArrayList<>();
        for (StarSystemAPI s : systems) {
            if (s.isProcgen() && s.getStar().getSpec().isBlackHole()) {
                tagets.add(s);
            }
        }
        while (tagets.size() < 5) {
            int j = random.nextInt(systems.size());
            tagets.add(systems.get(j));
        }
        StarSystemAPI system = tagets.get(random.nextInt(tagets.size()));
        PlanetAPI star = system.getStar();

        SectorEntityToken NCAbandonedStation = system.addCustomEntity("neut_exelionabandoned",
                "Abandoned Neutrino Facility", "station_side06", "neutral");
        NCAbandonedStation.setCircularOrbitPointingDown(star, 200, 2050, 90);
        NCAbandonedStation.getMemory().set("$abandonedStation", true);
        NCAbandonedStation.setSensorProfile(600f);
        NCAbandonedStation.setDiscoverable(true);
//        NCAbandonedStation.setSensorProfile(60f);
        MarketAPI market = Global.getFactory().createMarket("exelion_abandoned_station_market", NCAbandonedStation.getName(), 0);
        market.setPrimaryEntity(NCAbandonedStation);
        market.setFactionId(NCAbandonedStation.getFaction().getId());
        market.addCondition(Conditions.ABANDONED_STATION);
        market.addSubmarket("neutrino_abandon");
        NCAbandonedStation.setMarket(market);
        NCAbandonedStation.setCustomDescriptionId("neut_abandoned");
        NCAbandonedStation.setInteractionImage("illustrations", "abandoned_station");
//        StarSystemAPI system = sector.createStarSystem("");
//        StarSystemAPI keeperSystem = sector.createStarSystem(" ");
//        LocationAPI hyper = Global.getSector().getHyperspace();
//        system.setBackgroundTextureFilename("graphics/neut/backgrounds/Exelion2.jpg");
//        // create the star
//        PlanetAPI star = system.initStar("blackhole", "star_blackhole", 36f,
//                2000f, // extent of corona outside star
//                20f, // solar wind burn level
//                0f, // flare probability
//                10f); // CR loss multiplier, good values are in the range of 1-5
//        system.setLightColor(new Color(75, 75, 75)); // light color in entire system, affects all entities
//        system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 256f, 400, 70f);
//        system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 475, -60f);
//        system.addRingBand(star, "misc", "rings1", 256f, 1, Color.white, 256f, 550, 80f);
//        system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 475, -60f);
//        system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 375, 50f);
////		system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 225, 40f);		
////		system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 256f, 175, -50f);
//
//        system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 700, 110f);
//
//        system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 875, -70f);
//        system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 950, 90f);
//        system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 1025, 110f);
////		system.addRingBand(star, "misc", "rings1", 256f, 1, Color.white, 256f, 950, -90f);
////		system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 256f, 875, -70f);
//
//        system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 1200, -110f);
//
//        system.addRingBand(star, "misc", "rings1", 256f, 1, Color.white, 256f, 1375, 90f);
//        system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 256f, 1400, -50f);
//        system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 1450, 70f);
////		system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 256f, 1500, 80f);
////		system.addRingBand(star, "misc", "rings1", 256f, 1, Color.white, 256f, 1550, -90f);		 
//
//        system.addAsteroidBelt(star, 50, 500, 200, 10, -35, Terrain.ASTEROID_BELT, "Planet Debris Belt");
//        system.addAsteroidBelt(star, 50, 900, 200, 10, 55, Terrain.ASTEROID_BELT, "Planet Debris Belt");
//        system.addAsteroidBelt(star, 50, 1200, 200, 10, 45, Terrain.ASTEROID_BELT, "Planet Debris Belt");
//        system.addAsteroidBelt(star, 50, 1500, 200, 10, -35, Terrain.ASTEROID_BELT, "Planet Debris Belt");
//        // Jump Point Magic v2!
//        Collections.shuffle(locList);
//        List<JumpPointAPI> jumpPointInKeepers = new ArrayList<>();
//        for (Vector2f loc : locList) {
//            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint(null, "unstable wormhole");
//            jumpPoint.setStandardWormholeToNothingVisual();
//            jumpPoint.setFixedLocation(loc.x, loc.y);
//            hyper.addEntity(jumpPoint);
//            jumpPointInKeepers.add(jumpPoint);
//        }
//        JumpPointAPI jumpPointIn = Global.getFactory().createJumpPoint(null, "unstable wormhole");
//        jumpPointIn.setStandardWormholeToStarOrPlanetVisual(star);
//        jumpPointIn.setFixedLocation(0, 0);
//        keeperSystem.addEntity(jumpPointIn);
//
//        JumpPointAPI jumpPointKeeperOut = Global.getFactory().createJumpPoint(null, "unstable wormhole");
//        OrbitAPI keeperOutOrbit = Global.getFactory().createCircularOrbit(jumpPointIn, 20, 1000, 45);
//        jumpPointKeeperOut.setStandardWormholeToHyperspaceVisual();
//        jumpPointKeeperOut.setOrbit(keeperOutOrbit);
//        keeperSystem.addEntity(jumpPointKeeperOut);
//
//        JumpPointAPI jumpPointOut = Global.getFactory().createJumpPoint(null, "unstable wormhole");
//        OrbitAPI orbit2 = Global.getFactory().createCircularOrbit(star, 20, 3000, 45);
//        jumpPointOut.setOrbit(orbit2);
//        jumpPointOut.setStandardWormholeToHyperspaceVisual();
//        system.addEntity(jumpPointOut);
//
//        // random jump point link in hyper, just a little too evil.
//        List<JumpDestination> jumpDestinations = new ArrayList<>();
//        for (JumpPointAPI jumpPoint : jumpPointInKeepers) {
//            JumpDestination dests = new JumpDestination(jumpPoint, "unknown destination");
//            dests.setMinDistFromToken(1000f);
//            dests.setMaxDistFromToken(3000f);
//            jumpDestinations.add(dests);
//        }
//        JumpDestination jumpPointiKeeperInDest = new JumpDestination(jumpPointKeeperOut, "unknown destination");
//        JumpDestination jumpPointiKeeperOutDest = new JumpDestination(jumpPointInKeepers.get(0), "unknown destination");
//        JumpDestination jumpPointiInDest = new JumpDestination(star, "unknown destination");
//        jumpPointiInDest.setMinDistFromToken(500f);
//        jumpPointiInDest.setMaxDistFromToken(1000f);
//        JumpDestination jumpPointiOutDest = new JumpDestination(jumpPointInKeepers.get(0), "unknown destination");
//        for (int i = 0; i < jumpPointInKeepers.size(); i++) {
//            JumpPointAPI point = jumpPointInKeepers.get(i);
//            int j = MathUtils.getRandomNumberInRange(2, jumpPointInKeepers.size());
//            int k = i == 0 ? MathUtils.getRandomNumberInRange(0, j - 1) : -1;
//            Collections.shuffle(jumpDestinations);
//            for (int l = 0; l < j; l++) {
//                if (l == k) {
//                    point.addDestination(jumpPointiKeeperInDest);
//                } else {
//                    point.addDestination(jumpDestinations.get(l));
//                }
//            }
//        }
//        jumpPointKeeperOut.addDestination(jumpPointiKeeperOutDest);
//        jumpPointIn.addDestination(jumpPointiInDest);
//        jumpPointOut.addDestination(jumpPointiOutDest);
////        // Jump Point Magic!
////        JumpPointAPI jumpPointKeeperInOrbitC = Global.getFactory().createJumpPoint(null, "collapsed wormhole");
////        jumpPointKeeperInOrbitC.setStandardWormholeToNothingVisual();
////        if (randomLoc) {
////            Vector2f loc = locList.get(MathUtils.getRandomNumberInRange(0, locList.size() - 1));
////            jumpPointKeeperInOrbitC.setFixedLocation(loc.x, loc.y);
////        } else {
////            jumpPointKeeperInOrbitC.setFixedLocation(-20000, -10000);
////        }
////        jumpPointKeeperInOrbitC.close();
////        hyper.addEntity(jumpPointKeeperInOrbitC);
////
////        JumpPointAPI jumpPointKeeperIn = Global.getFactory().createJumpPoint(null, "unstable wormhole");
////
////        OrbitAPI orbit1 = Global.getFactory().createCircularOrbit(jumpPointKeeperInOrbitC, 20, 1500, 15);
////        jumpPointKeeperIn.setOrbit(orbit1);
////        jumpPointKeeperIn.setStandardWormholeToStarfieldVisual();
////        hyper.addEntity(jumpPointKeeperIn);
////
////        JumpPointAPI jumpPointIn = Global.getFactory().createJumpPoint(null, "unstable wormhole");
////        jumpPointIn.setStandardWormholeToStarOrPlanetVisual(star);
////        jumpPointIn.setFixedLocation(0, 0);
////        keeperSystem.addEntity(jumpPointIn);
////
////        JumpPointAPI jumpPointKeeperOut = Global.getFactory().createJumpPoint(null, "unstable wormhole");
////        OrbitAPI keeperOutOrbit = Global.getFactory().createCircularOrbit(jumpPointIn, 20, 1000, 45);
////        jumpPointKeeperOut.setStandardWormholeToHyperspaceVisual();
////        jumpPointKeeperOut.setOrbit(keeperOutOrbit);
////        keeperSystem.addEntity(jumpPointKeeperOut);
////
////        JumpPointAPI jumpPointOut = Global.getFactory().createJumpPoint(null, "unstable wormhole");
////        OrbitAPI orbit2 = Global.getFactory().createCircularOrbit(star, 20, 3000, 45);
////        jumpPointOut.setOrbit(orbit2);
////        jumpPointOut.setStandardWormholeToHyperspaceVisual();
////        system.addEntity(jumpPointOut);
////
////        JumpDestination jumpPointiKeeperInDest = new JumpDestination(jumpPointKeeperOut, "unknown destination");
////        JumpDestination jumpPointiKeeperOutDest = new JumpDestination(jumpPointKeeperIn, "hyperspace");
////        JumpDestination jumpPointiInDest = new JumpDestination(star, "unknown destination");
////        jumpPointiInDest.setMinDistFromToken(500f);
////        jumpPointiInDest.setMaxDistFromToken(1000f);
////        JumpDestination jumpPointiOutDest = new JumpDestination(jumpPointKeeperIn, "hyperspace");
////
////        jumpPointKeeperIn.addDestination(jumpPointiKeeperInDest);
////        jumpPointKeeperOut.addDestination(jumpPointiKeeperOutDest);
////        jumpPointIn.addDestination(jumpPointiInDest);
////        jumpPointOut.addDestination(jumpPointiOutDest);
////        // END
//        SectorEntityToken NCAbandonedStation = system.addOrbitalStation("neut_exelionabandoned", star, 200, 2050, 90, "Abandoned Neutrino Facility", "neutral");
//        NCAbandonedStation.getMemory().set("$abandonedStation", true);
//        MarketAPI market = Global.getFactory().createMarket("exelion_abandoned_station_market", NCAbandonedStation.getName(), 0);
//        market.setPrimaryEntity(NCAbandonedStation);
//        market.setFactionId(NCAbandonedStation.getFaction().getId());
//        market.addCondition(Conditions.ABANDONED_STATION);
//        market.addSubmarket("neutrino_abandon");
//        NCAbandonedStation.setMarket(market);
//
//        NCAbandonedStation.setCustomDescriptionId("neut_abandoned");
//        NCAbandonedStation.setInteractionImage("illustrations", "abandoned_station");
    }
}
