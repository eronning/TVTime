package edu.brown.cs.eronning.availability;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class SaveTimeHandler implements Route {

  private ServerData serverData;

  public SaveTimeHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    // TODO: This Class
    QueryParamsMap qm = req.queryMap();
    int numEvents = Integer.parseInt(qm.value("numEvents"));
    JsonParser parser = new JsonParser();
    JsonArray arr = parser.parse(qm.value("events")).getAsJsonArray();

    try {
      User currUser = serverData.getCurrUser();
      currUser.setAllFalse();
      for (int i = 0; i < numEvents; i++) {
        String toArray =
          arr.get(i).toString().replaceAll("\\[", "").replaceAll("\\]", "");
        String[] e = toArray.split(",");
        int startDay = Integer.parseInt(e[0]);
        int startHr =
          Integer.parseInt(e[1]) * 2 + (Integer.parseInt(e[2]) / 30);
        int endDay = Integer.parseInt(e[3]);
        int endHr = Integer.parseInt(e[4]) * 2 + (Integer.parseInt(e[5]) / 30);
        int startIndex = currUser.getIndex(startDay, startHr);
        int endIndex = currUser.getIndex(endDay, endHr);
        for (int j = startIndex; j < endIndex; j++) {
          currUser.setAvailableTime(j, true);
        }
      }
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      serverData.setPage("error");
      return new ModelAndView(ImmutableMap.of("title","TVTime: Error",
          "error", "User not logged in."), "error.ftl");
    }

    Map<String, Object> variables = ImmutableMap.of("title", "emptyResponse");
    serverData.setPage("availability");
    return new ModelAndView(variables, "availability.ftl");
  }
}