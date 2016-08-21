package edu.brown.cs.eronning.shows;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * AddShowHandler deals with a user adding a show.
 *
 * @author eronning
 *
 */
public class AddShowHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery database;

  /**
   * AddShowHandler constructor.
   *
   * @param serverData
   *          the data from the server
   * @param database
   *          the tvtime database
   */
  public AddShowHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> variables =
      ImmutableMap.of("showAdded", "empty response");
      
      try {
        User user = serverData.getCurrUser();
        boolean showAdded =
            user.addShow(database.getShow(serverData.getShowId()));
        variables = ImmutableMap.of("showAdded", showAdded);
      } catch (UserNotLoggedInException e) {
        variables = ImmutableMap.of("error", "User not logged in.");
        System.out.println("ERROR: Cannot access page, user not logged in.");
      }

    return GSON.toJson(variables);
  }
}