package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;

public class NeutrinoAddMarket {

    public static MarketAPI addMarketplace(
            String factionID,
            String econGroup,
            SectorEntityToken primaryEntity,
            ArrayList<SectorEntityToken> connectedEntities,
            String name,
            int size,
            ArrayList<String> marketConditions,
            ArrayList<String> marketIndustries,
            ArrayList<String> submarkets,
            float tarrif) {

        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID;

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setEconGroup(econGroup);
        newMarket.setPrimaryEntity(primaryEntity);
//        newMarket.setImmigrationClosed(true);
//        newMarket.setBaseSmugglingStabilityValue(0);
        newMarket.getTariff().modifyFlat("generator", tarrif);
        newMarket.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }
//        newMarket.setPlanetConditionMarketOnly(true); //overriding setting to apply planet only market conditions on it.
//        long seed = StarSystemGenerator.random.nextLong();
//        newMarket.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, seed);

        if (null != marketConditions) {
            for (String condition : marketConditions) {
                newMarket.addCondition(condition);
            }
        }
        if (null != newMarket.getConditions() && !newMarket.getConditions().isEmpty()) {
            for(MarketConditionAPI condition:newMarket.getConditions()){
                condition.setSurveyed(true);
            }
        }
        if (null != marketIndustries) {
            for (String industry : marketIndustries) {
                newMarket.addIndustry(industry);
            }
        }
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }
        globalEconomy.addMarket(newMarket, true);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);
//        newMarket.reapplyConditions();
//        newMarket.reapplyIndustries();
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        return newMarket;
    }
}
