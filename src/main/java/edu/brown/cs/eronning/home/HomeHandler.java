package edu.brown.cs.eronning.home;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * HomeHandler controls the home page.
 * 
 * @author eronning
 *
 */
public class HomeHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * HomeHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public HomeHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    serverData.setPage("home");
      Map<String, Object> variables = ImmutableMap.of("title", "Home: ");
      return new ModelAndView(variables, "home.ftl");
  }
}