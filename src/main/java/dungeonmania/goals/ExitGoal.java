package dungeonmania.goals;

import java.util.List;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class ExitGoal implements Goal {

    @Override
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;

        Player character = game.getPlayer();
        Position pos = character.getPosition();
        List<Exit> es = game.getMap().getEntities(Exit.class);
        if (es == null || es.size() == 0) return false;
        return es
            .stream()
            .map(Entity::getPosition)
            .anyMatch(pos::equals);
    }

    @Override
    public String toString(Game game) {
        if (this.achieved(game)) return "";

        return ":exit";
    }

    @Override
<<<<<<< src/main/java/dungeonmania/goals/ExitGoal.java
    public JSONObject getJSON() {
=======
    public JSONObject getJson() {
>>>>>>> src/main/java/dungeonmania/goals/ExitGoal.java
        JSONObject j = new JSONObject();
        j.put("goal", "exit");
        return j;
    }
}
