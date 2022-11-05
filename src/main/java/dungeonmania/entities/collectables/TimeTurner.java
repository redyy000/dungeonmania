package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class TimeTurner extends Collectable {
    public TimeTurner(Position position) {
        super(position);
    }

    public TimeTurner(JSONObject j) {
        super(j);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
