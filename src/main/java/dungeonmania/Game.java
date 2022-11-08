package dungeonmania;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.SwampTile;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.logical.LogicalEntity;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.goals.Goal;
import dungeonmania.map.GameMap;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game {
    private String id;
    private String name;
    private Goal goals;
    private GameMap map;
    private Player player;
    private BattleFacade battleFacade;
    private EntityFactory entityFactory;
    private boolean isInTick = false;
    private int killedEnemies = 0;
    private int nCollectedTreasure = 0;
    public static final int PLAYER_MOVEMENT = 0;
    public static final int PLAYER_MOVEMENT_CALLBACK = 1;
    public static final int BEFORE_AI_MOVE = 2;
    public static final int AI_MOVEMENT = 3;
    public static final int AI_MOVEMENT_CALLBACK = 4;

    private int tickCount = 0;
    private PriorityQueue<ComparableCallback> sub = new PriorityQueue<>();
    private PriorityQueue<ComparableCallback> addingSub = new PriorityQueue<>();

    private Stack<JSONObject> gameStates = new Stack<>(); //earliest states accessed last.
    private Stack<Position> moveHistory = new Stack<>(); //the last moves are the ones the ghost takes

    public Game(String dungeonName) {
        this.name = dungeonName;
        //this.map = new GameMap(); //uesless, gameMap set by builder.
        this.battleFacade = new BattleFacade();
    }

    public Game(JSONObject j) {
        this.id = j.getString("id");
        this.name = j.getString("name");
        this.battleFacade = new BattleFacade();
        this.isInTick = j.getBoolean("isInTick");
        this.killedEnemies = j.getInt("killedEnemies");
        this.nCollectedTreasure = j.getInt("nCollectedTreasure");
        this.tickCount = j.getInt("tickCount");
    }

    public void init() {
        this.id = UUID.randomUUID().toString();
        // map.init();
        this.tickCount = 0;
        player = map.getPlayer();

        register(() -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
        register(() -> saveGameState(), AI_MOVEMENT_CALLBACK, "saveGameState");
        saveGameState();
        List<Mercenary> mercs = map.getEntities(Mercenary.class);
        List<SwampTile> swampTiles = map.getEntities(SwampTile.class);
        List<LogicalEntity> logicalEntities = map.getEntities(LogicalEntity.class);

        for (Mercenary m : mercs) {
            register(() -> m.onTick(tickCount), BEFORE_AI_MOVE, "mindControlTimer" + m.getId());
        }
        for (SwampTile s : swampTiles) {
            register(() -> s.onTick(), BEFORE_AI_MOVE, "swampTilesSlow " + s.getId());
        }
        for (LogicalEntity l : logicalEntities) {
            register(() -> l.resetNotifsCount(), AI_MOVEMENT_CALLBACK,
                    "reset count notifs for tick on LogicEnttiy " + l.getId());
        }
    }

    public void initSavedGame() {
        player = map.getPlayer();
        register(() -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
        List<Mercenary> mercs = map.getEntities(Mercenary.class);
        for (Mercenary m : mercs) {
            register(() -> m.onTick(tickCount), AI_MOVEMENT, "mindControlTimer" + m.getId());
        }
        List<SwampTile> swampTiles = map.getEntities(SwampTile.class);
        for (SwampTile s : swampTiles) {
            register(() -> s.onTick(), AI_MOVEMENT, "swampTilesSlow " + s.getId());
        }
    }

    public Game tick(Direction movementDirection) {
        registerOnce(
            () -> player.move(this.getMap(), movementDirection), PLAYER_MOVEMENT, "playerMoves");
        tick();
        return this;
    }

    public Game tick(String itemUsedId) throws InvalidActionException {
        Entity item = player.getEntity(itemUsedId);
        if (item == null)
            throw new InvalidActionException(String.format("Item with id %s doesn't exist", itemUsedId));
        if (!(item instanceof Bomb) && !(item instanceof Potion))
            throw new IllegalArgumentException(String.format("%s cannot be used", item.getClass()));

        registerOnce(() -> {
            if (item instanceof Bomb)
                player.use((Bomb) item, map);
            if (item instanceof Potion)
                player.use((Potion) item, tickCount);
        }, PLAYER_MOVEMENT, "playerUsesItem");
        tick();
        return this;
    }

    /**
     * Battle between player and enemy
     * @param player
     * @param enemy
     */
    public void battle(Player player, Enemy enemy) {
        battleFacade.battle(this, player, enemy);
        if (player.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(enemy);
            this.killedEnemies++;
        }
    }

    public Game build(String buildable) throws InvalidActionException {
        List<String> buildables = player.getBuildables(this.map);
        if (!buildables.contains(buildable)) {
            throw new InvalidActionException(String.format("%s cannot be built", buildable));
        }
        registerOnce(() -> player.build(buildable, entityFactory, this.map), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public Game interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity e = map.getEntity(entityId);
        if (e == null || !(e instanceof Interactable))
            throw new IllegalArgumentException("Entity cannot be interacted");
        if (!((Interactable) e).isInteractable(player)) {
            throw new InvalidActionException("Entity cannot be interacted");
        }
        registerOnce(
            () -> ((Interactable) e).interact(player, this), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public <T extends Entity> long countEntities(Class<T> type) {
        return map.countEntities(type);
    }

    public void register(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id));
        else
            sub.add(new ComparableCallback(r, priority, id));
    }

    public void registerOnce(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id, true));
        else
            sub.add(new ComparableCallback(r, priority, id, true));
    }

    public void unsubscribe(String id) {
        for (ComparableCallback c : sub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
        for (ComparableCallback c : addingSub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
    }

    public int tick() {
        isInTick = true;
        sub.forEach(s -> s.run());
        isInTick = false;
        sub.addAll(addingSub);
        addingSub = new PriorityQueue<>();
        sub.removeIf(s -> !s.isValid());
        tickCount++;
        // update the weapons/potions duration
        return tickCount;
    }

    public int getTick() {
        return this.tickCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Goal getGoals() {
        return goals;
    }

    public void setGoals(Goal goals) {
        this.goals = goals;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory factory) {
        entityFactory = factory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BattleFacade getBattleFacade() {
        return battleFacade;
    }

    public void setBattleFacade(BattleFacade battleFacade) {
        this.battleFacade = battleFacade;
    }

    public int getNCollectedTreasure() {
        return this.nCollectedTreasure;
    }
    public int getKilledEnemies() {
        return this.killedEnemies;
    }
    public int numSpawners() {
        return this.map.getEntities(ZombieToastSpawner.class).size();
    }
    public void increaseNCollectedTreasure() {
        this.nCollectedTreasure = this.nCollectedTreasure + 1;
    }

    public JSONObject getJSON() {
        JSONObject j = new JSONObject();
        j.put("id", this.id);
        j.put("name", this.name);
        j.put("isInTick", this.isInTick);
        j.put("killedEnemies", this.killedEnemies);
        j.put("nCollectedTreasure", this.nCollectedTreasure);
        j.put("tickCount", this.tickCount);
        return j;
    }

    public void saveGameState() {
        // similar to DMC SaveGame(); Don't worry about config though.
        JSONObject newStateJson = new JSONObject();
        newStateJson.put("gameMap", this.getMap().getJSON());
        moveHistory.add(player.getPosition());
        this.gameStates.push(newStateJson);
    }

    public Game rewind(int ticks) throws InvalidActionException {
        if (!canRewind()) {
            throw new InvalidActionException("no time turner on player");
        }
        registerOnce(() -> revertToState(ticks), PLAYER_MOVEMENT, "rewind " + ticks + " ticks");
        tick();
        return this;
    }
    public Game registerRewind(int ticks) {
        registerOnce(() -> revertToState(ticks), PLAYER_MOVEMENT, "rewind " + ticks + " ticks");
        return this;
    }
    private boolean canRewind() {
        return player.getTimeTurnerID() != null;
    }
    public void revertToState(int ticks) {
        JSONObject state = this.gameStates.pop(); // if 0, get first.
        int nStates = gameStates.size();
        for (int stateI = 0; stateI < Math.min(ticks, nStates); stateI++) {
            state = this.gameStates.pop();
        }
        // remove all old entities callbacks, ie no old entities should move on the new game
        for (Entity e: map.getEntities()) {
            unsubscribe(e.getId());
        }
        Queue<Position> ghostMoves = getGhostMoves(ticks);
        this.setMap(new GameMap(this.player, this, state.getJSONArray("gameMap"), ghostMoves));
    }

    private Queue<Position> getGhostMoves(int ticks) {
        Stack<Position> moves = new Stack<>();
        int nMovesHistory = moveHistory.size();
        for (int i = 0; i < Math.min(ticks, nMovesHistory); i++) {
            moves.push(moveHistory.pop());
        }
        // added in wrong order. Reverse.
        Queue<Position> movesInOrder = new LinkedList<>();
        int n = moves.size();
        for (int i = 0; i < n; i++) {
            movesInOrder.add(moves.pop());
        }
        return movesInOrder;
    }

    public DungeonResponse getDungeonResponse() {
        List<EntityResponse> entityResponse = new ArrayList<>();
        getMap().getEntities().forEach(e -> {
            entityResponse.add(ResponseBuilder.getEntityResponse(this, e));
        });
        return new DungeonResponse(
                getId(),
                getName(),
                entityResponse,
                (getPlayer() != null) ? ResponseBuilder.getInventoryResponse(getPlayer().getInventory()) : null,
                getBattleFacade().getBattleResponses(),
                (getPlayer() != null) ? getPlayer().getBuildables(getMap()) : null,
                (getGoals().achieved(this)) ? ""
                        : getGoals().toString(this));
    }
}
