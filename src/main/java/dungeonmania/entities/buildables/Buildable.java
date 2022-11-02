package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.util.NameConverter;
import dungeonmania.util.Position;

public abstract class Buildable extends Entity implements InventoryItem, BattleItem {

    public Buildable(Position position) {
        super(position);
    }

    @Override
    public JSONObject getJSON() {
        // no x or y for a buildable.
        JSONObject j = new JSONObject();
        j.put("type", NameConverter.toSnakeCase(this.getClass().getSimpleName())); //use the string version.
        return j;
    }
}
