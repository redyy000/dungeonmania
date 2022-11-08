package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
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
        // Place boulder on switch and then check that the switch lights up
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
    }
}