package edu.brown.cs.eronning.together;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class FriendsForShowHandler implements Route {
  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery database;

  /**
   * FriendsForShowHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public FriendsForShowHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(Request req, Response res) {
    QueryParamsMap qm = req.queryMap();
    String showId = qm.value("showId");
    Show show = database.getShow(showId);

    Optional<User> userOption = serverData.getUser(serverData.getUserId());
    JsonArray jsonFriends = new JsonArray();
    int currEpisodeNum = 1;
    if (userOption.isPresent()) {
      User user = userOption.get();
      Optional<Episode> episodeOption = user.getCurrEpisode(show);
      if (episodeOption.isPresent()) {
        Episode episode = episodeOption.get();
        currEpisodeNum = episode.getEpisodeNum();
      }
      HashSet<String> friends = user.getFriends();
      for (String friendId : friends) {
        Optional<User> friendOption = serverData.getUser(friendId);
        if (friendOption.isPresent()) {
          User friend = friendOption.get();
          Collection<Show> friendShows = friend.getShows();
          if (friendShows.contains(show)) {
            JsonObject jsonFriend = new JsonObject();
            jsonFriend.addProperty("name", friend.getName());
            jsonFriend.addProperty("email", friend.getEmail());
            jsonFriend.addProperty("avatar", friend.getAvatar());
            int episodeNum = 1;
            Optional<Episode> friendEpisodeOption = friend.getCurrEpisode(show);
            if (friendEpisodeOption.isPresent()) {
              Episode friendEpisode = friendEpisodeOption.get();
              episodeNum = friendEpisode.getEpisodeNum();
            }
            jsonFriend.addProperty("episodeNum", episodeNum);
            jsonFriends.add(jsonFriend);
          }
        }
      }
    }
    Map<String, Object> variables =
      ImmutableMap.of("friendsForShow", jsonFriends, "currEpisodeNum",
        currEpisodeNum);
    return GSON.toJson(variables);
  }
}
