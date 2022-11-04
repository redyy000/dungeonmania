package dungeonmania.entities.enemies.enemyMovement;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class HostileMovement extends EnemyMovement {

    @Override
    public void move(Game game, Entity entity) {
        GameMap map = game.getMap();
        Position nextPos = map.dijkstraPathFind(entity.getPosition(), map.getPlayer().getPosition(), entity);
        map.moveTo(entity, nextPos);
    }
}
