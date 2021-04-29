//by Deathfly
//just a mock AI that can do nothing. use for manual control the shield drone.
package data.scripts.AIs.Ships;

import com.fs.starfarer.api.combat.ShipAIConfig;
import com.fs.starfarer.api.combat.ShipAIPlugin;
import com.fs.starfarer.api.combat.ShipwideAIFlags;

public final class Neutrino_NoAI implements ShipAIPlugin {

    private final ShipwideAIFlags AIFlages;

    public Neutrino_NoAI() {
        AIFlages = new ShipwideAIFlags();
    }

    @Override
    public void setDoNotFireDelay(float amount) {
    }

    @Override
    public void forceCircumstanceEvaluation() {
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public boolean needsRefit() {
        return false;
    }

    @Override
    public ShipwideAIFlags getAIFlags() {
        return AIFlages;
    }

    @Override
    public void cancelCurrentManeuver() {        
    }

    @Override
    public ShipAIConfig getConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
