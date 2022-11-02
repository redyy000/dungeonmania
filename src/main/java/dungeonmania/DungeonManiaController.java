package dungeonmania;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

public class DungeonManiaController {
    private Game game = null;
    private JSONObject configJson;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException(dungeonName + " is not a dungeon that exists");
        }

        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }

        try { //will always work anyway.
            this.configJson = loadConfig(configName);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            GameBuilder builder = new GameBuilder();
            game = builder.setConfigName(configName).setDungeonName(dungeonName).buildGame();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return null;
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.tick(itemUsedId));
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        return ResponseBuilder.getDungeonResponse(game.tick(movementDirection));
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        List<String> validBuildables = List.of("bow", "shield", "midnight_armour", "sceptre");
        if (!validBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Only bow, shield, midnight_armour and sceptre can be built");
        }

        return ResponseBuilder.getDungeonResponse(game.build(buildable));
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.interact(entityId));
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        JSONObject newgameJson = new JSONObject();
        newgameJson.put("config", this.configJson);

        // save all entities positions to JSON. Do not use a debug Mercenary when testing saves :).
        JSONObject currentDungeon = new JSONObject();
        List<JSONObject> entityJsons = game.getMap().getEntities().stream()
                                    .map(e -> e.getJSON()) //type and position.
                                    .collect(Collectors.toList());
        JSONArray entityJsonArray = new JSONArray(entityJsons);
        currentDungeon.put("entities", entityJsonArray);
        currentDungeon.put("goal-condition", game.getGoals().getJson());

        newgameJson.put("dungeon", currentDungeon);


        FileWriter file;
        try {
            file = new FileWriter(String.format("src/main/resources/saves/%s.json", name));
            file.write(newgameJson.toString());
            file.close();

        } catch (IOException e) {
            throw new IllegalArgumentException(name + " cannot be saved to a file. File writer.");
        }

        return ResponseBuilder.getDungeonResponse(game);
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        // List<String> saveNames = FileLoader.listFileNamesInResourceDirectory("saves");

        JSONObject savedJson;
        try {
            savedJson = loadSave(name);
        } catch (IOException e1) {
            throw new IllegalArgumentException("No such game with name " + name + " to load.");
        }

        try {
            GameBuilder builder = new GameBuilder();
            game = builder.buildGame(savedJson);
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            return null;
        }
    }

    // Returns JSONObject of save file/ save file. Throw if dne
    private JSONObject loadSave(String saveName) throws IOException {
        String saveFileName = String.format("/saves/%s.json", saveName);
        JSONObject saveFile;

        saveFile = new JSONObject(FileLoader.loadResourceFile(saveFileName)); //throws
        return saveFile;
    }
    /**
     * /games/all
     */
    public List<String> allGames() {
        return new ArrayList<>();
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(
            int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException {
        return null;
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        return null;
    }

    private JSONObject loadConfig(String configName) throws IOException {
        String configFile = String.format("/configs/%s.json", configName);
        JSONObject config;

        config = new JSONObject(FileLoader.loadResourceFile(configFile));
        return config;
    }
}
