package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ZombieTest {

    @Test
    @Tag("10-1")
    @DisplayName("Testing zombies movement")
    public void movement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieTest_movement", "c_zombieTest_movement");

        assertEquals(1, getZombies(res).size());

        // Teams may assume that random movement includes choosing to stay still, so we should just
        // check that they do move at least once in a few turns
        boolean zombieMoved = false;
        Position prevPosition = getZombies(res).get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.UP);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertTrue(zombieMoved);
    }

    @Test
    @Tag("10-2")
    @DisplayName("Testing zombies cannot move through closed doors and walls")
    public void doorsAndWalls() {
        //  W   W   W   W
        //  P   W   Z   W
        //      W   D   W
        //          K
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieTest_doorsAndWalls", "c_zombieTest_doorsAndWalls");
        assertEquals(1, getZombies(res).size());
        Position position = getZombies(res).get(0).getPosition();
        res = dmc.tick(Direction.UP);
        assertEquals(position, getZombies(res).get(0).getPosition());
    }

    @Test
    @Tag("10-3")
    @DisplayName("Testing zombie spawners spawn zombies every x ticks")
    public void toastSpawnXTicks() {
        // 5 Ticks
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieTest_toastSpawnXTicks", "c_zombieTest_toastSpawn5Ticks");
        int count = 0;
        assertEquals(0, getZombies(res).size());
        for (int i = 1; i <= 20; ++i) {
            if (i % 5 == 0) count++;
            res = dmc.tick(Direction.UP);
            assertEquals(count, getZombies(res).size());
        }

        // 20 Ticks
        dmc = new DungeonManiaController();
        res = dmc.newGame("d_zombieTest_toastSpawnXTicks", "c_zombieTest_toastSpawn20Ticks");
        count = 0;
        assertEquals(0, getZombies(res).size());
        for (int i = 1; i <= 60; ++i) {
            if (i % 20 == 0) count++;
            res = dmc.tick(Direction.UP);
            assertEquals(count, getZombies(res).size());
        }
    }

    @Test
    @Tag("10-4")
    @DisplayName("Testing that a dungeon with 2 spawners, spawns 2 zombies at a time")
    public void multipleToastSpawn() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieTest_multipleToastSpawn", "c_zombieTest_multipleToastSpawn");

        // We expect 2 new zombies every 2 ticks
        int count = 0;
        assertEquals(0, getZombies(res).size());
        for (int i = 1; i <= 7; ++i) {
            if (i % 2 == 0) count = count + 2;
            res = dmc.tick(Direction.UP);
            assertEquals(count, getZombies(res).size());
        }
    }

    @Test
    @Tag("10-5")
    @DisplayName("Testing zombie toast spawners spawn zombies in cardinally adjacent open squares")
    public void toastSpawnCardinallyAdjacent() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_zombieTest_toastSpawnCardinallyAdjacent", "c_zombieTest_toastSpawnCardinallyAdjacent");

        Position spawnerPos = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getPosition();
        List<Position> cardinallyAdjacentSquares = TestUtils.getCardinallyAdjacentPositions(spawnerPos);

        res = dmc.tick(Direction.UP);
        assertEquals(1, getZombies(res).size());

        // the zombie has spawned in a cardinally adjacent square
        Position zombiePos = getZombies(res).get(0).getPosition();
        assertTrue(cardinallyAdjacentSquares.contains(zombiePos));
    }

    @Test
    @Tag("10-6")
    @DisplayName("Testing zombie toast spawners won't spawn zombies if there are no cardinally adjacent open squares")
    public void toastCantSpawn() {
        //  P   W
        //  W   S   W
        //      W
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieTest_toastCantSpawn", "c_zombieTest_toastCantSpawn");

        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());
        assertEquals(0, getZombies(res).size());

        // tick to spawn
        res = dmc.tick(Direction.UP);
        assertEquals(0, getZombies(res).size());
    }

    @Test
    @Tag("10-7")
    @DisplayName("Testing destroying a zombie toast spawner")
    public void toastDestruction() {
        //  PLA  ZTS
        //  SWO
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieTest_toastDestruction", "c_zombieTest_toastDestruction");
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // cardinally adjacent: true, has sword: false
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(spawnerId)
        );
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // pick up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // cardinally adjacent: false, has sword: true
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(spawnerId)
        );
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // move right
        res = dmc.tick(Direction.RIGHT);

        // cardinally adjacent: true, has sword: true, but invalid_id
        assertThrows(IllegalArgumentException.class, () ->
                dmc.interact("random_invalid_id")
        );
        // cardinally adjacent: true, has sword: true
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner")); //I changed frome 1 to 0.
    }

    private List<EntityResponse> getZombies(DungeonResponse res) {
        return TestUtils.getEntities(res, "zombie_toast");
    }
}
