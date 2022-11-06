package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Entity {
    private boolean activated;
    private List<Bomb> bombs = new ArrayList<>();

    public Switch(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public Switch(JSONObject j) {
        super(j);
        this.activated = j.getBoolean("activated");
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (activated) {
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = true;
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = false;
        }
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("activated", this.activated);
        return j;
    }
}
