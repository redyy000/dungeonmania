package dungeonmania.entities.logical;


import dungeonmania.util.Position;

public class LightBulb extends LogicalEntity {

    public LightBulb(Position p, boolean activated, String logic) {
        super(p, activated, logic);
    }


    //TODO getJson is not usual type. replace "type" key.
}
