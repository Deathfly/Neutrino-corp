package data.missions.neutrino_specialdelivery;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

    @Override
    public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets so we can add ships and fighter wings to them.
        // In this scenario, the fleets are attacking each other, but
        // in other scenarios, a fleet may be defending or trying to escape
        api.initFleet(FleetSide.PLAYER, "CNC", FleetGoal.ATTACK, false);
        api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true);

		// Set a small blurb for each fleet that shows up on the mission detail and
        // mission results screens to identify each side.
        api.setFleetTagline(FleetSide.PLAYER, "Neutrino experimental transport fleet");
        api.setFleetTagline(FleetSide.ENEMY, "Corporate Sabotage squad");

		// These show up as items in the bulleted list under 
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("Defeat all Tri-Tachyon hired enemy forces");
        api.addBriefingItem("The CNC Deadblow is a valuable prototype and must survive");

		// Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters
        api.addToFleet(FleetSide.PLAYER, "neutrino_piledriver_standard", FleetMemberType.SHIP,  false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_piledriver_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_lathe_standard", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "neutrino_nausicaa2_standard", FleetMemberType.SHIP, false);
//        api.addToFleet(FleetSide.PLAYER, "neutrino_drohne_wing", FleetMemberType.FIGHTER_WING, false);
//        api.addToFleet(FleetSide.PLAYER, "neutrino_drohne_wing", FleetMemberType.FIGHTER_WING, false);
//        api.addToFleet(FleetSide.PLAYER, "neutrino_schwarzgeist_wing", FleetMemberType.FIGHTER_WING, false);

        // Set up the enemy fleet.
        api.addToFleet(FleetSide.ENEMY, "conquest_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "conquest_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "hyperion_Strike", FleetMemberType.SHIP, "TTS Ninja", false);
        api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "ISS Black Star", true);
//        api.addToFleet(FleetSide.ENEMY, "thunder_wing", FleetMemberType.FIGHTER_WING, false);
//        api.addToFleet(FleetSide.ENEMY, "broadsword_wing", FleetMemberType.FIGHTER_WING, false);

        api.defeatOnShipLoss("CNC Deadblow");

        // Set up the map.
        float width = 12000f;
        float height = 8000f;
        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

		// All the addXXX methods take a pair of coordinates followed by data for
        // whatever object is being added.
        // Add nebula clouds
        api.addNebula(minX + width * 0.4f, minY + height * 0.5f, 1000);
        api.addNebula(minX + width * 0.5f, minY + height * 0.5f, 1200);
        api.addNebula(minX + width * 0.6f, minY + height * 0.5f, 1400);

		// And a few random ones to spice up the playing field.
        // A similar approach can be used to randomize everything
        // else, including fleet composition.
        for (int i = 0; i < 5; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 400f;
            api.addNebula(x, y, radius);
        }

		// Add objectives. These can be captured by each side
        // and provide stat bonuses and extra command points to
        // bring in reinforcements.
        // Reinforcements only matter for large fleets - in this
        // case, assuming a 100 command point battle size,
        // both fleets will be able to deploy fully right away.
        api.addObjective(minX + width * 0.5f, minY + height * 0.5f,
                "sensor_array");
        api.addObjective(minX + width * 0.2f, minY + height * 0.25f,
                "comm_relay");
        api.addObjective(minX + width * 0.8f, minY + height * 0.75f,
                "nav_buoy");

        // Add an asteroid field
        api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
                20f, 70f, 100);

        // Add some planets.
        api.addPlanet(minX + width * 0.55f, minY + height * 0.85f, 256f, "desert", 150f);
        api.addPlanet(minX + width * 0.3f, minY + height * 0.35f, 150f, "cryovolcanic", 75f);
    }

}
