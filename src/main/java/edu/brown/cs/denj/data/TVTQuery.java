package edu.brown.cs.denj.data;

import java.util.List;
import java.util.Map;

/**
 * An interface defining methods for querying TV show and episode information.
 */
public interface TVTQuery {

  /**
   * Retrieves a sample of shows of a given genre.
   * @param genre the genre to find.
   * @return an array of shows of the genre.
   */
  List<Show> sampleByGenre(String genre);

  /**
   * Searches for the given search term, and produces a map
   * of resulting show names to their corresponding IDs.
   * @param entry the term being searched.
   * @return a map of show names to IDs.
   */
  Map<String, String> search(String entry);

  /**
   * Retrieves a set of top shows.
   * @return the set of top shows.
   */
  List<Show> topShows();

  /**
   * Gets the show corresponding to an ID, if it exists.
   * @param id the show ID.
   * @return the corresponding show, or null if not found.
   */
  Show getShow(String id);


  /**
   * Gets the next upcoming episode of the show with the given ID.
   * @param s the show to find nextEp of.
   * @return the next new episode of the show to air, if one exists.
   */
  Episode getNextEp(Show s);

//  default Episode getNextEp(Show s) {
//    return getNextEp(s.getID());
//  }

  /**
   * Gets the most recently premiered episode of the given show.
   * @param s the show to find the latest episode of.
   * @return the latest episode of s, or null if it doesn't exist.
   */
  Episode mostRecentEp(Show s);

  /**
   * Gets the description of an episode. The non-spoiler version of recap.
   * @param e the episode.
   * @return a description of the given episode as a String.
   * Null if not found.
   */
  String description(Episode e);

  /**
   * Gets a synopsis of the given episode.
   * @param e the episode.
   * @return a recap of the episode as a String.
   * Null if not found.
   */
  String recap(Episode e);

}
