package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
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
    @DisplayName("Test saving a loaded file")
    public void saveLoadTwice() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_entities", "c_basicGoalsTest_exit");

        // save then load.
        res = assertDoesNotThrow(() -> dmc.saveGame("testSaveTwice"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testSaveTwice"));

        // save then load.
        res = assertDoesNotThrow(() -> dmc.saveGame("testSaveTwice"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testSaveTwice"));
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

    @Test
    @DisplayName("Test saving then loading file with many different entities")
    public void loadEntities() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_entities", "c_basicGoalsTest_exit");
        //Save it
        res = assertDoesNotThrow(() -> dmc.saveGame("testLoad"));
        // Wait for a bit to let the thing write.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //load see if it building works.
        res = assertDoesNotThrow(() -> dmc.loadGame("testLoad"));
    }

    @Test
    @DisplayName("Test achievin boulders goal for five switches. Save before finish. then finish.")
    public void fiveSwitches()  {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_fiveSwitches", "c_basicGoalsTest_fiveSwitches");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move first four boulders onto switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));


        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("test5switch"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("test5switch"));

        // move last boulder onto switch
        res = dmc.tick(Direction.DOWN);
        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName("Test killing 3 enemy, 1 zts goal. Save with sword, not finished. Load, finish game.")
    public void enemieswithZTS() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalTest_zts", "_c_enemiesGoalTest");
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // move player down 3 times kill all. No done.
        res = dmc.tick(Direction.DOWN);
        assertEquals(2, TestUtils.getEntities(res, "mercenary").size());
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getEntities(res, "mercenary").size());
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "mercenary").size());
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        //right, get sword.
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("testEnemiesGoal"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testEnemiesGoal"));

        // break spawner
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));

        // goal done.
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName(
        "Test when the effects of a 2nd potion are 'queued'. Persistence."
    )
    public void potionQueuing() throws InvalidActionException {
        //  Wall   P_1/2/3    P_4   P_5/6/7/S_9/P_9     S_2     S_3
        //                          S_8/P_8             S_1     S_4
        //                          S_7                 S_6     S_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_potionQueuing", "c_potionsTest_potionQueuing");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getEntities(res, "spider").size());

        // buffer
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // pick up invincibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

        // pick up invisibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // consume invisibility potion (invisibility has duration 3)
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());

        // consume invincibility potion (invisibility has duration 2)
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("testPotionQueue"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testPotionQueue"));

        // meet spider, but not battle occurs (invisibility has duration 1)
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(0, res.getBattles().size());

        // meet spider again, battle does occur but won immediately
        // (invisibility has duration 0, invincibility in effect)
        res = dmc.tick(Direction.UP);
        assertEquals(0, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertEquals(1, res.getBattles().get(0).getRounds().size());
    }
}
