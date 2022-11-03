package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;


public class EnemiesGoal implements Goal {

    private int target;

    public EnemiesGoal(int target) {
        this.target = target;
    }

    @Override
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        int numKilled = game.getKilledEnemies();
        int numSpawners = game.numSpawners();
        return numKilled >= target && numSpawners == 0;

    }

    @Override
    public String toString(Game game) {
        if (this.achieved(game)) return "";
        return ":enemies"; //surely this is the right string
    }

    @Override
<<<<<<< src/main/java/dungeonmania/goals/EnemiesGoal.java
    public JSONObject getJSON() {
=======
    public JSONObject getJson() {
>>>>>>> src/main/java/dungeonmania/goals/EnemiesGoal.java
        JSONObject j = new JSONObject();
        j.put("goal", "enemies");
        return j;
    }
}
