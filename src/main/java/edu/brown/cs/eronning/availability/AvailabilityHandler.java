package edu.brown.cs.eronning.availability;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class AvailabilityHandler implements TemplateViewRoute {

  private ServerData serverData;

  public AvailabilityHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Availability: ");
    serverData.setPage("availability");
    return new ModelAndView(variables, "availability.ftl");
  }
}