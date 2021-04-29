// by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.MovingRay;
import com.fs.starfarer.combat.entities.Ship;
import data.scripts.plugins.Neutrino_ExtraParticlePlugin;
import data.scripts.util.Neutrino_ParticlesEffectLib;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class NeutHERPEffect implements BeamEffectPlugin {

    public NeutHERPEffect() {
    }
    private final IntervalUtil interval = new IntervalUtil(0.1F, 0.1F);
    private final IntervalUtil interval1 = new IntervalUtil(0.09F, 0.11F);
    private final IntervalUtil interval2 = new IntervalUtil(0.11F, 0.13F);
    private final IntervalUtil interval3 = new IntervalUtil(0.13F, 0.15F);
    private final IntervalUtil interval4 = new IntervalUtil(0.15F, 0.17F);
    private final IntervalUtil interval5 = new IntervalUtil(0.17F, 0.19F);

    private int count1 = 20;
    private int count2 = 20;
    private int count3 = 20;
    private int count4 = 20;
    private int count5 = 20;
    
    private int randomRange = 35;
   
    private final static Color Color1 = new Color(127, 50, 255, 255);
    private final static Color Color2 = new Color(255, 255, 255, 200);

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        ShipAPI playerShip = engine.getPlayerShip();
        if (beam.getSource() == playerShip){
            engine.maintainStatusForPlayerShip("NeutHERPEffect", "graphics/icons/tactical/venting_flux2.png", "Main Weapon Firing", "Vents Blocked", true);
        }
        if (engine.isPaused()) {
            return;
        }
        ((Ship) beam.getSource()).blockCommandForOneFrame(com.fs.starfarer.combat.entities.Ship.oo.valueOf("VENT_FLUX"));
        Vector2f vel = beam.getSource().getVelocity();
        float facing = beam.getWeapon().getCurrAngle();
        float brightness = beam.getBrightness();
        engine.addHitParticle(beam.getFrom(), vel, 85, brightness, 0.0001f, Color1);
        engine.addHitParticle(beam.getFrom(), vel, 60, brightness, 0.0001f, Color2);
        if (brightness >= 0.4) {
            interval.advance(amount);
            if (interval.intervalElapsed()) {
                Neutrino_ExtraParticlePlugin.AddParticlesOnSegmentEx(
                        beam.getFrom(),
                        MathUtils.getPointOnCircumference(beam.getFrom(), 300, facing),
                        10 * brightness,
                        1,
                        false,
                        20, 2,
                        brightness * 300, beam.getBrightness() * 750,
                        4, 10,
                        brightness, 1f,
                        0.05f, 0.2f,
                        Misc.interpolateColor(Color1, Color2, brightness));

            }
        }
        if (brightness >= 1) {
            if (count1 > 0) {
                interval1.advance(amount);
                if (interval1.intervalElapsed()) {
                    CombatEntityAPI e = engine.spawnProjectile(
                            beam.getSource(),
                            beam.getWeapon(),
                            "neutrino_herp1",
                            MathUtils.getRandomPointInCircle(beam.getFrom(), randomRange),
                            MathUtils.clampAngle(facing + MathUtils.getRandomNumberInRange(-1.5f, 1.5f)),
                            vel);
                    MovingRay r = (MovingRay) e;
                    r.getActiveLayers().clear();
                    r.getActiveLayers().add(CombatEngineLayers.UNDER_SHIPS_LAYER);
                    count1--;
                }
            }
            if (count2 > 0) {
                interval2.advance(amount);
                if (interval2.intervalElapsed()) {
                    CombatEntityAPI e = engine.spawnProjectile(
                            beam.getSource(),
                            beam.getWeapon(),
                            "neutrino_herp2",
                            MathUtils.getRandomPointInCircle(beam.getFrom(), randomRange),
                            MathUtils.clampAngle(facing + MathUtils.getRandomNumberInRange(-1.5f, 1.5f)),
                            vel);
                    MovingRay r = (MovingRay) e;
                    r.getActiveLayers().clear();
                    r.getActiveLayers().add(CombatEngineLayers.UNDER_SHIPS_LAYER);
                    count2--;
                }
            }
            if (count3 > 0) {
                interval3.advance(amount);
                if (interval3.intervalElapsed()) {
                    CombatEntityAPI e = engine.spawnProjectile(
                            beam.getSource(),
                            beam.getWeapon(),
                            "neutrino_herp3",
                            MathUtils.getRandomPointInCircle(beam.getFrom(), randomRange),
                            MathUtils.clampAngle(facing + MathUtils.getRandomNumberInRange(-1.5f, 1.5f)),
                            vel);
                    MovingRay r = (MovingRay) e;
                    r.getActiveLayers().clear();
                    r.getActiveLayers().add(CombatEngineLayers.UNDER_SHIPS_LAYER);
                    count3--;
                }
            }
            if (count4 > 0) {
                interval4.advance(amount);
                if (interval4.intervalElapsed()) {
                    CombatEntityAPI e = engine.spawnProjectile(
                            beam.getSource(),
                            beam.getWeapon(),
                            "neutrino_herp4",
                            MathUtils.getRandomPointInCircle(beam.getFrom(), randomRange),
                            MathUtils.clampAngle(facing + MathUtils.getRandomNumberInRange(-1.5f, 1.5f)),
                            vel);
                    MovingRay r = (MovingRay) e;
                    r.getActiveLayers().clear();
                    r.getActiveLayers().add(CombatEngineLayers.UNDER_SHIPS_LAYER);
                    count4--;
                }
            }
            if (count5 > 0) {
                interval5.advance(amount);
                if (interval5.intervalElapsed()) {
                    CombatEntityAPI e = engine.spawnProjectile(
                            beam.getSource(),
                            beam.getWeapon(),
                            "neutrino_herp5",
                            MathUtils.getRandomPointInCircle(beam.getFrom(), randomRange),
                            MathUtils.clampAngle(facing + MathUtils.getRandomNumberInRange(-1.5f, 1.5f)),
                            vel);
                    MovingRay r = (MovingRay) e;
                    r.getActiveLayers().clear();
                    r.getActiveLayers().add(CombatEngineLayers.UNDER_SHIPS_LAYER);
                    count5--;
                }
            }
        }
    }
}
