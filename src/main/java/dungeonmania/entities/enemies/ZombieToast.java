package dungeonmania.entities.enemies;


import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.enemies.enemyMovement.EnemyMovement;
import dungeonmania.entities.enemies.enemyMovement.RandomMovement;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;

    private EnemyMovement moveStrategy = new RandomMovement();

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack);
    }

    public ZombieToast(JSONObject j) {
        super(j);
        this.moveStrategy = EnemyMovement.getFromString(j.getString("movementStrategy"));
    }

    @Override
    public void move(Game game) {
        this.moveStrategy.move(game, this);
    }

    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("movementStrategy", this.moveStrategy.getName());
        return j;
    }
}
