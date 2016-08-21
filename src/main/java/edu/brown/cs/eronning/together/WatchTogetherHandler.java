package edu.brown.cs.eronning.together;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class WatchTogetherHandler implements TemplateViewRoute {

  private ServerData serverData;

  public WatchTogetherHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    try {
      User currUser = serverData.getCurrUser();
      Map<String, Object> variables;
      String possibleShowName = req.params(":showName");
      if (possibleShowName != null) {
        String showName = URLDecoder.decode(possibleShowName, "UTF-8");
        variables =
          ImmutableMap
            .of("title", "Schedule Watching with Friends", "myAvailTime",
              currUser.getAvailableTime(), "showToWatch", showName);
      } else {
        variables =
          ImmutableMap.of("title", "Schedule Watching with Friends",
            "myAvailTime", currUser.getAvailableTime(), "showToWatch", "");
      }
      return new ModelAndView(variables, "watchTogether.ftl");
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return new ModelAndView(ImmutableMap.of("title","TVTime: Error",
          "error", "User not logged in."), "error.ftl");
    } catch (UnsupportedEncodingException e) {
      System.out.println("ERROR: Cannot access page, show name could not be decoded.");
      return new ModelAndView(ImmutableMap.of("title","TVTime: Error",
          "error", "That's not a valid page! Please try again."), "error.ftl");
    }
  }
}
