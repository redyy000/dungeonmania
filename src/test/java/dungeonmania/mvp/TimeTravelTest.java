package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TimeTravelTest {
    @Test
    @DisplayName("Test mercenary and player positions rewind 1")
    public void simpleMovement() throws IllegalArgumentException {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravel1", "c_mercenaryTest_simpleMovement");
        assertEquals(new Position(8, 1), getMercPos(res));

        //pick up thing
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        // use
        res = dmc.rewind(1);

        assertEquals(new Position(8, 1), getMercPos(res));
    }

    @Test
    @DisplayName("Test mercenary and player positions rewind 5")
    public void simpleMovementBack5() throws IllegalArgumentException {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravel1", "c_mercenaryTest_simpleMovement");
        DungeonResponse startRes = res;
        assertEquals(new Position(8, 1), getMercPos(res));
        assertEquals(new Position(8, 1), getMercPos(startRes));

        //pick up thing
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        //avoid each other for 4 more ticks (let merc move 5 steps).
        res = dmc.tick(Direction.RIGHT); //player at 3, pikc up treasure
        res = dmc.tick(Direction.RIGHT); //at 4, pick up treasure
        res = dmc.tick(Direction.LEFT); //3
        res = dmc.tick(Direction.LEFT); //2
        assertEquals(new Position(3, 1), getMercPos(res));

        // use
        res = dmc.rewind(5);
        assertEquals(getMercPos(startRes), getMercPos(res));
        assertEquals(TestUtils.getEntities(startRes, "treasure").size(),
                        TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, res.getBattles().size());  //A battle happens (happens if move, then rewind.) QUESTIONABLE
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
