package dungeonmania.entities.buildables;


import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;

public class Shield extends Buildable implements BattleItem {
    private int durability;
    private double defence;

    public Shield(int durability, double defence) {
        super();
        this.durability = durability;
        this.defence = defence;
    }

    public Shield(JSONObject j) {
        super(j);
        this.durability = j.getInt("durability");
        this.defence = j.getDouble("defence");
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
            defence,
            1,
            1));
    }


    @Override
    public JSONObject getJSON() {
        JSONObject j = super.getJSON();
        j.put("durability", this.durability);
        j.put("defence", this.defence);
        return j;
    }
}
