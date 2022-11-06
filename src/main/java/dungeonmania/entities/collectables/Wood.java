package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class Wood extends Collectable {
    public Wood(Position position) {
        super(position);
    }

    public Wood(JSONObject j) {
        super(j);
    }
}
