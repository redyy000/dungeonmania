package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class SunStone extends Treasure {

    public SunStone(Position position) {
        super(position);
    }

    public SunStone(JSONObject j) {
        super(j);
    }

}
