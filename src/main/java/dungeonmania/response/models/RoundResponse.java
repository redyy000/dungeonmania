package dungeonmania.response.models;

import org.json.JSONObject;

public class RoundResponse {
    private double deltaPlayerHealth;
    private double deltaEnemyHealth;

    public RoundResponse(double deltaPlayerHealth, double deltaEnemyHealth) {
        this.deltaPlayerHealth = deltaPlayerHealth;
        this.deltaEnemyHealth = deltaEnemyHealth;
    }

    public RoundResponse(JSONObject j) {
        this.deltaPlayerHealth = j.getDouble("deltaPlayerHealth");
        this.deltaEnemyHealth = j.getDouble("deltaEnemyHealth");
    }

    public double getDeltaCharacterHealth() {
        return deltaPlayerHealth;
    }

    public double getDeltaEnemyHealth() {
        return deltaEnemyHealth;
    }

    public JSONObject getJSON() {
        return new JSONObject(this);
    }
}
