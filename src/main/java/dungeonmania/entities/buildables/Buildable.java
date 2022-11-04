package dungeonmania.entities.buildables;

import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.util.Position;

public abstract class Buildable extends Entity implements InventoryItem {

    public Buildable(Position p) {
        super(p);
    }
}
