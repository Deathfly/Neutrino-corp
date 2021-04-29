// by Deathfly
// WIP not done yet!
package data.scripts.util;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.Ship;
import com.fs.starfarer.loading.specs.EngineSlot;

import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class attitudeEngineControl {
    
    CombatEntityAPI entity;
    List<attitudeEngine> attitudeEngines;
    
    public attitudeEngineControl() {
    }
    
    public attitudeEngineControl(ShipAPI ship) {
    }
    
    public attitudeEngineControl(MissileAPI missile) {
        
    }
    
    public void init(CombatEntityAPI entity) {
        
    }
    
    public void advance(float amount) {
        
    }
    
    public class attitudeEngine {
        
        ShipEngineAPI coupledShipEngine;
        Vector2f relativeEngineLoc;
        float relativeEngineFacing;
        float[] attitudeContribution;
        boolean isSystem;
        
        
        public attitudeEngine(ShipEngineAPI shipEngine, CombatEntityAPI entity) {
            this.coupledShipEngine = shipEngine;
            this.isSystem = shipEngine.isSystemActivated();
            Vector2f loc = Vector2f.sub(shipEngine.getLocation(), entity.getLocation(), null);
            VectorUtils.rotate(loc, -entity.getFacing(), loc);
            this.relativeEngineLoc = loc;
            
            this.relativeEngineFacing = ((EngineSlot) shipEngine).getAngle();
            
            // OK, let's check how this engine contribution to ship's attitude
            float a, b, c = 0;
            // Check acc/deacc first.
            if (relativeEngineFacing > 90 && relativeEngineFacing < 270) {
                a = 1;
            } else if (relativeEngineFacing == 90 || relativeEngineFacing == 270) {
                a = 0;
            } else {
                a = -1;
            }
            // Then strafe.
            if (relativeEngineFacing == 180 || relativeEngineFacing == 90) {
                b = 0;
            } else if (relativeEngineFacing < 180) {
                b = 1;
            } else {
                b = -1;
            }
            // ...And trun. this is a little complicated
            Vector2f va = MathUtils.getPointOnCircumference(null, 1, relativeEngineFacing);
            Vector2f vb = new Vector2f(relativeEngineLoc.y, -relativeEngineLoc.x);
            float cross = Vector2f.dot(va, vb);
            if (cross == 0) {
                c = 0;
            } else {
                c = cross > 0 ? 1 : -1;
            }
            attitudeContribution = new float[]{a, b, c};
            ShipEngineControllerAPI shipEngineController;
            if (entity instanceof ShipAPI){
            }
        }
    }
}
