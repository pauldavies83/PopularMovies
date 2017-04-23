package uk.co.pauldavies83.popularmovies.views.movielist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.pauldavies83.popularmovies.PopularMoviesApplication;
import uk.co.pauldavies83.popularmovies.R;
import uk.co.pauldavies83.popularmovies.data.FavouritesColumns;
import uk.co.pauldavies83.popularmovies.data.MovieProvider;
import uk.co.pauldavies83.popularmovies.model.Movie;
import uk.co.pauldavies83.popularmovies.views.moviedetail.MovieDetailActivity;

public class MainActivity extends AppCompatActivity implements MovieGridAdapter.MovieClickListener {

    public static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    public static final String MOST_POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";

    public static final String MOVIE_PARCEL_KEY = "movie_parcel";

    private static final String GRID_SELECTED_FILTER = "grid_selected_filter";
    private static final int MOST_POPULAR_FILTER = 0;
    private static final int TOP_RATED_FILTER = 1;
    private static final int FAVOURITES_FILTER = 2;

    private String apiKey;
    private OkHttpClient okHttpClient;

    private RecyclerView movieGrid;
    private View progressBar;
    private TextView errorText;

    private MovieGridAdapter movieGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        apiKey = getResources().getString(R.string.tmdb_v3_api_key);
        okHttpClient = ((PopularMoviesApplication) getApplication()).getOkHttpClient();

        progressBar = findViewById(R.id.progress_bar);
        errorText = (TextView) findViewById(R.id.tv_error_text);
        createMovieGridView();
        switchViewFilter(getCurrentSelectedFilterFromPrefs());
    }

    private void fetchMovieData(String sortOrder) {
        errorText.setVisibility(View.GONE);
        new FetchMovieDataTask().execute(sortOrder);
    }

    private void createMovieGridView() {
        RecyclerView.LayoutManager layoutManager;
        if (isLandscape()) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            layoutManager = new GridLayoutManager(this, 2);
        }
        movieGrid = (RecyclerView) findViewById(R.id.rv_movie_grid);
        movieGrid.setLayoutManager(layoutManager);
        movieGridAdapter = new MovieGridAdapter(this, this);
        movieGrid.setAdapter(movieGridAdapter);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onMovieClicked(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_PARCEL_KEY, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_order, menu);

        MenuItem item = menu.findItem(R.id.sort_order_spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int item, long l) {
                switchViewFilter(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinner.setSelection(getCurrentSelectedFilterFromPrefs(), false);
        return true;
    }

    private int getCurrentSelectedFilterFromPrefs() {
        return getPreferences(MODE_PRIVATE).getInt(GRID_SELECTED_FILTER, MOST_POPULAR_FILTER);
    }

    private void switchViewFilter(int item) {
        getPreferences(MODE_PRIVATE).edit().putInt(GRID_SELECTED_FILTER, item).apply();
        switch (item) {
            case MOST_POPULAR_FILTER:
                fetchMovieData(MOST_POPULAR);
                break;
            case TOP_RATED_FILTER:
                fetchMovieData(TOP_RATED);
                break;
            case FAVOURITES_FILTER:
                fetchFavouriteMovieData();
                break;
        }
    }

    private void fetchFavouriteMovieData() {
        Cursor cursor = getContentResolver().query(MovieProvider.Favourites.FAVOURITES, null, null, null, null);
        List<Movie> movies = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Movie movie = new Movie(cursor.getString(cursor.getColumnIndex(FavouritesColumns._ID)),
                        cursor.getString(cursor.getColumnIndex(FavouritesColumns.TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavouritesColumns.OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(FavouritesColumns.VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(FavouritesColumns.RELEASE_DATE)),
                        cursor.getString(cursor.getColumnIndex(FavouritesColumns.POSTER_PATH)));
                movies.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
            movieGridAdapter.setMovies(movies.toArray(new Movie[movies.size()]));
            movieGrid.setVisibility(View.VISIBLE);
        } else {
            movieGrid.setVisibility(View.GONE);
            errorText.setText(getString(R.string.no_favourites));
            errorText.setVisibility(View.VISIBLE);
        }
    }

    class FetchMovieDataTask extends AsyncTask<String, Object, Movie[]> {
        @Override
        protected void onPreExecute() {
            movieGrid.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            String sortOrder;
            Movie[] movies = null;

            if (isOnline()) {
                if (params != null && params.length > 0) {
                    sortOrder = params[0];
                } else {
                    sortOrder = MOST_POPULAR;
                }

                Uri uri = Uri.parse(THEMOVIEDB_BASE_URL)
                        .buildUpon()
                        .appendPath(sortOrder)
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
                    movies = new Movie[results.length()];

                    for (int i = 0; i < results.length(); i++) {
                        movies[i] = new Movie(results.getJSONObject(i));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                movies = null;
            }

            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            progressBar.setVisibility(View.GONE);
            if (movies != null) {
                movieGridAdapter.setMovies(movies);
                movieGrid.setVisibility(View.VISIBLE);
            } else {
                errorText.setText(getString(R.string.connection_error));
                errorText.setVisibility(View.VISIBLE);
            }
        }
    }

}

