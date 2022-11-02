package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
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
}
