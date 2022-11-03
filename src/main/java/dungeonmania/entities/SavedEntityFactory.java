package dungeonmania.entities;

import org.json.JSONObject;

import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;

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
        case "wall":
            return new Wall(jsonEntity);
        case "boulder":
            return new Boulder(jsonEntity);
        case "switch":
            return new Switch(jsonEntity);
        case "exit":
            return new Exit(jsonEntity);
        case "treasure":
            return new Treasure(jsonEntity);
        case "wood":
            return new Wood(jsonEntity);
        case "arrow":
            return new Arrow(jsonEntity);
        case "bomb":
            return new Bomb(jsonEntity);

        default:
            return null;
        }
    }

    private static Player buildPlayer(JSONObject jsonEntity) {
        return new Player(jsonEntity);
    }
}
