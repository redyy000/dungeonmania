package dungeonmania.entities.logical;

import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class LightBulb extends Entity {
    private boolean activated;
    public LightBulb(Position p, boolean activated) {
        super(p);
    }
    public boolean isActivated() {
        return activated;
    }


    //TODO getJson is not usual type. replace "type" key.
}
