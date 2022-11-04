package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.util.Position;

// basically a tag. Nothing really different to Collectable.
public abstract class BribeMaterial extends Collectable implements InventoryItem {

    public BribeMaterial(Position position) {
        super(position);
    }

    public BribeMaterial(JSONObject j) {
        super(j);
    }

}
