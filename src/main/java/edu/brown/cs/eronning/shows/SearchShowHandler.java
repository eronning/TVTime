package edu.brown.cs.eronning.shows;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * GatherShowHandler gathers information for one show.
 *
 * @author eronning
 *
 */
public class SearchShowHandler implements Route {

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
  public SearchShowHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    String input = qm.value("input");
    Map<String, Object> variables =
      ImmutableMap.of("response", "empty response");
    return GSON.toJson(variables);
  }
}