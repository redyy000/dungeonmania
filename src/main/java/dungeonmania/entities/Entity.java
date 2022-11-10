package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.NameConverter;
import dungeonmania.util.Position;

import java.util.UUID;

import org.json.JSONObject;

public abstract class Entity {
    public static final int FLOOR_LAYER = 0;
    public static final int ITEM_LAYER = 1;
    public static final int DOOR_LAYER = 2;
    public static final int CHARACTER_LAYER = 3;

    private Position position;
    private String entityId;
    public Entity() {
        this.position = null; //for buildables.
        this.entityId = UUID.randomUUID().toString();
    }

    public Entity(Position position) {
        this.position = position;
        this.entityId = UUID.randomUUID().toString();
    }
    public Entity(JSONObject j) {
        this.position = null;
        if (j.has("position")) {
            this.position = new Position(j.getJSONObject("position"));
        }
        this.entityId = j.getString("id");
    }

    public boolean canMoveOnto(GameMap map, Entity entity) {
        return false;
    }

    public void onOverlap(GameMap map, Entity entity) {
        return;
    }

    public void onMovedAway(GameMap map, Entity entity) {
        return;
    }

    public void onDestroy(GameMap gameMap) {
        return;
    }

    public Position getPosition() {
        return position;
    }

    public String getId() {
        return entityId;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    public JSONObject getJSON() {
        JSONObject j = new JSONObject();
        j.put("type", NameConverter.toSnakeCase(this.getClass()));
        if (this.position != null) {
            j.put("position", this.position.getJSON());
        }
        j.put("id", this.entityId);
        return j;
    }
}
