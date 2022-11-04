package dungeonmania.entities.buildables;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.enemies.ZombieToast;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;

public class BuildableFactory {
    // Note that when we remove from here, we remove from the real inventory.
    private List<InventoryItem> items;

    public BuildableFactory(List<InventoryItem> l) {
        this.items = l;
    }

    public List<String> getBuildables(GameMap map) {
        boolean noZombies = !mapHasZombies(map);
        int wood = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        int sunStones = count(SunStone.class);
        int swords = count(Sword.class);

        List<String> result = new ArrayList<>();

        if (wood >= 1 && arrows >= 3) {
            result.add("bow");
        }
        if (wood >= 2 && (treasure >= 1 || keys >= 1 || sunStones >= 1)) {
            result.add("shield");
        }
        if (((wood >= 1 || arrows >= 2) && (sunStones >= 2))
         || ((wood >= 1 || arrows >= 2) && (sunStones == 1) && (treasure >= 1 || keys >= 1))) {
            result.add("sceptre");
        }
        if (swords >= 1 && sunStones >= 1 && noZombies) {
            result.add("midnight_armour");
        }
        return result;
    }

    public InventoryItem tryBuildItem(boolean remove, String itemType, EntityFactory factory, GameMap map) {
        List<String> buildableNames = getBuildables(map);
        if (buildableNames.contains("bow") && itemType.equals("bow")) {
            return buildBow(remove, factory);
        } else if (buildableNames.contains("shield") && itemType.equals("shield")) {
            return buildShield(remove, factory);
        } else if (buildableNames.contains("sceptre") && itemType.equals("sceptre")) {
            return buildSceptre(remove, factory);
        } else if (buildableNames.contains("midnight_armour") && itemType.equals("midnight_armour")) {
            return buildMidnightArmour(remove, factory);
        }
        return null;
    }

    private Bow buildBow(boolean remove, EntityFactory factory) {
        if (remove) {
            items.remove(getFirst(Wood.class));
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

    // precondition that sceptre is buildable.
    private Sceptre buildSceptre(boolean remove, EntityFactory factory) {
        Queue<String> combination = getSceptreCombination();
        if (remove) {
            consumeSceptreMaterials(combination);
        }
        return factory.buildSceptre();
    }

    private Queue<String> getSceptreCombination() {
        int wood = count(Wood.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        int sunStones = count(SunStone.class);
        // Queue is two strings.
        Queue<String> combination = new LinkedList<>();
        if (wood >= 1) {
            combination.add("wood");
        } else {
            combination.add("arrow");
        }

        if (sunStones >= 2) {
            combination.add("sun_stone");
        } else {
            if (treasure >= keys) {
                combination.add("treasure");
            } else {
                combination.add("key");
            }
        }
        return combination;
    }
    private void consumeSceptreMaterials(Queue<String> combination) {
        // 1 sun stone
        items.remove(getFirst(SunStone.class));
        //1 wood or 2 arrows
        if (combination.remove().equals("wood")) {
            items.remove(getFirst(Wood.class));
        } else {
            items.remove(getFirst(Arrow.class));
            items.remove(getFirst(Arrow.class));
        }
        // a treasure or key. If it was sun_stone, do nothing.
        String second = combination.remove();
        if (second.equals("treasure")) {
            items.remove(getFirst(Treasure.class));
        } else if (second.equals("key")) {
            items.remove(getFirst(Key.class));
        }
    }

    private MidnightArmour buildMidnightArmour(boolean remove, EntityFactory factory) {
        if (remove) {
            items.remove(getFirst(Sword.class));
            items.remove(getFirst(SunStone.class));
        }
        return factory.buildMidnightArmour();
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

    private boolean mapHasZombies(GameMap map) {
        return map.getEntities(ZombieToast.class).size() > 0;
    }
}
