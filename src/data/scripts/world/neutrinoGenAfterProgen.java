package data.scripts.world;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import data.scripts.world.systems.Exelion;

@SuppressWarnings("unchecked")
public class neutrinoGenAfterProgen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        new Exelion().generate(sector);
    }
}
