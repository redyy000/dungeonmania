package dungeonmania.entities.enemies.enemyMovement;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.util.Position;

public class SpiderMovement { //implements EnemyMovement

    // @Override
    // public void move(Game game, Entity entity) {
    //     if (!(entity instanceof Spider)) {
    //         return;
    //     }
    //     Spider spider = (Spider) entity;

    //     List<Position> movementTrajectory = spider.getMovementTrajectory();
    //     int nextPositionElement = spider.getNextPositionElement();
    //     Position nextPos = movementTrajectory.get(nextPositionElement);
        
    //     List<Entity> entities = game.getMap().getEntities(nextPos);
    //     if (entities != null && entities.size() > 0 && entities.stream().anyMatch(e -> e instanceof Boulder)) { // could use canmoveonto?
    //         spider.setForward(!spider.isForward());
    //         updateNextPosition(spider);
    //         updateNextPosition(spider);
    //     }
    //     nextPos = movementTrajectory.get(nextPositionElement);
    //     entities = game.getMap().getEntities(nextPos);
    //     if (entities == null
    //             || entities.size() == 0
    //             || entities.stream().allMatch(e -> e.canMoveOnto(game.getMap(), entity))) {
    //         game.getMap().moveTo(entity, nextPos);
    //         updateNextPosition(spider);
    //     }        
    // }

    // private void updateNextPosition(Spider spider) {
    //     if (spider.isForward()) {
    //         spider.setNextPositionElement(spider.getNextPositionElement() + 1);
    //         if (spider.getNextPositionElement()  == 8) {
    //             spider.setNextPositionElement(0);
    //         }
    //     } else {
    //         spider.setNextPositionElement(spider.getNextPositionElement() - 1);
    //         if (spider.getNextPositionElement() == -1) {
    //             spider.setNextPositionElement(7);
    //         }
    //     }
    // }

    
}
