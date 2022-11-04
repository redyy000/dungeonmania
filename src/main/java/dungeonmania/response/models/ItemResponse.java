package dungeonmania.response.models;

import org.json.JSONObject;

public final class ItemResponse {
    private final String id;
    private final String type;

    public ItemResponse(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public ItemResponse(JSONObject j) {
        this.id = j.getString("id");
        this.type = j.getString("type");
    }

    public final String getType() {
        return type;
    }

    public final String getId() {
        return id;
    }

    public JSONObject getJSON() {
        return new JSONObject(this);
    }
}
