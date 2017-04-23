package uk.co.pauldavies83.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import uk.co.pauldavies83.popularmovies.model.Movie;

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {

    public static final String AUTHORITY = "uk.co.pauldavies83.popularmovies.MovieProvider";
    public static final String FAVOURITES_SELECTION = FavouritesColumns._ID + " = ? ";

    @TableEndpoint(table = MovieDatabase.FAVOURITES)
    public static class Favourites {

        @ContentUri(
                path = "favourites",
                type = "vnd.android.cursor.dir/favourite")
        public static final Uri FAVOURITES = Uri.parse("content://" + AUTHORITY + "/favourites");
    }

    public static void addMovieToFavourites(@NonNull ContentResolver contentResolver, @NonNull Movie movie) {
        ContentValues values = new ContentValues();
        values.put(FavouritesColumns._ID, movie.getId());
        values.put(FavouritesColumns.TITLE, movie.getTitle());
        values.put(FavouritesColumns.OVERVIEW, movie.getOverview());
        values.put(FavouritesColumns.POSTER_PATH, movie.getPosterPath());
        values.put(FavouritesColumns.RELEASE_DATE, movie.getReleaseDate());
        values.put(FavouritesColumns.VOTE_AVERAGE, movie.getVoteAverage());
        contentResolver.insert(MovieProvider.Favourites.FAVOURITES, values);
    }

    public static boolean isMovieFavourite(@NonNull ContentResolver contentResolver, @NonNull String movieId) {
        Cursor query = contentResolver.query(
            MovieProvider.Favourites.FAVOURITES,
            null,
            FAVOURITES_SELECTION,
            new String[]{ movieId },
            null);
        return query != null && query.getCount() > 0;
    }

    public static void removeMovieFromFavourites(@NonNull ContentResolver contentResolver, @NonNull String movieId) {
        contentResolver.delete(MovieProvider.Favourites.FAVOURITES, FAVOURITES_SELECTION, new String[]{ movieId });
    }

}