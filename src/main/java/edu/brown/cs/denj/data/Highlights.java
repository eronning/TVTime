package edu.brown.cs.denj.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.base.Preconditions;

/**
 * Decides which episodes are skippable by some metric TBD. Having to do with
 * the average rating of all of a show's episodes.
 */
public class Highlights {

  /**
   * Analyze a show. Mark all episodes as skippable or not skippable.
   *
   * @param show
   *          the show to analyze.
   * @return a mapping of episodes to booleans. True if the episode is
   *         skippable, false otherwise.
   */
  public static List<Episode> getLowest(Show show, int startEp, int endEp,
    int num2Skip) {
    // Map<Episode, Double> ratingsMap = new HashMap<Episode, Double>();
    PriorityQueue<Episode> lowestQueue =
      new PriorityQueue<Episode>(new RatingComparator());

    for (int i = startEp; i <= endEp; i++) {
      Episode e = show.getEp(i);
      double rating = e.getRating();
      if (rating > 0) {
        lowestQueue.add(e);
      }

    }

    List<Episode> toReturn = new ArrayList<Episode>();
    for (int i = 0; i < num2Skip; i++) {
      if (lowestQueue.isEmpty()) {
        break;
      } else {
        toReturn.add(lowestQueue.remove());
      }
    }
    // toReturn.sort(new ChronologicalComparator());
    return toReturn;
  }

  // public static Set<Episode> skippable(Show show, int startEp, int endEp) {
  // return skippable(show, startEp, endEp, .75);
  // }

  /**
   * Determines skippable episodes of a show between two given episode numbers,
   * inclusive.
   *
   * @param show
   *          the show being analyzed
   * @param startEp
   *          the start episode number
   * @param endEp
   *          the end episode number
   * @param numDevs
   *          number of standard deviations below average an episode must be to
   *          be marked as skippable
   * @return the set of all episodes at least one standard deviation below the
   *         average episode rating of the show.
   */
  public static List<Episode> skippable(Show show, int startEp, int endEp,
    double numDevs) {
    Preconditions.checkNotNull(show);
    Preconditions.checkElementIndex(startEp, show.getNumEpsides());
    Preconditions.checkElementIndex(endEp, show.getNumEpsides());
    Preconditions.checkArgument(startEp < endEp);
    List<Episode> toCheck = new ArrayList<Episode>();
    List<Episode> toReturn = new ArrayList<Episode>();

    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (int i = startEp; i <= endEp; i++) {
      Episode e = show.getEp(i);
      double rating = e.getRating();
      if (rating > 0) {
        stats.addValue(rating);
        toCheck.add(e);
      }

    }
    System.out.println("Checking " + toCheck.size());
    double avg = stats.getMean();
    double stdDev = stats.getStandardDeviation();
    for (Episode e : toCheck) {
      double rating = e.getRating();
      if (rating < avg && avg - rating > stdDev * numDevs) {
        toReturn.add(e);
      }
    }

    // double avgRating = show.getAvgRating();
    return toReturn;
  }

  public static void main(String[] args) {
    XMLParser parser = new XMLParser();
    Show thrones = null;
    try {
      List<Show> top = parser.loadTop();
      for (Show s : top) {
        if (s.getId().equals("24493")) {
          thrones = s;
          break;
        }
      }
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // Show thrones = parser.parseShow("24493");
    // System.out.println(thrones);
    // Set<Episode> skippable = Highlights.skippable(thrones, 1, 10, -20);
    // Set<Episode> skippable2 = Highlights.skippable(thrones, 1, 10, 0);
    // Set<Episode> skippable3 = Highlights.skippable(thrones, 1, 10, .5);
    //
    // List<Episode> lowestRatings = Highlights.getLowest(thrones, 1, 10, 10);
    //
    // System.out.println(skippable.size());
    // System.out.println(skippable2.size());
    // System.out.println(skippable3.size());
    //
    // System.out.println(lowestRatings.size());
    // System.out.println(lowestRatings);

  }

  /**
   * Compares episodes by their ratings.
   */
  private static class RatingComparator implements Comparator<Episode> {

    @Override
    public int compare(Episode o1, Episode o2) {
      return Double.compare(o1.getRating(), o2.getRating());
    }
  }

  /**
   * Compares episodes by their ratings.
   */
  private static class ChronologicalComparator implements Comparator<Episode> {

    @Override
    public int compare(Episode o1, Episode o2) {
      return Double.compare(o1.getEpisodeNum(), o2.getEpisodeNum());
    }
  }
}
