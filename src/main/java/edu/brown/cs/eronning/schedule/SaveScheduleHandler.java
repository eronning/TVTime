package edu.brown.cs.eronning.schedule;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class SaveScheduleHandler implements Route {

  private ServerData serverData;
  private static final Gson GSON = new Gson();

  public SaveScheduleHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(Request req, Response res) {
    try {
      User currUser = serverData.getCurrUser();
      QueryParamsMap qm = req.queryMap();
      JsonParser parser = new JsonParser();
      JsonArray arr = parser.parse(qm.value("events")).getAsJsonArray();

      currUser.clearSchedule();

      for (int i = 0; i < arr.size(); i++) {
        String toArray =
          arr.get(i).toString().replaceAll("\\[", "").replaceAll("\\]", "");
        String[] e = toArray.split(",");
        String id = e[0];
        int startDay = Integer.parseInt(e[1]);
        int startHr =
          Integer.parseInt(e[2]) * 2 + (Integer.parseInt(e[3]) / 30);
        int endDay = Integer.parseInt(e[4]);
        int endHr = Integer.parseInt(e[5]) * 2 + (Integer.parseInt(e[6]) / 30);
        int startIndex = currUser.getIndex(startDay, startHr);
        int endIndex = currUser.getIndex(endDay, endHr) - 1;
        currUser.addShow(id, startIndex, endIndex);
      }

      return GSON.toJson(ImmutableMap.of("title", "emptyResponse"));
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    }
  }
}
