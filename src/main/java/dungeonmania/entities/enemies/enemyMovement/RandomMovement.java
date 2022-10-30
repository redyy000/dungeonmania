package dungeonmania.entities.enemies.enemyMovement;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class RandomMovement implements EnemyMovement {

    @Override
    public void move(Game game, Entity entity) {
        // Move random
        Position nextPos;
        GameMap map = game.getMap();

        Random randGen = new Random();
        List<Position> pos = entity.getPosition().getCardinallyAdjacentPositions();
        pos = pos
                .stream()
                .filter(p -> map.canMoveTo(entity, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            nextPos = entity.getPosition();
            map.moveTo(entity, nextPos);
        } else {
            nextPos = pos.get(randGen.nextInt(pos.size()));
            map.moveTo(entity, nextPos);
        }
    }
}
