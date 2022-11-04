package dungeonmania.entities.enemies.enemyMovement;

import org.json.JSONException;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.util.NameConverter;

public abstract class EnemyMovement {
    public abstract void move(Game game, Entity entity);

    // factory
    public static EnemyMovement getFromString(String type) {
        switch (type) {
        case "follow_movement":
            return new FollowMovement();
        case "hostile_movement":
            return new HostileMovement();
        case "random_movement":
            return new RandomMovement();
        default:
            throw new JSONException("no such enemy movement " + type);
        }
    }
    public String getName() {
        return  NameConverter.toSnakeCase(this.getClass().getSimpleName());
    }
}
