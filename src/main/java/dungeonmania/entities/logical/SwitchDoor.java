package dungeonmania.entities.logical;

import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class SwitchDoor extends Entity {
    private boolean open;
    public SwitchDoor(Position p) {
        super(p);
    }
}
