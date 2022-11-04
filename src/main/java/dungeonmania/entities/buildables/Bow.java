package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;

public class Bow extends Buildable implements BattleItem {

    private int durability;

    public Bow(int durability) {
        super(null);
        this.durability = durability;
    }

    public Bow(JSONObject j) {
        super(null); //TODO don't know if this is right.
        this.durability = j.getInt("durability");
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.getPlayer().remove(this);
        }
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            2,
            1));
    }

    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("durability", this.durability);
        return j;
    }
}
