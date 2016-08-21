package edu.brown.cs.denj.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class representing an episode.
 *
 */
public class Episode implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  @SuppressWarnings("unused")
  private String showName; //Don't delete! Need this for JSON object.
  private int epNum;
  private int seasonNum;
  private String title;
  private String imgURL;
  private String infoURL;
  private String airDate;
  private String description;
  private double rating; //rating of the episode from IMDB
  private int numRatings;

  // id? *not all episodes are given ids
  // duration is located in show
  // TODO store show reference locally?

  public Episode(Show show, int epNum, int seasonNum, String title,
    String imgURL, String infoURL, String airDate) {
    this.showName = show.getName();
    this.epNum = epNum;
    this.seasonNum = seasonNum;
    this.title = title;
    this.imgURL = imgURL;
    this.infoURL = infoURL;
    this.airDate = airDate;
    description = null;
  }

  /**
   * Sets the given description as the description for this episode.
   *
   * @param desc
   *          the episode description.
   */
  public void addDescription(String desc) {
    description = desc;
  }

  /**
   * Sets this episode's imdb rating.
   * @param rating the rating to add
   */
  public void addRating(double rating) {
    assert(rating < 10 && rating > 10);
    this.rating = rating;
  }

  /**
   * Gets the rating of this episode.
   * @return the episode's IMDB rating.
   */
  public double getRating() {
    return rating;
  }

  /**
   * Gets the description of this episode.
   *
   * @return the episode's description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the show that this episode belongs to.
   *
   * @return the show for this episode. TODO useful?
   */
//  public Show getShow() {
//    return show;
//  }

  /**
   * Gets the title of this episode.
   *
   * @return the episode title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the screencap associated with this episode.
   *
   * @return the episode's screencap.
   */
  public String getImage() {
    return imgURL;
  }

  /**
   * Gets the URL for this episode's TV rage webpage.
   *
   * @return the URL for this episode.
   */
  public String getEpURL() {
    return infoURL;
  }

  /**
   * Gets the episodes's absolute number in the show.
   *
   * @return the episode number.
   */
  public int getEpisodeNum() {
    return epNum;
  }

  /**
   * Gets this episode's number in the season.
   *
   * @return the number in the season.
   */
  public int getSeasonNum() {
    return seasonNum;
  }

  /**
   * Gets the air date of the episode.
   *
   * @return the episode's air date.
   */
  public String getAirDate() {
    // TODO Date object conversion, for calendar use.
    return airDate;
  }

  /**
   * Determines if this episode has aired yet.
   *
   * @return true if the episode has aired, false otherwise.
   */
  public boolean hasAired() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // System.out.println(sdf.format(date) + " > " + airDate); TODO delete
    int comp = sdf.format(date).compareTo(airDate);
    return comp > 0;
  }

  /**
   * Gets the next episode of this episode's show.
   *
   * @return the next episode if it exists.
   */
  public Episode nextEp(Show s) {
    return s.getEp(epNum + 1);
  }

  @Override
  public String toString() {
    return String.format("%d : \"%s\" -- Air Date: %s", epNum, title, airDate);
  }
}
