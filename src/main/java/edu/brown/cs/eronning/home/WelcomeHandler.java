package edu.brown.cs.eronning.home;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class WelcomeHandler implements TemplateViewRoute {
  private ServerData serverData;
  
  public WelcomeHandler(ServerData serverData) {
    this.serverData = serverData;
  }
  
  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Welcome to TVTime");
    serverData.setPage("welcome");
    return new ModelAndView(variables, "welcome.ftl");
  }
}