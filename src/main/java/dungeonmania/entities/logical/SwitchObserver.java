package dungeonmania.entities.logical;

import dungeonmania.map.GameMap;

public interface SwitchObserver {
    /**
     * Observe another Switch/Wire.
     * @param s
     */
    public void subscribe(Conductor s);

    /**
     * This object does something when notify is called. (when nearby is activated)
     * @param map
     */
    public void notify(GameMap map, boolean activated);
}
