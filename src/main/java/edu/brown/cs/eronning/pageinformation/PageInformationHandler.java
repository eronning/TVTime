package edu.brown.cs.eronning.pageinformation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * PageInformationHandler gives the information for any given page.
 *
 * @author eronning
 *
 */
public class PageInformationHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * PageInformationHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public PageInformationHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {

    Map<String, Object> variables = ImmutableMap.of("response", "no response");
    try {
      User currUser = serverData.getCurrUser();

      // TODO what is this weird friend thing doing here????
      User friend = new User("", "", "", "");
      Optional<User> friendOption =
        serverData.getUser(serverData.getFriendId());
      if (friendOption.isPresent()) {
        friend = friendOption.get();
        variables =
          ImmutableMap
            .<String, Object> builder()
          .put("encodedId", serverData.getEncodedUserId())
          .put("name", currUser.getName())
          .put("email", serverData.getUserId())
          .put("friendName", friend.getName())
          .put("friendEmail", serverData.getFriendId())
          .put("password", currUser.getPassword())
            .put("avatar", currUser.getAvatar())
          .put("recentlyWatched",
              currUser.sinceLastLogin(LocalDateTime.now()))
          .put("upcomingEvents", currUser.upcomingShows())
            // .put("availBlocks", Scheduler.getTimeBlocks(user.getCurrTime()))
          .put("page", serverData.getPage()).build();
      } else {
        variables =
          ImmutableMap
            .<String, Object> builder()
          .put("encodedId", serverData.getEncodedUserId())
          .put("name", currUser.getName())
          .put("email", serverData.getUserId())
          .put("password", currUser.getPassword())
          .put("avatar", currUser.getAvatar())
          // .put("availBlocks", Scheduler.getTimeBlocks(user.getCurrTime()))
          .put("recentlyWatched",
              currUser.sinceLastLogin(LocalDateTime.now()))
          .put("upcomingEvents", currUser.upcomingShows())
          .put("page", serverData.getPage()).build();
      }
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      variables = ImmutableMap.of("error", "User not logged in.");
    }

    return GSON.toJson(variables);
  }
}