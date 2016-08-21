package edu.brown.cs.denj.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * A class representing a show and all of its associated data.
 */
public class Show implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private Set<Episode> allEpisodes;
  private List<Season> seasons;
  private String name;
  private int numSeasons;
  private boolean ended;
  private String image;
  private String id;
  private int runtime;
  private List<String> genres;
  private String showPage;
  private String description;

  // private int rating; IMDB show rating
  // private double avgEpRating; //average rating of this show's episodes

  /**
   * Constructs a show with the given parameters.
   *
   * @param name
   *          the show title
   * @param numSeasons
   *          the total number of season.
   * @param ended
   *          whether the show is ended (no longer airing new episodes).
   * @param image
   *          URL to an image associated with the show.
   * @param id
   *          the TVRage ID of the show.
   * @param runtime
   *          the runtime of the show's episodes in minutes.
   */
  public Show(String name, int numSeasons, boolean ended, String image,
    String id, String showPage, int runtime, List<String> genres) {
    Preconditions.checkNotNull(name);
    this.name = name;
    this.numSeasons = numSeasons;
    allEpisodes = new HashSet<Episode>();
    seasons = new ArrayList<Season>();
    this.ended = ended;
    this.image = image;
    this.id = id;
    this.showPage = showPage;
    this.runtime = runtime;
    this.genres = genres;

    // avgEpRating = -1;
  }

  /**
   * Adds a season to this show.
   *
   * @param season
   *          the season to add.
   * @return true if the season was successfully added, false otherwise.
   */
  public boolean addSeason(Season season) {
    Preconditions.checkNotNull(season);
    if (seasons.size() < numSeasons) {
      seasons.add(season);
      for (Episode e : season.getEpisodes()) {
        allEpisodes.add(e);
      }
      return true;
    }
    return false;
  }

  /**
   * Sets the show description as the given string.
   *
   * @param desc
   *          the description to add.
   */
  public void addDescription(String desc) {
    description = desc;
  }

  /**
   * Gets the description of this show, if available.
   *
   * @return the show's description.
   */
  public String getDescription() {
    if (description == null || description.isEmpty()) {
      return "Description not found.";
    }
    return description;
  }

  /**
   * Gets the URL to this show's TVRage web page.
   *
   * @return the show's page URL.
   */
  public String getShowURL() {
    return showPage;
  }

  /**
   * Gets all the show's seasons.
   *
   * @return the list of the show's seasons.
   */
  public List<Season> getAllSeasons() {
    return seasons;
  }

  /**
   * Gets the number of seasons that the show has.
   *
   * @return the number of seasons.
   */
  public int getNumSeasons() {
    return numSeasons;
  }

  /**
   * Gets the number of episodes in this show.
   *
   * @return the total number of episodes. Includes episodes not yet aired.
   */
  public int getNumEpsides() {
    return allEpisodes.size();
  }

  /**
   * Gets the genres.
   *
   * @return the genres.
   */
  public List<String> getGenres() {
    return genres;
  }

  /**
   * Gets an episode from within the specified season.
   *
   * @param season
   *          the season number.
   * @param ep
   *          the episode number.
   * @return the corresponding episode of this show, if found.
   */
  public Episode getEpInSeason(int season, int ep) {
    if (season - 1 >= seasons.size()) {
      return null;
    }
    // Preconditions.checkElementIndex(season - 1, seasons.size());
    return seasons.get(season - 1).getEpisode(ep);
  }

  /**
   * Gets an episode of this show given it's absolute number.
   *
   * @param epNumber
   *          the number of the episode.
   * @return the corresponding episode of the show if it exists.
   */
  public Episode getEp(int epNumber) {
    int count = 0;
    for (Season s : seasons) {
      int numEps = s.size();
      if (count + numEps >= epNumber) {
        return s.getEpisode(epNumber - count);
      }
      count += numEps;
    }
    return null;
  }

  /**
   * Gets the most recent aired episode of a show.
   * @return the last episode to be aired.
   */
  public Episode getMostRecent() {
    Episode curr = null;
    for (Season s : seasons) {
      for (Episode e : s.getEpisodes()) {
        if (!e.hasAired()) {
          return curr;
        } else {
          curr = e;
        }
      }
    }
    return curr;
  }

  /**
   * Gets the set of all episodes for this show.
   *
   * @return the set of all of this show's episodes.
   */
  public Set<Episode> getAllEpisodes() {
    return allEpisodes;
  }

  /**
   * Returns true if the show status is ended.
   *
   * @return true if the show is ended, false otherwise.
   */
  public boolean isEnded() {
    return ended;
  }

  /**
   * Gets the ID of this show.
   *
   * @return the show ID.
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the title of this show.
   *
   * @return the show name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the corresponding image for this show.
   *
   * @return the show image URL.
   */
  public String getImage() {
    return image;
  }

  /**
   * Gets the runtime of this show's episodes in minutes.
   *
   * @return the runtime of the show's episodes.
   */
  public int getRuntime() {
    return runtime;
  }

  // /**
  // * Calculates and stores the average rating of all this show's episodes.
  // */
  // public void averageRating() {
  // double sum = 0.0;
  // int count = 0;
  // for (Episode e : allEpisodes) {
  // if (e.getRating() != -1) {
  // sum += e.getRating();
  // count++;
  // }
  // }
  // avgEpRating = sum / count;
  // }

  // /**
  // * Gets the average rating for all of this show's episodes.
  // * @return the average rating of the episodes.
  // */
  // public double getAvgRating() {
  // return avgEpRating;
  // }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Show)) {
      return false;
    }
    Show other = (Show) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

}
