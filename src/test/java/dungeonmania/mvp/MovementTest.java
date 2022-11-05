package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MovementTest {
    @Test
    @Tag("1-1")
    @DisplayName("Test the player can move down")
    public void testMovementDown() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame(
            "d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(),
        new Position(1, 2), false);

        // move player downward
        DungeonResponse actualDungonRes = dmc.tick(Direction.DOWN);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @Tag("1-2")
    @DisplayName("Test the player can move up")
    public void testMovementUp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovementUp", "c_movementTest_testMovementUp");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(),
        new Position(1, 0), false);

        // move player upward
        DungeonResponse actualDungonRes = dmc.tick(Direction.UP);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @Tag("1-3")
    @DisplayName("Test the player can move left")
    public void testMovementLeft() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame(
            "d_movementTest_testMovementLeft", "c_movementTest_testMovementLeft");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(),
        new Position(0, 1), false);

        // move player left
        DungeonResponse actualDungonRes = dmc.tick(Direction.LEFT);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @Tag("1-4")
    @DisplayName("Test the player can move right")
    public void testMovementRight() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame(
            "d_movementTest_testMovementRight", "c_movementTest_testMovementRight");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
        initPlayer.getType(), new Position(2, 1), false);

        // move player right
        DungeonResponse actualDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @DisplayName("Test the player moves correctly through swamp tiles")
    public void testMovementRightThroughSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame(
            "d_movementTest_testMovementThroughSwampTile", "c_movementTest_testMovementRight");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
        initPlayer.getType(), new Position(3, 1), false);

        // move player right (tick 1 onto swamp)
        DungeonResponse actualDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // move player right (tick 2 out of swamp)
        actualDungonRes = dmc.tick(Direction.RIGHT);
        actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @DisplayName("Test that an enemy avoids a costly swamp tile in path to player")
    public void testDijkstraThroughSwampTile() {
        //                                                  Wall    Wall    Wall    Wall
        // P1       P2              Sw              M3      M2      M1      .       Wall
        //                                                  Wall    Wall    Wall    Wall
        // Mercenary should never walk on swamp tile while unallied
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_movementTest_testMercenaryDijkstra", "c_movementTest_testMercenaryDijkstra");
        EntityResponse player = TestUtils.getPlayer(res).get();

        // move player right (tick 1)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        assertEquals(new Position(7, 1), getMercPos(res));

        // move player left (tick 2)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        assertEquals(new Position(6, 1), getMercPos(res));

        // move player right (tick 3)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        assertNotEquals(new Position(4, 1), getMercPos(res));

        // move player left (tick 4)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        assertNotEquals(new Position(4, 1), getMercPos(res));

        // move player right (tick 5)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        assertNotEquals(new Position(4, 1), getMercPos(res));
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
