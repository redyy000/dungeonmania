package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

public class TestUtils {

    public static List<EntityResponse> getEntities(DungeonResponse res) {
        return res.getEntities();
    }

    public static List<ItemResponse> getInventory(DungeonResponse res, String type) {
        return res.getInventory().stream().filter(it -> it.getType().startsWith(type)).collect(Collectors.toList());
    }

    public static String getFirstItemId(DungeonResponse res, String itemType) {
        return TestUtils.getInventory(res, itemType).get(0).getId();
    }

    public static Stream<EntityResponse> getEntitiesStream(DungeonResponse res, String type) {
        if (type.equals("zombie_toast")) {
            return res.getEntities().stream()
                    .filter(it -> it.getType().startsWith(type))
                    .filter(it -> !it.getType().startsWith("zombie_toast_spawner"));
        }
        if (type.equals("player")) { //similarly, it will confusing player and playerGhost.
            return res.getEntities().stream()
                    .filter(it -> it.getType().startsWith(type))
                    .filter(it -> !it.getType().startsWith("older_player"));
        }
        return res.getEntities().stream().filter(it -> it.getType().startsWith(type));
    }

    public static long countType(DungeonResponse res, String type) {
        return getEntitiesStream(res, type).count();
    }

    public static List<EntityResponse> getEntities(DungeonResponse res, String type) {
        return getEntitiesStream(res, type).collect(Collectors.toList());
    }

    public static Optional<EntityResponse> getPlayer(DungeonResponse res) {
        return getEntitiesStream(res, "player").findFirst();
    }

    public static Optional<EntityResponse> getEntityAtPos(DungeonResponse res, String type, Position pos) {
        return getEntitiesStream(res, type).filter(it -> it.getPosition().equals(pos)).findFirst();
    }

    public static Optional<EntityResponse> getEntityById(DungeonResponse res, String id) {
        return res.getEntities().stream().filter(it -> it.getId().equals(id)).findFirst();
    }

    public static int getManhattanDistance(Position pos1, Position pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY());
    }

    public static int countEntityOfTypeInInventory(DungeonResponse res, String type) {
        return getInventory(res, type).size();
    }

    public static List<Position> getEntityPositions(DungeonResponse res, String type) {
        return getEntities(res, type).stream().map(e -> e.getPosition()).collect(Collectors.toList());
    }

    public static double getEuclideanDistance(Position pos1, Position pos2) {
        int xDiff = pos1.getX() - pos2.getX();
        int yDiff = pos1.getY() - pos2.getY();
        return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
    }

    public static List<Position> getSpiderTrajectory(Position spawnPos) {
        List<Position> movementTrajectory = new ArrayList<>();
        int x = spawnPos.getX();
        int y = spawnPos.getY();
        movementTrajectory.add(new Position(x, y - 1));
        movementTrajectory.add(new Position(x + 1, y - 1));
        movementTrajectory.add(new Position(x + 1, y));
        movementTrajectory.add(new Position(x + 1, y + 1));
        movementTrajectory.add(new Position(x, y + 1));
        movementTrajectory.add(new Position(x - 1, y + 1));
        movementTrajectory.add(new Position(x - 1, y));
        movementTrajectory.add(new Position(x - 1, y - 1));
        return movementTrajectory;
    }

    public static List<Position> getCardinallyAdjacentPositions(Position pos) {
        int x = pos.getX();
        int y = pos.getY();
        List<Position> adjacentPositions = new ArrayList<>();
        adjacentPositions.add(new Position(x, y - 1));
        adjacentPositions.add(new Position(x + 1, y));
        adjacentPositions.add(new Position(x, y + 1));
        adjacentPositions.add(new Position(x - 1, y));
        return adjacentPositions;
    }

    public static String getGoals(DungeonResponse dr) {
        String goals = dr.getGoals();
        return goals != null ? goals : "";
    }

    public static DungeonResponse newGame(DungeonManiaController dmc, String dungeonName, String configName) {
        // legacy test using gameMode as second parameter
        if (!configName.startsWith("c_")) {
            throw new IllegalArgumentException(
                    "Second parameter to newGame should be config file name starting with 'c_'");
        }

        try {
            DungeonResponse dr = dmc.newGame(dungeonName, configName);
            if (dr == null)
                throw new Exception();
            return dr;
        } catch (Throwable e) {
            throw new RuntimeException();
        }
    }

    public static boolean atOrAdjacentTo(int x, int y, int toX, int toY) {
        return (x == toX || x == toX - 1 || x == toX + 1)
                && (y == toY || y == toY - 1 || y == toY + 1);
    }

    @Deprecated(forRemoval = true)
    public static int countEntitiesOfType(DungeonResponse response, String type) {
        return response.getEntities()
                .stream()
                .filter(e -> e.getType().startsWith(type))
                .collect(Collectors.toList()).size();
    }

    public static boolean entityAtPosition(DungeonResponse res, String type, Position pos) {
        return getEntitiesStream(res, type).anyMatch(
                it -> it.getPosition().equals(pos));
    }

    public static DungeonResponse newGame(
            DungeonManiaController dmc, String dungeonName, String gamemode, String configName) {
        return newGame(dmc, dungeonName, configName);
    }

    public static int countEntityOfType(List<EntityResponse> entities, String type) {
        if (type.equals("zombie_toast")) {
            return entities.stream()
                    .filter(e -> e.getType().startsWith(type))
                    .filter(e -> !e.getType().startsWith("zombie_toast_spawner"))
                    .collect(Collectors.toList()).size();
        }
        return entities.stream()
                .filter(e -> e.getType().startsWith(type))
                .collect(Collectors.toList())
                .size();
    }

    public static DungeonResponse genericSpiderSequence(DungeonManiaController controller, String configFile) {
        DungeonResponse initialResponse = controller.newGame("d_battleTest_basicSpider", configFile);
        List<EntityResponse> entities = initialResponse.getEntities();
        int spiderCount = countEntityOfType(entities, "spider");
        assertEquals(1, countEntityOfType(entities, "player"));
        assertEquals(1, spiderCount);
        return controller.tick(Direction.RIGHT);
    }

    public static DungeonResponse genericZombieSequence(DungeonManiaController controller, String configFile) {
        DungeonResponse response = controller.newGame("d_battleTest_basicZombie", configFile);
        List<EntityResponse> entities = response.getEntities();
        assertEquals(1, countEntityOfType(entities, "player"));
        assertEquals(1, countEntityOfType(entities, "zombie_toast"));

        for (int i = 0; i < 3; i++) {
            response = controller.tick(Direction.RIGHT);
            // Check if there is a battle - if there is one of the player or zombie is dead
            int battlesHeld = response.getBattles().size();
            if (battlesHeld != 0) {
                break;
            }
        }
        return response;
    }

    public static DungeonResponse genericMercenarySequence(DungeonManiaController controller, String configFile) {
        DungeonResponse response = controller.newGame("d_battleTest_basicMercenary", configFile);
        List<EntityResponse> entities = response.getEntities();
        int mercenaryCount = countEntityOfType(entities, "mercenary");
        assertEquals(1, countEntityOfType(entities, "player"));
        assertEquals(1, mercenaryCount);
        for (int i = 0; i < 3; i++) {
            response = controller.tick(Direction.RIGHT);
            // Check if there is a battle - if there is one of the player or merc is dead
            int battlesHeld = response.getBattles().size();
            if (battlesHeld != 0) {
                break;
            }
        }
        return response;
    }

    public static String getValueFromConfigFile(String fieldName, String configFilePath) {
        try {
            JSONObject config = new JSONObject(FileLoader.loadResourceFile("/configs/" + configFilePath + ".json"));
            if (!config.isNull(fieldName)) {
                return config.get(fieldName).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntityPos(res, "player");
    }

    public static Position getEntityPos(DungeonResponse res, String entity) {
        return getEntities(res, entity).get(0).getPosition();
    }

    public static boolean entityResponsesEqual(EntityResponse e1, EntityResponse e2) {
        return e1.getId().equals(e2.getId())
                && e1.getType().equals(e2.getType())
                && e1.getPosition().equals(e2.getPosition());
    }

    public static Map<Position, List<String>> positionMapEntities(
            List<EntityResponse> ers) {
        return ers.stream().collect(Collectors.toMap(
                EntityResponse::getPosition, it -> new ArrayList<>(Collections.singleton(it.getType())),
                (o, n) -> {
                    o.addAll(n);
                    return o;
                }));
    }

    public static boolean entityListEqual(List<EntityResponse> l1, List<EntityResponse> l2) {
        Map<Position, List<String>> m2 = positionMapEntities(l2);
        return l1.stream().allMatch(e -> m2.get(e.getPosition()).remove(e.getType()));
    }

    public static Map<String, Integer> countItemsInList(List<ItemResponse> irs) {
        return irs.stream().collect(Collectors.toMap(
                ItemResponse::getType, it -> 1, Integer::sum));
    }

    public static boolean itemListEqual(List<ItemResponse> l1, List<ItemResponse> l2) {
        return countItemsInList(l1).equals(countItemsInList(l2));
    }

    public static boolean dungeonResponseEqual(DungeonResponse d1, DungeonResponse d2) {
        boolean buildables = TestUtils.genericListsEqual(d1.getBuildables(), d2.getBuildables());
        boolean items = itemListEqual(d1.getInventory(), d2.getInventory());
        boolean goals = d1.getGoals().equals(d2.getGoals());
        boolean entities = entityListEqual(d1.getEntities(), d2.getEntities());
        return buildables && entities && items && goals;
    }

    static <T> boolean genericListsEqual(List<T> l1, List<T> l2) {
        return l1.containsAll(l2) && l2.containsAll(l1);
    }

}
