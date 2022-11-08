package dungeonmania.entities.logical;

import java.util.List;


import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class LogicalEntity extends Entity {

    private boolean activated;
    private String logic;
    private List<Conductor> nearbyConductors;
    private int nearbyActivated = 0;

    public LogicalEntity(Position position, boolean activated, String logic) {
        super(position);
        this.activated = activated;
        this.logic = logic;
    }

    // public void checkOnTick(GameMap map) {
    //     List<Entity> nearbyEntities = new ArrayList<>();
    //     this.getPosition()
    //         .getCardinallyAdjacentPositions()
    //         .stream()
    //         .forEach(position -> nearbyEntities.addAll(map.getEntities(position)));

    //     List<Conductor> nearbySwitches = nearbyEntities.stream()
    //                     .filter(entity -> entity instanceof Conductor)
    //                     .map(entity -> (Conductor) entity)
    //                     .collect(Collectors.toList());
    //     int countActivatedNearby = 0;
    //     for (Conductor s: nearbySwitches) {
    //         if (s.isActivated()) countActivatedNearby++;
    //     }
    //     actOnTick(countActivatedNearby);
    // }

    // private void actOnTick(int nearbyActivated) {
    //     if (this.logic.equals("or") && nearbyActivated >= 1) {
    //         this.activated = true;
    //     }

    // }

    public void subscribe(Conductor c) {
        this.nearbyConductors.add(c);
    }

    public void notify(GameMap map) {
        nearbyActivated++;
        if (this.logic.equals("or") && nearbyActivated >= 1) {
            this.activated = true;
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
    }

    public boolean isActivated() {
        return activated;
    }
    public String getLogic() {
        return this.logic;
    }
}
