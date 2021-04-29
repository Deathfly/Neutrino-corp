// by Deathfly
package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.combat.entities.PlasmaShot;
import com.fs.starfarer.combat.entities.Ship;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_CombatPluginCreator {

    public static final BaseEveryFrameCombatPlugin createPhontoVFXPlugin(final DamagingProjectileAPI anchor, final String weaponID) {

        BaseEveryFrameCombatPlugin tem = new BaseEveryFrameCombatPlugin() {
            CombatEntityAPI plasma = Global.getCombatEngine().spawnProjectile(anchor.getSource(), anchor.getWeapon(), weaponID, anchor.getLocation(), MathUtils.getRandomNumberInRange(0, 360), anchor.getVelocity());
            PlasmaShot plamsa_shot = (PlasmaShot) plasma;

            @Override
            public void advance(float amount, List<InputEventAPI> events) {
                CombatEngineAPI engine = Global.getCombatEngine();
                if (engine.isPaused()) {
                    return;
                }
                if (engine.isEntityInPlay(anchor)) {
                    plasma.getLocation().set(anchor.getLocation());
                    if (anchor.isFading()) {
                        plamsa_shot.setChargeProgress(((MissileAPI) anchor).getCurrentBaseAlpha());
                    }
                } else {//missile hit or destoryed
                    engine.removeEntity(plasma);
                    Global.getCombatEngine().removePlugin(this);
                }
            }
        };
        Global.getCombatEngine().addPlugin(tem);
        return tem;
    }

    public static EveryFrameCombatPlugin createDERPOnRemovePlugin(CombatEntityAPI entityToCheck) {
        BaseEveryFrameCombatPlugin tem = new DERPOnRemovePlugin(entityToCheck);
        Global.getCombatEngine().addPlugin(tem);
        return tem;
    }

    public static class DERPOnRemovePlugin extends EntityOnRemovePlugin {

        public DERPOnRemovePlugin(CombatEntityAPI entityToCheck) {
            super(entityToCheck);
        }

        @Override
        public void advance(float amount, List<InputEventAPI> events) {
            super.advance(amount, events);
            if (!flag) {
                return;
            }
            if (entity.getHitpoints() > 0) {
                engine.addHitParticle(entity.getLocation(), new Vector2f(0, 0), 80, 160, 8.5f, new Color(34, 144, 255, 255));
                engine.addSmoothParticle(entity.getLocation(), new Vector2f(0, 0), 175, 200, 10, new Color(222, 82, 38, 255));
                DamagingProjectileAPI proj = (DamagingProjectileAPI) entity;
                engine.spawnProjectile(proj.getSource(), proj.getWeapon(), "neutrino_derp_sub", proj.getLocation(), MathUtils.getRandomNumberInRange(0, 360), new Vector2f(0, 0));
            }
        }
    }

    public abstract static class EntityOnRemovePlugin extends BaseEveryFrameCombatPlugin {

        CombatEngineAPI engine = Global.getCombatEngine();
        CombatEntityAPI entity;
        boolean flag = false;

        public EntityOnRemovePlugin(CombatEntityAPI entityToCheck) {
            entity = entityToCheck;
        }

        @Override
        public void advance(float amount, List<InputEventAPI> events) {
            super.advance(amount, events);
            if (engine.isEntityInPlay(entity)) {
                return;
            }
            engine.removePlugin(this);
            flag = true;
        }

    }

    public static final EveryFrameCombatPlugin createWingPhasingPlugin(final ShipAPI fighter, final float fadeInTime, final boolean isLanding) {
        EveryFrameCombatPlugin tem = new BaseEveryFrameCombatPlugin() {
            float elapsed = 0f;
            float effectLevel = 0f;

            @Override
            public void advance(float amount, List<InputEventAPI> events) {
                CombatEngineAPI engine = Global.getCombatEngine();
                if (engine.isPaused()) {
                    return;
                }
                if (!fighter.isAlive()) {//should not happen but just in case
                    engine.removeEntity(fighter);
                    engine.removePlugin(this);
                    return;
                }
                //VFX
                if (fadeInTime == 0) {
                    effectLevel = isLanding ? 1 : 0;
                } else {
                    effectLevel = isLanding ? elapsed / fadeInTime : 1 - (elapsed / fadeInTime);
                }
                elapsed += amount * engine.getTimeMult().computeMultMod();
                fighter.getMutableStats().getTimeMult().modifyMult(this.toString(), 1 + effectLevel * 2);
                if (isLanding) {
                    fighter.abortLanding();
                    Ship s = (Ship) fighter;
                    s.setSinceLaunch(4);
                }
                fighter.setJitter(fighter.toString() + 1, new Color(255, 175, 255, 150), effectLevel * 2, 2, 2);
                fighter.setJitterUnder(fighter.toString() + 2, new Color(255, 175, 255, 150), effectLevel * 2, 7, 3);
//                fighter.getSpriteAPI().setAdditiveBlend();
                Ship s = (Ship) fighter;
                s.setApplyExtraAlphaToEngines(true);
                s.setExtraAlphaMult(1 - effectLevel);
                Vector2f vel = s.getVelocity();
                float duration = effectLevel * 0.3f;
                duration *= s.getVelocity().length() / s.getMaxSpeedWithoutBoost();

                float length = -1.5f;

                if (!fighter.getEngineController().isFlamedOut() && !fighter.getEngineController().isFlamingOut()) {
                    fighter.addAfterimage(new Color(255, 175, 255, 60), 0, 0, vel.getX() * length, vel.getY() * length, 3, 0, duration, duration, false, true, false);
                }
//                s.getSprite().setAlphaMult(0);
//                s.getSprite().setColor(new Color(255, 255, 255, 0));
                //turn down shield and hold fire
                if (fighter.getShield() != null) {
                    fighter.getShield().toggleOff();
                    fighter.blockCommandForOneFrame(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK);
                }
                fighter.blockCommandForOneFrame(ShipCommand.FIRE);
                fighter.blockCommandForOneFrame(ShipCommand.USE_SYSTEM);
                //when done
                if (elapsed > fadeInTime) {
                    fighter.setPhased(false);
                    if (isLanding) {
                        if (fighter.getWing() != null //just in case 
                                && fighter.getWing().getSource() != null
                                && fighter.getWing().getSource().getShip().isAlive()) {
                            fighter.getWing().getSource().land(fighter);
                        }
                        engine.removeEntity(fighter);
                    } else {
                        //unphase
                        fighter.getSpriteAPI().setNormalBlend();
                        fighter.getMutableStats().getTimeMult().unmodify(this.toString());
                    }
                    //self remove
                    Global.getCombatEngine().removePlugin(this);
                }
            }
        };
        Global.getCombatEngine().addPlugin(tem);
        return tem;
    }
}
