package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Conductor extends Entity {
    private List<SwitchObserver> subscribers = new ArrayList<>();
    private boolean activated;

    public Conductor(Position p) {
        super(p);
    }

    public Conductor(JSONObject j) {
        super(j);
        this.activated = j.getBoolean("activated");
    }

    /**
     * subscribe a given SwithcObserver to this object.
     * @param s
     */
    public void subscribe(SwitchObserver s) {
        subscribers.add(s);
    }

    /**
     * subscribe a given SwithcObserver to this object.
     * @param s
     */
    public void subscribe(SwitchObserver s, GameMap map) {
        subscribers.add(s);
        if (activated) {
            push(map, activated); //don't think its for each though.
        }
    }
    public void unsubscribe(SwitchObserver s) {
        subscribers.remove(s);
    }
    public boolean isActivated() {
        return this.activated;
    }
    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    /**
     * Tell all subscribers that this object just activated.
     * @param map
     */
    protected void push(GameMap map, boolean activated) {
        System.out.println(getId() + " is a conductor pushing.");
        subscribers.stream().forEach(b -> b.notify(map, activated));
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
    }

    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("activated", this.activated);
        return j;
    }
}
