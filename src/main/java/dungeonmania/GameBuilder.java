package dungeonmania;

import java.io.IOException;

import org.json.JSONObject;

import dungeonmania.entities.EntityFactory;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalFactory;
import dungeonmania.map.GameMap;

import dungeonmania.util.FileLoader;

/**
 * GameBuilder -- A builder to build up the whole game
 * @author      Webster Zhang
 * @author      Tina Ji
 */
public class GameBuilder {
    private String configName;
    private String dungeonName;

    private JSONObject config;
    private JSONObject dungeon;

    public GameBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public GameBuilder setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
        return this;
    }

    public GameBuilder setConfig(JSONObject config) {
        this.config = config;
        return this;
    }

    public GameBuilder setDungeon(JSONObject dungeon) {
        this.dungeon = dungeon;
        return this;
    }

    public Game buildGame() {
        loadConfig();
        loadDungeon();
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }

        Game game = new Game(dungeonName);
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);
        game.init();

        return game;
    }

    // Load saved file build-game.
    public Game buildGame(JSONObject savedJson) {
        /* KEys:
         * config,  goal-condition, game, gameMap.
         * this.config should remain null. Collect the config from the save file instead.
         */
        JSONObject savedConfig = savedJson.getJSONObject("config");
        Game game = new Game(savedJson.getJSONObject("game"));
        game.setEntityFactory(new EntityFactory(savedConfig));
        // SavedEntityFactory factory = new SavedEntityFactory(config); //create entities considering they're saved.
        // game.setEntityFactory(factory);
        GameMap map = new GameMap(game, savedJson.getJSONArray("gameMap"));
        game.setMap(map);
        if (!savedJson.isNull("goal-condition")) {
            Goal goal = GoalFactory.createGoal(savedJson.getJSONObject("goal-condition"), savedConfig);
            game.setGoals(goal);
        }
        game.initSavedGame();

        return game;
    }

    private void loadConfig() {
        String configFile = String.format("/configs/%s.json", configName);
        try {
            config = new JSONObject(FileLoader.loadResourceFile(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            config = null;
        }
    }

    private void loadDungeon() {
        String dungeonFile = String.format("/dungeons/%s.json", dungeonName);
        try {
            dungeon = new JSONObject(FileLoader.loadResourceFile(dungeonFile));
        } catch (IOException e) {
            dungeon = null;
        }
    }

    private void buildMap(Game game) {
        GameMap map = new GameMap(game, this.dungeon);
        game.setMap(map);
    }

    public void buildGoals(Game game) {
        if (!dungeon.isNull("goal-condition")) {
            Goal goal = GoalFactory.createGoal(dungeon.getJSONObject("goal-condition"), config);
            game.setGoals(goal);
        }
    }
}
