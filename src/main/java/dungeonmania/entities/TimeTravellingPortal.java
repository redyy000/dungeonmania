package dungeonmania.entities;

import org.json.JSONObject;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class TimeTravellingPortal extends Entity {
    public TimeTravellingPortal(Position position) {
        super(position);
    }

    public TimeTravellingPortal(JSONObject j) {
        super(j);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player)
            map.getGame().registerRewind(30);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

}
