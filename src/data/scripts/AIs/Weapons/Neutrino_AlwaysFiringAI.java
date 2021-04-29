// By Deathfly
package data.scripts.AIs.Weapons;

import com.fs.starfarer.api.combat.AutofireAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_AlwaysFiringAI implements AutofireAIPlugin {

    public WeaponAPI weapon;

    public Neutrino_AlwaysFiringAI(WeaponAPI weapon) {
        this.weapon = weapon;
    }

    @Override
    public void advance(float amount) { 
    }

    @Override
    public boolean shouldFire() {
        return true;
    }

    @Override
    public void forceOff() {

    }

    @Override
    public Vector2f getTarget() {
        return null;
    }

    @Override
    public ShipAPI getTargetShip() {
        return null;
    }

    @Override
    public WeaponAPI getWeapon() {
        return weapon;
    }

    @Override
    public MissileAPI getTargetMissile() {
       return null;
    }
}
