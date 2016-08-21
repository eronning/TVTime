package edu.brown.cs.eronning.friends;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * GroupHandler controls the groups page.
 *
 * @author eronning
 *
 */
public class GroupsHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * GroupsHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public GroupsHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "My Groups: ");
    serverData.setPage("groups");
    return new ModelAndView(variables, "groups.ftl");
  }
}
