package dungeonmania.entities;

import dungeonmania.Game;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.MidnightArmour;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.*;
import dungeonmania.entities.enemies.*;
import dungeonmania.map.GameMap;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class EntityFactory {
    private JSONObject config;
    private Random ranGen = new Random();

    public EntityFactory(JSONObject config) {
        this.config = config;
    }

    public Entity createEntity(JSONObject jsonEntity) {
        return constructEntity(jsonEntity);
    }

    public void spawnSpider(Game game) {
        GameMap map = game.getMap();
        int tick = game.getTick();
        int rate = config.optInt("spider_spawn_interval", 0);
        if (rate == 0 || (tick + 1) % rate != 0) return;
        int radius = 20;
        Position player = map.getPlayer().getPosition();

        Spider dummySpider = buildSpider(new Position(0, 0)); // for checking possible positions

        List<Position> availablePos = new ArrayList<>();
        for (int i = player.getX() - radius; i < player.getX() + radius; i++) {
            for (int j = player.getY() - radius; j < player.getY() + radius; j++) {
                if (Position.calculatePositionBetween(player, new Position(i, j)).magnitude() > radius) continue;
                Position np = new Position(i, j);
                if (!map.canMoveTo(dummySpider, np)) continue;
                availablePos.add(np);
            }
        }
        Position initPosition = availablePos.get(ranGen.nextInt(availablePos.size()));
        Spider spider = buildSpider(initPosition);
        map.addEntity(spider);
        game.register(() -> spider.move(game), Game.AI_MOVEMENT, spider.getId());
    }

    public void spawnZombie(Game game, ZombieToastSpawner spawner) {
        GameMap map = game.getMap();
        int tick = game.getTick();
        Random randGen = new Random();
        int spawnInterval = config.optInt("zombie_spawn_interval", ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL);
        if (spawnInterval == 0 || (tick + 1) % spawnInterval != 0) return;
        List<Position> pos = spawner.getPosition().getCardinallyAdjacentPositions();
        pos = pos
            .stream()
            .filter(p -> !map.getEntities(p).stream().anyMatch(e -> (e instanceof Wall)))
            .collect(Collectors.toList());
        if (pos.size() == 0) return;
        ZombieToast zt = buildZombieToast(pos.get(randGen.nextInt(pos.size())));
        map.addEntity(zt);
        game.register(() -> zt.move(game), Game.AI_MOVEMENT, zt.getId());
    }

    public Spider buildSpider(Position pos) {
        double spiderHealth = config.optDouble("spider_health", Spider.DEFAULT_HEALTH);
        double spiderAttack = config.optDouble("spider_attack", Spider.DEFAULT_ATTACK);
        return new Spider(pos, spiderHealth, spiderAttack);
    }

    public Player buildPlayer(Position pos) {
        double playerHealth = config.optDouble("player_health", Player.DEFAULT_HEALTH);
        double playerAttack = config.optDouble("player_attack", Player.DEFAULT_ATTACK);
        return new Player(pos, playerHealth, playerAttack);
    }

    public ZombieToast buildZombieToast(Position pos) {
        double zombieHealth = config.optDouble("zombie_health", ZombieToast.DEFAULT_HEALTH);
        double zombieAttack = config.optDouble("zombie_attack", ZombieToast.DEFAULT_ATTACK);
        return new ZombieToast(pos, zombieHealth, zombieAttack);
    }

    public ZombieToastSpawner buildZombieToastSpawner(Position pos) {
        int zombieSpawnRate = config.optInt("zombie_spawn_interval", ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL);
        return new ZombieToastSpawner(pos, zombieSpawnRate);
    }

    public Mercenary buildMercenary(Position pos) {
        double mercenaryHealth = config.optDouble("mercenary_health", Mercenary.DEFAULT_HEALTH);
        double mercenaryAttack = config.optDouble("mercenary_attack", Mercenary.DEFAULT_ATTACK);
        int mercenaryBribeAmount = config.optInt("bribe_amount", Mercenary.DEFAULT_BRIBE_AMOUNT);
        int mercenaryBribeRadius = config.optInt("bribe_radius", Mercenary.DEFAULT_BRIBE_RADIUS);
        return new Mercenary(pos, mercenaryHealth, mercenaryAttack, mercenaryBribeAmount, mercenaryBribeRadius);
    }

    public Assassin buildAssassin(Position pos) {
        double health = config.optDouble("assassin_health", Assassin.DEFAULT_HEALTH);
        double attack = config.optDouble("assassin_attack", Assassin.DEFAULT_ATTACK);
        int bribeAmount = config.optInt("assassin_bribe_amount", Assassin.DEFAULT_BRIBE_AMOUNT);
        int bribeRadius = config.optInt("assassin_bribe_radius", Assassin.DEFAULT_BRIBE_RADIUS);
        double failBribeProb = config.optDouble("assassin_bribe_fail_rate", Assassin.DEFAULT_BRIBE_PROBABILITY);
        return new Assassin(pos, health, attack, bribeAmount, bribeRadius, failBribeProb);
    }

    // for debugging Assassin using a seed
    public Assassin buildAssassin(Position pos, int seed) {
        double health = config.optDouble("assassin_health", Assassin.DEFAULT_HEALTH);
        double attack = config.optDouble("assassin_attack", Assassin.DEFAULT_ATTACK);
        int bribeAmount = config.optInt("assassin_bribe_amount", Assassin.DEFAULT_BRIBE_AMOUNT);
        int bribeRadius = config.optInt("assassin_bribe_radius", Assassin.DEFAULT_BRIBE_RADIUS);
        double failBribeProb = config.optDouble("assassin_bribe_fail_rate", Assassin.DEFAULT_BRIBE_PROBABILITY);
        return new Assassin(pos, health, attack, bribeAmount, bribeRadius, failBribeProb, seed);
    }

    public Bow buildBow() {
        int bowDurability = config.optInt("bow_durability");
        return new Bow(bowDurability);
    }

    public Shield buildShield() {
        int shieldDurability = config.optInt("shield_durability");
        double shieldDefence = config.optInt("shield_defence");
        return new Shield(shieldDurability, shieldDefence);
    }

    public Sceptre buildSceptre() {
        return new Sceptre();
    }

    public MidnightArmour buildMidnightArmour() {
        double aAttack = config.optInt("midnight_armour_attack");
        double aDefence = config.optInt("midnight_armour_defence");
        return new MidnightArmour(aAttack, aDefence);
    }

    private Entity constructEntity(JSONObject jsonEntity) {
        Position pos = new Position(jsonEntity.getInt("x"), jsonEntity.getInt("y"));

        switch (jsonEntity.getString("type")) {
        case "player":
            return buildPlayer(pos);
        case "zombie_toast":
            return buildZombieToast(pos);
        case "zombie_toast_spawner":
            return buildZombieToastSpawner(pos);
        case "mercenary":
            return buildMercenary(pos);
        case "assassin":
            return buildAssassin(pos);
        case "assassin_debug":
            int seed = jsonEntity.getInt("seed");
            return buildAssassin(pos, seed);
        case "wall":
            return new Wall(pos);
        case "boulder":
            return new Boulder(pos);
        case "switch":
            return new Switch(pos);
        case "exit":
            return new Exit(pos);
        case "treasure":
            return new Treasure(pos);
        case "sun_stone":
            return new SunStone(pos);
        case "wood":
            return new Wood(pos);
        case "arrow":
            return new Arrow(pos);
        case "bomb":
            int bombRadius = config.optInt("bomb_radius", Bomb.DEFAULT_RADIUS);
            return new Bomb(pos, bombRadius);
        case "invisibility_potion":
            int invisibilityPotionDuration = config.optInt(
                "invisibility_potion_duration",
                InvisibilityPotion.DEFAULT_DURATION);
            return new InvisibilityPotion(pos, invisibilityPotionDuration);
        case "invincibility_potion":
            int invincibilityPotionDuration = config.optInt("invincibility_potion_duration",
            InvincibilityPotion.DEFAULT_DURATION);
            return new InvincibilityPotion(pos, invincibilityPotionDuration);
        case "portal":
            return new Portal(pos, ColorCodedType.valueOf(jsonEntity.getString("colour")));
        case "sword":
            double swordAttack = config.optDouble("sword_attack", Sword.DEFAULT_ATTACK);
            int swordDurability = config.optInt("sword_durability", Sword.DEFAULT_DURABILITY);
            return new Sword(pos, swordAttack, swordDurability);
        case "spider":
            return buildSpider(pos);
        case "door":
            return new Door(pos, jsonEntity.getInt("key"));
        case "key":
            return new Key(pos, jsonEntity.getInt("key"));
        case "midnight_armour":
            return buildMidnightArmour();
        case "time_turner":
            return new TimeTurner(pos);
        case "time_travelling_portal":
            return new TimeTravellingPortal(pos);
        case "swamp_tile":
            int factor = jsonEntity.getInt("movement_factor");
            return new SwampTile(pos, factor);
        default:
            return null;
        }
    }
}
