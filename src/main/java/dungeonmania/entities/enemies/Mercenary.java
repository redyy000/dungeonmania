package dungeonmania.entities.enemies;

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

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;
    private boolean allied = false;
    private boolean controlled = false;
    private EnemyMovement movementStrategy = new HostileMovement();
    private int ticksUntilUnmindcontrolled = 0;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
    }
    public Mercenary(JSONObject j) {
        super(j);
        this.bribeAmount = j.getInt("bribeAmount");
        this.bribeRadius = j.getInt("bribeRadius");
        this.allied = j.getBoolean("allied");
        this.movementStrategy = EnemyMovement.getFromString(j.getString("movementStrategy"));
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
    protected boolean canBeBribed(Player player) {
        // TODO bribeRadius more than 0??
        int nCoins = player.countEntityOfType(Treasure.class);
        return bribeRadius >= 0 && nCoins >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    private void bribe(Player player) {
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }
    }

    @Override
    public void interact(Player player, Game game) {
        allied = true;
        if (canBeBribed(player)) {
            bribe(player);
        } else if (player.hasSceptre()) {
            controlled = true;
            this.ticksUntilUnmindcontrolled = 3;
        }
    }

    @Override
    public void move(Game game) {
        if (!(allied && game.getPlayer().isCardinallyAdjacentToOrEqual(this.getPosition()))
            && isSlowed()) {
            return;
        }
        if (allied && game.getPlayer().isCardinallyAdjacentToOrEqual(this.getPosition())
            && !(this.movementStrategy instanceof FollowMovement)) {
            movementStrategy = new FollowMovement();
        }
        this.movementStrategy.move(game, this);
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && (canBeBribed(player) || player.hasSceptre());
    }

    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("bribeAmount", this.bribeAmount);
        j.put("bribeRadius", this.bribeRadius);
        j.put("allied", this.allied);
        j.put("movementStrategy", this.movementStrategy.getName());
        return j;
    }

    protected int getBribeAmount() {
        return this.bribeAmount;
    }
    protected void setAllied(boolean b) {
        this.allied = b;
    }

    //mind control:
    public void onTick(int tick) {
        if (ticksUntilUnmindcontrolled == 0 && controlled) {
            this.allied = false;
            this.controlled = false;
        }
        if (ticksUntilUnmindcontrolled > 0) {
            ticksUntilUnmindcontrolled -= 1;
        }
    }

    protected void setTickUntilUnmindcontrolled(int ticks) {
        this.ticksUntilUnmindcontrolled = ticks;
    }
    protected void setControlled(boolean b) {
        this.controlled = b;
    }
}
