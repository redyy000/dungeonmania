package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class PersistenceTest {
    @Test
    @DisplayName("Test saving without error")
    public void saveOK() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_exit", "c_basicGoalsTest_exit");
        // move player to right, changing the game.
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = assertDoesNotThrow(() -> dmc.saveGame("testSave"));
    }

    @Test
    @DisplayName("Test saving then loading file without error (no checking of valid data)")
    public void loadOK() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_exit", "c_basicGoalsTest_exit");
        // move player to right, changing the game.
        res = dmc.tick(Direction.RIGHT);
        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = assertDoesNotThrow(() -> dmc.saveGame("testLoad"));
        // Wait for a bit to let the thing write.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //load
        res = assertDoesNotThrow(() -> dmc.loadGame("testLoad"));
    }

    @Test
    @DisplayName("Test saving then loading on player position.")
    public void saveLoad() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_exit", "c_basicGoalsTest_exit");
        Position posAtStart = TestUtils.getPlayerPos(res);

        // move player to right, changing the game.
        res = dmc.tick(Direction.RIGHT);

        Position posAtSave = TestUtils.getPlayerPos(res); //should be x=2, y =1 at save.
        assertNotEquals(posAtSave, posAtStart);
        res = assertDoesNotThrow(() -> dmc.saveGame("testSavePos"));
        // Wait for a bit to let the thing write.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //move right twice.
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        //assert player is at the position as when they saved after loading.
        res = dmc.loadGame("testSavePos");
        assertEquals(posAtSave, TestUtils.getPlayerPos(res));

        // assert movement is still correct
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 2), TestUtils.getPlayerPos(res));
    }

    @Test
    @DisplayName("Test saving then loading and exit goal still works.")
    public void loadExitGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_exit", "c_basicGoalsTest_exit");

        // move player to right
        res = dmc.tick(Direction.RIGHT);
        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));


        //Save game
        Position posAtSave = TestUtils.getPlayerPos(res); //should be x=2, y =1 at save.
        res = assertDoesNotThrow(() -> dmc.saveGame("testExit"));
        try {        // Wait for a bit to let the thing write.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Load game, assert player is at the position as when they saved, and goal not met.
        res = dmc.loadGame("testExit");
        assertEquals(posAtSave, TestUtils.getPlayerPos(res));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));


        // move player to exit        // assert goal met
        res = dmc.tick(Direction.RIGHT);
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName("Test save player inventory")
    public void saveInventory() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_InvSave", "c_basicGoalsTest_treasure");

        // move player to right
        res = dmc.tick(Direction.RIGHT);
        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // collect sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // collect key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        res = assertDoesNotThrow(() -> dmc.saveGame("testInvSave"));
        // Wait for a bit to let the thing write.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //load. Check player position, inventory correct, goal intact.
        res = assertDoesNotThrow(() -> dmc.loadGame("testInvSave"));
        assertEquals(new Position(5, 1), TestUtils.getPlayerPos(res));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));


        // collect 2 more treasure, finish goal.
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals("", TestUtils.getGoals(res));
    }
}
