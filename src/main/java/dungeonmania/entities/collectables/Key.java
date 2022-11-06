package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class Key extends Collectable {
    private int number;

    public Key(Position position, int number) {
        super(position);
        this.number = number;
    }

    public Key(JSONObject j) {
        super(j);
        this.number = j.getInt("number");
    }

    public int getnumber() {
        return number;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("number", this.number);
        return j;
    }
}
