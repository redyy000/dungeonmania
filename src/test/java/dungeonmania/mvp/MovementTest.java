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
        // Mercenary should never walk on swamp tile while unallied
        // swamp tile at x = 6, merc at 8.
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_movementTest_testMercenaryDijkstra", "c_movementTest_testMercenaryDijkstra");
        EntityResponse player = TestUtils.getPlayer(res).get();

        // move player right (tick 1)
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));

        // should go down or up
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(new Position(6, 1), getMercPos(res));
        System.out.println(getMercPos(res));

        // should be above or below swamp tile
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        assertNotEquals(new Position(6, 1), getMercPos(res));
        System.out.println(getMercPos(res));

        // should be top left/bottom right of swamp tile
        res = dmc.tick(Direction.RIGHT); //player at 5 after tick
        player = TestUtils.getPlayer(res).get();
        assertNotEquals(new Position(6, 1), getMercPos(res));
        System.out.println(getMercPos(res));

        // then i don't know when the merc will come back to y = 1
        // just confirm that in enough time, the guy indeed fights
        res = dmc.tick(Direction.LEFT); //4 after tick
        System.out.println(getMercPos(res));
        
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "mercenary").size());
    }

    @Test
    @DisplayName("Test that an enemy takes the least costly swamp tile in path to player")
    public void testDijkstraThroughManySwampTiles() {
        // Wall     Wall     Wall     Wall    Wall    Wall    Wall
        //                   Sw                               Wall
        // P1       P2       Sw                       M1      Wall
        //                   Sw                               Wall
        // Wall     Wall     Wall     Wall    Wall    Wall    Wall
        // Mercenary should path through the bottom swap tile to reach the player
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_movementTest_testMercenarySwampTiles", "c_movementTest_testMercenarySwampTiles");
        EntityResponse player = TestUtils.getPlayer(res).get();

        // move player right (tick 1)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();

        // move player left (tick 2)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();

        // move player right (tick 3)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        // Mercenary should never be on this swamp tile
        assertNotEquals(new Position(3, 3), getMercPos(res));

        // move player left (tick 4)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Mercenary should never be on these swamp tiles
        assertNotEquals(new Position(3, 2), getMercPos(res));
        assertNotEquals(new Position(3, 3), getMercPos(res));
        // Check merc is on the correct tile
        assertEquals(new Position(3, 4), getMercPos(res));

        // move player right (tick 5)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        // Mercenary should never be on these swamp tiles
        assertNotEquals(new Position(3, 2), getMercPos(res));
        assertNotEquals(new Position(3, 3), getMercPos(res));
        // Check merc is on the correct tile
        assertEquals(new Position(3, 4), getMercPos(res));
    }

    @Test
    @DisplayName("Test that an enemy is slowed correctly where there are multiple swamp tiles")
    public void testStuckThroughManySwampTiles() {
        // Wall     Wall     Wall     Wall    Wall    Wall    Wall
        //                   Sw                               Wall
        // P1       P2       Sw                       M1      Wall
        //                   Sw                               Wall
        // Wall     Wall     Wall     Wall    Wall    Wall    Wall
        // Mercenary should path through the bottom swap tile to reach the player
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_movementTest_testMercenarySwampTiles", "c_movementTest_testMercenarySwampTiles");
        EntityResponse player = TestUtils.getPlayer(res).get();

        // move player right (tick 1)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();

        // move player left (tick 2)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();

        // move player right (tick 3)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();

        // move player left (tick 4)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Check merc is on the correct tile
        assertEquals(new Position(3, 4), getMercPos(res));

        // move player right (tick 5)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        // Check merc is stuck on the correct tile
        assertEquals(new Position(3, 4), getMercPos(res));

        // move player left (tick 6)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Check merc is stuck on the correct tile
        assertEquals(new Position(3, 4), getMercPos(res));

        // move player right (tick 7)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        // Check merc is stuck on the correct tile
        assertEquals(new Position(3, 4), getMercPos(res));

        // move player left (tick 8)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Check merc is stuck on the correct tile
        assertEquals(new Position(3, 4), getMercPos(res));

        // move player up (tick 0)
        res = dmc.tick(Direction.UP);
        player = TestUtils.getPlayer(res).get();
        // Check merc has moved off the swamp correct tile
        assertNotEquals(new Position(3, 4), getMercPos(res));
    }

    @Test
    @DisplayName("Test that an enemy moves correctly through multiple swamp tiles")
    public void testMovementThroughMultipleSwampTiles() {
        //                          Wall    Wall    Wall    Wall    Wall    Wall    Wall
        // P1       P2              Sw      Sw      M3      M2      M1      .       Wall
        //                          Wall    Wall    Wall    Wall    Wall    Wall    Wall

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_movementTest_testMovementThroughMultipleSwampTiles", "c_movementTest_testMovementThroughMultipleSwampTiles");
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
        // Merc moves onto swamp tile
        assertEquals(new Position(5, 1), getMercPos(res));

        // move player left (tick 4)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Merc is stuck on swamp tile (tick 1/2)
        assertEquals(new Position(5, 1), getMercPos(res));

        // move player right (tick 5)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        // Merc is stuck on swamp tile (tick 2/2)
        assertEquals(new Position(5, 1), getMercPos(res));

        // move player left (tick 6)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Merc moves onto second swamp tile
        assertEquals(new Position(4, 1), getMercPos(res));

        // move player right (tick 7)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        // Merc is stuck on swamp tile (tick 1/3)
        assertEquals(new Position(4, 1), getMercPos(res));

        // move player left (tick 8)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Merc is stuck on swamp tile (tick 2/3)
        assertEquals(new Position(4, 1), getMercPos(res));

        // move player right (tick 9)
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        // Merc is stuck on swamp tile (tick 3/3)
        assertEquals(new Position(4, 1), getMercPos(res));

        // move player left (tick 10)
        res = dmc.tick(Direction.LEFT);
        player = TestUtils.getPlayer(res).get();
        // Merc moves off second swamp tile
        assertEquals(new Position(3, 1), getMercPos(res));
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
