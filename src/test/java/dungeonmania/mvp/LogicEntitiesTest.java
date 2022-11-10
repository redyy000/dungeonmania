package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

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
         *              Wi  Wi
         *                  LB */
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities4", "c_bombTest_placeDiagonallyActive");

        // iniitallly off
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName(
        "Test door activates through square of wires. Using OR")
    public void doorActivateOr() throws InvalidActionException {
        /*   Pl Bou Sw  Wi  Wi  row 2
         *              Wi  Wi     Starting position is 2 2, not 1 1.
         *                  SD */
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities5", "c_bombTest_placeDiagonallyActive");
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
        cantMoveThroughDoor(dmc);

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        //try walk through door, should succeed.
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 4), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.RIGHT); //try right, go because door open.
        assertEquals(new Position(6, 4), TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName(
        "Test door activates using AND")
    public void doorActivateAnd() throws InvalidActionException {
        /*   Pl Bou Sw  Wi  Wi  row 2
         *              Wi  Wi     Starting position is 2 2, not 1 1.
         *              Wi   SD */
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities5", "c_bombTest_placeDiagonallyActive");
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
        cantMoveThroughDoor(dmc); //can't move through door yet

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        //try walk through door, should succeed.
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 4), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.RIGHT); //try right, go because door open.
        assertEquals(new Position(6, 4), TestUtils.getEntities(res, "player").get(0).getPosition());
    }
    // must be for a door at 6 4. Will break the test if it fails any of its assertion.
    private DungeonResponse cantMoveThroughDoor(DungeonManiaController dmc) {
        DungeonResponse res;
        //try walk through door, should fail.
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 4), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.RIGHT); //try right, go nowhere because door.
        assertEquals(new Position(5, 4), TestUtils.getEntities(res, "player").get(0).getPosition());

        //return
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(2, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
        return res;
    }

    @Test
    @DisplayName(
        "Test door activates using xor")
    public void doorActivatexor() throws InvalidActionException {
        /*   Pl Bou Sw  SD   row 2
         *          Bou Sw     Starting position is 2 2, not 1 1.
         *                 */
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities6", "c_bombTest_placeDiagonallyActive");
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());

        //try go thorugh door shold fail:
        testDungeonBDoorImpassable(dmc);
        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        //try go thorugh door shold work:
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.DOWN); //doesnt move
        assertEquals(new Position(5, 2), TestUtils.getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN); //return to start.
        assertEquals(new Position(2, 2), TestUtils.getEntities(res, "player").get(0).getPosition());

        //activate second switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(2, 2), TestUtils.getEntities(res, "player").get(0).getPosition());

        //Door impassable now since 2 are activating.
        testDungeonBDoorImpassable(dmc);
    }

    private DungeonResponse testDungeonBDoorImpassable(DungeonManiaController dmc) {
        //try go thorugh door shold fail:
        DungeonResponse res;
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.DOWN); //doesnt move
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN); //return to start.
        return res;
    }


    @Test
    @DisplayName(
        "Test door doesnt activate by staggered activations, using coAnd")
    public void doorNoActivateCoAnd() throws InvalidActionException {
        /*   Pl Bou Sw  SD   row 2
         *          Bou Sw     Starting position is 2 2, not 1 1.
         *                 */
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities7", "c_bombTest_placeDiagonallyActive");
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());

        //try go thorugh door shold fail:
        testDungeonBDoorImpassable(dmc);
        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        testDungeonBDoorImpassable(dmc); //fail cuz only 1.

        //activate second switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(2, 2), TestUtils.getEntities(res, "player").get(0).getPosition());

        //Door impassable now even though 2 active.
        testDungeonBDoorImpassable(dmc);
    }

    @Test
    @DisplayName(
        "Test door activate by simultaneous, using coAnd")
    public void doorActivateCoAnd() throws InvalidActionException {
        /*   Pl Bou Sw  SD   row 2
         *          Wi  Wi     Starting position is 2 2, not 1 1.
         *                 */
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicEntities8", "c_bombTest_placeDiagonallyActive");
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());

        //try go thorugh door shold fail:
        testDungeonBDoorImpassable(dmc);
        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        // works now:
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN); //into switch door
        res = dmc.tick(Direction.RIGHT); //and away
        assertEquals(new Position(6, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
    }
}
