package data.missions.neutrino_stophammertime;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParams;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import data.missions.BaseRandomNeutrinoMissionDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.util.vector.Vector2f;

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
        api.setFleetTagline(FleetSide.PLAYER, "Runaway Experimental Autonomous Ship");
        api.setFleetTagline(FleetSide.ENEMY, "Unlucky Fleet");

        // These show up as items in the bulleted list under 
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("You will prove them all wrong, you will prove you are useful! You'll show them!");
        api.addBriefingItem("Take the hint, your ship looks like a hammer, use it as such.");
        api.addBriefingItem("Use your shield and tractor beam together to inflict damage while receiving next to none.");
        api.addBriefingItem("CNC McHammer must survive");

        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters
        api.addToFleet(FleetSide.PLAYER, "neutrino_hammer_standard", FleetMemberType.SHIP, "CNC McHammer", true);

//		api.addToFleet(FleetSide.PLAYER, "neutrino_drohne_wing", FleetMemberType.FIGHTER_WING, false);
        // Mark both ships as essential - losing either one results
        // in mission failure. Could also be set on an enemy ship,
        // in which case destroying it would result in a win.
        api.defeatOnShipLoss("CNC McHammer");
        List<FactionAPI> acceptableFactions = new ArrayList<>();

        // Instead of adding *all* factions, we are adding the vanilla factions only
        acceptableFactions.add(Global.getSector().getFaction(Factions.DIKTAT));
        acceptableFactions.add(Global.getSector().getFaction(Factions.HEGEMONY));
        acceptableFactions.add(Global.getSector().getFaction(Factions.INDEPENDENT));
//            acceptableFactions.add(Global.getSector().getFaction(Factions.KOL));
//            acceptableFactions.add(Global.getSector().getFaction(Factions.LIONS_GUARD));
        acceptableFactions.add(Global.getSector().getFaction(Factions.LUDDIC_CHURCH));
        acceptableFactions.add(Global.getSector().getFaction(Factions.LUDDIC_PATH));
        acceptableFactions.add(Global.getSector().getFaction(Factions.PIRATES));
        acceptableFactions.add(Global.getSector().getFaction(Factions.TRITACHYON));
        acceptableFactions.add(Global.getSector().getFaction(Factions.PERSEAN));
        Random r = new Random();
        String factionID = acceptableFactions.get(r.nextInt(acceptableFactions.size())).getId();
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(new FleetParamsV3(
                null,
                new Vector2f(),
                factionID,
                125f, // qualityOverride
                FleetTypes.TASK_FORCE,
                500f, // combatPts
                0f, // freighterPts 
                0f, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                125f // qualityBonus
        ));
//        CampaignFleetAPI fleet = FleetFactory.createGenericFleet(faction.getId(), side.toString(), qf, minFP);
        for (FleetMemberAPI m : fleet.getMembersWithFightersCopy()) {
            String variant = m.getVariant().getHullVariantId();
            if (m.getType() == FleetMemberType.FIGHTER_WING) {
//                api.addToFleet(side, variant, FleetMemberType.FIGHTER_WING, false);
            } else {
                api.addToFleet(FleetSide.ENEMY, variant, FleetMemberType.SHIP, m.getShipName(), fleet.getFlagship() == m);
            }
        }
        // Set up the enemy fleet.
//		api.addToFleet(FleetSide.ENEMY, "onslaught_Elite", FleetMemberType.SHIP, false);		
//		api.addToFleet(FleetSide.ENEMY, "onslaught_Elite", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "hound_Standard", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "hound_Standard", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "enforcer_Outdated", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "enforcer_Outdated", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "condor_FS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
//		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
//		
//		//api.addToFleet(FleetSide.ENEMY, "broadsword_wing", FleetMemberType.FIGHTER_WING, false);
//		api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);
//		api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);
//		api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);

        // Set up the map.
        float width = 7000f;
        float height = 5000f;
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

        // Add objectives. These can be captured by each side
        // and provide stat bonuses and extra command points to
        // bring in reinforcements.
        // Reinforcements only matter for large fleets - in this
        // case, assuming a 100 command point battle size,
        // both fleets will be able to deploy fully right away.
        api.addObjective(minX + width * 0.7f, minY + height * 0.25f, "sensor_array");
        api.addObjective(minX + width * 0.8f, minY + height * 0.75f, "nav_buoy");
        api.addObjective(minX + width * 0.2f, minY + height * 0.25f, "nav_buoy");

    }

}
