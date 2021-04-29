package data.scripts.weapons;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.hullmods.TEM_LatticeShield;
import data.scripts.NCModPlugin;
import data.scripts.plugins.Neutrino_LocalData.LocalData;
import data.scripts.util.Neutrino_ParticlesEffectLib;

import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;

public class NeutADVPhontoEffect implements OnHitEffectPlugin {

    private static final String KEY = "Neutrino_LocalData";
    private final Color color1 = new Color(86, 86, 255, 20);
    private final Color color2 = new Color(0, 0, 255, 30);
    private final Color color3 = new Color(173, 252, 251, 200);

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        final LocalData localData = (LocalData) engine.getCustomData().get(KEY);
        final Set<DamagingProjectileAPI> critSet = localData.critSet;
        if (critSet.contains(projectile) && target instanceof ShipAPI) {
            float dam = (projectile.getDamageAmount()) * 0.5f;
            final DamageType dmgType = DamageType.OTHER;
            int mult = 2;
            float emp = dam * 0.5f;
            float facing = projectile.getFacing();
            boolean shieldHitCheck = shieldHit || (target instanceof ShipAPI && NCModPlugin.TemplarsExists && ((ShipAPI) target).getVariant().getHullMods().contains("tem_latticeshield") && TEM_LatticeShield.shieldLevel(((ShipAPI) target)) > 0f);
            if (!shieldHitCheck) {
                //vfx
                Neutrino_ParticlesEffectLib.AddParticles(
                        10, 2, false,
                        point, 5f,
                        facing, 30f,
                        -200, -30,
                        5, 10,
                        0.7f, 1f,
                        0.5f, 2f,
                        color3);
                if (target instanceof ShipAPI) {
                    ((ShipAPI) target).getMutableStats().getEffectiveArmorBonus().modifyMult("NeutUnstablePhotonEffect", 0);
                    engine.applyDamage(target, point, projectile.getDamageAmount(), dmgType, projectile.getDamageAmount() * 0.5f, false, true, projectile.getSource());
                    ((ShipAPI) target).getMutableStats().getEffectiveArmorBonus().unmodify("NeutUnstablePhotonEffect");
                }
            } else {
                engine.applyDamage(target, point, projectile.getDamageAmount(), dmgType, 0, false, true, projectile.getSource());
                //vfx
                Neutrino_ParticlesEffectLib.AddParticles(
                        10, 1, false,
                        point, 5f,
                        facing, 30f,
                        -150, 100,
                        5, 10,
                        0.8f, 1f,
                        0.5f, 2f,
                        color3);
                for (int i = 0; i < mult; i++) {
                    //VFX
                    engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, new SimpleEntity(MathUtils.getRandomPointOnCircumference(point, MathUtils.getRandomNumberInRange(10, 30))),
                            dmgType,
                            0,
                            0,
                            150, // max range 
                            null,
                            5, // thickness
                            color1,
                            color2
                    );
                    if (shieldHitCheck && Math.random() < ((ShipAPI) target).getFluxTracker().getFluxLevel() + 0.2f) {
                        engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                                dmgType,
                                dam,
                                emp,
                                150, // max range 
                                null,
                                5, // thickness
                                color1,
                                color2
                        );
                    }
                }
                critSet.remove(projectile);
            }
        }
    }
}
