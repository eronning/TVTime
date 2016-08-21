package edu.brown.cs.eronning.schedule;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class SchedulePageHandler implements TemplateViewRoute {

  private ServerData serverData;

  public SchedulePageHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Scheduler: ");
    serverData.setPage("schedule");
    return new ModelAndView(variables, "schedule.ftl");
  }
}