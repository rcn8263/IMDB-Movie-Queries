package movies;

import cs.Genre;
import cs.TitleType;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * The subclass of the IMDB abstract class that implements all the required
 * abstract query methods.
 *
 * @author RIT CS
 * @author Ryan Nowak
 */
public class MyIMDB extends IMDB {
    /** The minimum number of votes a movie needs to be considered for top ranking */
    private final static int MIN_NUM_VOTES_FOR_TOP_RANKED = 1000;

    /**
     * Create IMDB using the small or large dataset.
     *
     * @param small true if the small dataset is desired, otherwise the large one
     * @throws FileNotFoundException
     */
    public MyIMDB(boolean small) throws FileNotFoundException {
        super(small);
    }

    @Override
    public Collection<Movie> getMovieTitleWithWords(String type, String words) {
        // we simply loop over movieList and add to our list the movies that
        // have the same type, and contain the words substring
        List<Movie> result = new LinkedList<>();

        TitleType titleType = TitleType.valueOf(type);   // titleType is TitleType.MOVIE
        for (Movie movie: super.movieList) {
            // for a particular Movie, movie
            if (movie.getTitleType() == titleType) {
                if (movie.getTitle().contains(words)) {
                    result.add(movie);
                }
            }
        }

        return result;
    }

    @Override
    public Movie findMovieByID(String ID) {
        return this.movieMap.get(ID);
    }

    @Override
    public Collection<Movie> getMoviesByYearAndGenre(String type, int year, String genre) {
        // we use Movie's natural order comparison which is to order Movie's of a
        // type by title and then year
        Set<Movie> result = new TreeSet<>();

        TitleType typeOfMovie = TitleType.valueOf(type);
        Genre genreOfMovie = Genre.valueOf(genre);
        for (Movie movie: this.movieList) {
            // for a particular Movie, movie
            if (movie.getGenres().contains(genreOfMovie) &&
                movie.getTitleType() == typeOfMovie &&
                movie.getYear() == year) {
                result.add(movie);
            }
        }
        return result;
    }

    @Override
    public Collection<Movie> getMoviesByRuntime(String type, int start, int end) {
        // we use a comparator which orders Movie's of a type by descending runtime
        // and then title
        Set<Movie> result = new TreeSet<>(new MovieComparatorRuntime());

        TitleType typeOfMovie = TitleType.valueOf(type);
        for (Movie movie: this.movieList) {
            if (movie.getTitleType() == typeOfMovie &&
                movie.getRuntimeMinutes() >= start &&
                movie.getRuntimeMinutes() <= end) {
                result.add(movie);
            }
        }
        return result;
    }

    @Override
    public Collection<Movie> getMoviesMostVotes(int num, String type) {
        // use a comparator that orders Movie's of a type by descending number
        // of votes

        List<Movie> result = new LinkedList<>();

        Set<Movie> sortedMostVotes = new TreeSet<>(new MovieComparatorVotes());
        TitleType typeOfMovie = TitleType.valueOf(type);
        for (Movie movie: this.movieList) {
            if (movie.getTitleType() == typeOfMovie) {
                sortedMostVotes.add(movie);
            }
        }

        Iterator iter = sortedMostVotes.iterator();
        for (int i = 0; i < num; i++) {
            result.add((Movie) iter.next());
        }

        return result;
    }

    @Override
    public Map<Integer, List<Movie>> getMoviesTopRated(int num, String type, int start, int end) {
        Map<Integer, List<Movie>> result = new TreeMap<>();

        Set<Rating> sortedRatings = new TreeSet<>();
        TitleType typeOfMovie = TitleType.valueOf(type);
        for (Movie movie: this.movieList) {
            if (movie.getTitleType() == typeOfMovie &&
                movie.getRating().getNumVotes() >= MIN_NUM_VOTES_FOR_TOP_RANKED &&
                movie.getYear() >= start &&
                movie.getYear() <= end) {
                sortedRatings.add(movie.getRating());
            }
        }

        for (int i = start; i <= end; i++) {
            result.put(i, new LinkedList<>());
        }

        for (Rating rating: sortedRatings) {
            if (result.get(this.movieMap.get(rating.getID()).getYear()).size() < num) {
                result.get(this.movieMap.get(rating.getID()).getYear()).
                            add(this.movieMap.get(rating.getID()));
            }
        }

        return result;
    }
}