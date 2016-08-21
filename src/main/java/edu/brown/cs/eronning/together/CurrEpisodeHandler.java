package edu.brown.cs.eronning.together;

import java.util.Optional;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;

public class CurrEpisodeHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery tvDatabase;

  public CurrEpisodeHandler(ServerData serverData, TVTQuery tvDatabase) {
    this.serverData = serverData;
    this.tvDatabase = tvDatabase;
  }

  @Override
  public Object handle(Request req, Response res) {
    QueryParamsMap qm = req.queryMap();
    String id = qm.value("showID");
    Show currShow = tvDatabase.getShow(id);

    try {
      Optional<Episode> ep = serverData.getCurrUser().getCurrEpisode(currShow);
      JsonObject toReturn = new JsonObject();
      if (ep.isPresent()) {
        int epNum = ep.get().getEpisodeNum();
        toReturn.addProperty("epNum", epNum);
        return toReturn;
      }
      toReturn.addProperty("epNum", 1);
      return toReturn;
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    }
    // TODO Auto-generated method stub
    //return null;
  }

}
