package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.hullmods.NeutrinoNeutroniumPlating;
import data.scripts.weapons.NeutAntiPhotonBeamEffect.separatelyAimBeam;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_LocalData extends BaseEveryFrameCombatPlugin {

    private static final String KEY = "Neutrino_LocalData";

    @Override
    public void init(CombatEngineAPI engine) {
        Global.getCombatEngine().getCustomData().put(KEY, new LocalData());
    }

    public static final class LocalData {

        public final Set<DamagingProjectileAPI> critSet = new HashSet<>();
        public final Map<ShipAPI, Float> guardianShieldRadiusMap = new HashMap<>(10);
        public final Map<ShipAPI, NeutrinoNeutroniumPlating.PowerAromr> powerAromrState = new HashMap<>(200);
        public final Map<BeamAPI,separatelyAimBeam> antiPhotonAimData = new HashMap<>(200);        
    }
    
}
