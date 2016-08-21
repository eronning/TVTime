package edu.brown.cs.eronning.shows;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Season;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * GatherShowHandler gathers information for one show.
 *
 * @author eronning
 *
 */
public class GatherShowHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery database;

  /**
   * GatherShowHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   * @param database
   *          of shows
   */
  public GatherShowHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> variables;
    Show show = database.getShow(serverData.getShowId());
    boolean hasShow = false;

    try {
      User currUser = serverData.getCurrUser();
      Collection<Show> userShows = currUser.getShows();
      for (Show s : userShows) {
        if (s.getId().equals(show.getId())) {
          hasShow = true;
          break;
        }
      }

      int numSeasons = show.getNumSeasons();
      List<Season> seasons = show.getAllSeasons();
      JsonArray showSeasons = new JsonArray();
      JsonArray showEpisodes = new JsonArray();
      for (Season s : seasons) {
        JsonArray season = new JsonArray();
        for (Episode ep : s.getEpisodes()) {
          JsonObject episode = new JsonObject();
          episode.addProperty("title", ep.getTitle());
          episode.addProperty("seasonNum", ep.getSeasonNum());
          episode.addProperty("episodeNum", ep.getEpisodeNum());
          episode.addProperty("image", ep.getImage());
          episode.addProperty("url", ep.getEpURL());
          episode.addProperty("hasAired", ep.hasAired());
          episode.addProperty("airDate", ep.getAirDate());
          episode.addProperty("description", ep.getDescription());
          season.add(episode);
          showEpisodes.add(episode);
        }
        showSeasons.add(season);
      }
      Optional<Episode> currEpisodeOption = currUser.getCurrEpisode(show);
      int currEpisodeNum = 0;
      int currSeasonNum = 0;
      if (currEpisodeOption.isPresent()) {
        Episode currEpisode = currEpisodeOption.get();
        currEpisodeNum = currEpisode.getEpisodeNum();
        currSeasonNum = currEpisode.getSeasonNum();
      }
      variables =
        ImmutableMap.<String, Object> builder()
        .put("currEpisodeNum", currEpisodeNum)
        .put("currSeasonNum", currSeasonNum).put("hasShow", hasShow)
        .put("showDescription", show.getDescription())
          .put("id", show.getId()).put("name", show.getName())
          .put("runtime", show.getRuntime()).put("isEnded", show.isEnded())
          .put("image", show.getImage()).put("numSeasons", numSeasons)
          .put("episodes", showEpisodes).put("seasons", showSeasons).build();
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      variables = ImmutableMap.of("error", "User not logged in.");
    }
    return GSON.toJson(variables);
  }
}