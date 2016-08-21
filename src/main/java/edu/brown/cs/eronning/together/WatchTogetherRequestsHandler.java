package edu.brown.cs.eronning.together;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.Group;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.TimeBlock;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import edu.brown.cs.eronning.tvtime.WatchRequest;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class WatchTogetherRequestsHandler implements Route {
  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery database;

  /**
   * WatchTogetherRequestsHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   * @param database
   *          is the database of shows
   */
  public WatchTogetherRequestsHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(Request req, Response res) {
    // try {
    // User currUser = serverData.getCurrUser();
    QueryParamsMap qm = req.queryMap();
    String showId = qm.value("showId");
    String timeString = qm.value("time");
    Show show = database.getShow(showId);
    JsonParser parser = new JsonParser();
    JsonArray friendGroups =
      parser.parse(qm.value("friendGroups")).getAsJsonArray();

    try {
      User currUser = serverData.getCurrUser();

      if (!timeString.isEmpty()) {
        JsonArray time = parser.parse(qm.value("time")).getAsJsonArray();
        int startDay = Integer.parseInt(time.get(0).toString());
        int startHr =
          Integer.parseInt(time.get(1).toString()) * 2
          + Integer.parseInt(time.get(2).toString()) / 30;
        int endDay = Integer.parseInt(time.get(3).toString());
        int endHr =
          Integer.parseInt(time.get(4).toString()) * 2
          + Integer.parseInt(time.get(5).toString()) / 30;
        int startIndex = currUser.getIndex(startDay, startHr);
        int endIndex = currUser.getIndex(endDay, endHr) - 1;
        TimeBlock tb = new TimeBlock(startIndex, endIndex);
        tb.setID(show.getId());
        Group myGroup = new Group(currUser, show);
        myGroup.chooseTime(tb);
        // make the group
        for (int i = 0; i < friendGroups.size(); i++) {
          String memberEmail =
            friendGroups.get(i).toString().replaceAll("\"", "");
          Optional<User> memberOption = serverData.getUser(memberEmail);
          if (memberOption.isPresent()) {
            User member = memberOption.get();
            myGroup.addUser(member);
          }
        }
        // send all of the invites
        for (int i = 0; i < friendGroups.size(); i++) {
          String memberEmail =
            friendGroups.get(i).toString().replaceAll("\"", "");
          Optional<User> memberOption = serverData.getUser(memberEmail);
          if (memberOption.isPresent()) {
            User member = memberOption.get();
            WatchRequest request =
              new WatchRequest(currUser, member, myGroup, LocalDateTime.now());
            member.invited(request);
          }
        }
        if (myGroup.getLeader().equals(currUser)) {
          WatchRequest request =
            new WatchRequest(currUser, currUser, myGroup, LocalDateTime.now());
          Preconditions.checkArgument(request.tryAccept(true));
          // assert(request.tryAccept(true));
        }

      } else {
        System.out.println("options");
        for (int i = 0; i < friendGroups.size(); i++) {
          JsonArray group = friendGroups.get(i).getAsJsonArray();
          Group myGroup = new Group(currUser, show);
          for (int j = 0; j < group.size(); j++) {
            JsonObject jsonMember = group.get(j).getAsJsonObject();
            String email =
              jsonMember.get("email").toString().replaceAll("\"", "");
            int sIndex = Integer.parseInt(jsonMember.get("sIndex").toString());
            int eIndex = Integer.parseInt(jsonMember.get("eIndex").toString());
            TimeBlock tb = new TimeBlock(sIndex, eIndex);
            tb.setID(show.getId());
            myGroup.chooseTime(tb);
            Optional<User> member = serverData.getUser(email);
            if (member.isPresent()) {
              myGroup.addUser(member.get());
            }
          }
          // send all of the invites
          for (int k = 0; k < group.size(); k++) {
            JsonObject jsonMember = group.get(k).getAsJsonObject();
            String email =
              jsonMember.get("email").toString().replaceAll("\"", "");
            Optional<User> memberOption = serverData.getUser(email);
            if (memberOption.isPresent()) {
              User member = memberOption.get();
              System.out.println(member.getName());
              if (member.getEmail().equals(currUser.getEmail())) {
                WatchRequest request =
                  new WatchRequest(currUser, currUser, myGroup,
                    LocalDateTime.now());
                Preconditions.checkArgument(request.tryAccept(true));
              } else {
                WatchRequest request =
                  new WatchRequest(currUser, member, myGroup,
                    LocalDateTime.now());
                member.invited(request);
              }
            }
          }
        }
      }

    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: the user is not logged in");
    }

    // Group myGroup = new Group(currUser, show);
    // myGroup.chooseTime(tb);
    // // make the group
    // for (int i = 0; i < group.size(); i++) {
    // String memberEmail = group.get(i).toString().replaceAll("\"", "");
    // Optional<User> memberOption = serverData.getUser(memberEmail);
    // if (memberOption.isPresent()) {
    // User member = memberOption.get();
    // myGroup.addUser(member);
    // }
    // }
    // // send all of the invites

    //
    // } catch (UserNotLoggedInException e) {
    // System.out.println("ERROR: the user is not logged in");
    // }

    Map<String, Object> variables = ImmutableMap.of("friendsForShow", "");
    return GSON.toJson(variables);
  }
}
