package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

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
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.DOWN); // 2 2. push the 22 boulder to 2 3
        assertEquals(new Position(2, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1); // not yet activated
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN); // 1 3
        res = dmc.tick(Direction.DOWN); // 1 4
        res = dmc.tick(Direction.RIGHT); // 2 4
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(5, 3), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.UP); //push boulder

        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0); //not yet activated, next move left will.
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1);
        assertEquals(new Position(5, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.LEFT);  // push boulder on 4 2 to 3 2
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
    }

    @Test
    @DisplayName("Testing Basic Light Bulb Or Function")
    public void lightBulbBasicOr() {
        // same map as above, but uses OR logic.
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasicOr",
        "c_logicTest_lightBulbBasic");
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.DOWN); // 2 2. push the 22 boulder to 2 3
        assertEquals(new Position(2, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0); // activated done since one switch.
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN); // 1 3
        res = dmc.tick(Direction.DOWN); // 1 4
        res = dmc.tick(Direction.RIGHT); // 2 4
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(5, 3), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.UP);

        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1); //still activated
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
        assertEquals(new Position(5, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.LEFT);  // push boulder on 4 2 to 3 2
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1); //activated after second boulder.
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
    }

    @Test
    @DisplayName("Testing Basic Light Bulb Xor Function")
    public void lightBulbBasicXor() {
        // same map as above, but uses XOR logic.
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightBulbBasicXor",
        "c_logicTest_lightBulbBasic");
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.DOWN); // 2 2. push the 22 boulder to 2 3
        assertEquals(new Position(2, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0); // activated done since one switch.
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN); // 1 3
        res = dmc.tick(Direction.DOWN); // 1 4
        res = dmc.tick(Direction.RIGHT); // 2 4
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(5, 3), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.UP);

        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1); //still activated
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 0);
        assertEquals(new Position(5, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.LEFT);  // push boulder on 4 2 to 3 2
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 0); //not activated since two switches near.
        assertTrue(TestUtils.countType(res, "light_bulb_off") == 1);
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
