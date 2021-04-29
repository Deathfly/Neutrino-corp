package data.missions.neutrino_randomsim;

import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import data.missions.BaseRandomNeutrinoMissionDefinition;
import org.lwjgl.input.Keyboard;

public class MissionDefinition extends BaseRandomNeutrinoMissionDefinition {

    private static int stat = 1;
    private static int iterator = 0;
    private static int iterator2 = 0;

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            if (stat == 1) {
                iterator++;
            }
            stat = 1;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            if (stat == 2) {
                iterator++;
            }
            stat = 2;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
            if (stat == 3) {
                iterator++;
            }
            stat = 3;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
            if (stat == 4) {
                iterator++;
            }
            stat = 4;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
            if (stat == 5) {
                iterator++;
            }
            stat = 5;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_6)) {
            if (stat == 5) {
                iterator2++;
            }
            stat = 5;
        }
        switch (stat) {
            case 1:
                iterator = chooseFactions("neutrinocorp", null, false, iterator) ? 0 : iterator;
                break;
            case 2:
                iterator = chooseFactions(null, "neutrinocorp", false, iterator) ? 0 : iterator;
                break;
            case 3:
                iterator = chooseFactions("neutrinocorp", null, true, iterator) ? 0 : iterator;
                break;
            case 4:
                iterator = chooseFactions(null, "neutrinocorp", true, iterator) ? 0 : iterator;
                break;
            case 5:
                iterator = chooseFactions("neutrinocorp", null, true, iterator) ? 0 : iterator;
                iterator2 = chooseFactions(null, "neutrinocorp", true, iterator2) ? 0 : iterator2;
                chooseFactions(null, null, true, iterator2, iterator);
                break;
            default:
                break;
        }
        super.defineMission(api);
    }
}
