package data.missions.neutrino_test;

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
        api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true);

        // Set a small blurb for each fleet that shows up on the mission detail and
        // mission results screens to identify each side.
        api.setFleetTagline(FleetSide.PLAYER, "Neutrino Demonstration Ships");
        api.setFleetTagline(FleetSide.ENEMY, "Sim Opponents");

        // These show up as items in the bulleted list under 
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("For more details, please check your EULA.");
        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters

        // Capitals
        api.addToFleet(FleetSide.PLAYER, "neutrino_jackhammer2_vanguard", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "neutrino_banshee_elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_hildolfr_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_colossus_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_bansheenorn_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_unsung_standard", FleetMemberType.SHIP, false);
//        api.addToFleet(FleetSide.PLAYER, "neutrino_rm-r_standard", FleetMemberType.SHIP, false);

        // Cruisers
        api.addToFleet(FleetSide.PLAYER, "neutrino_lathe_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_maul_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_nirvash_elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_theend_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_grinder_standard", FleetMemberType.SHIP, false);

        // Destroyers
        api.addToFleet(FleetSide.PLAYER, "neutrino_hacksaw_assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_sledgehammer_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_piledriver_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_piledriverC_standard", FleetMemberType.SHIP, false);        
        api.addToFleet(FleetSide.PLAYER, "neutrino_vice_elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_miter_standard", FleetMemberType.SHIP, false);

        // Frigates
        api.addToFleet(FleetSide.PLAYER, "neutrino_singularity_updated", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_causality_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_criticality_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_relativity_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_polarity_standard", FleetMemberType.SHIP, false);

        // Fighter wings (use a hax way to make you can pilot them)
        api.addToFleet(FleetSide.PLAYER, "neutrino_schwarm_Fighter", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_schwarm_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_schwarm_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_gepard1_Fighter", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_gepard1_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_gepard1_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_gepard2_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_gepard2_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_floh_Fighter", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_floh_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_floh_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_moskito_Fighter", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_moskito_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_moskito_wing", FleetMemberType.FIGHTER_WING, false);        
        api.addToFleet(FleetSide.PLAYER, "neutrino_drache_Bomber", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_drache_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_drache_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_schwarzgeist_Bomber", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_schwarzgeist_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_schwarzgeist_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_drohne_Scout", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_drohne_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_drohne_wing", FleetMemberType.FIGHTER_WING, false);

        // Others
        api.addToFleet(FleetSide.PLAYER, "neutrino_falken_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_blowtorch_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "neutrino_nausicaa2_standard", FleetMemberType.SHIP, false);     
        api.addToFleet(FleetSide.PLAYER, "neutrino_barghestcausality_variant", FleetMemberType.SHIP, false);   
        // Mark both ships as essential - losing either one results
        // in mission failure. Could also be set on an enemy ship,
        // in which case destroying it would result in a win.

        // Set up the enemy fleet.
        api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "onslaught_Outdated", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "onslaught_Outdated", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "onslaught_Outdated", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "onslaught_Outdated", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "hound_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "hound_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "broadsword_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "broadsword_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "broadsword_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "broadsword_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "piranha_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "piranha_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "piranha_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "piranha_wing", FleetMemberType.FIGHTER_WING, false);

        // Set up the map.
        float width = 24000f;
        float height = 18000f;
        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        // All the addXXX methods take a pair of coordinates followed by data for
        // whatever object is being added.
        // And a few random ones to spice up the playing field.
        // A similar approach can be used to randomize everything
        // else, including fleet composition.
        for (int i = 0; i < 7; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 800f;
            api.addNebula(x, y, radius);
        }
    }
}
