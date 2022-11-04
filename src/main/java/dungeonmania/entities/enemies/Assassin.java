package dungeonmania.entities.enemies;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.enemies.enemyMovement.EnemyMovement;
import dungeonmania.entities.enemies.enemyMovement.FollowMovement;
import dungeonmania.entities.enemies.enemyMovement.HostileMovement;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 10.0;
    public static final double DEFAULT_HEALTH = 10.0;
    public static final double DEFAULT_BRIBE_PROBABILITY = 0.3;

    private double failBribeProb = Assassin.DEFAULT_BRIBE_PROBABILITY;
    private Random rng;
    private Integer seed = null;

    public Assassin(Position position, double health, double attack,
                    int bribeAmount, int bribeRadius, double failBribeProb) {
        super(position, health, attack, bribeAmount, bribeRadius);
        this.failBribeProb = failBribeProb;
        this.rng = new Random();
    }
    public Assassin(JSONObject j) {
        super(j);
        this.failBribeProb = j.getDouble("failBribeProb");

        if (j.has("seed")) {
            this.seed = j.getInt("seed");
            this.rng = new Random(this.seed);
        } else {
            this.rng = new Random();
        }
    }

    // Assassin for debugging bribe odds.
    public Assassin(Position position, double health, double attack,
    int bribeAmount, int bribeRadius, double failBribeProb, int seed) {
        super(position, health, attack, bribeAmount, bribeRadius);
        this.failBribeProb = failBribeProb;
        this.seed = seed;
        this.rng = new Random(this.seed);
    }

    /**
     * Give money to the merc. See if it allows allying (return boolean).
     */
    private boolean bribe(Player player) {
        // use things
        for (int i = 0; i < this.getBribeAmount(); i++) {
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
        setAllied(bribe(player));
    }

    @Override
    public void move(Game game) {
        EnemyMovement moveStrategy;
        if (this.isAllied() && game.getPlayer().isCardinallyAdjacentToOrEqual(this.getPosition())) {
            moveStrategy = new FollowMovement();
        } else {
            moveStrategy = new HostileMovement();
        }
        moveStrategy.move(game, this);
    }

    @Override
    public boolean isInteractable(Player player) {
        return !this.isAllied() && this.canBeBribed(player);
    }

    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("failBribeProb", this.failBribeProb);
        if (this.seed != null) {
            j.put("seed", (int) this.seed);
        }
        return j;
    }
}
