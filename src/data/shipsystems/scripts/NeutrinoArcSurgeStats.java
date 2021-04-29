//By Deathfly
//WIP
package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class NeutrinoArcSurgeStats extends BaseShipSystemScript {

    private List<WeaponAPI> emitters;
    private final int ArcCount = 3;
    private int count = ArcCount;
    private IntervalUtil interval = new IntervalUtil(0.1f, 0.2f);
    private IntervalUtil glowInterval = new IntervalUtil(0.05f, 0.1f);
    private final float glowSize = 15f;
    private float sizeRandom = 0;
    private final float sizeRandomFactor = 3;
    private final Vector2f zero = new Vector2f(0, 0);
    private final float EMPRange = 700;
    

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null) {
            return;
        }
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            if (!ship.isAlive()) {
                return;
            }
        } else {
            return;
        }
        if (emitters.isEmpty()) {
            List<WeaponAPI> Weapons = ship.getAllWeapons();
            for (WeaponAPI weapon : Weapons) {
                if (weapon.getType() == WeaponAPI.WeaponType.SYSTEM) {
                    emitters.add(weapon);
                }
            }
            return;
        }
        glowInterval.advance(engine.getElapsedInLastFrame());
        if (glowInterval.intervalElapsed()) {
            sizeRandom = sizeRandomFactor * (float) Math.random();
        }
        for (WeaponAPI emitter : emitters) {
            engine.addHitParticle(emitter.getLocation(), zero, glowSize + sizeRandom, effectLevel, 0, Color.yellow);
        }
        if (count > 0) {
            interval.advance(engine.getElapsedInLastFrame());
            if (interval.intervalElapsed()) {
                //prepare target list
                List<CombatEntityAPI> targets = new ArrayList<>();
                for (ShipAPI tmp : Global.getCombatEngine().getShips()) {
                    if (tmp.getOwner() != ship.getOwner() && !tmp.isHulk() && !tmp.isStation() && !tmp.isShuttlePod() && MathUtils.isWithinRange(tmp, ship.getLocation(), EMPRange)) {
                        targets.add(tmp);
                    }
                }
                for (MissileAPI tmp : Global.getCombatEngine().getMissiles()) {
                    if (tmp.getOwner() != ship.getOwner() && MathUtils.isWithinRange(tmp, ship.getLocation(), EMPRange)) {
                        targets.add(tmp);
                    }
                }
                for (WeaponAPI emitter : emitters) {
                    CombatEntityAPI target = targets.get(MathUtils.getRandomNumberInRange(0, targets.size() - 1));
                    if (target instanceof MissileAPI){
                        engine.spawnEmpArc(ship, emitter.getLocation(), ship, target, DamageType.KINETIC, EMPRange, EMPRange, 10000f, "tachyon_lance_emp_impact", 20, Color.orange, Color.red);
                        
                    } else if(target instanceof ShipAPI){
                        
                    }
                }
                count --;
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        count = ArcCount;
    }    
}
