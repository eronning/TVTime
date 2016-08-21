package edu.brown.cs.eronning.shows;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateEpisodeHandler implements Route {
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
  public UpdateEpisodeHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    String showId = qm.value("showId");
    String episodeNumString = qm.value("episodeNum");
    try {
      int episodeNum = Integer.parseInt(episodeNumString);
      Optional<User> userOption = serverData.getUser(serverData.getUserId());
      if (userOption.isPresent()) {
        User user = userOption.get();
        Show show = database.getShow(showId);
        user.updateCurrEpisode(show, episodeNum);
      } else {
        System.out.println("ERROR: the user isn't present in update episode");
      }
    } catch (NumberFormatException e) {
      System.out
        .println("ERROR: the episode number was not valid in update episde");
    }

    Map<String, Object> variables;
    variables = ImmutableMap.of("response", "");
    return GSON.toJson(variables);
  }
}
