package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Collectable extends Entity {

    public Collectable(Position position) {
        super(position);
    }
    public Collectable(JSONObject j) {
        super(new Position(j));
    }
    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this)) return;
            map.destroyEntity(this);
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        return;
    }

    @Override
    public void onDestroy(GameMap gameMap) {
        return;
    }
}
