package edu.brown.cs.eronning.home;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * LogoutHandler handles logout.
 *
 * @author eronning
 *
 */
public class LogoutHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * LogoutHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public LogoutHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    serverData.setCurrUser(null);
    serverData.serializeSave();
    Map<String, Object> variables =
      ImmutableMap.of("response", "empty response");
    return GSON.toJson(variables);
  }
}