package dungeonmania.entities;

import org.json.JSONObject;

import dungeonmania.entities.collectables.Treasure;

public class SavedEntityFactory extends EntityFactory {
    // Unloads saved entities. Note that the JSON objects of saved entities are
    // different from their appearance in a d_dungeon.java file. (they have more fields).
    public SavedEntityFactory(JSONObject config) {
        super(config);
    }

    public Entity createEntity(JSONObject jsonEntity) {
        return constructEntity(jsonEntity);
    }

    private Entity constructEntity(JSONObject jsonEntity) {
        switch (jsonEntity.getString("type")) {
        case "player":
            return buildPlayer(jsonEntity);
        case "treasure":
            return buildTreasure(jsonEntity);
        case "exit":
            return new Exit(jsonEntity);
        default:
            return null;
        }
    }

    private Player buildPlayer(JSONObject jsonEntity) {
        return new Player(jsonEntity);
    }
    private Treasure buildTreasure(JSONObject jsonEntity) {
        return new Treasure(jsonEntity);
    }
}
