package dungeonmania.map;

import org.json.JSONException;
import org.json.JSONObject;

import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.SwampTile;

public class GraphNodeFactory {
    public static GraphNode createEntity(JSONObject jsonEntity, EntityFactory factory) {
        return constructEntity(jsonEntity, factory);
    }

    private static GraphNode constructEntity(JSONObject jsonEntity, EntityFactory factory) {
        switch (jsonEntity.getString("type")) {
        case "player":
        case "zombie_toast":
        case "zombie_toast_spawner":
        case "mercenary":
        case "assassin":
        case "assassin_debug":
        case "wall":
        case "boulder":
        case "switch":
        case "exit":
        case "treasure":
        case "wood":
        case "arrow":
        case "bomb":
        case "invisibility_potion":
        case "invincibility_potion":
        case "portal":
        case "sword":
        case "spider":
        case "door":
        case "key":
        case "sun_stone":
        case "time_turner":
        case "time_travelling_portal":
        case "wire":
        case "switch_door":
        case "light_bulb_on":
        case "light_bulb_off":
            return new GraphNode(factory.createEntity(jsonEntity));
        case "swamp_tile":
            SwampTile tile = (SwampTile) factory.createEntity(jsonEntity);
            return new GraphNode(tile, tile.getPosition(), tile.getMovementFactor());
        default:
            throw new JSONException("Graph Node Factory can't make " + jsonEntity.getString("type"));
        }
    }
}
