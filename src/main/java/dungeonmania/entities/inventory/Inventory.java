package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.SavedEntityFactory;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.BuildableFactory;
import dungeonmania.entities.buildables.Shield;

import dungeonmania.entities.collectables.Sword;



public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public void populateUsingJson(JSONObject invJson) {
        JSONArray itemsJsons = invJson.getJSONArray("items");
        for (int i = 0; i < itemsJsons.length(); i++) {
            JSONObject itemJson = itemsJsons.getJSONObject(i);
            InventoryItem itemInstance = constructItem(itemJson);
            add(itemInstance);
        }
    }

    private InventoryItem constructItem(JSONObject itemJson) {
        String type = itemJson.getString("type");
        switch (type) {
        case "treasure":
        case "sun_stone":
        case "wood":
        case "arrow":
        case "bomb":
        case "invisibility_potion":
        case "invincibility_potion":
        case "sword":
        case "key":
            return (InventoryItem) SavedEntityFactory.createEntity(itemJson);
        case "shield":
            return new Shield(itemJson);
        case "bow":
            return new Bow(itemJson);
        default:
            throw new JSONException("can't create into inventory: " + type);
        }
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables() {
        BuildableFactory bf = new BuildableFactory(this.items);
        return bf.getBuildables();
    }

    public InventoryItem tryBuildItem(Player p, boolean remove, String itemType, EntityFactory factory) {
        BuildableFactory bf = new BuildableFactory(this.items);
        InventoryItem newItem = bf.tryBuildItem(remove, itemType, factory); //expect these inventory items to go away.
        return newItem;
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId)) return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public JSONObject getJSON() {
        List<JSONObject> itemsJsons = getEntities().stream()
                    .map(e -> e.getJSON())
                    .collect(Collectors.toList());
        JSONArray itemsJsonArray = new JSONArray(itemsJsons);

        JSONObject j = new JSONObject();
        j.put("items", itemsJsonArray);
        return j;
    }
}
