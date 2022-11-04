package dungeonmania.entities;

import dungeonmania.map.GameMap;

import org.json.JSONObject;

import dungeonmania.entities.enemies.Spider;
import dungeonmania.util.Position;

public class Wall extends Entity {
    public Wall(Position position) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
    }

    public Wall(JSONObject j) {
        super(j);
    }
    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Spider;
    }
}
