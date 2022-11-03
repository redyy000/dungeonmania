package dungeonmania.entities.enemies;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.enemies.enemyMovement.EnemyMovement;
import dungeonmania.entities.enemies.enemyMovement.FollowMovement;
import dungeonmania.entities.enemies.enemyMovement.HostileMovement;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Assassin extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 10.0;
    public static final double DEFAULT_HEALTH = 10.0;
    public static final double DEFAULT_BRIBE_PROBABILITY = 0.3;

    private int bribeAmount = Assassin.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Assassin.DEFAULT_BRIBE_RADIUS;
    private double failBribeProb = Assassin.DEFAULT_BRIBE_PROBABILITY;
    private boolean allied = false;
    private Random rng;

    public Assassin(Position position, double health, double attack,
                    int bribeAmount, int bribeRadius, double failBribeProb) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.failBribeProb = failBribeProb;
        this.rng = new Random();
    }
    public Assassin(JSONObject j) {
        super(j);
        //TODO. DebugAssassin should like search for seed in setting the json.
    }

    // Assassin for debugging bribe odds.
    public Assassin(Position position, double health, double attack,
    int bribeAmount, int bribeRadius, double failBribeProb, int seed) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.failBribeProb = failBribeProb;
        this.rng = new Random(seed);
    }


    public boolean isAllied() {
        return allied;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied) return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        return bribeRadius >= 0 && player.countEntityOfType(Treasure.class) >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    private boolean bribe(Player player) {
        // use things
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }
        // maybe it will allow allying.
        int d = rng.nextInt(100);
        if (d < failBribeProb * 100) {
            return false;
        }
        return true;
    }

    @Override
    public void interact(Player player, Game game) {
        allied = bribe(player);
    }

    @Override
    public void move(Game game) {
        EnemyMovement moveStrategy;
        if (allied && game.getPlayer().isCardinallyAdjacentToOrEqual(this.getPosition())) {
            moveStrategy = new FollowMovement();
        } else {
            moveStrategy = new HostileMovement();
        }
        moveStrategy.move(game, this);
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && canBeBribed(player);
    }
}
