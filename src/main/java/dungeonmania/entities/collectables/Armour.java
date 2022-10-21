package dungeonmania.entities.collectables;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Armour extends Entity implements InventoryItem {

    public Armour(Position position) {
        super(position);
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDestroy(GameMap gameMap) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
