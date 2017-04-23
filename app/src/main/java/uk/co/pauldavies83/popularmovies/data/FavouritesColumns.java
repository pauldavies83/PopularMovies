package uk.co.pauldavies83.popularmovies.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface FavouritesColumns {
    @DataType(TEXT) @PrimaryKey
    String _ID = "_id";

    @DataType(TEXT)
    String TITLE = "title";

    @DataType(TEXT)
    String OVERVIEW = "overview";

    @DataType(TEXT)
    String VOTE_AVERAGE = "vote_average";

    @DataType(TEXT)
    String RELEASE_DATE = "release_date";

    @DataType(TEXT)
    String POSTER_PATH = "poster_path";
}
