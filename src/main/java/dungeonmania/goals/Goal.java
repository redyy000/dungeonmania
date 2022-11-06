package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;


public interface Goal {

    public boolean achieved(Game game);
    public String toString(Game game);
    public JSONObject getJSON();
}
