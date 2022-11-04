package dungeonmania.entities.collectables.potions;

import org.json.JSONObject;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.util.Position;

public class InvisibilityPotion extends Potion {
    public static final int DEFAULT_DURATION = 8;

    public InvisibilityPotion(Position position, int duration) {
        super(position, duration);
    }

    public InvisibilityPotion(JSONObject j) {
        super(j);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                0,
                0,
                1,
                1,
                false,
                false));
    }

}
