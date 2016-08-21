package edu.brown.cs.eronning.interactions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.account.AccountAvatarHandler;
import edu.brown.cs.eronning.account.AccountHandler;
import edu.brown.cs.eronning.account.AccountSettingsHandler;
import edu.brown.cs.eronning.availability.AvailabilityHandler;
import edu.brown.cs.eronning.availability.SaveTimeHandler;
import edu.brown.cs.eronning.friends.AddFriendHandler;
import edu.brown.cs.eronning.friends.FriendHandler;
import edu.brown.cs.eronning.friends.FriendsHandler;
import edu.brown.cs.eronning.friends.GatherFriendHandler;
import edu.brown.cs.eronning.friends.GroupsHandler;
import edu.brown.cs.eronning.friends.NavFriendHandler;
import edu.brown.cs.eronning.friends.PopulateFriendsHandler;
import edu.brown.cs.eronning.friends.RejectFriendHandler;
import edu.brown.cs.eronning.friends.RemoveFriendHandler;
import edu.brown.cs.eronning.friends.RequestFriendHandler;
import edu.brown.cs.eronning.friends.SearchFriendsHandler;
import edu.brown.cs.eronning.home.HomeHandler;
import edu.brown.cs.eronning.home.LogoutHandler;
import edu.brown.cs.eronning.home.NotificationsHandler;
import edu.brown.cs.eronning.home.WelcomeHandler;
import edu.brown.cs.eronning.pageinformation.PageInformationHandler;
import edu.brown.cs.eronning.schedule.ChangeWatchedHandler;
import edu.brown.cs.eronning.schedule.SaveScheduleHandler;
import edu.brown.cs.eronning.schedule.ScheduleGroupHandler;
import edu.brown.cs.eronning.schedule.ScheduleHandler;
import edu.brown.cs.eronning.schedule.SchedulePageHandler;
import edu.brown.cs.eronning.schedule.TimeBlockHandler;
import edu.brown.cs.eronning.schedule.UpdateCalHandler;
import edu.brown.cs.eronning.shows.AddShowHandler;
import edu.brown.cs.eronning.shows.GatherShowHandler;
import edu.brown.cs.eronning.shows.GatherShowsHandler;
import edu.brown.cs.eronning.shows.HighlightsHandler;
import edu.brown.cs.eronning.shows.HighlightsPageHandler;
import edu.brown.cs.eronning.shows.RemoveShowHandler;
import edu.brown.cs.eronning.shows.ShowHandler;
import edu.brown.cs.eronning.shows.ShowPageHandler;
import edu.brown.cs.eronning.shows.ShowsHandler;
import edu.brown.cs.eronning.shows.UpdateEpisodeHandler;
import edu.brown.cs.eronning.startup.FrontHandler;
import edu.brown.cs.eronning.startup.GetRecentlyWatchedHandler;
import edu.brown.cs.eronning.startup.LoginHandler;
import edu.brown.cs.eronning.startup.SignUpHandler;
import edu.brown.cs.eronning.startup.SignUpResultsHandler;
import edu.brown.cs.eronning.together.AcceptWatchRequestHandler;
import edu.brown.cs.eronning.together.CurrEpisodeHandler;
import edu.brown.cs.eronning.together.FriendsForShowHandler;
import edu.brown.cs.eronning.together.WatchTogetherHandler;
import edu.brown.cs.eronning.together.WatchTogetherRequestsHandler;
import edu.brown.cs.eronning.tvtime.ServerData;
import freemarker.template.Configuration;

/**
 * Gui class is the gui interaction with the user.
 *
 * @author eronning
 *
 */
public class Gui implements Interaction {

  private ServerData serverData;
  private TVTQuery database;
  private int port;

  /**
   * Gui constructor.
   *
   * @param options
   *          the options (for checking the port)
   * @param database
   */
  public Gui(int port, TVTQuery database) {
    this.port = port;
    this.database = database;
    this.serverData = new ServerData();
    run();
  }

  @Override
  public void run() {
    serverData.serializeLoad();
    runSparkServer(port);
  }

  /**
   * createEngine creates the FreeMarker engine.
   *
   * @return a freemarker engine
   */
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.\n",
        templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * runSparkServer runs the spark server.
   *
   * @param port
   *          the port to run the server on
   */
  private void runSparkServer(int port) {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    Spark.setPort(port);
    FreeMarkerEngine freeMarker = createEngine();

    // Initial Startup/Login-Related Routes
    Spark.post("/page/information", new PageInformationHandler(serverData));
    Spark.get("/tvtime", new FrontHandler(serverData), freeMarker);

    Spark.get("/signup", new SignUpHandler(serverData), freeMarker);
    Spark.post("/signup/results", new SignUpResultsHandler(serverData));

    Spark.post("/login", new LoginHandler(serverData));
    Spark.get("/welcome/:id", new WelcomeHandler(serverData), freeMarker);

    Spark
    .post("/getRecentlyWatched", new GetRecentlyWatchedHandler(serverData));
    Spark.get("/changeWatched", new ChangeWatchedHandler(serverData),
      freeMarker);
    // Spark.post("/submitChangedWatched/:id", new
    // SubmitChangeWatchedHandler());

    Spark.get("/home/:id", new HomeHandler(serverData), freeMarker);
    Spark.post("/notifications", new NotificationsHandler(serverData));

    // Friend-Related Routes
    Spark.post("/search", new SearchFriendsHandler(serverData, database));
    Spark.post("/friends/nav", new NavFriendHandler(serverData));
    Spark.post("/getFriends", new PopulateFriendsHandler(serverData));
    Spark.post("/gather/friend/data", new GatherFriendHandler(serverData));
    Spark.get("/friends/:id", new FriendsHandler(serverData), freeMarker);
    Spark.get("/friend/:id/:friendId", new FriendHandler(serverData),
      freeMarker);
    Spark.post("/friend/request", new RequestFriendHandler(serverData));
    Spark.post("/friend/add", new AddFriendHandler(serverData));
    Spark.post("/friend/deny", new RejectFriendHandler(serverData));
    Spark.post("/friend/remove", new RemoveFriendHandler(serverData));
    Spark.get("/groups/:id", new GroupsHandler(serverData), freeMarker);

    // Show-Related Routes
    Spark.post("/shows/page", new ShowPageHandler(serverData));
    Spark.post("/gather/shows/data", new GatherShowsHandler(serverData,
      database));
    Spark
      .post("/gather/show/data", new GatherShowHandler(serverData, database));
    Spark.get("/shows/:id", new ShowsHandler(serverData), freeMarker);
    Spark.get("/show/:id/:showId", new ShowHandler(serverData), freeMarker);
    Spark.post("/show/add", new AddShowHandler(serverData, database));
    Spark.post("/show/remove", new RemoveShowHandler(serverData, database));
    Spark.post("/update/current/episode", new UpdateEpisodeHandler(serverData,
      database));
    Spark.get("/highlights/:showID/:numBehind", new HighlightsPageHandler(serverData), freeMarker);
    Spark.post("/getHighlights", new HighlightsHandler(serverData, database));
    // Schedule-Related Routes
    Spark.get("/schedule/:id", new SchedulePageHandler(serverData), freeMarker);
    Spark.post("/schedule", new ScheduleHandler(serverData));
    Spark.post("/timeblocks", new TimeBlockHandler(serverData));
    Spark.get("/availability/:id", new AvailabilityHandler(serverData),
      freeMarker);
    Spark.post("/save", new SaveTimeHandler(serverData));
    Spark.post("/saveSchedule", new SaveScheduleHandler(serverData));
    Spark.post("/updateCal", new UpdateCalHandler(serverData));

    // Handlers for watching together
    Spark.get("/watchTogether", new WatchTogetherHandler(serverData),
      freeMarker);
    Spark.get("/watchTogether/:showName", new WatchTogetherHandler(serverData),
      freeMarker);
    Spark.post("/friends/for/show", new FriendsForShowHandler(serverData,
      database));
    Spark.post("/send/watch/together/requests",
        new WatchTogetherRequestsHandler(serverData, database));
    Spark.post("/scheduleGroup", new ScheduleGroupHandler(serverData));
    Spark.post("/acceptWatchRequest", new AcceptWatchRequestHandler(serverData, database));
    Spark.post("/rejectWatchRequest", new AcceptWatchRequestHandler(serverData, database));
    
    Spark.post("/currEpisode", new CurrEpisodeHandler(serverData, database));
    // Account-Related Routes
    Spark.get("/account/:id", new AccountHandler(serverData), freeMarker);
    Spark.post("/account/settings", new AccountSettingsHandler(serverData));
    Spark.post("/account/avatar", new AccountAvatarHandler(serverData));
    Spark.post("/logout", new LogoutHandler(serverData));
  }

  /**
   * ExceptionPrinter prints any exceptions.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      final int stat = 500;
      res.status(stat);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
