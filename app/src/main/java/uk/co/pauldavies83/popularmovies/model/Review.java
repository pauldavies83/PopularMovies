package uk.co.pauldavies83.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private final String id;
    private final String author;
    private final String body;
    private final String url;

    public Review(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        author =jsonObject.getString("author");
        body = jsonObject.getString("content");
        url = jsonObject.getString("url");
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public String getUrl() {
        return url;
    }
}
