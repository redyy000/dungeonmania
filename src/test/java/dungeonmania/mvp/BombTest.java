package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BombTest {

    @Test
    @Tag("8-1")
    @DisplayName("Test picking up a bomb removes the bomb from the map and adds the bomb to the inventory")
    public void pickUp() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_pickUp", "c_bombTest_pickUp");
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());
    }

    @Test
    @Tag("8-2")
    @DisplayName(
        "Test placing a bomb removes it from the inventory and places it on the map at the character's location")
    public void place() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_place", "c_bombTest_place");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // Remove bomb from inventory
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Bomb not in inventory
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());

        // Bomb in the position the character was previously
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(pos, TestUtils.getEntities(res, "bomb").get(0).getPosition());

        //Bomb can not be re-picked up
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
    }

    @Test
    @Tag("8-3")
    @DisplayName(
        "Test placing a bomb on a map diagonally adjacent to an active switch will not cause the bomb to detonate")
    public void placeDiagonallyAdjacentActiveSwitch() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeDiagonallyActive", "c_bombTest_placeDiagonallyActive");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Diagonally Adjacent
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Check Bomb did not explode
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "boulder").size());
        assertEquals(1, TestUtils.getEntities(res, "switch").size());
        assertEquals(2, TestUtils.getEntities(res, "wall").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("8-4")
    @DisplayName(
        "Test placing a bomb cardinally adjacent to an active switch, removing surrounding non-player entities"
    )
    public void placeCardinallyActive() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeCardinallyActive", "c_bombTest_placeCardinallyActive");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Check Bomb exploded
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "boulder").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
        assertEquals(0, TestUtils.getEntities(res, "wall").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }

    // Test placing a bomb on a map cardinally adjacent to an inactive switch,
    // and then activating the switch, causes the bomb to detonate,
    // removing surrounding entities except for the player
    @Test
    @Tag("8-5")
    @DisplayName(
        "Test placing a bomb on a map cardinally adjacent to an inactive switch, and then activating the switch")
    public void placeCardinallyActivated() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeCardinallyActivated", "c_bombTest_placeCardinallyActivated");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Activate Switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // Check Bomb exploded
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "boulder").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
        assertEquals(0, TestUtils.getEntities(res, "wall").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("8-6")
    @DisplayName("Test placing a bomb cardinally adjacent to an inactive switch does not cause the bomb to explode")
    public void placeCardinallyInactive() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeCardinallyInactive", "c_bombTest_placeCardinallyInactive");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Check Bomb did not explode
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "switch").size());
        assertEquals(1, TestUtils.getEntities(res, "wall").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("8-7")
    @DisplayName(
        "Test surrounding entities are removed when placing a bomb next to an active switch with bomb radius set to 2"
    )
    public void placeBombRadius2() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Check Bomb exploded with radius 2
        //
        //                 Boulder/Switch        Wall            Wall
        //                Bomb                   Treasure
        //
        //                Treasure
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "boulder").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
        assertEquals(0, TestUtils.getEntities(res, "wall").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("8-8")
    @DisplayName(
        "Test surrounding entities are removed when placing a bomb next to an active switch with bomb radius set to 10"
    )
    public void placeBombRadius10() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius10", "c_bombTest_placeBombRadius10");

        assertEquals(1, TestUtils.getEntities(res, "treasure").size());

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Check Bomb exploded with radius 10
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "boulder").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @DisplayName(
        "Test some that if bomb spawns away from switch it can still be used")
    public void placeCardinallyActivated2() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeCardinalActive2",
                                            "c_bombTest_placeCardinallyActivated");

        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "boulder").size());
        assertEquals(1, TestUtils.getEntities(res, "switch").size());
        assertEquals(1, TestUtils.getEntities(res, "wall").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());

        // Pick up Bomb
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Activate Switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // Check Bomb exploded
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "boulder").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
        assertEquals(0, TestUtils.getEntities(res, "wall").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }
}
