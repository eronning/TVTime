package edu.brown.cs.eronning.shows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * GatherShowsHandler gets the shows for the my shows page.
 *
 * @author eronning
 *
 */
public class GatherShowsHandler implements Route {

  private static final Gson GSON = new Gson();
  private TVTQuery database;
  private ServerData serverData;

  /**
   * GatherShowsHandler constructor.
   *
   * @param database
   *          of tv shows
   */
  public GatherShowsHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> variables;
    List<Show> shows = database.topShows();
    List<String> myShows = new ArrayList<String>();
    List<String> topShows = new ArrayList<String>();
    List<String> dramaShows = new ArrayList<String>();
    List<String> comedyShows = new ArrayList<String>();
    List<String> actionShows = new ArrayList<String>();
    List<String> crimeShows = new ArrayList<String>();
    List<String> thrillerShows = new ArrayList<String>();

    try {
      User currUser = serverData.getCurrUser();
      Collection<Show> myShowData = currUser.getShows();
      for (Show show : myShowData) {
        myShows.add(show.getImage());
      }
      JsonObject showsToSend = new JsonObject();
      for (Show show : shows) {
        String showImage = show.getImage();
        // arranges the shows by genre
        for (String genre : show.getGenres()) {
          if (genre.equals("Drama")) {
            dramaShows.add(showImage);
          }
          if (genre.equals("Comedy")) {
            comedyShows.add(showImage);
          }
          if (genre.equals("Action")) {
            actionShows.add(showImage);
          }
          if (genre.equals("Crime")) {
            crimeShows.add(showImage);
          }
          if (genre.equals("Thriller")) {
            thrillerShows.add(showImage);
          }
        }
        if (showImage != null
          && !showImage.equals("http://images.tvrage.com/shows/21/20475.jpg")) {
          // adds a show for with its information
          JsonObject responseJson = new JsonObject();
          responseJson.addProperty("id", show.getId());
          responseJson.addProperty("name", show.getName());
          responseJson.addProperty("image", showImage);
          responseJson.addProperty("runtime", show.getRuntime());
          responseJson.addProperty("seasons", show.getNumSeasons());
          responseJson.addProperty("genres", show.getGenres().toString());
          topShows.add(showImage);
          showsToSend.add(showImage, responseJson);
        }
      }
      // shuffles shows so the order isn't always the same
      Collections.shuffle(comedyShows);
      Collections.shuffle(dramaShows);
      Collections.shuffle(actionShows);
      Collections.shuffle(crimeShows);
      Collections.shuffle(thrillerShows);
      variables =
        ImmutableMap.<String, Object> builder().put("myShows", myShows)
          .put("topShows", topShows).put("dramaShows", dramaShows)
          .put("comedyShows", comedyShows).put("actionShows", actionShows)
          .put("crimeShows", crimeShows).put("thrillerShows", thrillerShows)
          .put("shows", showsToSend).build();
    
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      variables = ImmutableMap.of("error", "User not logged in.");
    }
    return GSON.toJson(variables);
  }
}