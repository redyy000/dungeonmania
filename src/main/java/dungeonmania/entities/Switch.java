package dungeonmania.entities;


import org.json.JSONObject;

import dungeonmania.entities.logical.Conductor;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Conductor {

    public Switch(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public Switch(JSONObject j) {
        super(j);
        setActivated(j.getBoolean("activated"));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            setActivated(true);
            push(map, true);
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            setActivated(false);
            push(map, false);
        }
    }


    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("activated", isActivated());
        return j;
    }
}
