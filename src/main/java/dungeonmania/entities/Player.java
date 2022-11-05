package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.TimeTurner;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;

    private Position previousPosition;
    private Position previousDistinctPosition;
    private Direction facing;
    private PlayerState state;

    public Player(Position position, double health, double attack) {
        super(position);
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        state = new PlayerState(this, false, false);
        this.previousPosition = position;
        this.previousDistinctPosition = null;
        this.facing = null;
    }

    public Player(JSONObject j) {
        super(j);
        this.battleStatistics = new BattleStatistics(j.getJSONObject("battleStatistics"));

        Inventory newInv = new Inventory();
        JSONObject invJson = j.getJSONObject("inventory");
        newInv.populateUsingJson(invJson);
        this.inventory = newInv;
        JSONArray queueJ = j.getJSONArray("queue");
        for (int i = 0; i < queueJ.length(); i++) {
            this.queue.add((Potion) SavedEntityFactory.createEntity(queueJ.getJSONObject(i)));
        }
        this.nextTrigger = j.getInt("nextTrigger");
        this.previousPosition = new Position(j.getJSONObject("previousPosition"));
        this.state = new PlayerState(this, j.getJSONObject("state"));

        // Optionals:
        this.previousDistinctPosition = null;
        this.facing = null;
        this.inEffective = null;

        if (j.has("previousDistinctPosition"))
            this.previousDistinctPosition = new Position(j.getJSONObject("previousDistinctPosition"));
        if (j.has("facing"))
            this.facing = j.getEnum(Direction.class, "facing");
        if (j.has("inEffective"))
            this.inEffective = (Potion) SavedEntityFactory.createEntity((j.getJSONObject("inEffective")));

    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public boolean hasSceptre() {
        return inventory.hasSceptre();
    }
    public BattleItem getWeapon() {
        return inventory.getWeapon();
    }

    public void useWeapon(Game game) {
        getWeapon().use(game);
    }

    public List<String> getBuildables(GameMap map) {
        return inventory.getBuildables(map);
    }

    public boolean build(String entity, EntityFactory factory, GameMap map) {
        InventoryItem item = inventory.tryBuildItem(this, true, entity, factory, map);
        if (item == null) return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied()) return;
            }
            map.getGame().battle(this, (Enemy) entity);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null) inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            state.transitionBase();
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        } else {
            state.transitionInvisible();
        }
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        if (state.isInvincible()) {
            return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                0,
                0,
                1,
                1,
                true,
                true));
        } else if (state.isInvisible()) {
            return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                0,
                0,
                1,
                1,
                false,
                false));
        }
        return origin;
    }
    @Override
    public double getHealth() {
        return getBattleStatistics().getHealth();
    }

    public boolean isCardinallyAdjacentToOrEqual(Position pos) {
        if (this.getPosition().getCardinallyAdjacentPositions().contains(pos)
           || this.getPosition().equals(pos)) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject getJSON() {
        JSONArray queueJ = new JSONArray();
        for (Potion p: this.queue) {
            queueJ.put(p.getJSON());
        }


        JSONObject j = super.getJSON();
        j.put("battleStatistics", this.battleStatistics.getJSON())
            .put("inventory", this.inventory.getJSON())
            .put("state", this.state.getJSON())
            .put("nextTrigger", this.nextTrigger)
            .put("previousPosition", this.previousPosition.getJSON())
            .put("queue", queueJ); // careful of queue into array.
        if (this.previousDistinctPosition != null)
            j.put("previousDistinctPosition", this.previousDistinctPosition.getJSON());
        if (this.facing != null) j.put("facing", this.facing);
        if (this.inEffective != null) j.put("inEffective", this.inEffective.getJSON());

        return j;
    }

    @Override
    public void setPosition(Position position) {
        previousPosition = this.getPosition();
        super.setPosition(position);
        if (!previousPosition.equals(this.getPosition())) {
            previousDistinctPosition = previousPosition;
        }
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public Position getPreviousPosition() {
        return previousPosition;
    }

    public Position getPreviousDistinctPosition() {
        return previousDistinctPosition;
    }

    public String getTimeTurnerID() {
        TimeTurner t = inventory.getFirst(TimeTurner.class);
        if (t == null) {
            return null;
        }
        return t.getId();
    }
}
