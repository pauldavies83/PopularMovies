package uk.co.pauldavies83.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {

    private final String id;
    private final String title;
    private final String overview;
    private final String vote_average;
    private final String release_date;
    private final String poster_path;

    public static final Parcelable.Creator CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel parcel) {
        id = parcel.readString();
        title = parcel.readString();
        overview = parcel.readString();
        vote_average = parcel.readString();
        release_date = parcel.readString();
        poster_path = parcel.readString();
    }

    public Movie(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        title = jsonObject.getString("title");
        overview =jsonObject.getString("overview");
        vote_average = jsonObject.getString("vote_average");
        release_date = jsonObject.getString("release_date");
        poster_path = jsonObject.getString("poster_path");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getPosterPath() {
        return poster_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(vote_average);
        parcel.writeString(release_date);
        parcel.writeString(poster_path);
    }

}
