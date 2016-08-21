package edu.brown.cs.eronning.shows;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * ShowHandler handles the show page.
 *
 * @author eronning
 */
public class ShowHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * ShowHandler constructor.
   *
   * @param serverData
   *          the data from the server
   */
  public ShowHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Show");
    try {
      serverData.setShowId(URLDecoder.decode(req.params(":showId"), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      System.out.println("problem decoding id");
    }
    serverData.setPage("show");
    return new ModelAndView(variables, "show.ftl");
  }
}
