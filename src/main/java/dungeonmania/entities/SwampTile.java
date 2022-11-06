package dungeonmania.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

// Open to affect Entities, but for now we only know behaviour with Enemy's.
public class SwampTile extends Entity {
    private int factor;
    private Map<Entity, Integer> ticksLeftForEntities = new HashMap<>();
    public SwampTile(Position position, int factor) {
        super(position);
        this.factor = factor;
    }

    public void subscribeEntity(Entity entity) {
        if (!ticksLeftForEntities.containsKey(entity)) {
            ticksLeftForEntities.put(entity, factor);
            if (entity instanceof Enemy) {
                ((Enemy) entity).setSlowed(true);
            }
        }
    }

    public void onTick() {
        List<Entity> entitiesToFree = new ArrayList<>();
        for (Map.Entry<Entity, Integer> entry : this.ticksLeftForEntities.entrySet()) {
            int ticksLeft = entry.getValue() - 1;
            if (ticksLeft < 0) {
                Entity e = entry.getKey();
                entitiesToFree.add(e);
                if (e instanceof Enemy) {
                    ((Enemy) e).setSlowed(false);
                }
            } else {
                entry.setValue(ticksLeft);
            }
        }
        for (Entity e: entitiesToFree) {
            this.ticksLeftForEntities.remove(e);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            this.subscribeEntity(entity);
        }
    }

    public int getMovementFactor() {
        return this.factor;
    }
}
