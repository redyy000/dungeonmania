package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Switch;

public class BouldersGoal implements Goal {
    @Override
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;

        return game.getMap().getEntities(Switch.class).stream().allMatch(s -> s.isActivated());

    }

    @Override
    public String toString(Game game) {
        if (this.achieved(game)) return "";
        return ":boulders";
    }

    @Override
<<<<<<< src/main/java/dungeonmania/goals/BouldersGoal.java
    public JSONObject getJSON() {
=======
    public JSONObject getJson() {
>>>>>>> src/main/java/dungeonmania/goals/BouldersGoal.java
        JSONObject j = new JSONObject();
        j.put("goal", "boulders");
        return j;
    }
}
