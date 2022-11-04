package dungeonmania.entities.collectables;


import org.json.JSONObject;

import dungeonmania.util.Position;

public class Treasure extends AnyTreasure {
    public Treasure(Position position) {
        super(position);
    }
    public Treasure(JSONObject j) {
        super(j);
    }
}
