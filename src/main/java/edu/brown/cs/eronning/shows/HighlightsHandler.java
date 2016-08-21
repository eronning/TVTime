package edu.brown.cs.eronning.shows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Highlights;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles accessing the highlights class to determine low rated episodes to
 * skip.
 */
public class HighlightsHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery database;

  public HighlightsHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(Request req, Response res) {
    try {
      User currUser = serverData.getCurrUser();
      String showID = serverData.getHighlightsShowId();
      System.out.println();
      int numToWatch = serverData.getHighlightsNumEpsBehind();
      serverData.setHighlightsNumEpsBehind(-1);
      Show show = database.getShow(showID);
      Optional<Episode> opt = currUser.getCurrEpisode(show);
      int currEp = 1;
      if (opt.isPresent()) {
        currEp = opt.get().getEpisodeNum();
      }
      // int currEp = currUser.getCurrEpisode(show).get().getEpisodeNum();
      // //TODO check

      List<Episode> epsInInterval = new ArrayList<Episode>();
      for (int i = currEp; i < currEp + numToWatch; i++) {
        epsInInterval.add(show.getEp(i));
      }
      // Getting the lowest half
      // List<Episode> skip = Highlights.getLowest(show, currEp, numToWatch +
      // currEp,
      // numToWatch/2);
      List<Episode> skip =
        Highlights.skippable(show, currEp, numToWatch + currEp, 0.75);
      for (Episode e : skip) {
        epsInInterval.remove(e);
      }

      return GSON.toJson(ImmutableMap.of("watch", epsInInterval, "skip", skip));

    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    } catch (Exception e) {
      e.printStackTrace();
      return GSON.toJson(ImmutableMap.of("error", "cry."));
    }
  }

}
