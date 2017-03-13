package uk.co.pauldavies83.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        movie = getIntent().getParcelableExtra(MainActivity.MOVIE_PARCEL_KEY);
        bindDataToView();
    }

    private void bindDataToView() {
        ImageView image = (ImageView) findViewById(R.id.movie_image);
        Picasso.with(getApplicationContext()).
                load(MovieGridAdapter.TMBD_BASE_IMAGE_URL + "w185" + movie.getPosterPath()).
                placeholder(R.mipmap.ic_launcher).
                into(image);

        setTitle(movie.getTitle());

        ((TextView)findViewById(R.id.tv_movie_synopsis)).setText(movie.getOverview());
        ((TextView)findViewById(R.id.tv_movie_user_rating)).setText(movie.getVoteAverage());
        ((TextView)findViewById(R.id.tv_movie_release_date)).setText(movie.getReleaseDate());
    }

}
