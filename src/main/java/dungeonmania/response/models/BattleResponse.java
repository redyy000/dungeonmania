package dungeonmania.response.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public final class BattleResponse {
    private final String enemy;
    private final double initialPlayerHealth;
    private final double initialEnemyHealth;
    private final List<ItemResponse> battleItems;
    private final List<RoundResponse> rounds;

    public BattleResponse() {
        this.initialPlayerHealth = 0;
        this.initialEnemyHealth = 0;
        this.enemy = "";
        this.battleItems = new ArrayList<>();
        this.rounds = new ArrayList<>();
    }

    public BattleResponse(String enemy, List<RoundResponse> rounds, List<ItemResponse> battleItems,
    double initialPlayerHealth, double initialEnemyHealth) {
        this.initialPlayerHealth = initialPlayerHealth;
        this.initialEnemyHealth = initialEnemyHealth;
        this.enemy = enemy;
        this.rounds = rounds;
        this.battleItems = battleItems;
    }

    public BattleResponse(JSONObject j) {
        this.enemy = j.getString("enemy");
        this.initialEnemyHealth = j.getDouble("initialEnemyHealth");
        this.initialPlayerHealth = j.getDouble("initialPlayerHealth");

        this.battleItems = new ArrayList<>();
        this.rounds = new ArrayList<>();
        JSONArray battleItemsJ = j.getJSONArray("battleItems");
        for (int i = 0; i < battleItemsJ.length(); i++) {
            this.battleItems.add(new ItemResponse(battleItemsJ.getJSONObject(i)));
        }
        JSONArray roundsJ = j.getJSONArray("rounds");
        for (int i = 0; i < roundsJ.length(); i++) {
            this.rounds.add(new RoundResponse(battleItemsJ.getJSONObject(i)));
        }
    }

    public final String getEnemy() {
        return enemy;
    }

    public final double getInitialPlayerHealth() {
        return initialPlayerHealth;
    }

    public final double getInitialEnemyHealth() {
        return initialEnemyHealth;
    }

    public final List<RoundResponse> getRounds() {
        return rounds;
    }

    public final List<ItemResponse> getBattleItems() {
        return battleItems;
    }

    public JSONObject getJSON() {
        JSONObject j = new JSONObject();
        j.put("enemy", this.enemy);
        j.put("initialEnemyHealth", this.initialEnemyHealth);
        j.put("initialPlayerHealth", this.initialPlayerHealth);

        JSONArray battleItemsJ = new JSONArray();
        this.battleItems.stream()
                        .forEach((ItemResponse i) -> battleItemsJ.put(i.getJSON()));
        j.put("battleItems", battleItemsJ);
        JSONArray roundsJ = new JSONArray();
        this.rounds.stream()
                    .forEach((RoundResponse i) -> roundsJ.put(i.getJSON()));
        j.put("rounds", roundsJ);
        return j;
    }
}
