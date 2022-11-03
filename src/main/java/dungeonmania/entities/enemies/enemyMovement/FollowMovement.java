package dungeonmania.entities.enemies.enemyMovement;


import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class FollowMovement extends EnemyMovement {

    @Override
    public void move(Game game, Entity entity) {
        GameMap map = game.getMap();
        Player player = game.getPlayer();
        Position nextPos = player.getPreviousDistinctPosition();
        map.moveTo(entity, nextPos);
    }
}
