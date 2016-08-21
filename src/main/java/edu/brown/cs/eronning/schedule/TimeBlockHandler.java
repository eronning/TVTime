package edu.brown.cs.eronning.schedule;

import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.denj.data.Show;
import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.TimeBlock;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;

public class TimeBlockHandler implements Route {

  private ServerData serverData;
  private static final Gson GSON = new Gson();

  public TimeBlockHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> response;
    try {
      User currUser = serverData.getCurrUser();
      List<TimeBlock> availTime = Scheduler
          .getTimeBlocks(currUser.getAvailableTime());
      Map<Show, TimeBlock> schedule = currUser.getSchedule();
      response = ImmutableMap.of("availBlocks", availTime,
                  "schedule", schedule,
                  "currAvailTb", Scheduler.getCurrAvailTime(schedule, currUser.getAvailableTime()));;
    } catch (UserNotLoggedInException e) {
      response = ImmutableMap.of("error", "User not logged in.");
      System.out.println("ERROR: Cannot access page, user not logged in.");
    }

    return GSON.toJson(response);
  }

}
