package edu.brown.cs.denj.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * A class representing a season of a show. Contains a list of episodes.
 */
public class Season implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final int seasonNum;
  private Show show;
  private List<Episode> episodes;

  /**
   * Constructs a season with the given number and show.
   *
   * @param seasonNum
   *          the season number of this season.
   * @param show
   *          the show that this season is part of.
   */
  public Season(int seasonNum, Show show) {
    this.seasonNum = seasonNum;
    this.show = show;
    this.episodes = new ArrayList<Episode>();
  }

  /**
   * Adds an episode to this season.
   *
   * @param ep
   *          the episode to add.
   */
  public void addEpisode(Episode ep) {
    Preconditions.checkNotNull(ep);
    episodes.add(ep);
  }

  /**
   * Produces a specific number episode from the season.
   *
   * @param epNum
   *          the episode number within the season (not absolute number)
   * @return the epNum episode of this season
   */
  public Episode getEpisode(int epNum) {
    if (epNum - 1 > episodes.size()) {
      return null;
    }
    Preconditions.checkPositionIndex(epNum - 1, episodes.size(),
      "Number is not a valid " + "or existing episode of season");

    return episodes.get(epNum - 1);
  }

  /**
   * Gets the list of all episodes in the season.
   *
   * @return the list of the season's episodes.
   */
  public List<Episode> getEpisodes() {
    return episodes;
  }

  /**
   * Produces the season number of this season.
   *
   * @return the season number of this season.
   */
  public int getSeasonNum() {
    return seasonNum;
  }

  /**
   * Gets the number of episodes in this season.
   *
   * @return the size of the list of episodes.
   */
  public int size() {
    return episodes.size();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("Season: %d\n", seasonNum));
    for (Episode e : episodes) {
      builder.append("\t" + e.toString() + "\n");
    }
    return builder.toString();
  }
}
