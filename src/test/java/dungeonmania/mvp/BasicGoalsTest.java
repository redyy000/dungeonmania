package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasicGoalsTest {

    @Test
    @Tag("13-1")
    @DisplayName("Test achieving a basic exit goal")
    public void exit() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_exit", "c_basicGoalsTest_exit");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // move player to exit
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }


    @Test
    @Tag("13-2")
    @DisplayName("Test achieving a basic boulders goal")
    public void oneSwitch()  {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_oneSwitch", "c_basicGoalsTest_oneSwitch");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-3")
    @DisplayName("Test achieving a boulders goal where there are five switches")
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

        // move last boulder onto switch
        res = dmc.tick(Direction.DOWN);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }


    @Test
    @Tag("13-4")
    @DisplayName("Test achieving a basic treasure goal")
    public void treasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_treasure", "c_basicGoalsTest_treasure");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-5")
    @DisplayName("Test killing 3 enemies goal(mercs only)")
    public void enemies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalTest_mercs", "_c_enemiesGoalTest");

        // move player to right
        res = dmc.tick(Direction.RIGHT);
        // assert no fights
        assertEquals(3, TestUtils.getEntities(res, "mercenary").size());
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // move player to right, fight once
        res = dmc.tick(Direction.RIGHT);
        // assert goal not met, killed 1.
        assertEquals(2, TestUtils.getEntities(res, "mercenary").size());
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // move player to right, fight twice
        res = dmc.tick(Direction.RIGHT);
        // assert goal not met, killed 2.
        assertEquals(1, TestUtils.getEntities(res, "mercenary").size());
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // move player to right, fight three
        res = dmc.tick(Direction.RIGHT);
        // assert goal met, killed 3.
        assertEquals(0, TestUtils.getEntities(res, "mercenary").size());
        assertEquals("", TestUtils.getGoals(res));

    }

    @Test
    @Tag("13-6")
    @DisplayName("Test killing 1 enemy, 1 zts goal")
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

        // break spawner
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));

        // goal done.
        assertEquals("", TestUtils.getGoals(res));
    }

    private int getNumKilled(DungeonResponse res) {
        // If we have had x battles and the player is still alive, we must have killed x spiders
        return res.getBattles().size();
    }
}
