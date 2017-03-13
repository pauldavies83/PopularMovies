package uk.co.pauldavies83.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MoviePosterViewHolder> {

    interface MovieClickListener {
        void onMovieClicked(Movie movie);
    }


    public static final String TMBD_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    private Context context;
    private MovieClickListener movieClickListener;
    private Movie[] movies = {};

    public MovieGridAdapter(Context context, MovieClickListener movieClickListener) {
        this.context = context;
        this.movieClickListener = movieClickListener;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View moviePosterView = inflater.inflate(R.layout.movie_poster, parent, false);
        return new MoviePosterViewHolder(moviePosterView);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        // TODO make the metrics stuff screens-size aware!

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        Picasso.with(context).
                load(TMBD_BASE_IMAGE_URL + "w185" + movies[position].getPosterPath()).
                placeholder(R.mipmap.ic_launcher).
                resize(metrics.widthPixels, (int)(metrics.widthPixels * 1.5)).
                into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }


    class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView movieImage;

        public MoviePosterViewHolder(View itemView) {
            super(itemView);
            movieImage = (ImageView) itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            movieClickListener.onMovieClicked(movies[getAdapterPosition()]);
        }
    }
}
