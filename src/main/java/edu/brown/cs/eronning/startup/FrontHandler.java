package edu.brown.cs.eronning.startup;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * FrontHandler handles the first page.
 *
 * @author eronning
 */
public class FrontHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * FrontHandler constructor.
   *
   * @param serverData
   *          data from the server
   */
  public FrontHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "TVTime");
    serverData.setPage("startup");
    return new ModelAndView(variables, "startup.ftl");
  }
}