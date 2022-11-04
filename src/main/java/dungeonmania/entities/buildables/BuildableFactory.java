package dungeonmania.entities.buildables;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.InventoryItem;

public class BuildableFactory {
    // Note that when we remove from here, we remove from the real inventory.
    private List<InventoryItem> items;

    public BuildableFactory(List<InventoryItem> l) {
        this.items = l;
    }

    public List<String> getBuildables() {
        int wood = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        int sunStones = count(SunStone.class);
        List<String> result = new ArrayList<>();

        if (wood >= 1 && arrows >= 3) {
            result.add("bow");
        }
        if (wood >= 2 && (treasure >= 1 || keys >= 1 || sunStones >= 1)) {
            result.add("shield");
        }
        return result;
    }

    public InventoryItem tryBuildItem(boolean remove, String itemType, EntityFactory factory) {
        List<String> buildableNames = getBuildables();
        if (buildableNames.contains("bow") && itemType.equals("bow")) {
            return buildBow(remove, factory);

        } else if (buildableNames.contains("shield") && itemType.equals("shield")) {
            return buildShield(remove, factory);
        }
        return null;
    }

    private Bow buildBow(boolean remove, EntityFactory factory) {
        if (remove) {
            items.remove(getFirst(Wood.class)); //is this okay?
            items.remove(getFirst(Arrow.class));
            items.remove(getFirst(Arrow.class));
            items.remove(getFirst(Arrow.class));
        }
        return factory.buildBow();
    }

    private Shield buildShield(boolean remove, EntityFactory factory) {
        if (remove) {
            items.remove(getFirst(Wood.class));
            items.remove(getFirst(Wood.class));
            if (has(SunStone.class)) {
                return factory.buildShield(); //build without removing.
            } else if (has(Treasure.class)) {
                items.remove(getFirst(Treasure.class));
            } else {
                items.remove(getFirst(Key.class));
            }
        }
        return factory.buildShield();
    }

    private <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) count++;
        return count;
    }

    private <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) return itemType.cast(item);
        return null;
    }

    private <T extends InventoryItem> boolean has(Class<T> itemType) {
        return getFirst(itemType) != null;
    }
}
