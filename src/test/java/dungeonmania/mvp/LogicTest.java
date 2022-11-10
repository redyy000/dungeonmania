package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// import java.util.ArrayList;
// import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogicTest {

    @Test
    @DisplayName("Testing Basic Light Bulb And function")
    public void lightBulbBasicAnd() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasic",
        "c_logicTest_lightBulbBasic");
        // Place boulder on switch and then check that the switch lights up
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
    }

    @Test
    @DisplayName("Testing Basic Light Bulb Or Function")
    public void lightBulbBasicOr() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasicOr",
        "c_logicTest_lightBulbBasicOr");
        // Place boulder on switch and then check that the switch lights up
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
    }

    @Test
    @DisplayName("Testing Basic Light Bulb Xor Function")
    public void lightBulbBasicXor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasicXor",
        "c_logicTest_lightBulbBasicOr");
        // Place boulder on switch and then check that the switch lights up
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
    }

    @Test
    @DisplayName("Again Testing Basic Light Bulb Xor Function")
    public void lightBulbBasicXorOff() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasicXor2",
        "c_logicTest_lightBulbBasic");
        // Place boulder on switch and then check that the switch lights up
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0);
    }

    @Test
    @DisplayName("Testing Basic Light Bulb CO_AND Function")
    public void lightBulbBasicCOAND() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasicCOAND",
        "c_logicTest_lightBulbBasicCOAND");
        // Place boulder on switch and then check that the switch lights up
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
    }

    @Test
    @DisplayName("Again Testing Basic Light Bulb CO_AND Function")
    public void lightBulbBasicCOANDoff() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasicCOAND2",
        "c_logicTest_lightBulbBasicCOAND");
        // Place boulder on switch and then check that the switch doesn't light up
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1);
    }
    
}