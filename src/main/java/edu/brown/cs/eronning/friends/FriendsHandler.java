package edu.brown.cs.eronning.friends;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * FriendsHandler controls the friends page.
 *
 * @author eronning
 *
 */
public class FriendsHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * FriendsHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public FriendsHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Friends: ");
    serverData.setPage("friends");
    return new ModelAndView(variables, "friends.ftl");
  }
}