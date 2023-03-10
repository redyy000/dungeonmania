package dungeonmania.entities.enemies;

import java.util.Queue;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class OlderPlayer extends Enemy  {
    private Queue<Position> moveQueue;

    public OlderPlayer(Position position, double health, double attack,
                    Queue<Position> moveHistory) {
        super(position, health, attack);
        this.moveQueue = moveHistory;
    }

    public OlderPlayer(JSONObject j, Queue<Position> moveHistory) {
        super(j);
        this.moveQueue = moveHistory;
    }

    @Override
    public void move(Game game) {
        GameMap map = game.getMap();
        Position nextPos = moveQueue.peek();
        if (nextPos == null) {
            map.destroyEntity(this);
            return;
        }
        if (map.canMoveTo(this, nextPos)) {
            map.moveTo(this, nextPos);
            moveQueue.remove();
        } //otherwise don't move, keep trying this move on next ticks
        return;
    }
}
