package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;


import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class LogicalEntity extends Entity implements SwitchObserver {

    private boolean activated;
    private String logic;
    private List<Conductor> nearbyConductors = new ArrayList<>();

    private int notificationsThisTick = 0;

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

    @Override
    public void subscribe(Conductor c) {
        this.nearbyConductors.add(c);
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
        for (Conductor c: this.nearbyConductors) {
            c.unsubscribe(this); //make conductors forget this.
        }
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    public String getLogic() {
        return this.logic;
    }

    public int nearbyActivated() {
        return (int) nearbyConductors.stream().filter(c -> c.isActivated()).count();
    }

    @Override
    public void notify(GameMap map, boolean activated) {
        String l = getLogic();
        int n = nearbyActivated();
        if (l.equals("or") && n >= 1) {
            setActivated(true); return;
        } else if (l.equals("and") && n >= 2 && nearbyConductors.size() == n) {
            setActivated(true); return;
        } else if (l.equals("xor") && n == 1) {
            setActivated(true); return;
        } else if (l.equals("co_and") && notificationsThisTick >= 2
                    && n >= 2) {
            setActivated(true); return;
        }
        setActivated(false);
        return;
    }

    public void resetNotifsCount() {
        this.notificationsThisTick = 0;
    }
}
