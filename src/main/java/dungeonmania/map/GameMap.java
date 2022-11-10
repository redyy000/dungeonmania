package dungeonmania.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.Portal;
import dungeonmania.entities.SwampTile;
import dungeonmania.entities.Switch;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.logical.Conductor;
import dungeonmania.entities.logical.SwitchObserver;
import dungeonmania.entities.logical.Wire;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;


public class GameMap {
    private Game game;
    private Map<Position, GraphNode> nodes = new HashMap<>();
    private Player player;

    /**
     * Initialise the game map
     * 1. pair up portals
     */

    public GameMap(Game game, JSONObject dungeon) {
        this.game = game;
        dungeon.getJSONArray("entities").forEach(e -> {
            JSONObject jsonEntity = (JSONObject) e;
            GraphNode newNode = GraphNodeFactory.createEntity(jsonEntity, game.getEntityFactory());
            Entity entity = newNode.getEntities().get(0);

            if (newNode != null)
                addNode(newNode);

            if (entity instanceof Player)
                setPlayer((Player) entity);
        });
        init(); // added by me
    }

    public GameMap(Game game, JSONArray gameMapJson) {
        /*
         * {[{position, node}, {position, node}]"}
         */
        this.game = game;
        for (int i = 0; i < gameMapJson.length(); i++) {
            JSONObject nodeJson = gameMapJson.getJSONObject(i);
            Position nodePosition = new Position(nodeJson.getJSONObject("position"));
            GraphNode nodeNode = new GraphNode(nodeJson.getJSONObject("node"));
            if (nodeNode.tryGetPlayer() != null) {
                //set player when found
                this.player = nodeNode.tryGetPlayer();
            }
            this.nodes.put(nodePosition, nodeNode);
        }
        init();
    }

    /**
     * Create the game map after rewinding. Creates a player ghost.
     * @param game
     * @param gameMapJson
     * @param moveHistory
     */
    public GameMap(Player realPlayer, Game game, JSONArray gameMapJson, Queue<Position> moveHistory) {
        /*
         * {[{position, node}, {position, node}]"}
         */
        this.game = game;
        this.player = realPlayer;
        GraphNode playerNode = new GraphNode(realPlayer);
        this.nodes.put(playerNode.getPosition(), playerNode);

        for (int i = 0; i < gameMapJson.length(); i++) {
            JSONObject nodeJson = gameMapJson.getJSONObject(i);
            Position nodePosition = new Position(nodeJson.getJSONObject("position"));
            GraphNode nodeNode = new GraphNode(nodeJson.getJSONObject("node"), moveHistory);
            this.nodes.put(nodePosition, nodeNode);
        }
        init();
    }

    public void init() {
        initPairPortals();
        initRegisterMovables();
        initRegisterSpawners();
        // initRegisterBombsAndSwitches();
        initRegisterObserversAndConductors();
    }

    private void initRegisterBombsAndSwitches() {
        List<Bomb> bombs = getEntities(Bomb.class);
        List<Switch> switchs = getEntities(Switch.class);
        for (Bomb b: bombs) {
            for (Switch s: switchs) {
                if (Position.isAdjacent(b.getPosition(), s.getPosition())) {
                    b.subscribe(s);
                    s.subscribe(b);
                }
            }
        }
    }

    private void initRegisterObserversAndConductors() {
        List<Entity> obss = getEntities().stream()
                            .filter(e -> e instanceof SwitchObserver)
                            .collect(Collectors.toList());
        List<Entity> conductors = getEntities().stream()
                            .filter(e -> e instanceof Conductor)
                            .map(e -> (Conductor) e)
                            .collect(Collectors.toList());
        for (Entity o: obss) {
            for (Entity c: conductors) {
                if (Position.isAdjacent(o.getPosition(), c.getPosition())
                && !(c instanceof Wire && o instanceof Bomb)) {
                    SwitchObserver observer = (SwitchObserver) o;
                    Conductor conductor = (Conductor) c;
                    observer.subscribe(conductor);
                    conductor.subscribe(observer);
                }
            }
        }
    }

    // Pair up portals if there's any
    private void initPairPortals() {
        Map<String, Portal> portalsMap = new HashMap<>();
        nodes.forEach((k, v) -> {
            v.getEntities()
                    .stream()
                    .filter(Portal.class::isInstance)
                    .map(Portal.class::cast)
                    .forEach(portal -> {
                        String color = portal.getColor();
                        if (portalsMap.containsKey(color)) {
                            portal.bind(portalsMap.get(color));
                        } else {
                            portalsMap.put(color, portal);
                        }
                    });
        });
    }

    private void initRegisterMovables() {
        List<Enemy> enemies = getEntities(Enemy.class);
        enemies.forEach(e -> {
            game.register(() -> e.move(game), Game.AI_MOVEMENT, e.getId());
        });
    }

    private void initRegisterSpawners() {
        List<ZombieToastSpawner> zts = getEntities(ZombieToastSpawner.class);
        zts.forEach(e -> {
            game.register(() -> e.spawn(game), Game.AI_MOVEMENT, e.getId());
        });
        game.register(() -> game.getEntityFactory().spawnSpider(game), Game.AI_MOVEMENT, "spiderSpawner");
    }

    public void moveTo(Entity entity, Position position) {
        if (!canMoveTo(entity, position)) return;

        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(position);
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    public void moveTo(Entity entity, Direction direction) {
        if (!canMoveTo(entity, Position.translateBy(entity.getPosition(), direction))) return;
        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(Position.translateBy(entity.getPosition(), direction));
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    private void triggerMovingAwayEvent(Entity entity) {
        List<Runnable> callbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity)
            callbacks.add(() -> e.onMovedAway(this, entity));
        });
        callbacks.forEach(callback -> {
            callback.run();
        });
    }

    private void triggerOverlapEvent(Entity entity) {
        List<Runnable> overlapCallbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity || e instanceof SwampTile)
            overlapCallbacks.add(() -> e.onOverlap(this, entity));
        });
        overlapCallbacks.forEach(callback -> {
            callback.run();
        });
    }

    public boolean canMoveTo(Entity entity, Position position) {
        return !nodes.containsKey(position) || nodes.get(position).canMoveOnto(this, entity);
    }


    public Position dijkstraPathFind(Position src, Position dest, Entity entity) {
        // if inputs are invalid, don't move
        if (!nodes.containsKey(src) || !nodes.containsKey(dest))
        return src;

        Map<Position, Integer> dist = new HashMap<>();
        Map<Position, Position> prev = new HashMap<>();
        Map<Position, Boolean> visited = new HashMap<>();

        prev.put(src, null);
        dist.put(src, 0);

        PriorityQueue<Position> q = new PriorityQueue<>((x, y) ->
            Integer.compare(dist.getOrDefault(x, Integer.MAX_VALUE), dist.getOrDefault(y, Integer.MAX_VALUE)));
        q.add(src);

        while (!q.isEmpty()) {
            Position curr = q.poll();
            if (curr.equals(dest) || dist.get(curr) > 200) break;
            // check portal
            if (nodes.containsKey(curr) && nodes.get(curr).getEntities().stream().anyMatch(Portal.class::isInstance)) {
                Portal portal = nodes.get(curr).getEntities()
                    .stream()
                    .filter(Portal.class::isInstance).map(Portal.class::cast)
                    .collect(Collectors.toList())
                    .get(0);
                List<Position> teleportDest = portal.getDestPositions(this, entity);
                teleportDest.stream()
                .filter(p -> !visited.containsKey(p))
                .forEach(p -> {
                    dist.put(p, dist.get(curr));
                    prev.put(p, prev.get(curr));
                    q.add(p);
                });
                continue;
            }
            visited.put(curr, true);
            List<Position> neighbours = curr.getCardinallyAdjacentPositions()
            .stream()
            .filter(p -> !visited.containsKey(p))
            .filter(p -> !nodes.containsKey(p) || nodes.get(p).canMoveOnto(this, entity))
            .collect(Collectors.toList());

            neighbours.forEach(n -> {
                int newDist = dist.get(curr) + (nodes.containsKey(n) ? nodes.get(n).getWeight() : 1);
                if (newDist < dist.getOrDefault(n, Integer.MAX_VALUE)) {
                    q.remove(n);
                    dist.put(n, newDist);
                    prev.put(n, curr);
                    q.add(n);
                }
            });
        }
        Position ret = dest;
        if (prev.get(ret) == null || ret.equals(src)) return src;
        while (!prev.get(ret).equals(src)) {
            ret = prev.get(ret);
        }
        return ret;
    }

    public void removeNode(Entity entity) {
        Position p = entity.getPosition();
        if (nodes.containsKey(p)) {
            nodes.get(p).removeEntity(entity);
            if (nodes.get(p).size() == 0) {
                nodes.remove(p);
            }
        }
    }

    public void destroyEntity(Entity entity) {
        removeNode(entity);
        entity.onDestroy(this);
    }

    public void addEntity(Entity entity) {
        addNode(new GraphNode(entity));
    }

    public void addNode(GraphNode node) {
        Position p = node.getPosition();

        if (!nodes.containsKey(p))
        nodes.put(p, node);
        else {
            GraphNode curr = nodes.get(p);
            curr.mergeNode(node);
            nodes.put(p, curr);
        }
    }

    public Entity getEntity(String id) {
        Entity res = null;
        for (Map.Entry<Position, GraphNode> entry : nodes.entrySet()) {
            List<Entity> es = entry.getValue().getEntities()
            .stream()
            .filter(e -> e.getId().equals(id))
            .collect(Collectors.toList());
            if (es != null && es.size() > 0) {
                res = es.get(0);
                break;
            }
        }
        return res;
    }

    public List<Entity> getEntities(Position p) {
        GraphNode node = nodes.get(p);
        return (node != null) ? node.getEntities() : new ArrayList<>();
    }

    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        nodes.forEach((k, v) -> entities.addAll(v.getEntities()));
        return entities;
    }

    public <T extends Entity> List<T> getEntities(Class<T> type) {
        return getEntities().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public <T extends Entity> long countEntities(Class<T> type) {
        return getEntities().stream().filter(type::isInstance).count();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


    public JSONArray getJSON() {
        //NOTe that gameMap returns a JSONARRAY rather than Object.
        /*
         * [{position, node}, {position, node}]
         */
        // create an array of position and graphNode, to mirror a Map.
        JSONArray mapJson = new JSONArray();
        for (Position key: this.nodes.keySet()) {
            JSONObject nodeJson = new JSONObject();
            nodeJson.put("position", key.getJSON());
            nodeJson.put("node", this.nodes.get(key).getJSON());
            mapJson.put(nodeJson);
        }
        return mapJson;
    }
}
