package dungeonmania.entities.logical;


import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wire extends Conductor {
    private boolean activated;
    public Wire(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
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
