package dungeonmania.entities;

import dungeonmania.map.GameMap;

import org.json.JSONObject;

import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.util.Position;

public class Door extends Entity {
    private boolean open = false;
    private int number;

    public Door(Position position, int number) {
        super(position.asLayer(Entity.DOOR_LAYER));
        this.number = number;
    }

    public Door(JSONObject j) {
        super(j);
        this.open = j.getBoolean("open");
        this.number = j.getInt("number");
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (open || entity instanceof Spider) {
            return true;
        }
        return (entity instanceof Player && (hasKey((Player) entity) || hasSunStone((Player) entity)));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (!(entity instanceof Player))
            return;

        Player player = (Player) entity;
        Inventory inventory = player.getInventory();

        SunStone sunStone = inventory.getFirst(SunStone.class);
        if (sunStone != null) {
            open();
        }

        Key key = inventory.getFirst(Key.class);
        if (hasKey(player)) {
            inventory.remove(key);
            open();
        }
    }

    private boolean hasKey(Player player) {
        Inventory inventory = player.getInventory();
        Key key = inventory.getFirst(Key.class);

        return (key != null && key.getnumber() == number);
    }

    private boolean hasSunStone(Player player) {
        Inventory inventory = player.getInventory();
        SunStone sunStone = inventory.getFirst(SunStone.class);

        return (sunStone != null);
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }

    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("open", this.open);
        j.put("number", this.number);
        return j;
    }
}
