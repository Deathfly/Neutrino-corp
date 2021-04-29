//by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import static data.scripts.util.AdvForce.applyMomentum;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class NeutTractorbeamEffect implements BeamEffectPlugin {

    private float coreAlpha = 0;
    private Color coreColor;

    public NeutTractorbeamEffect() {

    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        if (engine.isPaused()) {
            return;
        }
        CombatEntityAPI target = beam.getDamageTarget();
        if (target != null && beam.didDamageThisFrame()) {
            target.setMass(target.getMass() + 1000);
            applyMomentum(target, beam.getTo(), Vector2f.sub(beam.getTo(), beam.getFrom(), null), -100, false);
            target.setMass(target.getMass() - 1000);
            coreAlpha = coreAlpha < 1 ? coreAlpha + 0.1f : 1f;
            
        } else {
            coreAlpha = coreAlpha > 0 ? coreAlpha - 0.1f : 0f;
        }
        coreAlpha = coreAlpha < 0 ? 0:coreAlpha;
        coreAlpha = coreAlpha > 1 ? 1:coreAlpha;
        coreColor = new Color((int) (255 * coreAlpha), (int) (255 * coreAlpha), (int) (255 * coreAlpha));
        beam.setCoreColor(coreColor);
    }
}
