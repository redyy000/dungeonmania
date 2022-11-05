package dungeonmania.entities;


import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.TimeTurner;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.enemies.Assassin;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.PlayerGhost;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.enemies.ZombieToast;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.util.Position;

public class SavedEntityFactory {
    // Unloads saved entities. Note that the JSON objects of saved entities are
    // different from their appearance in a d_dungeon.java file. (they have more fields).
    // This factory doesn't need a config. Config sets initial values, here we get latest.

    //Shouldn't be instantiated.

    public static Entity createEntity(JSONObject jsonEntity) {
        return constructEntity(jsonEntity);
    }

    public static PlayerGhost createPlayerGhost(JSONObject jsonEntity, Queue<Position> moveHistory) {
        return new PlayerGhost(jsonEntity, moveHistory);
    }

    private static Entity constructEntity(JSONObject jsonEntity) {
        switch (jsonEntity.getString("type")) {
        case "player":
            return buildPlayer(jsonEntity);
        case "zombie_toast":
            return new ZombieToast(jsonEntity);
        case "zombie_toast_spawner":
            return new ZombieToastSpawner(jsonEntity);
        case "spider":
            return new Spider(jsonEntity);
        case "mercenary":
            return new Mercenary(jsonEntity);
        case "assassin":
            return new Assassin(jsonEntity);
        case "assassin_debug":
            return new Assassin(jsonEntity);
        case "wall":
            return new Wall(jsonEntity);
        case "boulder":
            return new Boulder(jsonEntity);
        case "switch":
            return new Switch(jsonEntity);
        case "exit":
            return new Exit(jsonEntity);
        case "treasure":
            return new Treasure(jsonEntity);
        case "sun_stone":
            return new SunStone(jsonEntity);
        case "wood":
            return new Wood(jsonEntity);
        case "arrow":
            return new Arrow(jsonEntity);
        case "bomb":
            return new Bomb(jsonEntity);
        case "sword":
            return new Sword(jsonEntity);
        case "key":
            return new Key(jsonEntity);
        case "door":
            return new Door(jsonEntity);
        case "invisibility_potion":
            return new InvisibilityPotion(jsonEntity);
        case "invincibility_potion":
            return new InvincibilityPotion(jsonEntity);
        case "portal":
            return new Portal(jsonEntity);
        case "time_turner":
            return new TimeTurner(jsonEntity);
        default:
            throw new JSONException(jsonEntity.getString("type") + " can't be made from save");
        }
    }

    private static Player buildPlayer(JSONObject jsonEntity) {
        return new Player(jsonEntity);
    }
}
