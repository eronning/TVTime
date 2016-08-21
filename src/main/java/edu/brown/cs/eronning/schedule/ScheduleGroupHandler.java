package edu.brown.cs.eronning.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.TimeBlock;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class ScheduleGroupHandler implements Route {

  private ServerData serverData;
  private static final Gson GSON = new Gson();

  public ScheduleGroupHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  public JsonArray getPartitions(List<User> userList, int runtime) {
    JsonArray partitions = new JsonArray();
    JsonArray updatedPartitions = new JsonArray();
    try {
      User currUser = serverData.getCurrUser();
      Set<Set<Set<User>>> top3 =
        GroupGenerator.suggestGroups(userList, runtime);
      Iterator<Set<Set<User>>> iter = top3.iterator();

      while (iter.hasNext()) {
        JsonArray groups = new JsonArray();

        Set<Set<User>> set = iter.next();
        Iterator<Set<User>> iterSet = set.iterator();
        while (iterSet.hasNext()) {
          Set<User> userSet = iterSet.next();
          Map<Integer, List<TimeBlock>> map =
            Scheduler.getCommonTimeblocks(new ArrayList<User>(userSet));
          List<TimeBlock> list = map.get(userSet.size());
          int startIndex = -1;
          int endIndex = -1;
          if (list.size() != 0) {
            list.sort(new Scheduler.tbComparator());
            TimeBlock tb = list.get(0);
            startIndex = tb.getStartIndex();
            int blocksNeeded = (int) Math.ceil(runtime / 30.0);
            endIndex = startIndex + blocksNeeded - 1;
          }
          Iterator<User> iterUser = userSet.iterator();
          JsonArray jsonGroup = new JsonArray();
          while (iterUser.hasNext()) {
            User user = iterUser.next();
            JsonObject jsonUser = new JsonObject();
            jsonUser.addProperty("name", user.getName());
            jsonUser.addProperty("email", user.getEmail());
            jsonUser.addProperty("sIndex", startIndex);
            jsonUser.addProperty("eIndex", endIndex);
            jsonGroup.add(jsonUser);
          }
          groups.add(jsonGroup);
        }
        partitions.add(groups);
      }
      for (int i = 0; i < partitions.size(); i++) {
        JsonArray pairing = partitions.get(i).getAsJsonArray();
        for (int j = 0; j < pairing.size(); j++) {
          JsonArray group = pairing.get(j).getAsJsonArray();
          JsonObject user = group.get(0).getAsJsonObject();
          String userName = user.get("name").toString().replaceAll("\"", "");
          if (updatedPartitions.contains(pairing)
            || (userName.equals(currUser.getName()) && group.size() == 1)) {
            break;
          }
          for (int k = 0; k < pairing.size(); k++) {
            JsonArray groupSearch = (JsonArray) pairing.get(k);
            JsonObject userSearch = (JsonObject) groupSearch.get(0);
            String userSearchName =
              userSearch.get("name").toString().replaceAll("\"", "");
            if (groupSearch.size() == 1
              && userSearchName.equals(currUser.getName())) {
              break;
            }
          }
          updatedPartitions.add(pairing);
        }

      }
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: the user is not logged in");
    }
    return updatedPartitions;
  }

  @Override
  public Object handle(Request req, Response res) {
    QueryParamsMap qm = req.queryMap();
    int runtime = Integer.parseInt(qm.value("runtime"));

    JsonParser parser = new JsonParser();
    JsonArray arr = parser.parse(qm.value("users")).getAsJsonArray();

    List<User> userList = new ArrayList<User>();
    for (int i = 0; i < arr.size(); i++) {
      String userEmail = arr.get(i).toString().replaceAll("\"", "");
      Optional<User> ou = serverData.getUser(userEmail);
      if (ou.isPresent()) {
        userList.add(ou.get());
      } else {
        System.err.println("Error: no user to find");
      }
    }

    Map<Integer, List<TimeBlock>> commonTB =
      Scheduler.getCommonTimeblocks(userList);
    List<TimeBlock> list = commonTB.get(userList.size());

    if (list.size() == 0) {
      // No Common Available Time!
      JsonArray partitions = getPartitions(userList, runtime);
      Map<String, Object> response =
        ImmutableMap.of("status", "1", "partitions", partitions);
      return GSON.toJson(response);
    } else {

      list.sort(new Scheduler.tbComparator());
      int blocksNeeded = (int) Math.ceil(runtime / 30.0);
      TimeBlock toReturn;

      if (list.get(0).getSize() > blocksNeeded) {
        toReturn =
          new TimeBlock(list.get(0).getStartIndex(), list.get(0)
            .getStartIndex() + blocksNeeded - 1);
      } else if (list.get(0).getSize() == blocksNeeded) {
        toReturn = list.get(0);
      } else {
        // No Common Timeblock big enough
        JsonArray partitions = getPartitions(userList, runtime);
        Map<String, Object> response =
          ImmutableMap.of("status", "1", "partitions", partitions);
        return GSON.toJson(response);
      }

      // Success
      Map<String, Object> response =
        ImmutableMap.of("status", "0", "tb", toReturn);
      return GSON.toJson(response);
    }

  }

}
