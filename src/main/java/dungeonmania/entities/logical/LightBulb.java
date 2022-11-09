package dungeonmania.entities.logical;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class LightBulb extends LogicalEntity {

    public LightBulb(Position p, boolean activated, String logic) {
        super(p, activated, logic);
    }

    public LightBulb(JSONObject j) {
        super(j);
    }
}
