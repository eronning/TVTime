package edu.brown.cs.eronning.friends;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * FriendHandler handles the friend page.
 *
 * @author eronning
 */
public class FriendHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * FriendHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public FriendHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Friend");
    serverData.setPage("friend");
    return new ModelAndView(variables, "friend.ftl");
  }
}