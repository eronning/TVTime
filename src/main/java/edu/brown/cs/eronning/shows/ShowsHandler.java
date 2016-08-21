package edu.brown.cs.eronning.shows;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * ShowsHandler handles the shows page.
 *
 * @author eronninghome/jzhong595%40gmail.com
 */
public class ShowsHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * ShowsHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public ShowsHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Shows: ");
    serverData.setPage("shows");
    return new ModelAndView(variables, "shows.ftl");
  }
}