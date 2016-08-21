package edu.brown.cs.eronning.shows;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ShowPageHandler class.
 *
 * @author eronning
 *
 */
public class ShowPageHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * ShowPageHandler constructor.
   *
   * @param serverData
   *          the data from the server
   */
  public ShowPageHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> variables =
      ImmutableMap.of("encodedId", serverData.getEncodedUserId());
    return GSON.toJson(variables);
  }
}