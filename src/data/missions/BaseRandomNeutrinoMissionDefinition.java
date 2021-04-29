// by Deathfly
// Credit to MesoTroniK, mod form his code.
package data.missions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
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
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class BaseRandomNeutrinoMissionDefinition implements MissionDefinitionPlugin {

    // Types of objectives that may be randomly used
    private static final String[] OBJECTIVE_TYPES
            = {
                "sensor_array", "nav_buoy", "comm_relay"
            };
    private static final Map<String, Float> QUALITY_FACTORS = new HashMap<>(10);
    private FactionAPI enemy;
    private boolean flagshipChosen = false;
    private FactionAPI player;
    private final Random rand = new Random();
    private boolean fullRandom = false;

    static {
        QUALITY_FACTORS.put("neutrinocorp", 0.85f);
        QUALITY_FACTORS.put(Factions.DIKTAT, 0.5f);
        QUALITY_FACTORS.put(Factions.HEGEMONY, 0.5f);
        QUALITY_FACTORS.put(Factions.INDEPENDENT, 0.5f);
//        QUALITY_FACTORS.put(Factions.KOL, 0.5f);
//        QUALITY_FACTORS.put(Factions.LIONS_GUARD, 0.75f);
        QUALITY_FACTORS.put(Factions.LUDDIC_CHURCH, 0.25f);
        QUALITY_FACTORS.put(Factions.LUDDIC_PATH, 0f);
        QUALITY_FACTORS.put(Factions.PIRATES, 0f);
        QUALITY_FACTORS.put(Factions.TRITACHYON, 0.85f);
        QUALITY_FACTORS.put(Factions.PERSEAN, 0.5f);
    }

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        if (player == null || enemy == null) {
            chooseFactions(null, null, fullRandom, 0);
        }

        api.initFleet(FleetSide.PLAYER, "", FleetGoal.ATTACK, false, 5);
        api.initFleet(FleetSide.ENEMY, "", FleetGoal.ATTACK, true, 5);

        api.setFleetTagline(FleetSide.PLAYER, player.getDisplayNameLong() + " forces");
        api.setFleetTagline(FleetSide.ENEMY, enemy.getDisplayNameLong() + " forces");

        // Fleet size randomization
        int size = MathUtils.getRandomNumberInRange(1, 3);
//        int size = 30 + (int) ((float) Math.random() * 170);
//        float difficulty = 0.7f + rand.nextFloat() * 0.3f;

        // Actual fleet generation call
//        int playerFP = generateFleet(player, api, FleetSide.PLAYER, (int) (size * difficulty), QUALITY_FACTORS.get(player.getId()) == null ? rand.nextFloat() : QUALITY_FACTORS.get(player.getId()));
        int playerFP = generateFleet(player, api, FleetSide.PLAYER, size, QUALITY_FACTORS.get(player.getId()) == null ? rand.nextFloat() : QUALITY_FACTORS.get(player.getId()));
        int enemyFP = generateFleet(enemy, api, FleetSide.ENEMY, size, QUALITY_FACTORS.get(enemy.getId()) == null ? rand.nextFloat() : QUALITY_FACTORS.get(enemy.getId()));

        // Set up the map
        float width = 13000f + 13000f * (size / 200);
        float height = 13000f + 13000f * (size / 200);
        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        int objectiveCount = (int) Math.floor(size / 35f);

        while (objectiveCount > 0) {
            String type = OBJECTIVE_TYPES[rand.nextInt(3)];

            if (objectiveCount == 1) {
                api.addObjective(0, 0, type);
                objectiveCount -= 1;
            } else {
                float theta = (float) (Math.random() * Math.PI);
                double radius = Math.min(width, height);
                radius = radius * 0.1 + radius * 0.3 * Math.random();
                int x = (int) (Math.cos(theta) * radius);
                int y = (int) (Math.sin(theta) * radius);
                api.addObjective(x, -y, type);
                api.addObjective(-x, y, type);
                objectiveCount -= 2;
            }
        }

        // Show the factions versus and their FP
        api.addBriefingItem(player.getDisplayName() + "  (" + playerFP + ")   vs.  " + enemy.getDisplayName() + "  (" + enemyFP + ")");

        // Chance of generating a nebula
        float nebulaChance = MathUtils.getRandomNumberInRange(0, 100);

        // So basically half the time (if less than 50 out of 100)
        if (nebulaChance < 50) {
            // Do regular nebula generation
            float nebulaCount = 10 + (float) Math.random() * 30;
            float nebulaSize = (float) Math.random();

            for (int i = 0; i < nebulaCount; i++) {
                float x = (float) Math.random() * width - width / 2;
                float y = (float) Math.random() * height - height / 2;
                float nebulaRadius = (400f + (float) Math.random() * 1600f) * nebulaSize;
                api.addNebula(x, y, nebulaRadius);
            }

            api.addBriefingItem("Nebulosity:  " + (int) (((nebulaCount * nebulaSize) / 40f) * 100) + "%");

        } else {
            // Mention that there is no nebula, this line could be commented out if you don't want this line item added
            api.addBriefingItem("Nebulosity: N/A");
        }

        // Asteroid generation random chance
        float asteroidChance = MathUtils.getRandomNumberInRange(0, 100);

        // If chance is less than 50
        if (asteroidChance < 50) {

            // Do regular asteroid generation
            // Minimum asteroid speed between 15 and 50
            int minAsteroidSpeed = MathUtils.getRandomNumberInRange(15, 50);

            // Asteroid count
            int asteroidCount = size + (int) (size * 4 * Math.pow(Math.random(), 2));

            // Add the asteroid field
            api.addAsteroidField(
                    minX + width * 0.5f, // X
                    minY + height * 0.5f, // Y
                    rand.nextInt(90) - 45 + (rand.nextInt() % 2) * 180, // Angle
                    100 + (int) (Math.random() * height / 2), // Width
                    minAsteroidSpeed, // Min speed
                    minAsteroidSpeed * 1.1f, // Max speed
                    asteroidCount); // Count

            api.addBriefingItem("Asteroid Density:  " + (int) ((asteroidCount / 1000f) * 100) + "%");
            api.addBriefingItem("Asteroid Speed:  " + minAsteroidSpeed);
        } else {
            // If not asteroid field, specify as N/A, you can comment this out
            api.addBriefingItem("Asteroid Density: N/A");
            api.addBriefingItem("Asteroid Speed: N/A");
        }

    }

    protected boolean chooseFactions(String playerFactionId, String enemyFactionId, boolean fullRandom, int i) {
        return chooseFactions(playerFactionId, enemyFactionId, fullRandom, i, i);
    }

    protected boolean chooseFactions(String playerFactionId, String enemyFactionId, boolean fullRandom, int i, int j) {
        player = Global.getSector().getFaction(playerFactionId);
        enemy = Global.getSector().getFaction(enemyFactionId);
        this.fullRandom = fullRandom;
        boolean outOfBorder = false;
        List<FactionAPI> acceptableFactions = new ArrayList<>(40);
        if (!fullRandom) {
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
        } else {
            // or not?
            acceptableFactions.addAll(Global.getSector().getAllFactions());
            // and opps...
            for (FactionAPI faction : Global.getSector().getAllFactions()) {
                if (!faction.isShowInIntelTab() || faction.isPlayerFaction()) {
                    acceptableFactions.remove(faction);
                }
            }
//            acceptableFactions.remove(Global.getSector().getFaction(Factions.PLAYER));
//            acceptableFactions.remove(Global.getSector().getFaction(Factions.NEUTRAL));
//            acceptableFactions.remove(Global.getSector().getFaction(Factions.KOL));
        }
        if (i > acceptableFactions.size()) {
            i = 0;
            outOfBorder = true;
        }
        if (j > acceptableFactions.size()) {
            j = 0;
            outOfBorder = true;
        }
        // If the player faction is not null, and is not specified a parameter in the input, choose from one of the acceptable factions
        player = player != null ? player : acceptableFactions.get(i);

        acceptableFactions.remove(player);

        enemy = enemy != null ? enemy : acceptableFactions.get(j);
        return outOfBorder;
    }

    // Generate a fleet from the campaign fleet generator
    int generateFleet(FactionAPI faction, MissionDefinitionAPI api, FleetSide side, int size, float qf) {

        float combat = 0f;
        float tanker = 0f;
        float freighter = 0f;
        String fleetType = FleetTypes.PATROL_SMALL;
        switch (size) {
            case 1:
                combat = Math.round(50f + (float) Math.random() * 10f);
                fleetType = FleetTypes.PATROL_SMALL;
                break;
            case 2:
                combat = Math.round(100f + (float) Math.random() * 20f);
                fleetType = FleetTypes.PATROL_MEDIUM;
                break;
            case 3:
                combat = Math.round(200f + (float) Math.random() * 50f);
                fleetType = FleetTypes.PATROL_LARGE;
                tanker = 2f;
                freighter = 2f;
                break;
            default:
        }
        combat *= 1.25f;
        FleetParamsV3 fleetParam = new FleetParamsV3(
                null,
                new Vector2f(),
                faction.getId(),
                0f, // qualityOverride
                fleetType,
                combat, // combatPts
                freighter, // freighterPts 
                tanker, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                qf // qualityBonus
        );
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(fleetParam);
        
//        CampaignFleetAPI fleet = FleetFactory.createGenericFleet(faction.getId(), side.toString(), qf, minFP);
        if (fleet.getMembersWithFightersCopy().isEmpty()) return 0;
        for (FleetMemberAPI m : fleet.getMembersWithFightersCopy()) {
            String variant = m.getVariant().getHullVariantId();
            if (m.getType() == FleetMemberType.FIGHTER_WING) {
//                api.addToFleet(side, variant, FleetMemberType.FIGHTER_WING, false);
            } else {
                api.addToFleet(side, variant, FleetMemberType.SHIP, m.getShipName(), fleet.getFlagship() == m);
            }
        }
        return fleet.getFleetPoints();
    }
}
