package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BattleTest {

    public void assertBattleCalculations(
            BattleResponse battle, boolean enemyDies, String configFilePath, String enemyType) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = battle.getInitialPlayerHealth(); // Should come from config
        double enemyHealth = battle.getInitialEnemyHealth(); // Should come from config
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double
                .parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(-enemyAttack / 10, round.getDeltaCharacterHealth(), 0.001);
            assertEquals(-playerAttack / 5, round.getDeltaEnemyHealth(), 0.001);
            // Delta health is negative
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    @Test
    @Tag("11-1")
    @DisplayName("Test player battles spider and player dies")
    public void testPlayerDiesWhenBattleSpider() {
        // Set player and spider health in config here
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericSpiderSequence(
                controller, "c_battleTests_basicSpiderPlayerDies");
        List<EntityResponse> entities = postBattleResponse.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "player") == 0);
    }

    @Test
    @Tag("11-2")
    @DisplayName("Test player battles spider and spider dies")
    public void testSpiderDiesWhenBattle() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericSpiderSequence(
                controller, "c_battleTests_basicSpiderSpiderDies");
        List<EntityResponse> entities = postBattleResponse.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 0);
    }

    @Test
    @Tag("11-3")
    @DisplayName("Test player battles zombie and player dies")
    public void testPlayerDiesWhenBattleZombie() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericZombieSequence(
                controller, "c_battleTests_basicZombiePlayerDies");
        List<EntityResponse> entities = postBattleResponse.getEntities();
        assertTrue(
                TestUtils.countEntityOfType(entities, "player") == 0);
    }

    @Test
    @Tag("11-4")
    @DisplayName("Test player battles zombie and zombie dies")
    public void testZombieDiesWhenBattle() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericZombieSequence(
                controller, "c_battleTests_basicZombieZombieDies");
        List<EntityResponse> entities = postBattleResponse.getEntities();

        assertTrue(TestUtils.countEntityOfType(entities, "zombie") == 0);
    }

    @Test
    @Tag("11-5")
    @DisplayName("Test player battles mercenary and player dies")
    public void testPlayerDiesWhenBattleMercenary() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericMercenarySequence(
                controller, "c_battleTests_basicMercenaryPlayerDies");
        List<EntityResponse> entities = postBattleResponse.getEntities();

        assertTrue(TestUtils.countEntityOfType(entities, "player") == 0);
    }

    @Test
    @Tag("11-6")
    @DisplayName("Test player battles mercenary and mercenary dies")
    public void testMercenariyDiesWhenBattle() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericMercenarySequence(
                controller, "c_battleTests_basicMercenaryMercenaryDies");
        List<EntityResponse> entities = postBattleResponse.getEntities();

        assertTrue(TestUtils.countEntityOfType(entities, "mercenary") == 0);
    }

    @Test
    @Tag("11-7")
    @DisplayName("Test player wins a battle against a spider with an invinicibility potion")
    public void testPlayerWinsSpiderBattleWithInvincibilityPotion() throws InvalidActionException {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_battleTests_battleWithInvincibility";
        controller.newGame("d_battleTest_invincibleSpider", config);

        DungeonResponse preBattleResponse = controller.tick(Direction.RIGHT);
        String potionId = preBattleResponse.getInventory()
                .stream()
                .filter(item -> item.getType().equals("invincibility_potion"))
                .findFirst()
                .get()
                .getId();

        controller.tick(potionId);
        controller.tick(Direction.RIGHT);

        DungeonResponse postBattleResponse = controller.tick(Direction.RIGHT);
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        List<EntityResponse> entities = postBattleResponse.getEntities();

        // Test that the player has won the battle
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "spider"));

        // One hit kill = should be entire health
        int enemyHealth = Integer.parseInt(TestUtils.getValueFromConfigFile("spider_health", config));
        assertEquals(0, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.001);
        // Delta health is negative so take negative here
        assertTrue(-battle.getRounds().get(0).getDeltaEnemyHealth() >= enemyHealth);
    }

    @Test
    @Tag("11-8")
    @DisplayName("Test player wins a battle against a zombie with an invincibility potion")
    public void testPlayerWinsZombieBattleWithInvincibilityPotion() throws InvalidActionException {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_battleTests_battleWithInvincibility";
        controller.newGame("d_battleTest_invincibleZombie", "c_battleTests_battleWithInvincibility");

        int potionLength = Integer.parseInt(TestUtils.getValueFromConfigFile("invincibility_potion_duration", config));
        DungeonResponse preBattleResponse = controller.tick(Direction.RIGHT);
        String potionId = preBattleResponse.getInventory().stream()
                .filter(item -> item.getType().equals("invincibility_potion"))
                .findFirst()
                .get()
                .getId();
        controller.tick(potionId);
        for (int i = 0; i < potionLength - 1; i++) {
            controller.tick(Direction.RIGHT);
        }
        DungeonResponse postBattleResponse = controller.tick(Direction.RIGHT);
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        List<EntityResponse> entities = postBattleResponse.getEntities();

        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "zombie"));

        // One hit kill = should be entire health
        int enemyHealth = Integer.parseInt(TestUtils.getValueFromConfigFile("zombie_health", config));
        assertEquals(0, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.001);
        // Delta health is negative so take negative here
        assertTrue(-battle.getRounds().get(0).getDeltaEnemyHealth() >= enemyHealth);
    }

    @Test
    @Tag("11-9")
    @DisplayName("Test a player wins a battle against a mercenary with an invincibility potion")
    public void testPlayerWinsMercenaryBattleWithInvincibilityPotion() throws InvalidActionException {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_battleTests_battleWithInvincibility";
        controller.newGame("d_battleTest_invincibleMercenary", "c_battleTests_battleWithInvincibility");

        int potionLength = Integer.parseInt(TestUtils.getValueFromConfigFile("invincibility_potion_duration", config));
        DungeonResponse preBattleResponse = controller.tick(Direction.RIGHT);
        String potionId = preBattleResponse.getInventory().stream()
                .filter(item -> item.getType().equals("invincibility_potion"))
                .findFirst()
                .get()
                .getId();
        controller.tick(potionId);
        for (int i = 0; i < potionLength - 1; i++) {
            controller.tick(Direction.RIGHT);
        }

        List<EntityResponse> entities = preBattleResponse.getEntities();
        DungeonResponse postBattleResponse = controller.tick(Direction.RIGHT);
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        entities = postBattleResponse.getEntities();

        // Test that the player has won the battle
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "mercenary"));

        // One hit kill = should be entire health
        int enemyHealth = Integer.parseInt(TestUtils.getValueFromConfigFile("mercenary_health", config));
        assertEquals(0, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.001);
        // Delta health is negative so take negative here
        assertTrue(-battle.getRounds().get(0).getDeltaEnemyHealth() >= enemyHealth);
    }

    @Test
    @Tag("11-10")
    @DisplayName("Test the player battles three enemies consecutively and defeats them")
    public void testPlayerBattlingEnemiesConsecutivelyDefeatsThem() {
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_battleTest_consecutiveEnemies", "c_battleTests_threeConsecutiveEnemies");

        DungeonResponse postBattleResponse = controller.tick(Direction.RIGHT);
        List<EntityResponse> entities = postBattleResponse.getEntities();
        int spiderCount = TestUtils.countEntityOfType(entities, "spider");
        int zombieCount = TestUtils.countEntityOfType(entities, "zombie_toast");
        int mercCount = TestUtils.countEntityOfType(entities, "mercenary");
        assertEquals(1, spiderCount);
        assertEquals(0, zombieCount);
        assertEquals(1, mercCount);

        postBattleResponse = controller.tick(Direction.RIGHT);
        entities = postBattleResponse.getEntities();
        spiderCount = TestUtils.countEntityOfType(entities, "spider");
        zombieCount = TestUtils.countEntityOfType(entities, "zombie_toast");
        mercCount = TestUtils.countEntityOfType(entities, "mercenary");
        assertEquals(1, spiderCount);
        assertEquals(0, zombieCount);
        assertEquals(0, mercCount);

        postBattleResponse = controller.tick(Direction.RIGHT);
        entities = postBattleResponse.getEntities();
        spiderCount = TestUtils.countEntityOfType(entities, "spider");
        zombieCount = TestUtils.countEntityOfType(entities, "zombie_toast");
        mercCount = TestUtils.countEntityOfType(entities, "mercenary");
        assertEquals(0, spiderCount);
        assertEquals(0, zombieCount);
        assertEquals(0, mercCount);
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
    }

    @Test
    @Tag("11-11")
    @DisplayName("Test basic health calculations spider - player wins")
    public void testRoundCalculationsSpider() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericSpiderSequence(
                controller, "c_battleTests_basicSpiderSpiderDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations(battle, true, "c_battleTests_basicSpiderSpiderDies", "spider");
    }

    @Test
    @Tag("11-12")
    @DisplayName("Test basic health calculations zombie - player wins")
    public void testRoundCalculationsZombie() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericZombieSequence(
                controller, "c_battleTests_basicZombieZombieDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations(battle, true, "c_battleTests_basicZombieZombieDies", "zombie");
    }

    @Test
    @Tag("11-13")
    @DisplayName("Test basic health calculations mercenary - player wins")
    public void testRoundCalculationsMercenary() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericMercenarySequence(
                controller, "c_battleTests_basicMercenaryMercenaryDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations(battle, true, "c_battleTests_basicMercenaryMercenaryDies", "mercenary");
    }

    @Test
    @Tag("11-14")
    @DisplayName("Test basic health calculations spider - player loses")
    public void testHealthBelowZeroSpider() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericSpiderSequence(
                controller, "c_battleTests_basicSpiderPlayerDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations(battle, false, "c_battleTests_basicSpiderPlayerDies", "spider");
    }

    @Test
    @Tag("11-15")
    @DisplayName("Test basic health calculations zombie - player loses")
    public void testHealthBelowZeroZombie() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericZombieSequence(
                controller, "c_battleTests_basicZombiePlayerDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations(battle, false, "c_battleTests_basicZombiePlayerDies", "zombie");
    }

    @Test
    @Tag("11-16")
    @DisplayName("Test basic health calculations mercenary - player loses")
    public void testHealthBelowZeroMercenary() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericMercenarySequence(
                controller, "c_battleTests_basicMercenaryPlayerDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations(battle, false, "c_battleTests_basicMercenaryPlayerDies", "mercenary");
    }

    @Test
    @Tag("11-17")
    @DisplayName("Test attack doubles with bow - spider")
    public void testBowAttackDoubleSpider() throws InvalidActionException {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        String config = "c_battleTests_bowDoubleAttack";
        DungeonResponse res = controller.newGame("d_battleTest_bowTest", config);

        // Pick up Wood
        controller.tick(Direction.RIGHT);

        // Pick up Arrow x3
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.RIGHT);
        controller.build("bow");
        res = controller.tick(Direction.RIGHT); // battle happens after this tick

        // Get the Battle Response
        BattleResponse battle = res.getBattles().get(0);
        RoundResponse firstRound = battle.getRounds().get(0);

        // check the bow doubles attack
        int playerAttack = Integer.parseInt(TestUtils.getValueFromConfigFile("player_attack", config));
        // Delta health is negative so take negative here
        assertEquals(2 * playerAttack / 5, -firstRound.getDeltaEnemyHealth(), 0.001);
        assertEquals(2 * playerAttack / 5, -battle.getRounds().get(1).getDeltaEnemyHealth(), 0.001);
    }

    @Test
    @Tag("11-18")
    @DisplayName("Test shield reduces enemy attack")
    public void testShieldReducesEnemyAttack() throws InvalidActionException {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        String config = "c_battleTests_shieldEffect";
        DungeonResponse res = controller.newGame("d_battleTest_shieldTest", config);

        // Pick up Wood
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);

        // Pick up treasure
        res = controller.tick(Direction.RIGHT);

        // Pick up key
        res = controller.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        res = controller.build("shield");

        res = controller.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);

        RoundResponse firstRound = battle.getRounds().get(0);

        // Assumption: Shield effect calculation to reduce damage makes enemyAttack =
        // enemyAttack - shield effect
        int enemyAttack = Integer.parseInt(TestUtils.getValueFromConfigFile("spider_attack", config));
        int shieldEffect = Integer.parseInt(TestUtils.getValueFromConfigFile("shield_defence", config));
        int expectedDamage = (enemyAttack - shieldEffect) / 10;
        // Delta health is negative so take negative here
        assertEquals(expectedDamage, -firstRound.getDeltaCharacterHealth(), 0.001);
    }

    @Test
    @Tag("11-19")
    @DisplayName("Test bow durability")
    public void testBowDurability() throws InvalidActionException {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_battleTests_bowDurability";
        DungeonResponse res = controller.newGame("d_battleTest_bowDurabilityTest", config);

        List<EntityResponse> entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(3, TestUtils.countEntityOfType(entities, "zombie_toast"));

        // Pick up Wood
        res = controller.tick(Direction.RIGHT);

        // Pick up Arrow x3
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        res = controller.build("bow");
        res = controller.tick(Direction.RIGHT);

        // Battle three zombies - third zombie you shouldn't see effect anymore
        // Note the bow durability is 2 in this test
        while (TestUtils.countEntityOfType(entities, "zombie_toast") != 0) {
            res = controller.tick(Direction.RIGHT);
            entities = res.getEntities();
        }

        assertTrue(res.getBattles().size() != 0);
        List<BattleResponse> battles = res.getBattles();
        BattleResponse firstBattle = battles.get(0);

        assertNotEquals(0, firstBattle.getBattleItems().size());
        assertTrue(firstBattle.getBattleItems().get(0).getType().startsWith("bow"));

        BattleResponse lastBattle = battles.get(battles.size() - 1);

        // the bow is not used
        assertEquals(0, lastBattle.getBattleItems().size());
    }

    @Test
    @Tag("11-20")
    @DisplayName("Test shield durability")
    public void testShieldDurability() throws InvalidActionException {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_battleTests_shieldDurability";
        DungeonResponse res = controller.newGame("d_battleTest_shieldDurabilityTest", config);

        int enemyAttack = Integer.parseInt(TestUtils.getValueFromConfigFile("zombie_attack", config));

        // Set shield durability in config
        // Assumption: Durability is the number of battles a shield lasts
        // Assumption: Shield effect calculation to reduce damage makes enemyAttack =
        // enemyAttack - shield effect
        int shieldEffect = Integer.parseInt(TestUtils.getValueFromConfigFile("shield_defence", config));

        // Set the shield durability in the config
        List<EntityResponse> entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(3, TestUtils.countEntityOfType(entities, "zombie_toast"));

        // Pick up Wood
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);

        // Pick up treasure
        res = controller.tick(Direction.RIGHT);

        // Pick up key
        res = controller.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        res = controller.build("shield");

        // Battle three zombies - third zombie you shouldn't see effect anymore
        // Durability is 2
        while (TestUtils.countEntityOfType(entities, "zombie_toast") != 0) {
            res = controller.tick(Direction.RIGHT);
            entities = res.getEntities();
        }

        assertTrue(res.getBattles().size() != 0);
        List<BattleResponse> battles = res.getBattles();
        BattleResponse firstBattle = battles.get(0);
        assertNotEquals(0, firstBattle.getBattleItems().size());
        assertTrue(firstBattle.getBattleItems().get(0).getType().startsWith("shield"));

        BattleResponse lastBattle = battles.get(battles.size() - 1);

        // the shield is not used
        assertEquals(0, lastBattle.getBattleItems().size());
    }

    @Test
    @Tag("11-21")
    @DisplayName("Test sword increases attack damage")
    public void testSwordIncreasesAttackDamage() {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_battleTests_swordIncreasesAttackDamage";
        controller.newGame("d_battleTest_swordIncreasesAttackDamage", config);
        // Pick up sword
        DungeonResponse res = controller.tick(Direction.RIGHT);

        // Battle the zombie
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        List<BattleResponse> battles = res.getBattles();
        BattleResponse battle = battles.get(0);

        // This is the attack without the sword
        double playerBaseAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", config));
        double swordAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("sword_attack", config));

        RoundResponse firstRound = battle.getRounds().get(0);

        assertEquals((playerBaseAttack + swordAttack) / 5, -firstRound.getDeltaEnemyHealth(), 0.001);
    }

    @Test
    @Tag("11-22")
    @DisplayName("Test sword durability")
    public void testSwordDurability() {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_battleTests_swordDurability";
        DungeonResponse res = controller.newGame("d_battleTest_swordDurabilityTest", config);

        List<EntityResponse> entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(3, TestUtils.countEntityOfType(entities, "zombie_toast"));

        // Pick up sword
        res = controller.tick(Direction.RIGHT);

        // Battle three zombies - third zombie you shouldn't see effect anymore
        // durability of the sword is 2 battles
        while (TestUtils.countEntityOfType(entities, "zombie_toast") != 0) {
            res = controller.tick(Direction.RIGHT);
            entities = res.getEntities();
        }

        assertTrue(res.getBattles().size() != 0);
        List<BattleResponse> battles = res.getBattles();
        BattleResponse firstBattle = battles.get(0);

        assertNotEquals(0, firstBattle.getBattleItems().size());
        assertTrue(firstBattle.getBattleItems().get(0).getType().startsWith("sword"));

        BattleResponse lastBattle = battles.get(battles.size() - 1);

        // the sword is not used
        assertEquals(0, lastBattle.getBattleItems().size());
    }
}
