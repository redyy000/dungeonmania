package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Treasure extends Collectable implements InventoryItem {
    public Treasure(Position position) {
        super(position);
    }
    public Treasure(JSONObject j) {
        super(j);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this)) return;
            map.getGame().increaseNCollectedTreasure();
            map.destroyEntity(this);
        }
    }

}
