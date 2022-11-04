package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;

public abstract class Buildable extends Entity implements InventoryItem {

    public Buildable() {
        super();
    }
    public Buildable(JSONObject j) {
        super(j);
    }
}
