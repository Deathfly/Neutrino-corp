//by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatAsteroidAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.BeamWeaponRay;
import java.awt.Color;

public class NeutPACBeamEffect implements BeamEffectPlugin {

    public NeutPACBeamEffect() {
    }
    private boolean runOnce = false;
    private Color color1;
    private Color color2 = Color.black; 
    
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        BeamWeaponRay b = (BeamWeaponRay) beam;
        if (!runOnce) {
            b.getActiveLayers().clear();
            b.getActiveLayers().add(CombatEngineLayers.BELOW_SHIPS_LAYER);
            beam.getDamage().setMultiplier(0.75f);
            color1 = beam.getCoreColor();
            runOnce = true;     
        }
        ShipAPI playerShip = engine.getPlayerShip();
        if (beam.getSource() == playerShip) {
            engine.maintainStatusForPlayerShip("NeutPACEffect", "graphics/icons/tactical/venting_flux2.png", "Main Weapon Firing", "Vents Blocked", true);
        }
        if (engine.isPaused()) {
            return;
        }
//        Vector2f tem = Vector2f.sub(b.getFrom(), b.getTo(), null);
//        VectorUtils.resize(tem, 0.001f);
//        b.getTo().set(Vector2f.add(tem, b.getTo(), null));
//        b.render();
        beam.getSource().blockCommandForOneFrame(ShipCommand.VENT_FLUX);
        float chargeLvl = beam.getWeapon().getChargeLevel();
        if (chargeLvl<1){
            b.setCoreColor(Misc.interpolateColor(color2,color1,chargeLvl*chargeLvl));
        }
        if (beam.getDamageTarget() != null) {
            CombatEntityAPI target = beam.getDamageTarget();
            if (target instanceof CombatAsteroidAPI) {
                engine.applyDamage(target, beam.getTo(), 5000 * amount, DamageType.OTHER, 0, false, false, beam.getSource());
            } else if (target instanceof ShipAPI && beam.didDamageThisFrame()) {
                ShipAPI targetShip = (ShipAPI)target;                
                targetShip.getMutableStats().getEffectiveArmorBonus().modifyMult("NeutPACBeamEffect", 0);
                targetShip.getMutableStats().getArmorDamageTakenMult().modifyMult("NeutPACBeamEffect", 5);
                engine.applyDamage(target, beam.getTo(), beam.getDamage().getDamage()*0.025f *beam.getHitGlowBrightness(), DamageType.OTHER, 0, false, false, beam.getSource());
                targetShip.getMutableStats().getEffectiveArmorBonus().unmodifyMult("NeutPACBeamEffect");
                targetShip.getMutableStats().getArmorDamageTakenMult().unmodifyMult("NeutPACBeamEffect");
                
            }
        }
    }
}
