package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;

public class OrGoal implements Goal {
    private Goal goal1;
    private Goal goal2;

    public OrGoal(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    @Override
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;

        return goal1.achieved(game) || goal2.achieved(game);
    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) return "";
        else return "(" + goal1.toString(game) + " OR " + goal2.toString(game) + ")";
    }

    @Override
<<<<<<< src/main/java/dungeonmania/goals/OrGoal.java
    public JSONObject getJSON() {
        JSONArray subgoalsJ = new JSONArray();
        subgoalsJ.put(this.goal1.getJSON());
        subgoalsJ.put(this.goal2.getJSON());
=======
    public JSONObject getJson() {
        JSONArray subgoalsJ = new JSONArray();
        subgoalsJ.put(this.goal1.getJson());
        subgoalsJ.put(this.goal2.getJson());
>>>>>>> src/main/java/dungeonmania/goals/OrGoal.java

        JSONObject j = new JSONObject();
        j.put("goal", "OR");
        j.put("subgoals", subgoalsJ);
        return j;
    }
}
