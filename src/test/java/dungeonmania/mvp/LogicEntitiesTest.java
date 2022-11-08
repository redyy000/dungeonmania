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
        assertEquals(1, TestUtils.getEntities(res, "light_bulb").size());
        assertEquals(1, TestUtils.getEntities(res, "wire").size());
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
    }

}
