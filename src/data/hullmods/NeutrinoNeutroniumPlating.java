package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.CombatEngine;
import static data.scripts.NCModPlugin.ShaderLibExists;
import data.scripts.plugins.Neutrino_LocalData;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class NeutrinoNeutroniumPlating extends BaseHullMod {

    private static final String KEY = "Neutrino_LocalData";

//    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
//
//    static {
//
//    }
    public static final float ARMOR_DAMAGE_REDUCTION_PERCENT = -5f;
    public static final float POWER_ARMOR_ACTIVE_THRESHOLD = 1f;
//    public static final float POWER_ARMOR_RADIO_TO_ARMOR_PER_CELL = 0.2f;
    public static final float ARMOR_RESTORE_DELAY = 5f;
    public static final float ARMOR_RESTORE_BREAK_THRESHOLD = 5f;
    public static final float HULL_DAMAGE_TAKEN_MULT_WHILE_ACTIVE = 0.5f;
    public static final float ENGINE_AND_WEAPON_DAMAGE_TAKEN_MULT_WHILE_ACTIVE = 0.75f;
    public static final float EMP_DAMAGE_TAKEN_MULT = 0.75f;
    public static final float CORONA_EFFECT_REDUCTION = 0.2f;
    public static final float VENTING_MULT = 0.5f;
    public static final float LOW_CR_THRESHOLD = 0.4f;

    private static final Color COLOR1 = new Color(0, 0, 0, 50);
    private static final Color COLOR2 = new Color(150, 150, 150, 50);
    private static final Color COLOR3 = new Color(200, 200, 200, 50);
    private static final float POWER_ARMOR_BONUS_PERCENT = 25;
    //private static final Map<HullSize, Float> POWER_ARMOR_BONUS_PERCENT = new HashMap<>();
    private static final Map<HullSize, Float> POWER_ARMOR_BONUS_MULT = new HashMap<>();
    private static final Map<HullSize, Float> POWER_ARMOR_FULL_RESTORE_TIME = new HashMap<>();

    static {
//        POWER_ARMOR_BONUS_PERCENT.put(HullSize.FIGHTER, 50f);
//        POWER_ARMOR_BONUS_PERCENT.put(HullSize.FRIGATE, 40f);
//        POWER_ARMOR_BONUS_PERCENT.put(HullSize.DESTROYER, 35f);
//        POWER_ARMOR_BONUS_PERCENT.put(HullSize.CRUISER, 35f);
//        POWER_ARMOR_BONUS_PERCENT.put(HullSize.CAPITAL_SHIP, 30f);
        POWER_ARMOR_BONUS_MULT.put(HullSize.FIGHTER, 2f);
        POWER_ARMOR_BONUS_MULT.put(HullSize.FRIGATE, 4f);
        POWER_ARMOR_BONUS_MULT.put(HullSize.DESTROYER, 4f);
        POWER_ARMOR_BONUS_MULT.put(HullSize.CRUISER, 4f);
        POWER_ARMOR_BONUS_MULT.put(HullSize.CAPITAL_SHIP, 4f);
        POWER_ARMOR_FULL_RESTORE_TIME.put(HullSize.FIGHTER, 60f);
        POWER_ARMOR_FULL_RESTORE_TIME.put(HullSize.FRIGATE, 60f);
        POWER_ARMOR_FULL_RESTORE_TIME.put(HullSize.DESTROYER, 90f);
        POWER_ARMOR_FULL_RESTORE_TIME.put(HullSize.CRUISER, 120f);
        POWER_ARMOR_FULL_RESTORE_TIME.put(HullSize.CAPITAL_SHIP, 150f);
    }
//    public static final Map<ShipAPI, PowerAromr> powerAromrState = new WeakHashMap<>(200);
    private String id;
    private float extraArmor = 0;
    private static final float ACR_VFX_THRESHOLD = 0.6f;
//    private final float overloadWhenCollapse = 2f;

    public static class PowerAromr {

        public final int x, y;
        public float armorValueWithoutPlating[][];
        public final float maxExtarArmor, extarArmorRegenPerSec, resetThreshold, maxArmorPerCell, maxPowerArmorPerCell;
        public float extarArmor, powerArmorPerCell, hullPointAtLastFrame, sinceLastDamage, overloadColorChangeTimer;
        public boolean active, atFullStrength, shouldRegan, justFull, justDown, justRestore, justPaused;
        public final ShipAPI ship;
        public final IntervalUtil reflashInterval = new IntervalUtil(0.2f, 1f);

        @SuppressWarnings("unchecked")
        public PowerAromr(ShipAPI ship) {
            this.ship = ship;
            ArmorGridAPI armorGrid = ship.getArmorGrid();
            overloadColorChangeTimer = 0;
            active = atFullStrength = true;
            shouldRegan = justFull = justDown = justRestore = justPaused = false;
            x = armorGrid.getGrid().length;
            y = armorGrid.getGrid()[0].length;
            maxArmorPerCell = armorGrid.getMaxArmorInCell();
//            maxExtarArmor = POWER_ARMOR_BONUS_PERCENT.get(ship.getHullSize()) * 0.01f * armorGrid.getArmorRating() * POWER_ARMOR_BONUS_MULT.get(ship.getHullSize());
            maxExtarArmor = POWER_ARMOR_BONUS_PERCENT * 0.01f * armorGrid.getArmorRating() * POWER_ARMOR_BONUS_MULT.get(ship.getHullSize());
            extarArmor = maxExtarArmor;
//            maxPowerArmorPerCell = POWER_ARMOR_BONUS_PERCENT.get(ship.getHullSize()) * 0.01f * armorGrid.getMaxArmorInCell();
            maxPowerArmorPerCell = POWER_ARMOR_BONUS_PERCENT * 0.01f * armorGrid.getMaxArmorInCell();
            powerArmorPerCell = maxPowerArmorPerCell;
            hullPointAtLastFrame = ship.getHitpoints();
            extarArmorRegenPerSec = maxExtarArmor / POWER_ARMOR_FULL_RESTORE_TIME.get(ship.getHullSize());
            resetThreshold = maxExtarArmor * POWER_ARMOR_ACTIVE_THRESHOLD;
            armorValueWithoutPlating = new float[x][y];
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    armorValueWithoutPlating[i][j] = Math.min(armorGrid.getArmorValue(i, j), Math.max(0, maxArmorPerCell - maxPowerArmorPerCell));
                }
            }
        }

    }

    public final void refleshArmorDamageDecal(ShipAPI ship) throws NoSuchMethodException {
        ship.syncWithArmorGridState();
        ship.syncWeaponDecalsWithArmorDamage();
    }

    public final PowerAromr powerArmorAdvance(ShipAPI ship, Map<ShipAPI, PowerAromr> powerAromrState, float amount) {
        PowerAromr powerAromr = powerAromrState.get(ship);
        if (powerAromr == null) {
            powerAromr = new PowerAromr(ship);
            powerAromrState.put(ship, powerAromr);
            return powerAromr;
        }
        if (powerAromr.justPaused) {
            ArmorGridAPI armorGrid = ship.getArmorGrid();
            powerAromr.powerArmorPerCell = powerAromr.active ? powerAromr.maxPowerArmorPerCell * powerAromr.extarArmor / powerAromr.maxExtarArmor : 0;
            for (int i = 0; i < powerAromr.x; i++) {
                for (int j = 0; j < powerAromr.y; j++) {
                    armorGrid.setArmorValue(i, j, Math.min(powerAromr.maxArmorPerCell, Math.max(0, powerAromr.armorValueWithoutPlating[i][j] + powerAromr.powerArmorPerCell)));
                }
            }
            powerAromr.justPaused = false;
            return powerAromr;
        }
        ArmorGridAPI armorGrid = powerAromr.ship.getArmorGrid();
        if (powerAromr.active) {
            powerAromr.reflashInterval.advance(amount);
            powerAromr.justRestore = false;
            if (ship.getFluxTracker().isOverloaded() && ship.getFluxTracker().getOverloadTimeRemaining() > 1f) {
                powerAromr.extarArmor = 0;
                powerAromr.active = false;
                powerAromr.justDown = true;
                powerAromr.powerArmorPerCell = 0;
                for (int i = 0; i < powerAromr.x; i++) {
                    for (int j = 0; j < powerAromr.y; j++) {
                        armorGrid.setArmorValue(i, j, Math.min(powerAromr.maxArmorPerCell, Math.max(0, powerAromr.armorValueWithoutPlating[i][j])));
                    }
                }
                unapplyBuffs(ship);
                powerAromrState.put(ship, powerAromr);
//                powerAromr.overloadColorChangeTimer = overloadWhenCollapse;
                return powerAromr;
            }
            float damageOnArmor = 0;
            //debug
//            boolean[][] vc = validArmorCellCheck(ship);
//            boolean[][] ib = inBoundArmorCellCheck(ship);
//            Global.getCombatEngine().addSmoothParticle(ship.getLocation(), new Vector2f(0, 0), 15, 1, amount, Color.green);
//            Ship s = (Ship)ship;
//            s.setRenderBounds(true);
//            armorDebuging(ship,amount);
            for (int i = 0; i < powerAromr.x; i++) {
                for (int j = 0; j < powerAromr.y; j++) {
                    //debug?
//                    float ii = i;
//                    float jj = j;
//                    Vector2f point = getCellLocation(ship, ii, jj);
//                    if (ib[i][j]) {
//                        Global.getCombatEngine().addSmoothParticle(point, new Vector2f(0, 0), 15, 1, amount, Color.cyan);
//                    } else if (vc[i][j]) {
//                        Global.getCombatEngine().addSmoothParticle(point, new Vector2f(0, 0), 15, 1, amount, Color.yellow);
//                    } else {
//                        Global.getCombatEngine().addSmoothParticle(point, new Vector2f(0, 0), 15, 1, amount, Color.RED);
//                    }
                    float damageOnThisCell = powerAromr.armorValueWithoutPlating[i][j] + powerAromr.powerArmorPerCell - armorGrid.getArmorValue(i, j);
                    if (damageOnThisCell < 0) {
                        powerAromr.armorValueWithoutPlating[i][j] = Math.min(powerAromr.maxArmorPerCell - powerAromr.maxPowerArmorPerCell, powerAromr.armorValueWithoutPlating[i][j] - damageOnThisCell);
                    } else if (damageOnThisCell > 0) {
                        damageOnArmor += damageOnThisCell;
                    }
                }
            }
//            if (powerAromr.ship.getHitpoints() < powerAromr.hullPointAtLastFrame) {
//                damageOnArmor += (powerAromr.hullPointAtLastFrame - powerAromr.ship.getHitpoints()) * ((1 - HULL_DAMAGE_TAKEN_MULT_WHILE_ACTIVE) / HULL_DAMAGE_TAKEN_MULT_WHILE_ACTIVE);
//            }
            if (damageOnArmor > 0) {
                powerAromr.extarArmor -= damageOnArmor;
                powerAromr.hullPointAtLastFrame = powerAromr.ship.getHitpoints();
                powerAromr.atFullStrength = false;
                if (powerAromr.extarArmor <= 0) {
                    powerAromr.extarArmor = 0;
                    powerAromr.active = false;
                    powerAromr.justDown = true;
                }
                if (damageOnArmor > ARMOR_RESTORE_BREAK_THRESHOLD * POWER_ARMOR_BONUS_MULT.get(ship.getHullSize()) * amount) {
                    powerAromr.sinceLastDamage = 0;
                    powerAromr.shouldRegan = false;
                }
            } else {
                if (powerAromr.atFullStrength) {
                    if (powerAromr.justFull) {
                        powerAromr.justFull = false;
                        powerAromrState.put(ship, powerAromr);
                    }
                    return powerAromr;
                }
                powerAromr.sinceLastDamage += amount;
                powerAromr.shouldRegan = powerAromr.sinceLastDamage > ARMOR_RESTORE_DELAY && !powerAromr.atFullStrength;
            }
            if (powerAromr.justDown) {
                powerAromr.powerArmorPerCell = 0;
                for (int i = 0; i < powerAromr.x; i++) {
                    for (int j = 0; j < powerAromr.y; j++) {
                        armorGrid.setArmorValue(i, j, Math.min(powerAromr.maxArmorPerCell, Math.max(0, powerAromr.armorValueWithoutPlating[i][j])));
                    }
                }
//                ship.getFluxTracker().beginOverloadWithTotalBaseDuration(overloadWhenCollapse);
//                powerAromr.overloadColorChangeTimer = overloadWhenCollapse;
            } else {
                if (powerAromr.shouldRegan) {
                    powerAromr.extarArmor += powerAromr.extarArmorRegenPerSec * amount;
                    if (powerAromr.extarArmor > powerAromr.maxExtarArmor) {
                        powerAromr.extarArmor = powerAromr.maxExtarArmor;
                        powerAromr.atFullStrength = powerAromr.justFull = true;
                    }
                }
                powerAromr.powerArmorPerCell = powerAromr.maxPowerArmorPerCell * powerAromr.extarArmor / powerAromr.maxExtarArmor;
                for (int i = 0; i < powerAromr.x; i++) {
                    for (int j = 0; j < powerAromr.y; j++) {
                        armorGrid.setArmorValue(i, j, Math.min(powerAromr.maxArmorPerCell, Math.max(0, powerAromr.armorValueWithoutPlating[i][j] + powerAromr.powerArmorPerCell)));
                    }
                }
            }
            applyPowerAromrBuffs(ship, powerAromr);
            powerAromrState.put(ship, powerAromr);
            return powerAromr;
        } else {
            powerAromr.justDown = false;
            if (ship.getFluxTracker().isOverloaded()) {
                powerAromr.extarArmor = 0;
            } else {
                powerAromr.extarArmor += powerAromr.extarArmorRegenPerSec * amount;
            }
            if (powerAromr.extarArmor >= powerAromr.resetThreshold) {
                powerAromr.active = powerAromr.justRestore = true;
                powerAromr.powerArmorPerCell = powerAromr.maxPowerArmorPerCell * powerAromr.extarArmor / powerAromr.maxExtarArmor;
                powerAromr.hullPointAtLastFrame = powerAromr.ship.getHitpoints();
                for (int i = 0; i < powerAromr.x; i++) {
                    for (int j = 0; j < powerAromr.y; j++) {
                        powerAromr.armorValueWithoutPlating[i][j] = Math.min(armorGrid.getArmorValue(i, j), powerAromr.maxArmorPerCell - powerAromr.maxPowerArmorPerCell);
                        armorGrid.setArmorValue(i, j, Math.min(powerAromr.maxArmorPerCell, Math.max(0, powerAromr.armorValueWithoutPlating[i][j]) + powerAromr.powerArmorPerCell));
                    }
                }
                powerAromr.shouldRegan = true;
            }
            unapplyBuffs(ship);
            powerAromrState.put(ship, powerAromr);
            return powerAromr;
        }

    }

    public final void applyPowerAromrBuffs(ShipAPI ship, PowerAromr powerAromr) {
        MutableShipStatsAPI stats = ship.getMutableStats();
        float mult = Math.min(ship.getCurrentCR() / LOW_CR_THRESHOLD, 1);
        if (ship.getFluxTracker().isVenting()) {
            mult *= VENTING_MULT;
        } else if (ship.isPhased()) {
            mult = 100;
        }
        stats.getEffectiveArmorBonus().modifyFlat(id, mult * powerAromr.maxExtarArmor);
        stats.getHullDamageTakenMult().modifyMult(id, HULL_DAMAGE_TAKEN_MULT_WHILE_ACTIVE);
        stats.getWeaponDamageTakenMult().modifyMult(id, ENGINE_AND_WEAPON_DAMAGE_TAKEN_MULT_WHILE_ACTIVE);
        stats.getEngineDamageTakenMult().modifyMult(id, ENGINE_AND_WEAPON_DAMAGE_TAKEN_MULT_WHILE_ACTIVE);
        stats.getEmpDamageTakenMult().modifyMult(id, EMP_DAMAGE_TAKEN_MULT);
        stats.getMaxArmorDamageReduction().modifyFlat(id, 1);
    }

    public final void unapplyBuffs(ShipAPI ship) {
        MutableShipStatsAPI stats = ship.getMutableStats();
        stats.getEffectiveArmorBonus().unmodify(id);
        stats.getHullDamageTakenMult().unmodify(id);
        stats.getWeaponDamageTakenMult().unmodify(id);
        stats.getEngineDamageTakenMult().unmodify(id);
        stats.getEmpDamageTakenMult().unmodify(id);
        stats.getMaxArmorDamageReduction().unmodify(id);
    }

    private Vector2f[] spawnRandomEMPArcOnShip(ShipAPI ship, String impactSoundId, float thickness, Color fringe, Color core) {
        float radius = ship.getCollisionRadius();
        Vector2f p1 = new Vector2f();
        for (int i = 0; i < 25; i++) {
            if (p1 == null || !CollisionUtils.isPointWithinBounds(p1, ship)) {
                p1 = MathUtils.getRandomPointInCircle(ship.getLocation(), radius);
                if (i == 24) {
                    return null;
                }
            } else {
                break;
            }
        }
        CombatEntityAPI arcFrom = new AnchoredEntity(ship, p1);
        Vector2f p2 = new Vector2f();
        for (int i = 0; i < 25; i++) {
            if (p2 == null || !CollisionUtils.isPointWithinBounds(p2, ship)) {
                p2 = MathUtils.getRandomPointInCircle(ship.getLocation(), radius);
                if (i == 24) {
                    return null;
                }
            } else {
                break;
            }
        }
        CombatEntityAPI arcTo = new AnchoredEntity(ship, p2);
        Global.getCombatEngine().spawnEmpArc(ship, arcFrom.getLocation(), arcFrom, arcTo, DamageType.OTHER, 0f, 0f, radius, impactSoundId, thickness, fringe, core);
        return new Vector2f[]{p1, p2};
    }

//    private void changeOverloadColor(float amount, PowerAromr powerAromr) {
//        if (powerAromr.overloadColorChangeTimer > 0) {
//            powerAromr.ship.setOverloadColor(Misc.interpolateColor(COLOR1, Color.white, powerAromr.overloadColorChangeTimer / overloadWhenCollapse));
//            powerAromr.overloadColorChangeTimer -= amount;
//        } else {
//            powerAromr.overloadColorChangeTimer = 0;
//            powerAromr.ship.resetOverloadColor();
//        }
//    }
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, CORONA_EFFECT_REDUCTION);
        stats.getArmorDamageTakenMult().modifyPercent(id, ARMOR_DAMAGE_REDUCTION_PERCENT);
//        stats.getEffectiveArmorBonus().modifyPercent(id, POWER_ARMOR_BONUS_PERCENT.get(hullSize));
        stats.getEffectiveArmorBonus().modifyPercent(id, POWER_ARMOR_BONUS_PERCENT);
        stats.getHullDamageTakenMult().modifyMult(id, HULL_DAMAGE_TAKEN_MULT_WHILE_ACTIVE);
        this.id = id;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
//        extraArmor = ship.getArmorGrid().getArmorRating() * POWER_ARMOR_BONUS_PERCENT.get(ship.getHullSize()) * POWER_ARMOR_BONUS_MULT.get(ship.getHullSize()) * 0.01f;
        extraArmor = ship.getHullSpec().getArmorRating() * POWER_ARMOR_BONUS_PERCENT * POWER_ARMOR_BONUS_MULT.get(ship.getHullSize()) * 0.01f;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        // Refit screen check,
        if (ship.getOriginalOwner() == -1) {
            return;
        }
        CombatEngineAPI engine = Global.getCombatEngine();
        final Neutrino_LocalData.LocalData localData = (Neutrino_LocalData.LocalData) Global.getCombatEngine().getCustomData().get(KEY);
        Map<ShipAPI, PowerAromr> powerAromrState = localData.powerAromrState;
        if (!ship.isAlive()) {
            if (powerAromrState.get(ship) != null) {
                powerArmorAdvance(ship, powerAromrState, POWER_ARMOR_FULL_RESTORE_TIME.get(ship.getHullSize()));
                unapplyBuffs(ship);
                powerAromrState.remove(ship);
            }
            return;
        } else {
            if (powerAromrState.get(ship) == null) {
                powerArmorAdvance(ship, powerAromrState, amount);
            }
        }
        if (ship.isPhased()) {
            if (powerAromrState.get(ship) != null) {
                applyPowerAromrBuffs(ship, powerAromrState.get(ship));
            }
            return;
        }
        if (((CombatEngine) (engine)).isCombatOver()) {
            if (powerAromrState.get(ship) != null) {
                powerArmorAdvance(ship, powerAromrState, POWER_ARMOR_FULL_RESTORE_TIME.get(ship.getHullSize()));
                powerAromrState.remove(ship);
            }
            return;
        }
        PowerAromr powerAromr = powerArmorAdvance(ship, powerAromrState, amount);
        if (ship.getTravelDrive().isActive()) {
            powerArmorAdvance(ship, powerAromrState, POWER_ARMOR_FULL_RESTORE_TIME.get(ship.getHullSize()));
        }
        if (powerAromr.active) {
            if (powerAromr.atFullStrength) {
                if (powerAromr.justFull) {
                    // Back to full strength VFX here.
                    applyArmorGlow(ship, COLOR2, 0.25f, 5, 2, 5);
                    //ship.setJitter(ship, COLOR2, 0.25f, 5, 0);
                    //ship.setJitterUnder(ship, COLOR2, 0.25f, 15, 2f, 5f
                }
                // Full strength VFX here.
                applyArmorGlow(ship, COLOR2, 0.25f, 3, 2, 5);
                //ship.setJitter(ship, COLOR2, 0.25f, 3, 0);
                //ship.setJitterUnder(ship, COLOR2, 0.25f, 15, 2f, 5f);
            } else {
                if (powerAromr.sinceLastDamage < ARMOR_RESTORE_DELAY) {
                    if (powerAromr.sinceLastDamage < 0.1) {
                        applyArmorGlow(ship, COLOR3, 0.25f, 3, 1, 2);
                        //ship.setJitter(ship, COLOR3, 0.25f, 3, 0f);
                        //ship.setJitterUnder(ship, COLOR3, 0.25f, 15, 1f, 2f);
                    } else {
                        float radio = powerAromr.sinceLastDamage / ARMOR_RESTORE_DELAY;
                        applyArmorGlow(ship, Misc.interpolateColor(COLOR1, COLOR3, radio), 0.25f, 3, 1, 2);
                        //ship.setJitter(ship, Misc.interpolateColor(COLOR1, COLOR3, radio), 0.25f, 3, 0);
                        //ship.setJitterUnder(ship, Misc.interpolateColor(COLOR1, COLOR3, radio), 0.25f, 15, 1f, 2f);
//                        Global.getSoundPlayer().playLoop("neut_gravityplateloop", ship + "neut_gravityplateloop", 1, radio, ship.getLocation(), new Vector2f(0, 0));
                    }
                } else if (powerAromr.justRestore) {
                    applyArmorGlow(ship, COLOR3, 0.25f, 3, 2, 5);
                    //ship.setJitter(ship, COLOR3, 0.25f, 3, 0);
                    //ship.setJitterUnder(ship, COLOR3, 0.25f, 15, 2f, 5f);
                    Global.getSoundPlayer().playSound("neut_gravityplateactivate", 1, 1, ship.getLocation(), new Vector2f(0, 0));
                } else if (powerAromr.shouldRegan) {
                    applyArmorGlow(ship, COLOR2, 0.25f, 5, 2, 5);
                    //ship.setJitter(ship, COLOR2, 0.25f, 5, 0);
                    //ship.setJitterUnder(ship, COLOR2, 0.25f, 20, 2f, 5f);
                    ship.getMutableStats().getPeakCRDuration().modifyFlat(id, (ship.getMutableStats().getPeakCRDuration().getFlatBonus(id) == null ? -amount : ship.getMutableStats().getPeakCRDuration().getFlatBonus(id).getValue() - amount));
//                    ship.getMutableStats().getCRLossPerSecondPercent().modifyMult(id, 2);
                } else {
                    applyArmorGlow(ship, COLOR2, 0.25f, 5, 2, 5);
                    //ship.setJitter(ship, COLOR2, 0.25f, 5, 0);
                    //ship.setJitterUnder(ship, COLOR2, 0.25f, 20, 2f, 5f);
//                    ship.getMutableStats().getCRLossPerSecondPercent().unmodifyMult(id);
                }
            }
            if (powerAromr.reflashInterval.intervalElapsed()) {
                float aromrRadio = powerAromr.extarArmor / powerAromr.maxExtarArmor;
//                ((Ship) ship).syncWithArmorGridState();
//                ((Ship) ship).syncWeaponDecalsWithArmorDamage();
//                ((Ship) ship).getDecalRenderer().脭00000();
                try {
                    refleshArmorDamageDecal(ship);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(NeutrinoNeutroniumPlating.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (aromrRadio < ACR_VFX_THRESHOLD) {
                    for (int i = 0; i < POWER_ARMOR_BONUS_MULT.get(ship.getHullSize()); i++) {
                        if (Math.random() > aromrRadio + 0.2f) {
                            spawnRandomEMPArcOnShip(ship, null, 4 - aromrRadio * 5, new Color(0, 0, 0, 0), COLOR1);
//                            if (DPs == null || DPs[0] == null || DPs[1] == null) {
//                                continue;
//                            }
//                            engine.applyDamage(ship, DPs[0], 0.0000001f, DamageType.OTHER, 0, true, true, ship);
//                            engine.applyDamage(ship, DPs[1], 0.0000001f, DamageType.OTHER, 0, true, true, ship);

                        }
                    }
                }
            }
        } else {
            if (ship.getAIFlags() != null) {
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP);
            }
            if (powerAromr.justDown) {
                Global.getSoundPlayer().playSound("neut_gravityplatebreak", 1, 1, ship.getLocation(), new Vector2f(0, 0));
                applyArmorGlow(ship, COLOR3, 0.25f, 12, 5, 10);
                //ship.setJitter(ship, COLOR3, 0.2f, 12, 10f);
                for (int i = 0; i < POWER_ARMOR_BONUS_MULT.get(ship.getHullSize()) * 2; i++) {
                    spawnRandomEMPArcOnShip(ship, null, 15f, COLOR2, COLOR3);
                }
            } else {
                applyArmorGlow(ship, new Color(0f, 0f, 0f, 0f), 0.2f, 1, 0, 0);
                //ship.setJitter(ship, new Color(0f, 0f, 0f, 0f), 0.2f, 1, 0);
            }
        }
//        changeOverloadColor(amount, powerAromr);
        localData.powerAromrState.putAll(powerAromrState);
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        // Allows any ship with a Neutrino hull id  
        return ship.getHullSpec().getHullId().startsWith("neutrino_");
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize
    ) {
        if (index == 0) {
            return "" + POWER_ARMOR_BONUS_PERCENT;
//            return "" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.FRIGATE).intValue()
//                    + "/" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.DESTROYER).intValue()
//                    + "/" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.CRUISER).intValue()
//                    + "/" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.CAPITAL_SHIP).intValue();
        }
        if (index == 1) {
            return "" + (int) -ARMOR_DAMAGE_REDUCTION_PERCENT + "%";
        }
        if (index == 2) {
            return "" + (int) extraArmor;
            //            return "" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.FRIGATE).intValue() * POWER_ARMOR_BONUS_MULT.get(HullSize.FRIGATE).intValue()
            //                    + "/" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.DESTROYER).intValue() * POWER_ARMOR_BONUS_MULT.get(HullSize.DESTROYER).intValue()
            //                    + "/" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.CRUISER).intValue() * POWER_ARMOR_BONUS_MULT.get(HullSize.CRUISER).intValue()
            //                    + "/" + POWER_ARMOR_BONUS_PERCENT.get(HullSize.CAPITAL_SHIP).intValue() * POWER_ARMOR_BONUS_MULT.get(HullSize.CAPITAL_SHIP).intValue();
        }
        if (index == 3) {
            return "" + (int) (100f - ENGINE_AND_WEAPON_DAMAGE_TAKEN_MULT_WHILE_ACTIVE * 100f) + "%";
        }
        if (index == 4) {
            return "" + (int) (100f - HULL_DAMAGE_TAKEN_MULT_WHILE_ACTIVE * 100f) + "%";
        }
        if (index == 5) {
            return "" + (int) (EMP_DAMAGE_TAKEN_MULT * 100f) + "%";
        }
        if (index == 6) {
            return "" + (int) ARMOR_RESTORE_DELAY;
        }
        if (index == 7) {
            return "" + POWER_ARMOR_FULL_RESTORE_TIME.get(HullSize.FRIGATE).intValue()
                    + "/" + POWER_ARMOR_FULL_RESTORE_TIME.get(HullSize.DESTROYER).intValue()
                    + "/" + POWER_ARMOR_FULL_RESTORE_TIME.get(HullSize.CRUISER).intValue()
                    + "/" + POWER_ARMOR_FULL_RESTORE_TIME.get(HullSize.CAPITAL_SHIP).intValue();
        }
        if (index == 8) {
            return "" + (int) (POWER_ARMOR_ACTIVE_THRESHOLD * 100f) + "%";
        }
        if (index == 9) {
            return "" + (int) (VENTING_MULT * 100f);
        }
        return null;
    }

    public void applyArmorGlow(ShipAPI ship, Color color, float intensity, int copies, float jitterDistMin, float jitterDistMax) {
        if (ShaderLibExists) {
            StandardLight light = new StandardLight(ship.getLocation(), ship.getVelocity(), new Vector2f(0, 0), ship);
            light.getColor().set(new Vector3f(color.getRed(), color.getGreen(), color.getBlue()));
            light.setLifetime(Global.getCombatEngine().getElapsedInLastFrame());
            light.setSize(ship.getCollisionRadius());
            light.setIntensity(MathUtils.getRandomNumberInRange(0.0001f, 0.00025f));
            LightShader.addLight(light);
        } else {
            ship.setJitter(ship, color, intensity, copies, 0, 0);
        }
        ship.setJitterUnder(ship, color, intensity, copies * 4, jitterDistMin, jitterDistMax);
    }
}
