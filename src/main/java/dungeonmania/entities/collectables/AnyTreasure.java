package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.PlayerGhost;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class AnyTreasure extends Collectable {
    public AnyTreasure(Position position) {
        super(position);
    }
    public AnyTreasure(JSONObject j) {
        super(j);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this)) return;
            map.getGame().increaseNCollectedTreasure();
            map.destroyEntity(this);
        } else if (entity instanceof PlayerGhost) {
            map.destroyEntity(this);
        }
    }
}
