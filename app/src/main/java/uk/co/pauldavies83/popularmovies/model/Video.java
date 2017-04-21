package uk.co.pauldavies83.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Video {

    private final String id;
    private final String key;
    private final String name;

    public Video(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        key =jsonObject.getString("key");
        name = jsonObject.getString("name");
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
