package dungeonmania.mvp.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class ArmourTest {
    @Test
    public void pickUpKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_pickUpKey", "c_DoorsKeysTest_pickUpKey");

        assertEquals(1, TestUtils.getEntities(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getEntities(res, "key").size());

    }
}
