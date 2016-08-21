package edu.brown.cs.eronning.schedule;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class ChangeWatchedHandler implements TemplateViewRoute {  
  
  private ServerData serverData;

  public ChangeWatchedHandler(ServerData serverData) {
    this.serverData = serverData;
  }
  
  @Override
  public ModelAndView handle(Request req, Response res) {
    try {
      Map<String, Object> variables;
      serverData.getCurrUser();
      variables = ImmutableMap.of("title", "Change What You Watched");
      return new ModelAndView(variables, "changeWatched.ftl");
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return new ModelAndView(ImmutableMap.of("title","TVTime: Error",
          "error", "User not logged in."), "error.ftl");
    }
  }
}