package dungeonmania.entities.logical;

import dungeonmania.entities.Entity;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends LogicalEntity {
    public SwitchDoor(Position p, boolean activated, String logic) {
        super(p, activated, logic);
    }


    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (isActivated() || entity instanceof Spider) {
            return true;
        }
        return false;
    }
}
