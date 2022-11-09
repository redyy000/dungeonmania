package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class LogicEntitiesTest {
    @Test
    @DisplayName(
        "Test light bulb exists, wire, switchdoor exists.")
    public void logicEntitesExist() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities1", "c_bombTest_placeDiagonallyActive");

        // Check Bomb did not explode
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "wire").size());
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
    }

    @Test
    @DisplayName(
        "Test light bulb activates")
    public void lightBulbActivates() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities1", "c_bombTest_placeDiagonallyActive");

        // iniitallly off
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName(
        "Test light bulb activates through 1 wire")
    public void lightBulbActivates1() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntites2", "c_bombTest_placeDiagonallyActive");

        // iniitallly off
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName(
        "Test light bulb activates through 2 wires")
    public void lightBulbActivates2() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities3", "c_bombTest_placeDiagonallyActive");

        // iniitallly off
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName(
        "Test light bulb activates through square of wires")
    public void lightBulbActivates3() throws InvalidActionException {
        /*   Pl Bou Sw  Wi  Wi
         *              Wi  Wi  LB
         */
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities4", "c_bombTest_placeDiagonallyActive");

        // iniitallly off
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }
}
