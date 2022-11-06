package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class Arrow extends Collectable {
    public Arrow(Position position) {
        super(position);
    }
    public Arrow(JSONObject j) {
        super(j);
    }
}
