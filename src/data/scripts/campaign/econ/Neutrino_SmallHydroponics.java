package data.scripts.campaign.econ;

import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.econ.WorldFarming;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
//import static com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin.getProductionMult;

public class Neutrino_SmallHydroponics extends WorldFarming {

    public Neutrino_SmallHydroponics() {
        super(0.25f, 0.00001f, 10000);
    }

    @Override
    public void apply(String id) {
//        market.getDemand(Commodities.CREW).getDemand().modifyFlat(id, 100);
//        market.getDemand(Commodities.CREW).getNonConsumingDemand().modifyFlat(id, 100 * 0.99f);
//
//        float crewDemandMet = market.getDemand(Commodities.CREW).getClampedFractionMet();
//
//        market.getDemand(Commodities.ORGANICS).getDemand().modifyFlat(id, 2500 * crewDemandMet);
//        market.getDemand(Commodities.HEAVY_MACHINERY).getDemand().modifyFlat(id, 100 * crewDemandMet);
//
//        float productionMult = getProductionMult(market, Commodities.VOLATILES);
//
//        market.getCommodityData(Commodities.FOOD).getSupply().modifyFlat(id, 5000 * crewDemandMet * productionMult);
    }

    @Override
    public boolean showIcon() {
        return true;
    }

    
    
    @Override
    public void unapply(String id) {
//        market.getDemand(Commodities.CREW).getDemand().unmodify(id);
//        market.getDemand(Commodities.CREW).getNonConsumingDemand().unmodify(id);
//
//        market.getDemand(Commodities.ORGANICS).getDemand().unmodify(id);
//        market.getDemand(Commodities.HEAVY_MACHINERY).getDemand().unmodify(id);
//
//        market.getCommodityData(Commodities.FOOD).getSupply().unmodify(id);
    }

}
