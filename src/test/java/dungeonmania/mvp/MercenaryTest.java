package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MercenaryTest {

    @Test
    @Tag("12-1")
    @DisplayName("Test mercenary in line with Player moves towards them")
    public void simpleMovement() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_simpleMovement", "c_mercenaryTest_simpleMovement");

        assertEquals(new Position(8, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
    }

    @Test
    @Tag("12-2")
    @DisplayName("Test mercenary stops if they cannot move any closer to the player")
    public void stopMovement() {
        //                  Wall     Wall    Wall
        // P1       P2      Wall      M1     Wall
        //                  Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_stopMovement", "c_mercenaryTest_stopMovement");

        Position startingPos = getMercPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getMercPos(res));
    }

    @Test
    @Tag("12-3")
    @DisplayName("Test mercenaries can not move through closed doors")
    public void doorMovement() {
        //                  Wall     Door    Wall
        // P1       P2      Wall      M1     Wall
        // Key              Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_doorMovement", "c_mercenaryTest_doorMovement");

        Position startingPos = getMercPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getMercPos(res));
    }

    @Test
    @Tag("12-4")
    @DisplayName("Test mercenary moves around a wall to get to the player")
    public void evadeWall() {
        //                  Wall      M2
        // P1       P2      Wall      M1
        //                  Wall      M2
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_evadeWall", "c_mercenaryTest_evadeWall");

        res = dmc.tick(Direction.RIGHT);
        assertTrue(new Position(4, 1).equals(getMercPos(res))
            || new Position(4, 3).equals(getMercPos(res)));
    }

    @Test
    @Tag("12-5")
    @DisplayName("Testing a mercenary can be bribed with a certain amount")
    public void bribeAmount() {
        //                                                          Wall     Wall     Wall    Wall    Wall
        // P1       P2/Treasure      P3/Treasure    P4/Treasure      M4       M3       M2     M1      Wall
        //                                                          Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeAmount", "c_mercenaryTest_bribeAmount");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up second treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // pick up third treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getMercPos(res));

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("12-6")
    @DisplayName("Testing a mercenary can be bribed within a radius")
    public void bribeRadius() {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1       P2/Treasure      P3    P4      M4       M3       M2     M1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeRadius", "c_mercenaryTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // attempt bribe
        assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("12-7")
    @DisplayName("Testing an allied mercenary does not battle the player")
    public void allyBattle() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      M2      M1      Wall
        //                                  Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyBattle", "c_mercenaryTest_allyBattle");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("12-8")
    @DisplayName("Testing an allied mercenary follows the player")
    public void allyMovement() {
        //                 Wall    Wall   Wall
        // P1       Tr      __      M1    Wall
        //                 Wall    Wall   Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMovement", "c_mercenaryTest_allyMovement");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(new Position(4, 1), getMercPos(res));

        //move once pick up treasure
        res = dmc.tick(Direction.RIGHT); // at 2.
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        //interact
        res = assertDoesNotThrow(() -> dmc.interact(mercId)); //player still at 2.
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        // now he should move next to player's "last square". Hence it should move to 1.
        assertEquals(new Position(1, 1), getMercPos(res));

        //check he follows well with some random movements.
        res = dmc.tick(Direction.DOWN); //at 2 2
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.DOWN); // at 2 3, previous 2 2.
        assertEquals(new Position(2, 2), getMercPos(res));
        res = dmc.tick(Direction.UP); // Note that the player can move into the Merc, and they swap.
        assertEquals(new Position(2, 3), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 2), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 2), getMercPos(res));
    }

    @Test
    @Tag("12-9")
    @DisplayName("Testing an allied mercenary follows/sticks only after getting close")
    public void allyMovementFar() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeRadius", "c_mercenaryTest_allyMovement");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        //move once pick up treasure
        res = dmc.tick(Direction.RIGHT); // at 2.
        assertEquals(new Position(7, 1), getMercPos(res));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        //interact
        res = assertDoesNotThrow(() -> dmc.interact(mercId)); //player still at 2. M at 6 because move closer.
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res)); //shouldnt stick yet.

        res = dmc.tick(Direction.RIGHT); // at 3, Merc at 5.
        assertEquals(new Position(5, 1), getMercPos(res));

        res = dmc.tick(Direction.RIGHT); // at 4, Merc adjacent and would go to 3 hence.
        assertEquals(new Position(3, 1), getMercPos(res));
    }

    @Test
    @Tag("12-10")
    @DisplayName("Testing a bribe only works sometimes")
    public void assassinFailBribe() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest", "c_mercenaryTest_allyMovement");
        // note that despite making it as an assassin debug, its type is still an Assassin only.
        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        //move once pick up treasure
        res = dmc.tick(Direction.RIGHT); // at 2.
        assertEquals(new Position(10, 1), getAssPos(res));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        //move to have 2 treasure. Need  to have treasure leftover after bribe so that AssIsAllied() works.
        res = dmc.tick(Direction.RIGHT); // at 3.
        assertEquals(new Position(9, 1), getAssPos(res));
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        //Try bribing.
        res = assertDoesNotThrow(() -> dmc.interact(assId)); //player still at 3. M at 5 because move closer.
        assertEquals(new Position(8, 1), getAssPos(res));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertFalse(isAssassinAllied(res)); //under seed 100, fails bribe once.

        // collect anothe treasure
        res = dmc.tick(Direction.RIGHT); // at 4 after.
        getAssPos(res);
        assertEquals(new Position(7, 1), getAssPos(res));
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        res = assertDoesNotThrow(() -> dmc.interact(assId)); //player still at 4. Under seed 100, should ally.
        assertEquals(new Position(6, 1), getAssPos(res));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertTrue(isAssassinAllied(res));

    }

    @Test
    @DisplayName("Testing a merc cannot be bribed with sun stone")
    public void noBribeSunStone() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      M2      M1      Wall
        //                                  Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_sunstoneBribe", "c_mercenaryTest_allyBattle");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // achieve bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

    @Test
    @Tag("12-11")
    @DisplayName("Testing movement of a bribed assassin")
    public void assassinBribeSuccessMovement() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest", "c_mercenaryTest_assassinMovement");
        // note that despite making it as an assassin debug, its type is still an Assassin only.
        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        //move once pick up treasure
        res = dmc.tick(Direction.RIGHT); // at 2.
        assertEquals(new Position(10, 1), getAssPos(res));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        //Try bribing. (Bribe is guaranteed in config)
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        assertEquals(new Position(9, 1), getAssPos(res));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertTrue(isAssassinAllied(res));

        // Player moves left, assassin should follow left
        res = dmc.tick(Direction.LEFT); // at 1 after.
        assertEquals(new Position(8, 1), getAssPos(res));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Player moves left, assassin should follow left
        res = dmc.tick(Direction.LEFT); // at 0 after.
        assertEquals(new Position(7, 1), getAssPos(res));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Player moves left, assassin should follow left
        res = dmc.tick(Direction.LEFT); // at -1 after.
        assertEquals(new Position(6, 1), getAssPos(res));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Player moves left, assassin should follow left
        res = dmc.tick(Direction.LEFT); // at -2 after.
        assertEquals(new Position(5, 1), getAssPos(res));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertTrue(isAssassinAllied(res));

    }

    @Test
    @DisplayName("mind control sceptre")
    public void mindControl() {
        //                                         Wall     Wall     Wall    Wall    Wall
        // P1       P2/mat      P3/mat    P4,5/mat           M5       M4       M3       M2     M1
        //                                         Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreControl", "c_generateTest");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up three sceptre matrials
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        // 4 ticks have occured, moving the merc from 10 to 6.
        assertEquals(new Position(6, 1), getMercPos(res)); //shouldnt stick yet.

        // achieve mind control.
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // should not have battled after mingling for 3 ticks.
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, res.getBattles().size());
        // mind control after 3 ticks is over. Hostile. Should Battle:
        assertEquals(new Position(6, 1), getMercPos(res));
        assertEquals(new Position(5, 1), TestUtils.getPlayerPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

    @Test
    @DisplayName("mind control sceptre Assassin")
    public void mindControlAssassin() {
        //                                         Wall     Wall     Wall    Wall    Wall
        // P1       P2/mat      P3/mat    P4,5/mat           M5       M4       M3       M2     M1
        //                                         Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreControlAss", "c_assassinWillBeBribed");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up three sceptre matrials
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        // 4 ticks have occured, moving the merc from 10 to 6.
        assertEquals(new Position(6, 1), getAssPos(res)); //shouldnt stick yet.

        // achieve mind control.
        res = assertDoesNotThrow(() -> dmc.interact(assId));

        // should not have battled after mingling for 3 ticks.
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, res.getBattles().size());
        // mind control after 3 ticks is over. Hostile. Should Battle:
        assertEquals(new Position(6, 1), getAssPos(res));
        assertEquals(new Position(5, 1), TestUtils.getPlayerPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
    private Position getAssPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "assassin").get(0).getPosition();
    }
    private boolean isAssassinAllied(DungeonResponse res) {
        // if the assassin can still be interacted with (given you have enough treasure)
        // then it is not allied.
        return !TestUtils.getEntities(res, "assassin").get(0).isInteractable();

    }
}
