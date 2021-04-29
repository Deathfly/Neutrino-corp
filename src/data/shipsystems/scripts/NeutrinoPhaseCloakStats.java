package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import static com.fs.starfarer.api.impl.combat.PhaseCloakStats.MAX_TIME_MULT;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class NeutrinoPhaseCloakStats extends BaseShipSystemScript {

    private final Object STATUSKEY1 = new Object();
    private final Object STATUSKEY2 = new Object();
    private final Object JETTER1 = new Object();
    private final Object JETTER2 = new Object();
    private final float SHIP_ALPHA_MULT = 0.2f;
    private final float MAX_TIME_MULT = 3f;
    private final IntervalUtil interval = new IntervalUtil(0.1f, 0.1f);

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        boolean player = false;
        ShipSystemAPI cloak;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
            cloak = ship.getPhaseCloak();
            if (cloak == null) {
                cloak = ship.getSystem();
            }
            if (cloak == null) {
                return;
            }
        } else {
            return;
        }

        if (player) {
            maintainStatus(ship, state, effectLevel);
        }
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
        if (state == State.IDLE || state == State.COOLDOWN) {
            unapply(stats, id);
            return;
        }
//        if (state == State.COOLDOWN) {
//            ship.setPhased(false);
//        }
        ship.setPhased(true);

        ship.setExtraAlphaMult(1f - (1f - SHIP_ALPHA_MULT) * effectLevel);

        ship.setApplyExtraAlphaToEngines(true);
        float shipTimeMult;
        float maxTimeMult = getMaxTimeMult(stats);
        if (state == State.ACTIVE || state == State.IN) {
            shipTimeMult = 1f + (maxTimeMult - 1f) * effectLevel;
            ship.getMutableStats().getFighterRefitTimeMult().modifyMult(id, 1/shipTimeMult);
        } else {
            float cooldown = cloak.getCooldownRemaining();
            float cooldownLevel = cooldown / cloak.getCooldown();
            shipTimeMult = Math.min(maxTimeMult, 1f + (maxTimeMult - 1f) * cooldownLevel);
            ship.getMutableStats().getFighterRefitTimeMult().unmodify(id);
        }
        stats.getTimeMult().modifyMult(id, shipTimeMult);
        if (player) {
            Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
        } else {
            Global.getCombatEngine().getTimeMult().unmodify(id);
        }
        ship.setApplyExtraAlphaToEngines(true);
        stats.getFluxDissipation().modifyMult(id, 1 - effectLevel * 0.5f);
        //VFX
//        interval.advance(Global.getCombatEngine().getElapsedInLastFrame());
        int alpha = 100 - (int)(80 * effectLevel);
        ship.setJitter(JETTER1 + ship.toString(), new Color(255, 175, 255, alpha), 0.2f, 1, 20);
        ship.setJitterUnder(JETTER1 + ship.toString(), new Color(255, 175, 255, alpha), 0.2f, 1, 20);
        if (interval.intervalElapsed()) {

//            if (state == State.OUT || state == State.IN || state == State.ACTIVE) {
//                            int alpha = (int) (50 + effectLevel * 100);
//                Vector2f random1 = MathUtils.getRandomPointInCircle(null, 20f);
//                ship.addAfterimage(new Color(255, 175, 255, alpha), random1.x, random1.y, random1.x, random1.y, 10, 0.1f, 0.2f, 0.2f, false, false, true);
//                ship.addAfterimage(new Color(255, 175, 255, alpha), random1.x, random1.y, random1.x, random1.y, 10, 0.1f, 0.2f, 0.2f, false, false, false);
//            }
        }
        if (state != State.ACTIVE) {

            float jitterLevel = effectLevel;
            jitterLevel = (float) Math.sqrt(jitterLevel);
            ship.setJitter(JETTER2 + ship.toString(), new Color(255, 175, 255, 100), jitterLevel, 2, 3);
            ship.setJitterUnder(JETTER2 + ship.toString(), new Color(255, 175, 255, 100), jitterLevel, 10, 7);
            Global.getSoundPlayer().playLoop("system_temporalshell_loop", STATUSKEY1, 1, 1, ship.getLocation(), new Vector2f(0, 0));
        }
        int alpha2 = 45;
        float duration = effectLevel * 0.3f;
        Vector2f vel = ship.getVelocity();
        duration *= ship.getVelocity().length() / ship.getMaxSpeedWithoutBoost();
//        duration *= duration;
        float length = -1.5f;
//        ship.addAfterimage(new Color(255, 175, 255, alpha), 0, 0, vel.getX() * length, vel.getY() * length, 1, 0, duration, duration, true, true, true);
        if (!ship.getEngineController().isFlamedOut() && !ship.getEngineController().isFlamingOut()) {
            ship.addAfterimage(new Color(255, 175, 255, alpha2), 0, 0, vel.getX() * length, vel.getY() * length, 3, 0, duration, duration, false, true, false);
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getFluxDissipation().unmodify(id);
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        Global.getCombatEngine().getTimeMult().unmodify(id);
        stats.getTimeMult().unmodify(id);
        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

    public float getMaxTimeMult(MutableShipStatsAPI stats) {
        return 1f + (MAX_TIME_MULT - 1f) * stats.getDynamic().getValue(Stats.PHASE_TIME_BONUS_MULT);
    }

    private void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
        ShipSystemAPI cloak = playerShip.getPhaseCloak();
        if (cloak == null) {
            cloak = playerShip.getSystem();
        }
        if (cloak == null) {
            return;
        }

        if (effectLevel > 0) {
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
                    "graphics/neut/icons/hullsys/neutrino_PhaseCloak_StatusIcon.png", "Micro-Phase Actuators", "flux dissipate rate -50%", true);
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
                    cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "time flow altered", false);
        }
    }
}
