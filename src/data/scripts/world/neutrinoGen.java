package data.scripts.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.RepLevel;

import com.fs.starfarer.api.impl.campaign.ids.Factions;

import data.scripts.world.systems.CoronaAustralis;
import data.scripts.world.systems.CorvusAddon;
import data.scripts.world.systems.Exelion;

@SuppressWarnings("unchecked")
public class neutrinoGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        new CoronaAustralis().generate(sector);
//        new Exelion().generate(sector);
        new CorvusAddon().generate(sector);
 	
        FactionAPI neutrinocorp = sector.getFaction("neutrinocorp");
        neutrinocorp.setRelationship(Factions.PLAYER, RepLevel.NEUTRAL);

        neutrinocorp.setRelationship(Factions.HEGEMONY, RepLevel.NEUTRAL);
        neutrinocorp.setRelationship(Factions.TRITACHYON, RepLevel.HOSTILE);
        neutrinocorp.setRelationship(Factions.PIRATES, RepLevel.HOSTILE);
        neutrinocorp.setRelationship(Factions.INDEPENDENT, RepLevel.FAVORABLE);
        neutrinocorp.setRelationship(Factions.LUDDIC_CHURCH, RepLevel.INHOSPITABLE);
        neutrinocorp.setRelationship(Factions.KOL, RepLevel.HOSTILE);
        neutrinocorp.setRelationship(Factions.LUDDIC_PATH, RepLevel.VENGEFUL);
        neutrinocorp.setRelationship(Factions.PERSEAN, RepLevel.FAVORABLE);
        neutrinocorp.setRelationship("lotus_pirates", RepLevel.HOSTILE);
        neutrinocorp.setRelationship("exigency", RepLevel.HOSTILE);
        neutrinocorp.setRelationship("exipirated", RepLevel.HOSTILE);
        neutrinocorp.setRelationship("diableavionics", RepLevel.SUSPICIOUS);
//        List<FactionAPI> factions = sector.getAllFactions();
//        for (FactionAPI faction: factions){
//            if (!faction.isShowInIntelTab()){
//                neutrinocorp.setRelationship(faction.getId(), RepLevel.NEUTRAL);
//            }
//        }

    }
}
