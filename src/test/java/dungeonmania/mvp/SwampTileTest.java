package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SwampTileTest {
    @Test
    @DisplayName("Testing a mercenary gets slowed. Factor 2")
    public void mercSlowed() {
        //                                                          Wall     Wall     Wall    Wall    Wall
        // P1       P2/               P3/                P4/      M4       M3       M2     M1      Wall
        //                                                          Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileMerc", "c_mercenaryTest_bribeAmount");

        // get him on tile
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));

        // 1 tick cant move
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));

        // 2 tick cant move
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));

        // 3 tick can move
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getMercPos(res));
    }

    @Test
    @DisplayName("Testing an assassin gets slowed. Factor 2")
    public void assassinSlowed() {
        //                                                          Wall     Wall     Wall    Wall    Wall
        // P1       P2/               P3/                P4/      M4       M3       M2     M1      Wall
        //                                                          Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileAssassin", "c_assassinWillBeBribed");

        // get him on tile
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getAssPos(res));

        // 1 tick cant move
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getAssPos(res));

        // 2 tick cant move
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getAssPos(res));

        // 3 tick can move
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getAssPos(res));
    }

    @Test
    @DisplayName("Testing a spider gets slowed. Factor 2")
    public void spiderSlowed() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTile_testSpider", "c_spiderTest_wallMovement");

        // Make sure spider has moved onto swamp tile
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 4), TestUtils.getEntities(res, "spider").get(0).getPosition());

        // Make sure spider is still on swamp tile (tick 1)
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 4), TestUtils.getEntities(res, "spider").get(0).getPosition());

        // Make sure spider is still on swamp tile (tick 2)
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 4), TestUtils.getEntities(res, "spider").get(0).getPosition());

        // Make sure spider has moved off swamp tile
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(6, 4), TestUtils.getEntities(res, "spider").get(0).getPosition());
    }

    @Test
    @DisplayName("Testing a zombie gets slowed. Factor 2")
    public void zombieSlowed() {
        //  W   W   W   W
        //  P   W   Z   W
        //      W   S   W
        //           
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTile_testZombie", "c_zombieTest_doorsAndWalls");

        // Make sure zombie has moved onto swamp tile
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 3), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // Make sure zombie is still on swamp tile (tick 1)
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(3, 3), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // Make sure zombie is still on swamp tile (tick 2)
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 3), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // Make sure spider has moved off swamp tile
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(3, 4), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private Position getAssPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "assassin").get(0).getPosition();
    }
}
