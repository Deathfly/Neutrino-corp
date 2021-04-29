package data.missions.neutrino_retribution;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

    @Override
    public void defineMission(MissionDefinitionAPI api) {

        // Set up the fleets
        api.initFleet(FleetSide.PLAYER, "CNC", FleetGoal.ATTACK, false);
        api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true);

        // Set a blurb for each fleet
        api.setFleetTagline(FleetSide.PLAYER, "A Neutrino corporation envoy");
        api.setFleetTagline(FleetSide.ENEMY, "Tri-Tachyon Regional Manager's flagship ");

        // These show up as items in the bulleted list under 
        // "Tactical Objectives" on the mission detail screen
//        api.addBriefingItem("The Lathe vents exceptionally quick, use this to your advantage");
//        api.addBriefingItem("Be aware that your Pin-point shield doesn't fully cover your ship's silhouette");

        // Set up the player's fleet
        api.addToFleet(FleetSide.PLAYER, "neutrino_jackhammer2_standard", FleetMemberType.SHIP, "CNC Hawking", true);
		//api.addToFleet(FleetSide.PLAYER, "ex2_red_tempest_Attack", FleetMemberType.SHIP, true);
        //api.addToFleet(FleetSide.PLAYER, "xyphos_wing", FleetMemberType.FIGHTER_WING, false);

        // Mark a ship as essential, if you want
        //api.defeatOnShipLoss("ISS Black Star");
        // Set up the enemy fleet
        api.addToFleet(FleetSide.ENEMY, "paragon_Elite", FleetMemberType.SHIP, false);
        // Set up the map.
        float width = 20000f;
        float height = 12000f;
        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        // All the addXXX methods take a pair of coordinates followed by data for
        // whatever object is being added.
        // Add two big nebula clouds
        api.addNebula(minX + width * 0.66f, minY + height * 0.5f, 2000);
        api.addNebula(minX + width * 0.25f, minY + height * 0.6f, 1000);
        api.addNebula(minX + width * 0.25f, minY + height * 0.4f, 1000);

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
        api.addAsteroidField(-(minY + height), minY + height, -45, 2000f,
                20f, 70f, 100);

        api.addPlanet(minX + width * 0.8f, minY + height * 0.8f, 300f, "jungle", 300f);
    }

}
