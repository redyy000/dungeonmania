package dungeonmania.entities.logical;


import java.util.ArrayList;
import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wire extends Conductor implements SwitchObserver {

    private List<Conductor> subscriptions = new ArrayList<>();

    public Wire(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    /**
     * keep track of who you are watching
     */
    @Override
    public void subscribe(Conductor s) {
        this.subscriptions.add(s);
    }


    @Override
    public void notify(GameMap map, boolean activated) {
        if (activated == isActivated()) {
            return; //if same signal as currently, do nothing.
        }
        if (!activated && nearbyActivated() == 0) {
            setActivated(false);
            push(map, false);
            return;
        }
        setActivated(activated);
        push(map, activated);
    }

    private int nearbyActivated() {
        return (int) subscriptions.stream().filter(c -> c.isActivated()).count();
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
        for (Conductor c: this.subscriptions) {
            c.unsubscribe(this); //make conductors forget this.
        }
    }

    // @Override
    // public boolean isActivated(GameMap map) {
    //     List<Conductor> nearbyConductors = getNearbyConductors(map);
    //     for (Conductor c : nearbyConductors) {
    //         if (c.isActivated(map))
    //     }
    //     return this.activated;
    // }

    // public void checkOnTick(GameMap map) {
    //     List<Conductor> nearbyConductors = getNearbyConductors(map);
    //     int countActivatedNearby = 0;
    //     for (Conductor s: nearbyConductors) {
    //         if (s.isActivated()) countActivatedNearby++;
    //     }

    //     if (countActivatedNearby >= 1) {
    //         this.activated = true;
    //     }

    //     for (Conductor s: nearbyConductors) {
    //         if (isActivated()) s.setActivated(true);
    //     }
    // }

    // private List<Conductor> getNearbyConductors(GameMap map) {
    //     List<Entity> nearbyEntities = new ArrayList<>();
    //     this.getPosition()
    //         .getCardinallyAdjacentPositions()
    //         .stream()
    //         .forEach(position -> nearbyEntities.addAll(map.getEntities(position)));

    //     List<Conductor> nearbyConductors = nearbyEntities.stream()
    //                     .filter(entity -> entity instanceof Conductor)
    //                     .map(entity -> (Conductor) entity)
    //                     .collect(Collectors.toList());
    //     return nearbyConductors;
    // }

}
