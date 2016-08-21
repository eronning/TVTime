package edu.brown.cs.eronning.home;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.eronning.tvtime.FriendRequest;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import edu.brown.cs.eronning.tvtime.UserRequest;
import edu.brown.cs.eronning.tvtime.WatchRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class NotificationsHandler implements Route {

  private ServerData serverData;
  private static final Gson GSON = new Gson();
  private User currUser;

  public NotificationsHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> variables;

    try {
      currUser = serverData.getCurrUser();
      List<UserRequest> notifications = currUser.getNotifications();
      System.out.println(notifications.size());
      JsonArray jNotifications = new JsonArray();
      for (UserRequest n : notifications) {
        JsonObject jNotification = new JsonObject();

        User sender = n.getSender();
        jNotification.addProperty("senderName", sender.getName());
        jNotification.addProperty("senderID", sender.getEmail());
        User recipient = n.getRecipient();
        jNotification.addProperty("recipientName", recipient.getName());
        jNotification.addProperty("recipientID", recipient.getEmail());
        System.out.println(n.isAccepted());
        if (n.isAccepted()) {
          jNotification.addProperty("isAcceptance", true);
          if (n instanceof FriendRequest) {
            jNotification.addProperty("isFriendReq", true);
          } else {
            assert (n instanceof WatchRequest);
            jNotification.addProperty("isFriendReq", false);
            jNotification.addProperty("showName", ((WatchRequest) n).getShow()
              .getName());
            jNotification.addProperty("showID", ((WatchRequest) n).getShow()
              .getId());
            LocalDateTime time =
              ((WatchRequest) n).getGroup().getTime()
                .nextStart(LocalDateTime.now());
            DateTimeFormatter formatter =
              DateTimeFormatter.ofPattern("EEEE, h:mm a");
            jNotification.addProperty("watchTime", formatter.format(time));
            List<String> groupMembers = new ArrayList<>();
            ((WatchRequest) n).getGroup().getMembers()
              .forEach((mem) -> groupMembers.add(mem.getName()));
            jNotification.addProperty("groupMembers",
              stringifyMembers(groupMembers));
          }
        } else {
          jNotification.addProperty("isAcceptance", false);
          if (n instanceof FriendRequest) {
            jNotification.addProperty("isFriendReq", true);
          } else {
            assert (n instanceof WatchRequest);
            jNotification.addProperty("isFriendReq", false);
            jNotification.addProperty("showName", ((WatchRequest) n).getShow()
              .getName());
            jNotification.addProperty("showID", ((WatchRequest) n).getShow()
              .getId());
            LocalDateTime time =
              ((WatchRequest) n).getGroup().getTime()
                .nextStart(LocalDateTime.now());
            DateTimeFormatter formatter =
              DateTimeFormatter.ofPattern("EEEE, h:mm a");
            jNotification.addProperty("watchTime", formatter.format(time));
            List<String> groupMembers = new ArrayList<>();
            ((WatchRequest) n).getGroup().getMembers()
              .forEach((mem) -> groupMembers.add(mem.getName()));
            jNotification.addProperty("groupMembers",
              stringifyMembers(groupMembers));
          }
        }
        jNotifications.add(jNotification);
      }
      variables = ImmutableMap.of("notifications", jNotifications);
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      variables = ImmutableMap.of("error", "User not logged in.");
    } catch (Exception e) {
      e.printStackTrace();
      variables = ImmutableMap.of("error", "Omg.");
    }

    return GSON.toJson(variables);
  }

  private String stringifyMembers(List<String> members)
    throws UserNotLoggedInException {
    int originalNumMem = members.size();
    String res = ". The watch group consists of: ";
    for (int i = 0; i < originalNumMem; i++) {
      res += members.get(i);
      res += ", ";
    }
    return res.substring(0, res.length() - 1) + ".";
    // members.remove(serverData.getCurrUser().getName());
    // if (members.size() == 0) {
    // return res + "!";
    // } else {
    // assert (members.size() >= 1);
    // while (!members.isEmpty()) {
    // if (members.size() != 1) {
    // res += (", " + members.get(0));
    // } else if (originalNumMem == 2) {
    // res += (" and " + members.get(0));
    // } else {
    // res += (", and " + members.get(0));
    // }
    // members.remove(0);
    // }
    // return res + "!";
    // }
  }
}