package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class TimeTurner extends Collectable {
    public TimeTurner(Position position) {
        super(position);
    }

    public TimeTurner(JSONObject j) {
        super(j);
    }
}
