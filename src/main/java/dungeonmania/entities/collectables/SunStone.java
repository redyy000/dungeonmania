package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.util.Position;

// no methods or attributes that make it special.
public class SunStone extends AnyTreasure {

    public SunStone(Position position) {
        super(position);
    }

    public SunStone(JSONObject j) {
        super(j);
    }
}
