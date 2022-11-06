package dungeonmania.entities;

import org.json.JSONObject;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Exit extends Entity {
    public Exit(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public Exit(JSONObject j) {
        super((new Position(j.getJSONObject("position"))));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
