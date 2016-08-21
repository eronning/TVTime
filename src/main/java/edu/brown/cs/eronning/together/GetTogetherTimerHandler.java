package edu.brown.cs.eronning.together;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class GetTogetherTimerHandler implements TemplateViewRoute {
  private ServerData serverData;

  public GetTogetherTimerHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    List<User> usersToMatch = new ArrayList<User>();
    try {
      User currUser = serverData.getCurrUser();
      usersToMatch.add(currUser);
      
      QueryParamsMap qm = req.queryMap();
      String userString = qm.value("users");
      String[] userIDs = userString.split("; ");
      for (int i = 0; i < userIDs.length; i++) {
        Optional<User> diffUserOption = serverData.getUser(userIDs[i]);
        if (diffUserOption.isPresent()) {
          usersToMatch.add(diffUserOption.get());
        } else {
          System.out.println("ERROR: some selected user doesn't exist.");
        }
      }

      boolean[] sharedTime = Scheduler.getSharedTime(usersToMatch);
      for (int i = 0; i < sharedTime.length; i++) {
        if (sharedTime[i]) {
          Map<String, Object> variables =
            ImmutableMap.of("title", "Schedule Watching with Friends",
              "sharedTime", sharedTime);
          return new ModelAndView(variables, "availability.ftl");
        }
      }
      Map<String, Object> variables =
          ImmutableMap.of("title", "Schedule Watching with Friends", "error",
            "Oh no! Looks like there are no times where everyone is available!");
      return new ModelAndView(variables, "watchTogether.ftl");
      
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return new ModelAndView(ImmutableMap.of("title","TVTime: Error",
          "error", "User not logged in."), "error.ftl");
    }
  }
}