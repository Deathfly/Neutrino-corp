//by Deathfly
package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.Ship;
import static data.scripts.plugins.Neutrino_CombatPluginCreator.createWingPhasingPlugin;
import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class NeutRingEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    //opps, seems like I use too many hard coded magic numbers in this one, too bad.
    private final float idleFPS = 10f;
    private final float activeFPS = 24f;
//    private final float froceVentFPSMult = 2f;
    private final float deltaFPS = 6f;
    private final Color phaseColor = new Color(255, 175, 255, 255);
    private float activeTimer = 0f;

    private final String id = "Ring";
//    private final float ringVentRange = 0.4f;

    private Set<ShipAPI> returning = new HashSet<>();
    private Set<ShipAPI> tem = new HashSet<>();

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        ShipAPI ship = weapon.getShip();

//        // flux part
//        float fluxLevel = ship.getFluxTracker().getFluxLevel();
//        float overFluxRatio = 0;
//        if (ship.isPhased() && fluxLevel > 1 - ringVentRange) {
//            overFluxRatio = (fluxLevel + ringVentRange - 1) / ringVentRange;
////            float phaseCostRatio = 1 - 2 * overFluxRatio;
////            ship.getMutableStats().getPhaseCloakUpkeepCostBonus().modifyMult(id, phaseCostRatio);
//            float timeMult = ((fluxLevel - (1 - ringVentRange)) * 10f) + 1;
//            ship.getMutableStats().getTimeMult().modifyMult(id, timeMult);
//            if (ship == engine.getPlayerShip()) {
//                engine.getTimeMult().modifyMult(id, 1 / timeMult);
//            } else {
//                engine.getTimeMult().unmodify(id);
//            }
//        } else {
//            ship.getMutableStats().getPhaseCloakUpkeepCostBonus().unmodifyMult(id);
//            ship.getMutableStats().getTimeMult().unmodify();
//            engine.getTimeMult().unmodify(id);
//        }
//
        // Animation part
        AnimationAPI anime = weapon.getAnimation();
        float currentFPS = anime.getFrameRate();
        float progress = Math.min(1, (currentFPS - idleFPS) / (activeFPS - idleFPS));
        progress = progress < 0 ? 0 : progress;
        Color color = Misc.interpolateColor(Color.WHITE, phaseColor, progress);
        weapon.getSprite().setColor(color);
        if (ship.isHulk()) {
            if (currentFPS > 0) {
                currentFPS -= deltaFPS * amount;
                currentFPS = currentFPS < 0 ? 0 : currentFPS;
                anime.setFrameRate(currentFPS);
                return;
            } else {
                weapon.getSprite().setNormalBlend();
                return;
            }
        } else if (activeTimer > 0 && currentFPS < activeFPS) {
            currentFPS += deltaFPS * amount;
            currentFPS = currentFPS > activeFPS ? activeFPS : currentFPS;
            anime.setFrameRate(currentFPS);
        } else if (activeTimer < 0 && currentFPS > idleFPS) {
            currentFPS -= deltaFPS * amount;
            currentFPS = currentFPS < idleFPS ? idleFPS : currentFPS;
            anime.setFrameRate(currentFPS);
        }
        if (activeTimer > -2) {
            activeTimer -= amount;
        }

        for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
            if (bay.getWing() != null && !bay.getWing().getWingMembers().isEmpty()) {
                returning.removeAll(bay.getWing().getWingMembers());
                tem.addAll(bay.getWing().getWingMembers());
                for (ShipAPI fighter : bay.getWing().getWingMembers()) {
                    Ship s = (Ship) fighter;
                    if (s.isAnimatedLaunch() && s.getSinceLaunch() == 0) {
                        activeTimer = 2;
                        s.setSinceLaunch(4f);
                        s.advance(4f);
//                        fighter.getSpriteAPI().setAlphaMult(0.1f);
                        s.setPhased(true);
                        Vector2f.add(ship.getLocation(), MathUtils.getRandomPointOnCircumference(null, 50 + ship.getCollisionRadius()), s.getLocation());
                        createWingPhasingPlugin(fighter, 1f, false);
                        if (ship.isPhased()) {
                            if (ship.getFluxLevel() < 0.9f) {
                                ship.getFluxTracker().increaseFlux(ship.getFluxTracker().getMaxFlux() / 10, true);
                            } else {
                                ship.getFluxTracker().beginOverloadWithTotalBaseDuration(0.1f);
                            }
                        }
//                        s.setColor(new Color(255,255,255,50));
//                        s.fadeToColor(3, new Color(255,255,255,255));
//                        s.getSprite().setAlphaMult(0);
                    }
                }
            }
        }
        if (returning != null && !returning.isEmpty()) {
            for (Iterator<ShipAPI> iterator = returning.iterator(); iterator.hasNext();) {
                ShipAPI next = iterator.next();
                if (next.isLanding() || MathUtils.isWithinRange(ship, next, 50)) {
                    activeTimer = 1.5f;
                    Ship s = (Ship) next;
                    s.abortLanding();
                    s.setSinceLaunch(4f);
                    s.advance(4f);
//                    s.setAnimatedLaunch();
//                    s.advance(10f);
                    s.setPhased(true);
//                    landingFighter.abortLanding();
//                    landingFighter.setPhased(true);
                    createWingPhasingPlugin(next, 1f, true);
                    iterator.remove();
                }
            }
        }
        returning.addAll(tem);
        tem.clear();
        ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
            if (playerShip != null && !playerShip.isHulk() && playerShip == ship && ship.isPhased()) {
                Global.getCombatEngine().maintainStatusForPlayerShip(id + 1, "graphics/neut/icons/hullsys/neutrino_PhaseCloak_StatusIcon.png", "Phase Ring", "Generate hard flux while launching fighters", activeTimer > 1.5f);
            }
//        // Animation part
//        AnimationAPI anime = weapon.getAnimation();
//        float currentFPS = anime.getFrameRate();
//        Color color = Misc.interpolateColor(Color.WHITE, phaseColor, ship.getPhaseCloak().getEffectLevel());
//        if (ship.isHulk()) {
//            if (currentFPS > 0) {
//                float setFPS = currentFPS - deltaFPS * amount;
//                if (setFPS < 0) {
//                    setFPS = 0;
//                }
//                anime.setFrameRate(setFPS);
//                weapon.getSprite().setColor(color);
//            } else {
//                return;
//            }
//        } else if (ship.getPhaseCloak().isActive() && currentFPS < activeFPS * (overFluxRatio * froceVentFPSMult)) {
//            float setFPS = currentFPS + deltaFPS * amount;
//            if (setFPS > activeFPS * (overFluxRatio * froceVentFPSMult)) {
//                setFPS = activeFPS * (overFluxRatio * froceVentFPSMult);
//            }
//            anime.setFrameRate(setFPS);
//            weapon.getSprite().setColor(color);
//            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
//            if (Global.getCurrentState() == GameState.COMBAT && playerShip != null && !playerShip.isHulk() && playerShip == ship) {
//                Global.getCombatEngine().maintainStatusForPlayerShip(id + 1,
//                        "graphics/neut/icons/hullsys/neutrino_PhaseCloak_StatusIcon.png", "Phase Ring", "Venting hard flux into p-space", false);
//                Global.getCombatEngine().maintainStatusForPlayerShip(id + 2,
//                        "graphics/neut/icons/hullsys/neutrino_PhaseCloak_StatusIcon.png", "Phase Ring", "Ship speed reduced", true);
//            }
//        } else if (!ship.getPhaseCloak().isCoolingDown() && currentFPS > idleFPS) {
//            float setFPS = currentFPS - deltaFPS * amount;
//            if (setFPS < idleFPS) {
//                setFPS = idleFPS;
//            }
//            anime.setFrameRate(setFPS);
//            weapon.getSprite().setColor(color);
//        }
    } 
}
