package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.AutofireAIPlugin;
import com.fs.starfarer.api.combat.DroneLauncherShipSystemAPI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAIPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.AIs.Missiles.Neutrino_AdvancedTorpedoAI;
import data.scripts.AIs.Missiles.Neutrino_JavelinTorpedoAI;
import data.scripts.AIs.Missiles.Neutrino_PhotonTorpedoAI;
import data.scripts.AIs.Missiles.Neutrino_UnstablePhotonAI;
import data.scripts.AIs.Ships.Neutrino_NoAI;
import data.scripts.world.neutrinoGen;
import data.scripts.world.neutrinoGenAfterProgen;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import exerelin.campaign.SectorManager;


public class NCModPlugin extends BaseModPlugin {

    private static final String neutrino_advancedtorpedo_ID = "neutrino_advancedtorpedocase";
    private static final String neutrino_photon_torpedo_ID = "neutrino_photon_torpedo";
    private static final String neutrino_lightphoton_torpedo_ID = "neutrino_lightphoton_torpedo";
    private static final String neutrino_javelintorpedo_ID = "neutrino_javelintorpedo";
    private static final String neutrino_unstable_photon_ID = "neutrino_unstable_photon";
    private static final String neutrino_split_photon1_ID = "neutrino_split_photon1";
    private static final String neutrino_split_photon2_ID = "neutrino_split_photon2";
    private static final String neutrino_split_final_ID = "neutrino_split_final";
    private static final String neutrino_photongun_ID = "neutrino_photongun";
    private static final String neutrino_guardianshield_ID = "neutrino_guardianshield";
    private static final String neutrino_adv_torpedo_payload1_ID = "neutrino_adv_torpedo_payload1";
    private static final String neutrino_adv_torpedo_payload2_ID = "neutrino_adv_torpedo_payload2";
    private static final String neutrino_adv_torpedo_payload3_ID = "neutrino_adv_torpedo_payload3";

    public static final boolean SSPExists;
    public static final boolean TemplarsExists;
    public static final boolean ExerlinExists;
    public static final boolean ShaderLibExists;
    static{
        boolean tem;
        try {
            Global.getSettings().getScriptClassLoader().loadClass("org.dark.shaders.util.ShaderLib");  
            tem = true;
        } catch (ClassNotFoundException ex) {
            tem = false;
        }
        ShaderLibExists = tem;
        try {
            Global.getSettings().getScriptClassLoader().loadClass("data.scripts.TEMModPlugin");
            tem = true;
        } catch (ClassNotFoundException ex) {
            tem = false;
        }
        TemplarsExists = tem;
        try {
            Global.getSettings().getScriptClassLoader().loadClass("data.scripts.SSPModPlugin");
            tem = true;
        } catch (ClassNotFoundException ex) {
            tem = false;
        }
        SSPExists = tem;
        try {
            Global.getSettings().getScriptClassLoader().loadClass("exerelin.campaign.SectorManager");
            tem = true;
        } catch (ClassNotFoundException ex) {
            tem = false;
        }
        ExerlinExists = tem;
    }
    
    @Override
    public void onApplicationLoad() {
  
        try {
            Global.getSettings().getScriptClassLoader().loadClass("org.lazywizard.lazylib.LazyLib");
        } catch (ClassNotFoundException ex){
            throw new RuntimeException("Neutrino requires LazyLib!");
        }
        ShaderLib.init();
        LightData.readLightDataCSV("data/lights/neutrino_bling.csv");
//        setUnlockBySkill("neutrino_omegaupgrade", "computer_systems", 10);
//        setUnlockBySkill("neutrino_hightarqueturretmotor", "mechanical_engineering", 5);
//        setUnlockBySkill("neutrino_broadsideShield", "applied_physics", 5);
        
    }

    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            case neutrino_advancedtorpedo_ID:
                return new PluginPick<MissileAIPlugin>(new Neutrino_AdvancedTorpedoAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case neutrino_lightphoton_torpedo_ID:
            case neutrino_photon_torpedo_ID:
            case neutrino_photongun_ID:
                return new PluginPick<MissileAIPlugin>(new Neutrino_PhotonTorpedoAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case neutrino_javelintorpedo_ID:
                if (launchingShip != null && launchingShip.getVariant().getHullMods().contains("eccm")) {
                    return new PluginPick<MissileAIPlugin>(new Neutrino_JavelinTorpedoAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
                }
                break;
            case neutrino_unstable_photon_ID:
            case neutrino_split_photon1_ID:
            case neutrino_split_photon2_ID:
            case neutrino_split_final_ID:
            case neutrino_adv_torpedo_payload1_ID:
            case neutrino_adv_torpedo_payload2_ID:
            case neutrino_adv_torpedo_payload3_ID:
                return new PluginPick<MissileAIPlugin>(new Neutrino_UnstablePhotonAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            default:
        }
        return null;
    }

    @Override
    public PluginPick<ShipAIPlugin> pickShipAI(FleetMemberAPI member, ShipAPI ship) {
        switch (ship.getHullSpec().getHullId()) {
            case neutrino_guardianshield_ID:
                return new PluginPick<ShipAIPlugin>(new Neutrino_NoAI(), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            default:
        }
        return null;
    }

    @Override
    public PluginPick<AutofireAIPlugin> pickWeaponAutofireAI(WeaponAPI weapon) {
        return null;
    }

//    @Override
//    public PluginPick<ShipAIPlugin> pickDroneAI(ShipAPI drone,
//            ShipAPI mothership, DroneLauncherShipSystemAPI system) {
//        String id = drone.getHullSpec().getHullId();
//
//        if (id.equals("neutrino_aegis")) {
//            return new PluginPick<ShipAIPlugin>(new Neutrino_NoAI(), CampaignPlugin.PickPriority.MOD_SPECIFIC);
//        }
//        return null;
//    }

    @Override
    public void onNewGame() {
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getCorvusMode()) {
            new neutrinoGen().generate(Global.getSector());
        }
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("neutrinocorp");
    }

    @Override
    public void onNewGameAfterProcGen() {
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getCorvusMode()) {
            new neutrinoGenAfterProgen().generate(Global.getSector());
        }
    }    
}
