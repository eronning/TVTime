package edu.brown.cs.eronning.schedule;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.denj.data.Show;
import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.TimeBlock;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

public class ScheduleHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  public ScheduleHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> response;
    try {
      User user = serverData.getCurrUser();
      Map<Show, TimeBlock> results = Scheduler.schedule(user);
      response = ImmutableMap.of("size", results.size());
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      response = ImmutableMap.of("error", "User not logged in.");
    }
    return GSON.toJson(response);
  }

}