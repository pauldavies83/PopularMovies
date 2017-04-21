package uk.co.pauldavies83.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.pauldavies83.popularmovies.model.Movie;
import uk.co.pauldavies83.popularmovies.model.Review;
import uk.co.pauldavies83.popularmovies.model.Video;

import static uk.co.pauldavies83.popularmovies.MainActivity.MOVIE_PARCEL_KEY;
import static uk.co.pauldavies83.popularmovies.MainActivity.THEMOVIEDB_BASE_URL;

public class MovieDetailActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Movie movie;
    private String apiKey;
    private OkHttpClient okHttpClient;

    private RecyclerView videoListView;
    private RecyclerView reviewListView;
    private ReviewListAdapter reviewListAdapter;
    private VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() == null || !getIntent().hasExtra(MainActivity.MOVIE_PARCEL_KEY)) {
            finish();
        }

        setContentView(R.layout.activity_movie_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movie = getIntent().getParcelableExtra(MainActivity.MOVIE_PARCEL_KEY);
        apiKey = getResources().getString(R.string.tmdb_v3_api_key);
        okHttpClient = new OkHttpClient();

        createListViews();
        bindDataToView();
        new FetchMovieVideosTask().execute(movie.getId());
        new FetchMovieReviewsTask().execute(movie.getId());
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(MOVIE_PARCEL_KEY, movie);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_favourite:
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);
                item.setIcon(isChecked ? R.drawable.ic_favourite_added : R.drawable.ic_favourite_not_added);

                return true;
            default:
                return false;
        }
    }

    private void bindDataToView() {
        ImageView image = (ImageView) findViewById(R.id.movie_image);
        Picasso.with(getApplicationContext()).
                load(MovieGridAdapter.TMBD_BASE_IMAGE_URL + "w185" + movie.getPosterPath()).
                placeholder(R.mipmap.ic_launcher).
                into(image);

        setTitle(movie.getTitle());
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(movie.getTitle());
        }

        ((TextView)findViewById(R.id.tv_movie_synopsis)).setText(movie.getOverview());
        ((TextView)findViewById(R.id.tv_movie_user_rating)).setText(getString(R.string.rating_template, movie.getVoteAverage()));
        ((TextView)findViewById(R.id.tv_movie_release_date)).setText(movie.getReleaseDate());
    }

    private void createListViews() {
        reviewListView = (RecyclerView) findViewById(R.id.review_list);
        reviewListView.setLayoutManager(new LinearLayoutManager(this));
        reviewListAdapter = new ReviewListAdapter(this);
        reviewListView.setAdapter(reviewListAdapter);

        videoListView = (RecyclerView) findViewById(R.id.video_list);
        videoListView.setLayoutManager(new LinearLayoutManager(this));
        videoListAdapter = new VideoListAdapter(this);
        videoListView.setAdapter(videoListAdapter);
    }

    class FetchMovieVideosTask extends AsyncTask<String, Object, Video[]> {
        @Override
        protected Video[] doInBackground(String... params) {
            String movieId;
            Video[] videos = null;

            if (isOnline() && params != null && params.length > 0) {
                movieId = params[0];

                Uri uri = Uri.parse(THEMOVIEDB_BASE_URL)
                        .buildUpon()
                        .appendPath(movieId)
                        .appendPath("videos")
                        .appendQueryParameter("api_key", apiKey)
                        .build();

                Request request = new Request.Builder()
                        .url(uri.toString())
                        .get()
                        .build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray results = jsonResponse.getJSONArray("results");
                    videos = new Video[results.length()];

                    for (int i = 0; i < results.length(); i++) {
                        videos[i] = new Video(results.getJSONObject(i));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                videos = new Video[0];
            }

            return videos;
        }

        @Override
        protected void onPostExecute(Video[] videos) {
            if (videos != null) {
                videoListAdapter.setVideos(videos);
                findViewById(R.id.tv_trailers_title).setVisibility(View.VISIBLE);
                findViewById(R.id.divider).setVisibility(View.VISIBLE);
                videoListView.setVisibility(View.VISIBLE);
            }
        }
    }

    class FetchMovieReviewsTask extends AsyncTask<String, Object, Review[]> {
        @Override
        protected Review[] doInBackground(String... params) {
            String movieId;
            Review[] reviews = null;

            if (isOnline() && params != null && params.length > 0) {
                movieId = params[0];

                Uri uri = Uri.parse(THEMOVIEDB_BASE_URL)
                        .buildUpon()
                        .appendPath(movieId)
                        .appendPath("reviews") // "videos" for videos
                        .appendQueryParameter("api_key", apiKey)
                        .build();

                Request request = new Request.Builder()
                        .url(uri.toString())
                        .get()
                        .build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray results = jsonResponse.getJSONArray("results");
                    reviews = new Review[results.length()];

                    for (int i = 0; i < results.length(); i++) {
                        reviews[i] = new Review(results.getJSONObject(i));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                reviews = new Review[0];
            }

            return reviews;
        }

        @Override
        protected void onPostExecute(Review[] reviews) {
            if (reviews != null) {
                reviewListAdapter.setReviews(reviews);
                findViewById(R.id.review_list).setVisibility(View.VISIBLE);
                findViewById(R.id.divider2).setVisibility(View.VISIBLE);
                reviewListView.setVisibility(View.VISIBLE);
            }
        }
    }


}
