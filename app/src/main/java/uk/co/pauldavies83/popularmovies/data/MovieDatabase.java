package uk.co.pauldavies83.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = MovieDatabase.VERSION)
public class MovieDatabase {

    public static final int VERSION = 2;

    @Table(FavouritesColumns.class)
    public static final String FAVOURITES = "favourites";
}
