package edu.brown.cs.eronning.shows;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class HighlightsPageHandler implements TemplateViewRoute {
  private ServerData serverData;

  /**
   * HomeHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public HighlightsPageHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    try {
      String showID = req.params(":showId");
      String numString = req.params(":numBehind");
      int numToWatch = Integer.parseInt(numString) * -1;
      serverData.setHighlightsShowId(showID);
      serverData.setHighlightsNumEpsBehind(numToWatch);
      serverData.setPage("highlights");
      Map<String, Object> variables = ImmutableMap.of("title", "Highlights: ");
      return new ModelAndView(variables, "highlights.ftl");
    } catch (Exception e) {
      Map<String, Object> variables = ImmutableMap.of("title", "Highlights: ");
      return new ModelAndView(variables, "highlights.ftl");
    }

  }
}
