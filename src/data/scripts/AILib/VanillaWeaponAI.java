//package data.scripts.AILib;
//
////import com.fs.profiler.Profiler;
//import com.fs.starfarer.api.combat.*;
//import com.fs.starfarer.api.loading.WeaponSlotAPI;
//import com.fs.starfarer.combat.CombatEngine;
//import com.fs.starfarer.combat.entities.Missile;
//import com.fs.starfarer.combat.entities.Ship;
//import com.fs.starfarer.combat.entities.ship.trackers.C;
//import com.fs.starfarer.combat.systems.thissuper;
//import com.fs.starfarer.loading.WeaponSpreadsheetLoader;
//import com.fs.starfarer.loading.specs.S;
//import com.fs.starfarer.loading.specs.ifsuper;
//import com.fs.starfarer.prototype.Utils;
//import com.fs.starfarer.util.IntervalTracker;
//import com.fs.util.container.repo.ObjectRepository;
//import java.awt.geom.Line2D;
//import java.util.*;
//import org.lazywizard.lazylib.CollisionUtils;
//import org.lazywizard.lazylib.MathUtils;
//import org.lazywizard.lazylib.VectorUtils;
//import org.lwjgl.util.vector.Vector2f;
//
//// Referenced classes of package com.fs.starfarer.combat.ai:
////			M
//public class VanillaWeaponAI implements AutofireAIPlugin {
//
//    private enum WeaponState{
//        WAITING,
//        TRACKING,
//        FIRING
//    }
//    private static final float D200000 = 0.05F;
//    private static final float F800000 = 0.5F;
//    private static final float float1 = 0.5F;
//    private WeaponAPI weapon;
//    private float Object;
//    private float D4O0000;
//    private WeaponState weaponState;
//    private float o00000;
//    private H F400000;
//    private ShipAPI source;
//    private IntervalTracker oO0000;
//    private boolean flag1;
//    private boolean D2O0000;
//    private boolean D5O0000;
//    private float F600000;
//    private float D400000;
//    private H D800000;
//    private Vector2f horizontal = new Vector2f(1.0F, 0.0F);
//    private Vector2f vertical = new Vector2f(0.0F, 1.0F);
//
//    public VanillaWeaponAI(WeaponAPI weapon) {
//        weaponState = WeaponState.WAITING;
//        o00000 = 1.0F;
//        oO0000 = new IntervalTracker(0.125F, 0.25F);
//        flag1 = false;
//        D2O0000 = false;
//        D5O0000 = true;
//        F600000 = 0.0F;
//        D400000 = 0.0F;
//        D800000 = null;
//        this.weapon = weapon;
//        source = weapon.getShip();
//        D4O0000 = (float) Math.random() * 0.05F;
//        if (weapon.getSize() == com.fs.starfarer.api.combat.WeaponAPI.WeaponSize.SMALL || weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD)) {
//            o00000 = 0.25F;
//        } else if (weapon.getSize() == com.fs.starfarer.api.combat.WeaponAPI.WeaponSize.MEDIUM) {
//            o00000 = 0.5F;
//        } else if (weapon.getSize() == com.fs.starfarer.api.combat.WeaponAPI.WeaponSize.LARGE) {
//            o00000 = 1.0F;
//        }
//    }
//
//    @Override
//    public void forceOff() {
//        SetTargert(((H) (null)));
//        weaponState = WeaponState.WAITING;
//        Object = 0.0F;
//        D4O0000 = (float) Math.random() * 0.05F;
//    }
//
//    @Override
//    public void advance(float f) {
//        Object += f;
//        if (F400000 != D800000) {
//            D800000 = F400000;
//            F600000 = 0.0F;
//            D400000 = 0.0F;
//        }
//        if (weapon.isFiring()) {
//            F600000 += f;
//            D400000 = 0.0F;
//        } else {
//            D400000 += f;
//        }
//        if (D400000 > 3F) {
//            F600000 = 0.0F;
//            D400000 = 0.0F;
//        }
//        if (weapon.isDisabled() || weapon.usesAmmo() && weapon.getAmmo() == 0) {
//            weaponState = WeaponState.WAITING;
////            Profiler.super();
//            return;
//        }
//        oO0000.advance(f);
//        if (oO0000.intervalElapsed()) {
//            D500000();
//        }
//        if (weaponState == WeaponState.TRACKING && D300000() && (!weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) || (F400000 instanceof Missile)) && targetInArc(0.0F)) {
//            D600000();
//            if (D5O0000) {
//                weaponState = WeaponState.FIRING;
//            }
//            if (Object >= D4O0000 && (!weapon.isPulse() || weapon.getChargeLevel() == 0.0F)) {
//                updateTarget();
//                if (weaponState == WeaponState.TRACKING) {
//                    D4O0000 = (float) Math.random() * 0.5F + 0.5F;
//                    D4O0000 *= o00000;
//                } else {
//                    D4O0000 = (float) Math.random() * 0.05F + 0.05F;
//                }
//                if (weaponState == WeaponState.TRACKING && D300000() && targetInArc(0.0F)) {
//                    D600000();
//                    if (D5O0000) {
//                        weaponState = WeaponState.FIRING;
//                    }
//                    Object = 0.0F;
//                }
////                Profiler.super();
//            }
//        }
//    }
//
//    private void D600000() {
//        if (weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) || !weapon.usesAmmo() || weapon.getSpec().getAmmoPerSecond() <= 0.0F) {
//            return;
//        }
//        if (weapon.getAmmo() <= 1) {
//            D5O0000 = false;
//        }
//        if (!D5O0000 && ((float) weapon.getAmmo() >= (float) weapon.getMaxAmmo() * 0.34F || weapon.getAmmo() >= 5)) {
//            D5O0000 = true;
//        }
//    }
//
//    private void SetTargert(H h) {
//        F400000 = h;
//        D500000();
//    }
//
//    private void updateTarget() {
//        if (!D300000() || !targetInArc(30F) || weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) && !(F400000 instanceof Missile)) {
//            SetTargert(((H) (null)));
//            pickTarget();
//        } else if (source.getShipTarget() != null && source.getShipTarget() != F400000 && source.getShipTarget().getOriginalOwner() != source.getOwner() && canAimAtTarget(null, source.getShipTarget())) {
//            SetTargert(((H) (null)));
//            if (weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD)) {
//                pickTarget();
//            } else {
//                SetTargert(source.getShipTarget());
//                if (!D300000()) {
//                    SetTargert(((H) (null)));
//                    pickTarget();
//                }
//            }
//        }
//        if (F400000 != null) {
//            weaponState = WeaponState.TRACKING;
//        } else {
//            weaponState = WeaponState.WAITING;
//        }
//    }
//
//    private boolean D200000() {
//        return false;
//    }
//
//    private void pickTarget() {
//        if (D300000() && (!weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) || (F400000 instanceof Missile))) {
//            return;
//        }
//        boolean flag = weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD_ONLY) && !weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.ANTI_FTR);
//        boolean flag1 = weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD_ONLY) && weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.ANTI_FTR);
//        if ((weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) || weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD_ONLY)) && !weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.STRIKE)) {
//            String();
//            if (D300000()) {
//                return;
//            }
//        }
//        if (flag) {
//            return;
//        }
//        if (weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) && weapon.getAmmoTracker() != null && weapon.getAmmoTracker().D200000() > 0.0F && weapon.getAmmo() <= 10 && weapon.getSpec().getReloadSize() <= 1.0F && weapon.getAmmo() < weapon.getMaxAmmo()) {
//            if (weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.ANTI_FTR)) {
//                flag1 = true;
//            } else {
//                return;
//            }
//        }
//        ShipAPI ship = weapon.getShip();
//        WeaponSlotAPI weaponSlot = weapon.getSlot();
//        Vector2f vector2f = weaponSlot.computePosition(ship);
//        T t = CombatEngine.getInstance().getFogOfWar(ship.getOwner());
//        CombatEngine combatengine = CombatEngine.getInstance();
//        List list = combatengine.getObjects().getList(com / fs / starfarer / combat / entities / Ship);
//        float f = 3.402823E+038F;
//        ArrayList arraylist = new ArrayList();
//        Iterator iterator = list.iterator();
//        while (iterator.hasNext()) {
//            Ship ship1 = (Ship) iterator.next();
//            if (t != null && !t.isVisible(ship1) && !ship.isDrone() || ship1.isShuttlePod() || flag1 && !ship1.isFighter() && !ship1.isDrone() || ship1.isFighter() && weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.STRIKE) && !weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) && !weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD_ONLY) || ship1.isFrigate() && weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.STRIKE) && !weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.USE_VS_FRIGATES) || ship.getOwner() == ship1.getOwner() || ship1.isHulk() || ship1.getOwner() == 100) {
//                continue;
//            }
//            float f1 = M.o00000(vector2f, ship1, true);
//            float f2 = Utils.D600000(vector2f, ship1.getLocation());
//            boolean flag2 = f2 <= weapon.getRange() + f1;
//            if (!flag2) {
//                continue;
//            }
//            Vector2f vector2f1 = M.o00000(ship, ship1, weapon.getProjectileSpeed(), false, F600000);
//            float f3 = ((1.75F * f1) / (6.283185F * f2)) * 360F;
//            boolean flag3 = weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.DO_NOT_AIM);
//            float f4 = weapon.getRange();
//            boolean flag4 = Utils.o00000(weaponSlot.computeMidArcAngle(ship), weaponSlot.getArc() + f3, vector2f, vector2f1);
//            if (flag3 && !flag4) {
//                f4 *= 0.75F;
//                flag4 = true;
//            }
//            if (!flag2 || !flag4) {
//                continue;
//            }
//            if (ship.getShipTarget() != null && ship1 == ship.getShipTarget()) {
//                SetTargert(((H) (ship1)));
//                break;
//            }
//            float f5 = Utils.new (weapon.getCurrAngle(), vector2f, vector2f1);
//            if (f5 < f) {
//                f = f5;
//                SetTargert(((H) (ship1)));
//            }
//        }
//        if (!arraylist.isEmpty()) {
//            int i = (int) (Math.random() * (double) arraylist.size());
//            SetTargert((H) arraylist.get(i));
//        }
//    }
//
//    private boolean canAimAtTarget(Vector2f vector2f, ShipAPI ship) {
//        WeaponSlotAPI weaponSlot = weapon.getSlot();
//        if (vector2f == null) {
//            vector2f = weaponSlot.computePosition(source);
//        }
//        float f = M.o00000(vector2f, ship, true);
//        Vector2f vector2f1 = M.o00000(source, ship, weapon.getProjectileSpeed(), false, F600000
//        );
//        float f1 = Utils.D600000(vector2f, vector2f1);
//        float f2 = ((1.75F * f) / (6.283185F * f1)) * 360F;
//        boolean flag = weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.DO_NOT_AIM);
//        float f3 = weapon.getRange();
//        boolean flag1 = Utils.o00000(weaponSlot.computeMidArcAngle(source), weaponSlot.getArc() + f2, source.getLocation(), vector2f1);
//        if (flag && !flag1) {
//            f3 *= 0.75F;
//            flag1 = true;
//        }
//        boolean flag2 = f1 <= weapon.getRange() + f;
//        return flag2 && flag1;
//    }
//
//    private void String() {
//        ShipAPI ship = weapon.getShip();
//        WeaponSlotAPI weaponSlot = weapon.getSlot();
//        Vector2f vector2f = weaponSlot.computePosition(ship);
//        CombatEngine combatengine = CombatEngine.getInstance();
//        List list = combatengine.getObjects().getList(com / fs / starfarer / combat / entities / Missile);
//        float f = 3.402823E+038F;
//        ArrayList arraylist = new ArrayList();
//        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
//            Missile missile = (Missile) iterator.next();
//            if (missile.getCollisionClass() != CollisionClass.NONE && missile.getOwner() != ship.getOwner()) {
//                float f1 = missile.getCollisionRadius();
//                Vector2f vector2f1 = M.o00000(ship, missile, weapon.getProjectileSpeed(), false, F600000);
//                boolean flag = Utils.D600000(vector2f, vector2f1) <= weapon.getRange() + f1;
//                if (flag) {
//                    boolean flag1 = Utils.o00000(weaponSlot.computeMidArcAngle(ship), weaponSlot.getArc() + 15F, ship.getLocation(), vector2f1);
//                    boolean flag2 = weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.DO_NOT_AIM);
//                    float f2 = weapon.getRange();
//                    if (flag2 && !flag1) {
//                        f2 *= 0.75F;
//                        flag1 = true;
//                    }
//                    if (flag && flag1) {
//                        arraylist.add(missile);
//                    }
//                }
//            }
//        }
//
//        if (arraylist.isEmpty()) {
//            SetTargert(((H) (null)));
//        } else {
//            int i = (int) (Math.random() * (double) arraylist.size());
//            SetTargert((H) arraylist.get(i));
//        }
//    }
//
//    private boolean D300000() {
//        if (F400000 == null) {
//            return false;
//        }
//        if ((F400000 instanceof Ship) && ((Ship) F400000).isHulk()) {
//            return false;
//        }
//        if (F400000.getOwner() == 100 || F400000.getOwner() == weapon.getShip().getOwner()) {
//            return false;
//        }
//        T t = CombatEngine.getInstance().getFogOfWar(source.getOwner());
//        if (t != null && !t.isVisible(F400000) && !source.isDrone()) {
//            return false;
//        }
//        CombatEngine combatengine = CombatEngine.getInstance();
//        List list = combatengine.getObjects().getList(com / fs / starfarer / combat / entities / Ship);
//        List list1 = combatengine.getObjects().getList(com / fs / starfarer / combat / entities / Missile);
//        ArrayList arraylist = new ArrayList();
//        arraylist.addAll(list);
//        arraylist.addAll(list1);
//        boolean flag = F400000 != null && arraylist.contains(F400000) && !F400000.isExpired();
//        if (!flag) {
//            SetTargert(((H) (null)));
//        }
//        return flag;
//    }
//
//    private void D500000() {
//        flag1 = false;
//        D2O0000 = false;
//        if (F400000 != null) {
//            Vector2f vector2f = weapon.getSlot().computePosition(source);
//            Vector2f vector2f1 = Utils.D300000(weapon.getCurrAngle());
//            vector2f1.scale(weapon.getRange());
//            Vector2f.add(vector2f, vector2f1, vector2f1);
//            if (M.o00000(source, F400000, vector2f, vector2f1)) {
//                flag1 = true;
//            }
//            CollisionClass collisionclass = weapon.getProjectileCollisionClass();
//            boolean flag = false;
//            if (collisionclass != CollisionClass.RAY_FIGHTER && collisionclass != CollisionClass.PROJECTILE_FIGHTER && collisionclass != CollisionClass.MISSILE_NO_FF && collisionclass != CollisionClass.PROJECTILE_NO_FF) {
//                flag = true;
//            }
//            if (flag) {
//                float f = weapon.getRange();
//                if (!weapon.isBeam()) {
//                    f += 150F;
//                }
//                if (M.o00000(source, F400000, vector2f, vector2f1, weapon.getProjectileSpeed(), weapon.getDerivedStats().getBurstFireDuration(), f
//                ) > 0.1F) {
//                    D2O0000 = true;
//                }
//            }
//        }
//    }
//
//    public boolean shouldFire() {
//        if (weaponState != WeaponState.FIRING) {
//            return false;
//        }
//        if (F400000 == null) {
//            return false;
//        }
//        if (flag1 || D2O0000) {
//            return false;
//        }
//        if (weapon.getFluxCostToFire() > source.getFluxAvailable()) {
//            return false;
//        }
//        if (F400000 instanceof Ship) {
//            Ship ship = (Ship) F400000;
//            if (ship.isPhased()) {
//                if (weapon.usesAmmo()) {
//                    return false;
//                }
//                if (weapon.getCooldown() >= 5F) {
//                    return false;
//                }
//                com.fs.starfarer.api.combat.ShipwideAIFlags shipwideaiflags = source.getAIFlags();
//                if (shipwideaiflags == null) {
//                    if (ship.isPhased() && (!ship.isUnphasing() || (float) Math.random() < 0.95F)) {
//                        return false;
//                    }
//                } else {
//                    float f = weapon.getFluxCostToFire() / source.getMaxFlux();
//                    float f1 = source.getFluxLevel() - source.getHardFluxLevel();
//                    float f2 = 1.0F - source.getFluxLevel();
//                    boolean flag = f1 <= 0.1F && f2 >= f * 4F;
//                    return flag;
//                }
//            }
//        }
//        return true;
//    }
//
//    public Vector2f getTarget() {
//        if (F400000 == null) {
//            return null;
//        } else {
//            Vector2f vector2f = M.o00000(weapon.getShip(), F400000, weapon.getProjectileSpeed(), false, F600000);
//            return vector2f;
//        }
//    }
//
//    @Override
//    public Ship getTargetShip() {
//        return (F400000 instanceof Ship) ? (Ship) F400000 : null;
//    }
//
//    @Override
//    public WeaponAPI getWeapon() {
//        return weapon;
//    }
//
//    private boolean targetInArc(float f) {
//        if (F400000 == null) {
//            return false;
//        }
//        if (weapon.getAmmo() <= 0) {
//            return false;
//        }
//        ShipAPI ship = weapon.getShip();
//        Vector2f vector2f = M.o00000(ship, F400000, weapon.getProjectileSpeed(), true, F600000);
//        if (vector2f == null) {
//            return false;
//        }
//        Vector2f vector2f1 = weapon.getSlot().computePosition(ship);
//        float f1 = M.o00000(vector2f1, F400000, true);
//        float f2 = Utils.D600000(vector2f1, vector2f);
//        float f3 = ((1.5F * f1 * 0.5F) / (6.283185F * f2)) * 360F;
//        if (weapon.isBeam() && !weapon.isBurstBeam() && weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.PD) && !(F400000 instanceof Ship)) {
//            f3 += 15F;
//        }
//        if ((weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.DO_NOT_AIM) || Utils.o00000(weapon.getSlot().computeMidArcAngle(ship), weapon.getSlot().getArc() + f3 * 2.0F, vector2f1, vector2f)) && f2 <= weapon.getRange() + f1) {
//            float f4 = Utils.D300000(vector2f1, vector2f);
//            float f5 = Utils.o00000(weapon.getCurrAngle(), f4) - f3;
//            if (f5 < 0.0F || weapon.hasAIHint(com.fs.starfarer.api.combat.WeaponAPI.AIHints.DO_NOT_AIM)) {
//                f5 = 0.0F;
//            }
//            return f5 <= 0.0F;
//        } else {
//            return false;
//        }
//    }
//
//    public static float o00000(Vector2f vector2f, H h, boolean flag) {
//        if (!(h instanceof Ship)) {
//            return h.getCollisionRadius();
//        }
//        Ship ship = (Ship) h;
//        if (flag) {
//            ShieldAPI shield = ship.getShield();
//            if (shield != null && shield.isOn()) {
//                Vector2f vector2f1 = MathUtils.getPointOnCircumference(null, 1, shield.getFacing() - shield.getActiveArc() / 2.0F);
//                Vector2f vector2f2 = MathUtils.getPointOnCircumference(null, 1, shield.getFacing() + shield.getActiveArc() / 2.0F);
//                Vector2f vector2f3 = MathUtils.getPointOnCircumference(null, 1, shield.getFacing());
//                vector2f1.scale(shield.getRadius());
//                vector2f2.scale(shield.getRadius());
//                vector2f3.scale(shield.getRadius());
//                Vector2f.add(vector2f1, shield.getLocation(), vector2f1);
//                Vector2f.add(vector2f2, shield.getLocation(), vector2f2);
//                Vector2f.add(vector2f3, shield.getLocation(), vector2f3);
//                Vector2f vector2f4 = ship.getLocation();
//                Vector2f vector2f5 = vector2f;
//                if (CollisionUtils.getCollisionPoint(vector2f1, vector2f2, vector2f4, vector2f5) != null) {
//                    return Vector2f.sub(ship.getLocation(), vector2f3, null).length();
//                }
//            }
//        }
//        float f = ship.getSprite().getWidth();
//        float f1 = ship.getSprite().getHeight();
//        float f2 = ship.getSprite().getCenterX();
//        float f3 = ship.getSprite().getCenterY();
//        float f4 = 0.0F;
//        if (f3 != -1F) {
//            f4 = f1 / 2.0F - f3;
//        }
//        float f5 = 0.0F;
//        if (f2 != -1F) {
//            f5 = f / 2.0F - f2;
//        }
//        float f6 = f / f1;
//        float f7 = ship.getFacing();
//        float f8 = Utils.D300000(ship.getLocation(), vector2f);
//        float f9 = Utils.o00000(f7, f8);
//        boolean flag1 = false;
//        boolean flag2 = false;
//        if (f9 > 90F) {
//            f9 = 180F - f9;
//            flag1 = true;
//        }
//        if (Utils.new (f7, f8) > 0.0F) {
//            flag2 = true;
//        }
//        float f10 = ship.getCollisionRadius();
//        f10 = f1 / 2.0F;
//        f10 = Math.min(f10, ship.getCollisionRadius());
//        float f11 = f10;
//        float f12 = f10 * f6;
//        if (flag1) {
//            f11 -= f4;
//        } else {
//            f11 += f4;
//        }
//        if (flag2) {
//            f12 -= f5;
//        } else {
//            f12 += f5;
//        }
//        float f13 = Math.min(f11, f12);
//        float f14 = Math.max(f11, f12);
//        float f15 = Utils.o00000(f8 - f7 - 90F);
//        float f16;
//        if (f11 > f12) {
//            f16 = (f13 * f14) / (float) Math.sqrt(Math.pow(f14 * (float) Math.cos(Math.toRadians(f15)), 2D) + Math.pow(f13 * (float) Math.sin(Math.toRadians(f15)), 2D));
//        } else {
//            f16 = (f13 * f14) / (float) Math.sqrt(Math.pow(f14 * (float) Math.sin(Math.toRadians(f15)), 2D) + Math.pow(f13 * (float) Math.cos(Math.toRadians(f15)), 2D));
//        }
//        return f16 + 10F;
//    }
//
////    public static Vector2f getCollisionPoint(Vector2f start1, Vector2f end1, Vector2f start2, Vector2f end2) {
////        float f = (end2.y - start2.y) * (end1.x - start1.x) - (end2.x - start2.x) * (end1.y - start1.y);
////        float f1 = (end2.x - start2.x) * (start1.y - start2.y) - (end2.y - start2.y) * (start1.x - start2.x);
////        float f2 = (end1.x - start1.x) * (start1.y - start2.y) - (end1.y - start1.y) * (start1.x - start2.x);
////        if (f == 0.0F && (f1 != 0.0F || f2 != 0.0F)) {
////            return null;
////        }
////        if (f == 0.0F && f1 == 0.0F && f2 == 0.0F) {
////            float f3;
////            float f7;
////            if (start1.x < end1.x) {
////                f3 = start1.x;
////                f7 = end1.x;
////            } else {
////                f3 = end1.x;
////                f7 = start1.x;
////            }
////            float f5;
////            float f8;
////            if (start1.y < end1.y) {
////                f5 = start1.y;
////                f8 = end1.y;
////            } else {
////                f5 = end1.y;
////                f8 = start1.y;
////            }
////            if (start2.x >= f3 && start2.x <= f7 && start2.y >= f5 && start2.y <= f8) {
////                return new Vector2f(start2);
////            }
////            if (end2.x >= f3 && end2.x <= f7 && end2.y >= f5 && end2.y <= f8) {
////                return new Vector2f(end2);
////            } else {
////                return null;
////            }
////        }
////        float f4 = f1 / f;
////        float f6 = f2 / f;
////        if (f4 >= 0.0F && f4 <= 1.0F && f6 >= 0.0F && f6 <= 1.0F) {
////            Vector2f vector2f4 = new Vector2f();
////            vector2f4.x = start1.x + f4 * (end1.x - start1.x);
////            vector2f4.y = start1.y + f4 * (end1.y - start1.y);
////            return vector2f4;
////        } else {
////            return null;
////        }
////    }
//    public static float D300000(Vector2f vector2f, Vector2f vector2f1) {
//
//        Vector2f vector2f2 = Vector2f.sub(vector2f1, vector2f, new Vector2f());
//        if (vector2f2.lengthSquared() == 0.0F) {
//            return 0.0F;
//        }
//        float f = (float) Math.toDegrees(Vector2f.angle(vector2f2, D800000));
//        float f1 = (float) Math.toDegrees(Vector2f.angle(vector2f2, o00000));
//        if (f1 <= 90F) {
//            return f;
//        } else {
//            return 360F - f;
//        }
//    }
//}
