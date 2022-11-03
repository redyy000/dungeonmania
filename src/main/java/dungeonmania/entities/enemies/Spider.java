package dungeonmania.entities.enemies;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;

import dungeonmania.util.Position;


public class Spider extends Enemy {

    private List<Position> movementTrajectory;
    private int nextPositionElement;
    private boolean forward;

    public static final int DEFAULT_SPAWN_RATE = 0;
    public static final double DEFAULT_ATTACK = 5;
    public static final double DEFAULT_HEALTH = 10;

    public Spider(Position position, double health, double attack) {
        super(position.asLayer(Entity.DOOR_LAYER + 1), health, attack);
        /**
         * Establish spider movement trajectory Spider moves as follows:
         *  8 1 2       10/12  1/9  2/8
         *  7 S 3       11     S    3/7
         *  6 5 4       B      5    4/6
         */
        movementTrajectory = position.getAdjacentPositions();
        nextPositionElement = 1;
        forward = true;
    };

    public Spider(JSONObject j) {
        super(j);
        this.movementTrajectory = new ArrayList<Position>();
        JSONArray trajectoriesJ = j.getJSONArray("movementTrajectory");
        for (int i = 0; i < trajectoriesJ.length(); i++) {
            this.movementTrajectory.add(new Position(trajectoriesJ.getJSONObject(i)));
        }
        this.forward = j.getBoolean("forward");
        this.nextPositionElement = j.getInt("nextPositionElement");

    }

    private void updateNextPosition() {
        if (forward) {
            nextPositionElement++;
            if (nextPositionElement == 8) {
                nextPositionElement = 0;
            }
        } else {
            nextPositionElement--;
            if (nextPositionElement == -1) {
                nextPositionElement = 7;
            }
        }
    }

    @Override
    public void move(Game game) {
        // EnemyMovement movementStrategy = new SpiderMovement();
        // movementStrategy.move(game, this);
        Position nextPos = movementTrajectory.get(nextPositionElement);
        List<Entity> entities = game.getMap().getEntities(nextPos);
        if (entities != null && entities.size() > 0 && entities.stream().anyMatch(e -> e instanceof Boulder)) {
            forward = !forward;
            updateNextPosition();
            updateNextPosition();
        }
        nextPos = movementTrajectory.get(nextPositionElement);
        entities = game.getMap().getEntities(nextPos);
        if (entities == null
                || entities.size() == 0
                || entities.stream().allMatch(e -> e.canMoveOnto(game.getMap(), this))) {
            game.getMap().moveTo(this, nextPos);
            updateNextPosition();
        }
    }

    public List<Position> getMovementTrajectory() {
        return movementTrajectory;
    }

    public int getNextPositionElement() {
        return nextPositionElement;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void setNextPositionElement(int nextPositionElement) {
        this.nextPositionElement = nextPositionElement;
    }

    public JSONObject getJSON() {
        JSONObject j = new JSONObject();

        JSONArray movementsJsons = new JSONArray();
        this.movementTrajectory.stream()
                            .forEach((Position p) -> movementsJsons.put(p.getJSON()));
        j.put("movementTrajectory", movementsJsons);
        j.put("forward", this.forward);
        j.put("nextPositionElement", this.nextPositionElement);
        return j;
    }
}
