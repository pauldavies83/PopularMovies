package uk.co.pauldavies83.popularmovies;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().hasExtra(MainActivity.MOVIE_PARCEL_KEY)) {
            movie = getIntent().getParcelableExtra(MainActivity.MOVIE_PARCEL_KEY);
            bindDataToView();
        }
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

}
