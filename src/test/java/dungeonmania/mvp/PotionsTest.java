package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PotionsTest {
    @Test
    @Tag("6-1")
    @DisplayName("Test invincibility potion can be picked up and consumed")
    public void invincibilityPotion() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invincibilityPotion", "c_potionsTest_invincibilityPotion");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

        // pick up invincibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());

        // consume invincibility potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
    }

    @Test
    @Tag("6-2")
    @DisplayName("Test invisibility potion can be picked up and consumed")
    public void invisibilityPotion() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invisibilityPotion", "c_potionsTest_invisibilityPotion");

        assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invisibility_potion").size());

        // pick up invisibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());

        // consume invisibility potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invisibility_potion").size());
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
    }

    @Test
    @Tag("6-3")
    @DisplayName("Test the effects of the invincibility potion only last for a limited time")
    public void invincibilityDuration() throws InvalidActionException {
        //   S1_2   S1_3       P_1
        //   S1_1   S1_4/P_4   P_2/POT/P_3
        //          P_5        S2_2         S2_3
        //          P_6        S2_1         S2_4
        //          P_7/S2_7   S2_6         S2_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invincibilityDuration", "c_potionsTest_invincibilityDuration");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(2, TestUtils.getEntities(res, "spider").size());

        // pick up invincibility_potion
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

        // consume invincibility_potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));

        // meet first spider, battle won immediately using invincibility_potion
        // we need to check that the effects exist before they are worn off,
        // otherwise teams which don't implement potions will pass
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertEquals(1, res.getBattles().get(0).getRounds().size());

        // meet second spider and battle without effects of potion
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "spider").size());
        assertEquals(2, res.getBattles().size());
        assertTrue(res.getBattles().get(1).getRounds().size() >= 1);
        assertEquals(0, res.getBattles().get(1).getBattleItems().size());
    }

    @Test
    @Tag("6-4")
    @DisplayName("Test the effects of the invisibility potion only last for a limited time")
    public void invisibilityDuration() throws InvalidActionException {
        //   S1_2   S1_3       P_1
        //   S1_1   S1_4/P_4   P_2/POT/P_3/P_5
        //   S1_6   S1_5       P_6                              S2_2       S2_3
        //                     P_7                 P_8/S2_8     S2_1       S2_4
        //                                         S2_7         S2_6       S2_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invisibilityDuration", "c_potionsTest_invisibilityDuration");

        assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invisibility_potion").size());
        assertEquals(2, TestUtils.getEntities(res, "spider").size());

        // pick up invisibility_potion
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // consume invisibility_potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));

        // meet first spider, battle does not occur because the player is invisible
        // we need to check that the effects exist before they are worn off,
        // otherwise teams which don't implement potions will pass
        res = dmc.tick(Direction.LEFT);
        assertEquals(2, TestUtils.getEntities(res, "spider").size());
        assertEquals(0, res.getBattles().size());

        // meet second spider and battle because the player is no longer invisible
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertTrue(res.getBattles().get(0).getRounds().size() >= 1);
    }

    @Test
    @Tag("6-5")
    @DisplayName("Test invincibility potions do not change spider movement")
    public void invincibilitySpiderMovement() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_potionsTest_invincibilitySpiderMovement", "c_potionsTest_invincibilitySpiderMovement");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getEntities(res, "spider").size());

        Position pos = TestUtils.getEntities(res, "spider").get(0).getPosition();
        List<Position> movementTrajectory = TestUtils.getSpiderTrajectory(pos);
        int nextPositionElement = 0;

        // pick up invincibility_potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());
        nextPositionElement++;

        // consume invincibility potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        nextPositionElement++;

        // Assert Circular Movement of Spider
        for (int i = 0; i <= 10; ++i) {
            res = dmc.tick(Direction.RIGHT);
            assertEquals(movementTrajectory.get(nextPositionElement),
            TestUtils.getEntities(res, "spider").get(0).getPosition());
            nextPositionElement++;
            nextPositionElement = nextPositionElement % 8;
        }
    }

    // Test when the effects of a 2nd potion are 'queued'
    // and will take place the tick following the previous potion wearing off
    @Test
    @Tag("6-8")
    @DisplayName(
        "Test when the effects of a 2nd potion are 'queued'"
    )
    public void potionQueuing() throws InvalidActionException {
        //  Wall   P_1/2/3    P_4   P_5/6/7/S_9/P_9     S_2     S_3
        //                          S_8/P_8             S_1     S_4
        //                          S_7                 S_6     S_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_potionQueuing", "c_potionsTest_potionQueuing");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getEntities(res, "spider").size());

        // buffer
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // pick up invincibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

        // pick up invisibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // consume invisibility potion (invisibility has duration 3)
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());

        // consume invincibility potion (invisibility has duration 2)
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

        // meet spider, but not battle occurs (invisibility has duration 1)
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(0, res.getBattles().size());

        // meet spider again, battle does occur but won immediately
        // (invisibility has duration 0, invincibility in effect)
        res = dmc.tick(Direction.UP);
        assertEquals(0, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertEquals(1, res.getBattles().get(0).getRounds().size());
    }
}
