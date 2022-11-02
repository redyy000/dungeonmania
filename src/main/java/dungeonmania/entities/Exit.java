package dungeonmania.entities;

import org.json.JSONObject;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Exit extends Entity {
    public Exit(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public Exit(JSONObject j) {
        // create Position with JSON. If for soemreason not right layer, set it.
        super((new Position(j)).asLayer(Entity.ITEM_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
