package dungeonmania.entities;

import org.json.JSONObject;

import dungeonmania.entities.collectables.Treasure;

public class SavedEntityFactory {
    // Unloads saved entities. Note that the JSON objects of saved entities are
    // different from their appearance in a d_dungeon.java file. (they have more fields).

    //Shouldn't be instantiated.

    public static Entity createEntity(JSONObject jsonEntity) {
        return constructEntity(jsonEntity);
    }

    private static Entity constructEntity(JSONObject jsonEntity) {
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

    private static Player buildPlayer(JSONObject jsonEntity) {
        return new Player(jsonEntity);
    }
    private static Treasure buildTreasure(JSONObject jsonEntity) {
        return new Treasure(jsonEntity);
    }
}
