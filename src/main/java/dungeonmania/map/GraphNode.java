package dungeonmania.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.SavedEntityFactory;
import dungeonmania.util.Position;

public class GraphNode {
    private Position position;
    private List<Entity> entities = new ArrayList<>();

    private int weight = 1;

    public GraphNode(Entity entity, int weight) {
        this(entity, entity.getPosition(), weight);
    }

    public GraphNode(Entity entity) {
        this(entity, entity.getPosition(), 1);
    }

    public GraphNode(Entity entity, Position p, int weight) {
        this.position = p;
        this.entities.add(entity);
        this.weight = weight;
    }

    public GraphNode(JSONObject j) {
        this.position = new Position(j.getJSONObject("position"));

        List<Entity> entities = new ArrayList<>();
        JSONArray entitiesJson = j.getJSONArray("entities");
        for (int i = 0; i < entitiesJson.length(); i++) {
            JSONObject entityJson = entitiesJson.getJSONObject(i);
            Entity entity = SavedEntityFactory.createEntity(entityJson);
            entities.add(entity);
        }
        this.entities = entities;

        this.weight = j.getInt("weight");
    }

    /**
     * Create a GraphNode, that will make player_ghost instead of player.
     * @param j a node JSONObject
     * @param moveHistory moveHistory of the real player
     */
    public GraphNode(JSONObject j, Queue<Position> moveHistory) {
        this.position = new Position(j.getJSONObject("position"));

        List<Entity> entities = new ArrayList<>();
        JSONArray entitiesJson = j.getJSONArray("entities");
        for (int i = 0; i < entitiesJson.length(); i++) {
            JSONObject entityJson = entitiesJson.getJSONObject(i);
            Entity entity;
            if (entityJson.getString("type").equals("player")) {
                entity = SavedEntityFactory.createPlayerGhost(entityJson, moveHistory);
            } else {
                entity = SavedEntityFactory.createEntity(entityJson);
            }
            entities.add(entity);
        }
        this.entities = entities;
        this.weight = j.getInt("weight");
    }

    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entities.size() == 0 || entities.stream().allMatch(e -> e.canMoveOnto(map, entity));
    }

    public int getWeight() {
        return weight;
    }

    public void addEntity(Entity entity) {
        if (!this.entities.contains(entity))
            this.entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public int size() {
        return entities.size();
    }

    public void mergeNode(GraphNode node) {
        List<Entity> es = node.entities;
        es.forEach(this::addEntity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Position getPosition() {
        return position;
    }

    public JSONObject getJSON() {
        JSONObject j = new JSONObject();
        j.put("position", this.position.getJSON());

        JSONArray entitiesJson = new JSONArray();
        this.entities.stream()
                    .forEach((Entity e) -> entitiesJson.put(e.getJSON()));
        j.put("entities", entitiesJson);
        j.put("weight", this.weight);
        return j;
    }

    //returns a Player object, or Null if none in the node.
    public Player tryGetPlayer() {
        Player p = null;
        for (Entity e: this.entities) {
            if (e instanceof Player) {
                p = (Player) e;
            }
        }

        return p;
    }
}
