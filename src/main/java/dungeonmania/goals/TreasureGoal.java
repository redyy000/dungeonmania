package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;

public class TreasureGoal implements Goal {
    private int target;

    public TreasureGoal(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;

        return game.getNCollectedTreasure() >= target;

    }
    public String toString(Game game) {
        if (this.achieved(game)) return "";

        return ":treasure";
    }

    @Override
<<<<<<< src/main/java/dungeonmania/goals/TreasureGoal.java
    public JSONObject getJSON() {
=======
    public JSONObject getJson() {
>>>>>>> src/main/java/dungeonmania/goals/TreasureGoal.java
        JSONObject j = new JSONObject();
        j.put("goal", "treasure");
        return j;
    }
}
