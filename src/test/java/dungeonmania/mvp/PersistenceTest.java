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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testSaveTwice"));

        // save then load.
        res = assertDoesNotThrow(() -> dmc.saveGame("testSaveTwice"));
        try {
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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
            Thread.sleep(1000);
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

    @Test
    @DisplayName("Persistence test chain teleporting between multiple portals")
    public void testMultiplePortalsChain() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_PortalsTest_testMultiplePortalsChain", "c_PortalsTest_testMultiplePortalsChain");
        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("testPortalsChain"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testPortalsChain"));

        Position bluePortalPos = new Position(1, 1);
        Position greyPortalPos = new Position(5, 1);
        Position greenPortalPos = new Position(1, 5);
        Position yellowPortalPos = new Position(5, 5);

        // Move into the red portal
        res = dmc.tick(Direction.RIGHT);
        Position playerPos = TestUtils.getPlayer(res).get().getPosition();

        // Player should end up at one of the outside portals
        assertTrue(TestUtils.getManhattanDistance(playerPos, bluePortalPos) == 1
                || TestUtils.getManhattanDistance(playerPos, greyPortalPos) == 1
                || TestUtils.getManhattanDistance(playerPos, greenPortalPos) == 1
                || TestUtils.getManhattanDistance(playerPos, yellowPortalPos) == 1);
    }

    @Test
    @DisplayName("Persistence Doors Test player cannot pickup two keys at the same time")
    public void cannotPickupTwoKeys() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_cannotPickupTwoKeys", "c_DoorsKeysTest_cannotPickupTwoKeys");

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("test2Keys"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("test2Keys"));

        assertEquals(2, TestUtils.getEntities(res, "key").size());

        // pick up key_1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getEntities(res, "key").size());


        // pick up key_2
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getEntities(res, "key").size());
    }

    @Test
    @DisplayName("Persistence Test doors remain open and the player can move through the door without a key")
    public void doorRemainsOpen() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_doorRemainsOpen", "c_DoorsKeysTest_doorRemainsOpen");

        // pick up key
        res = dmc.tick(Direction.RIGHT);

        // open door
        res = dmc.tick(Direction.RIGHT);

        // player no longer has a key but can move freely through door
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
        pos = TestUtils.getEntities(res, "player").get(0).getPosition();


        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("testDoorOpen"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testDoorOpen"));

        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
        pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Perssitence Test player can pick up a second key after using the first")
    public void canPickupSecondKeyAfterFirstUse() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_DoorsKeysTest_canPickupSecondKeyAfterFirstUse", "c_DoorsKeysTest_canPickupSecondKeyAfterFirstUse");

        assertEquals(2, TestUtils.getEntities(res, "key").size());

        // pick up key_1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getEntities(res, "key").size());

        // walk through door_1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getEntities(res, "key").size());

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("testPickUpKey2"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("testPickUpKey2"));

        // pick up key_2
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getEntities(res, "key").size());
    }

    @Test
    @DisplayName("Persistnece Testing a map with 4 conjunction goal")
    public void andAll() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_andAll", "c_complexGoalsTest_andAll");

        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // kill spider
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("4goals"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("4goals"));

        // pickup treasure
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        // move to exit
        res = dmc.tick(Direction.DOWN);
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName("Test building a shield with treasure")
    public void buildShieldWithTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_BuildablesTest_BuildShieldWithTreasure", "c_BuildablesTest_BuildShieldWithTreasure");
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Pick up Wood x2
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        // Pick up Treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("shield"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("shield"));

        // Build Shield
        assertEquals(0, TestUtils.getInventory(res, "shield").size());
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("shield"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("shield"));

        assertEquals(1, TestUtils.getInventory(res, "shield").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @DisplayName("Pesistence Test building a bow")
    public void buildBow() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_BuildablesTest_BuildBow", "c_BuildablesTest_BuildBow");

        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());

        // Pick up Wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up Arrow x3
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "arrow").size());

        // Build Bow
        assertEquals(0, TestUtils.getInventory(res, "bow").size());
        res = assertDoesNotThrow(() -> dmc.build("bow"));
        assertEquals(1, TestUtils.getInventory(res, "bow").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());

        //save and load
        res = assertDoesNotThrow(() -> dmc.saveGame("Bow"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = assertDoesNotThrow(() -> dmc.loadGame("Bow"));
        assertEquals(1, TestUtils.getInventory(res, "bow").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
    }
}
