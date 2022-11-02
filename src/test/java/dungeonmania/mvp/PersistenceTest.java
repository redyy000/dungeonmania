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
        res = dmc.loadGame("testLoad");
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
        res = dmc.loadGame("testSavePos");

        //assert player is at the position when saved.
        assertEquals(posAtSave, TestUtils.getPlayerPos(res));

        // assert movement is still correct
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 2), TestUtils.getPlayerPos(res));
    }
}
