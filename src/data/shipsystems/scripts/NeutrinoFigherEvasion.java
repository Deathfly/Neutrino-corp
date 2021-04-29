package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class NeutrinoFigherEvasion extends BaseShipSystemScript {

    Color colorFrom = new Color(120, 165, 200, 255);
    Color colorTo = new Color(235, 113, 113, 185);
    boolean runOnce = false;
    CollisionClass cc;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id);
            stats.getAcceleration().unmodifyMult(id);
            stats.getDeceleration().unmodifyMult(id);
            stats.getAcceleration().modifyFlat(id, 6000f);
            stats.getDeceleration().modifyFlat(id, 6000f);
            stats.getMaxTurnRate().unmodify(id);
        } else if (state == ShipSystemStatsScript.State.IN) {
            stats.getMaxSpeed().modifyFlat(id, stats.getMaxSpeed().base);
            stats.getAcceleration().modifyFlat(id, 6000f);
            stats.getDeceleration().modifyFlat(id, 6000f);
            stats.getTurnAcceleration().modifyFlat(id, 90f);
            stats.getMaxTurnRate().modifyFlat(id, 40f);
        } else {
            stats.getMaxSpeed().modifyFlat(id, stats.getMaxSpeed().base);
            stats.getAcceleration().modifyMult(id, 0);
            stats.getDeceleration().modifyMult(id, 0);
        }
        //VFX
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        }
        if (ship != null) {
            if (!runOnce) {
                if (ship.getHullSpec().getHullSize().ordinal() > 1) {
                    cc = CollisionClass.SHIP;
                } else {
                    cc = CollisionClass.FIGHTER;
                }
                runOnce = true;
            }
            Color jetColor = Misc.interpolateColor(colorFrom, colorTo, effectLevel);
            ship.setJitter(this, jetColor, effectLevel * 2, 1, 0);
            ship.setJitterUnder(this, jetColor, effectLevel * 2, 2, 1);
//            if (state == ShipSystemStatsScript.State.IN) {
            int alpha = 15;
            float duration = effectLevel * 0.3f;
            Vector2f vel = ship.getVelocity();
            duration *= ship.getVelocity().length() / ship.getMaxSpeedWithoutBoost();
//        duration *= duration;
            float length = -1.5f;
//        ship.addAfterimage(new Color(255, 175, 255, alpha), 0, 0, vel.getX() * length, vel.getY() * length, 1, 0, duration, duration, true, true, true);
            if (!ship.getEngineController().isFlamedOut() && !ship.getEngineController().isFlamingOut()) {
                ship.addAfterimage(new Color(jetColor.getRed(), jetColor.getGreen(), jetColor.getBlue(), alpha), 0, 0, vel.getX() * length, vel.getY() * length, 3, 0, duration, duration, true, true, false);
            }
            ship.setCollisionClass(CollisionClass.NONE);
//            }
//            int alpha = 100;
//            Color afterimageColor = new Color(jetColor.getRed(), jetColor.getGreen(), jetColor.getBlue(), alpha);
//            float duration = effectLevel * 0.3f;
////            duration *= duration;
//            Vector2f vel = ship.getVelocity();
//            float length = -0.2f;
//
////            ship.addAfterimage(Misc.interpolateColor(colorFrom, colorTo, effectLevel), 0, 0, vel.getX() * length, vel.getY() * length, 3, 0, duration, duration, true, true, true);
//            ship.addAfterimage(afterimageColor, 0, 0, vel.getX() * length, vel.getY() * length, 3, 0, duration, duration, true, false, false);
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            if (ship != null && cc != null) {
                ship.setCollisionClass(cc);
            }
        }
    }
}
