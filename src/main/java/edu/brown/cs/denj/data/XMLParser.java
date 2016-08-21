package edu.brown.cs.denj.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class for parsing XML with TV show info from TVRage.
 */
public class XMLParser {

  private LatestNextEpHandler latestAndNext;

  private static final String TOP_SHOW_PATH = "src/main"
    + "/resources/static/data/topShows";

  /**
   * Initializes XMLParser.
   */
  public XMLParser() {
    latestAndNext = null;
  }

  /**
   * Parses the show of the given ID.
   *
   * @param showID
   *          the id of the show to look up.
   * @return A show object containing all relevant information of the show.
   * @throws ParserConfigurationException
   *           error on SAX parser config.
   * @throws SAXException
   *           XML parsing error.
   * @throws IOException
   *           on error in web communication.
   */
  public Show parseShow(String showID) throws ParserConfigurationException,
    SAXException, IOException {
    System.out.println("Parsing show " + showID);
    URL showURL =
      new URL("http://services.tvrage.com/feeds/full_show_info.php?sid="
        + showID);
    SAXParserFactory parserFactor = SAXParserFactory.newInstance();
    SAXParser parser = parserFactor.newSAXParser();
    SAXHandler handler = new SAXHandler();

    parser.parse(new InputSource(showURL.openStream()), handler);
    Show toReturn = handler.getShow();
    System.out.println("Populating descriptions...");
    populateDescriptions(toReturn);
    System.out.println("Populating ratings...");
    populateRatings(toReturn);
    return toReturn;
  }

  /**
   * Populates the descriptions for a show and each of its episodes.
   * @param s the show to retrieve descriptions for.
   */
  public void populateDescriptions(Show s) {
    try {
      s.addDescription(showDescription(s));
    } catch (IOException e1) {
      s.addDescription("ERROR: Could not retrieve description");
    }
    for (Episode ep : s.getAllEpisodes()) {
      try {
        ep.addDescription(epDescription(ep));
      } catch (IOException e) {
        ep.addDescription("ERROR: Could not retrieve description");
      }
    }
  }

  /**
   * Populates the ratings for a show and all it's episodes, where available.
   * @param s the show to populate with ratings from IMDB.
   */
  public void populateRatings(Show s) {
    for (Episode e : s.getAllEpisodes()) {
      e.addRating(epRating(e));
    }
    //s.averageRating();
  }

  /**
   * Gets the IMDB rating for an episode.
   * @return the rating of the episode out of 10.
   */
  public double epRating(Episode e) {
    if (e.hasAired()) {
      try {
        String id = epIMDB(e);
        if (id == null) {
          System.out.println("IMDB ID for: " + e.toString() + "-- NOT FOUND");
          return -1;
        }
        URL u =
          new URL(String.format(
            "http://www.omdbapi.com/?i=%s&plot=short&r=json", id));
        BufferedReader reader =
          new BufferedReader(new InputStreamReader(u.openStream()));
        JsonParser parser = new JsonParser();
        JsonObject parsed = parser.parse(reader).getAsJsonObject();
        try {
          if (parsed == null) {
            return -1;
          }
          return parsed.get("imdbRating").getAsDouble();
        } catch (NumberFormatException ex) {
          System.out.println("NUMBER FORMAT for " + e.toString());
          return backupRating(id);
          //return -1;
        } catch (NullPointerException bad) {
          return -1;
        }

      } catch (IOException e1) {
        return -1;
      }
    }
    return -1;
  }

  /**
   * Queries the TVRage API for the given search term. Produces a mapping of all
   * resulting show titles to their corresponding TVRage ID.
   *
   * @param term
   *          the term to search.
   * @return A mapping of resulting shows to their IDs.
   * @throws SAXException
   *           on XML parsing error.
   * @throws IOException
   *           URL connection error.
   * @throws ParserConfigurationException
   *           parser configuration error.
   */
  public Map<String, String> search(String term) throws SAXException,
    IOException, ParserConfigurationException {
    try {
      String encode = term.replaceAll("\\s+", "_");
      URL epInfoURL =
        new URL("http://services.tvrage.com/feeds/search.php?show=" + encode);
      SAXParserFactory parserFactor = SAXParserFactory.newInstance();
      SAXParser parser = parserFactor.newSAXParser();
      SearchResultsHandler handler = new SearchResultsHandler();

      parser.parse(new InputSource(epInfoURL.openStream()), handler);
      return handler.getResults();

    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Gets the next upcoming episode of a given show if one exists.
   *
   * @param show
   *          the show to find the next episode of.
   * @return the next episode, or null if not found.
   * @throws ParserConfigurationException
   *           parser configuration error.
   * @throws SAXException
   *           on XML parsing error.
   * @throws IOException
   *           URL connection error.
   */
  public Episode upcoming(Show show) throws ParserConfigurationException,
    SAXException, IOException {
    Preconditions.checkNotNull(show, "Cannot find next episode of null!");

    if (latestAndNext == null || !latestAndNext.getShow().equals(show)) {
      parseLatestAndNext(show);
    }
    return latestAndNext.getNextEp();
  }

  /**
   * Gets the latest episode.
   *
   * @param show
   *          the show to find the latest episode of.
   * @return most recently premiered episode of the show.
   * @throws SAXException
   *           xml parsing error.
   * @throws IOException
   *           error communicating with URL.
   * @throws ParserConfigurationException
   *           error in SAX parser configuration.
   */
  public Episode latest(Show show) throws SAXException, IOException,
    ParserConfigurationException {
    Preconditions.checkNotNull(show, "Cannot find next episode of null!");

    if (latestAndNext == null || !latestAndNext.getShow().equals(show)) {
      parseLatestAndNext(show);
    }
    if (latestAndNext != null) {
      return latestAndNext.getLatestEp();
    }
    return null;
  }

  public void parseLatestAndNext(Show show) throws SAXException, IOException,
    ParserConfigurationException {
    Preconditions.checkNotNull(show, "Cannot find next episode of null!");
    try {
      String id = show.getId();
      Preconditions.checkNotNull(id);
      URL epInfoURL =
        new URL("http://services.tvrage.com/feeds/episodeinfo.php?" + "sid="
          + show.getId() + "&ep=1x01");
      SAXParserFactory parserFactor = SAXParserFactory.newInstance();
      SAXParser parser = parserFactor.newSAXParser();
      LatestNextEpHandler handler = new LatestNextEpHandler(show);

      parser.parse(new InputSource(epInfoURL.openStream()), handler);
      System.out.println("new handler");
      latestAndNext = handler;
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Parses all IDs in the top episodes file and returns the resulting set of
   * shows.
   *
   * @return the set of 50 top shows on IMDB.
   * @throws IOException
   *           on read file error.
   */
  @Deprecated
  public Set<Show> parseTopShows() throws IOException {
    // List<String> ids = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new FileReader(TOP_SHOW_PATH));
    String input;
    Set<Show> toReturn = new HashSet<Show>();
    while ((input = reader.readLine()) != null) {
      try {
        Show query = parseShow(input);
        if (query != null) {
          // System.out.println(query.getName());
          toReturn.add(query);
        }
      } catch (ParserConfigurationException | SAXException e) {
        continue;
      }
      // ids.add(input);
    }
    reader.close();
    // System.out.println(ids);
    return toReturn;
  }

  /**
   * Queries IMDB for 50 most popular shows.
   *
   * @return the Set of IDs of the Top shows.
   * @throws IOException
   *           connection to URL error.
   */
  public void updateTopShows() throws IOException {
    List<String> titleList = new ArrayList<String>();
    Document doc =
      Jsoup.connect("http://www.imdb.com/search/title?title_type=tv_series")
        .get();
    Elements showThings =
      doc.select("table.results > tbody " + "td.title > a[href^=/title/]");

    for (Element e : showThings) {
      titleList.add(e.text().toLowerCase());
      // String id;
      System.out.println(e.text()); // covert to lower case
    }
    System.out.println();

    // FileOutputStream fileOut = new FileOutputStream("topShowData");
    // ObjectOutputStream out = new ObjectOutputStream(fileIn);

    BufferedWriter writer = new BufferedWriter(new FileWriter(TOP_SHOW_PATH));
    for (String title : titleList) {
      try {
        Map<String, String> res = search(title);
        String id = res.get(title);

        if (id != null) {
          System.out.println(id);
          writer.write(id + "\n");
        } else {
          for (String resID : res.values()) {
            id = resID; // get the first result of the query
            break;
          }
          System.out.println(id);
          writer.write(id + "\n");
        }
      } catch (SAXException | ParserConfigurationException e1) {
        // skip
        continue;
      }
    }
    writer.close();
  }

  private void serializeTop() throws IOException {
    //FileOutputStream fileOut = new FileOutputStream("topShowData");
    //ObjectOutputStream out = new ObjectOutputStream(fileOut);

    BufferedReader reader = new BufferedReader(new FileReader(TOP_SHOW_PATH));
    String input;
    List<Show> toReturn = new ArrayList<Show>();
    int count = 0;
    while ((input = reader.readLine()) != null) {
      count++;
      try {
        System.out.println("#" + count + " parsing " + input);
        Show query = parseShow(input);
        if (query != null) {
          System.out.println("Parsed show");
          toReturn.add(query);
        }
      } catch (ParserConfigurationException | SAXException
        | java.net.SocketException e) {
        System.out.println("Didn't parse show");
        continue;
      }
    }
    reader.close();
    System.out.println("Got the shows");
    serializeWrite(toReturn);
//    out.writeObject(toReturn);
//    out.close();
  }

  private void serializeWrite(List<Show> shows) throws IOException {
    FileOutputStream fileOut = new FileOutputStream("topShowData");
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(shows);
    out.close();
  }

  public List<Show> loadTop() throws IOException {
    FileInputStream fileIn = new FileInputStream("topShowData");
    ObjectInputStream in = new ObjectInputStream(fileIn);
    try {
      return (List<Show>) in.readObject();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Gets the given episode's description.
   *
   * @param ep
   *          Episode object to get description for.
   * @return String containing summary, if found.
   * @throws IOException
   *           URL connection error.
   */
  public String epDescription(Episode ep) throws IOException {
    String desc = getDescription(ep.getEpURL());
    if (desc == null) {
      desc = backupDesc(ep);
    }
    return desc;
    // Document doc = Jsoup.connect(ep.getEpURL()).get();
    // Element synopsis = doc.select("div.show_synopsis").first();
    // return synopsis.text();
  }

  /**
   * Gets the show's description.
   * @param s the show to get description of.
   * @return the show's description as a string
   * @throws IOException URL connection error.
   */
  public String showDescription(Show s) throws IOException {
    return getDescription(s.getShowURL());
  }

  private String getDescription(String url) throws IOException {
    Document doc = Jsoup.connect(url).get();
    Element synopsis = doc.select("div.show_synopsis").first();
    if (synopsis.text().isEmpty()) {
      Element attempt2 = doc.select("div > div.left.padding_bottom_10").first();
      if (attempt2 != null) {
        return attempt2.text();
      } else {
        return null;
      }
      // System.out.println(attempt2.text());
    }
    return synopsis.text();
  }

  /**
   * A backup means of obtaining a description if not found through TVRage.
   * Queries OMDB api.
   * @param ep the episode to find the description of.
   * @return the episode description.
   */
  private String backupDesc(Episode ep) {
    //TODO this works pretty well. We might consider using this instead.
    //The descriptions might also be better/more appropriate.
    //They're pretty succinct. It's also pretty fast to do this.
    System.out.println("Looking for backup description...");
    try {
      String imdb = epIMDB(ep);
      URL u =
          new URL(String.format(
              "http://www.omdbapi.com/?i=%s&plot=short&r=json", imdb));
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(u.openStream()));
      JsonParser parser = new JsonParser();
      JsonObject parsed = parser.parse(reader).getAsJsonObject();

      return parsed.get("Plot").getAsString();
    } catch (IOException e) {
      return null;
    }

  }

  private double backupRating(String imdbID) {
    System.out.println("looking for backup rating");
    try {
//      URL u =
//          new URL(String.format(
//              "http://www.imdb.com/title/%s/", imdbID));
      Document doc = Jsoup.connect(String.format(
          "http://www.imdb.com/title/%s/", imdbID)).get();
      Element rating = doc.select("div#pagecontent span"
          + "[itemprop=ratingValue]").first();
      double toReturn = Double.parseDouble(rating.text());
      System.out.println("success");
      return toReturn;

    } catch (Exception e) {
      return -1;
    }

  }

  /**
   * Gets the given episode's synopsis.
   *
   * @param ep
   *          Episode object to get recap for.
   * @return String containing full recap, if found.
   * @throws IOException
   *           URL connection error.
   */
  public String epRecap(Episode ep) throws IOException {
    Document doc = Jsoup.connect(ep.getEpURL() + "/recap").get();
    Element synopsis = doc.select("div.show_synopsis").first();
    if (synopsis == null) {
      return "Not found.";
    }
    return synopsis.text();
  }

  /**
   * Produces IDs of shows that have recently aired episodes.
   *
   * @return a set of IDs of shows that have aired episodes recently.
   * @throws IOException
   *           connection to server error.
   */
  public Set<String> recentEps() throws IOException {
    Set<String> showIDs = new HashSet<String>();
    Document doc =
      Jsoup.connect("http://www.tvrage.com/recent_episodes.php").get();
    Elements spans = doc.select("td.b1").select("span[title]");
    // Elements spans = doc.select("span[title]");
    for (Element e : spans) {
      String title = e.attr("title");

      // try {
      // System.out.println(title);
      // String id = search(title).get(title);
      String id = title;
      if (id != null) {
        showIDs.add(id);
      }
      // } catch (SAXException | ParserConfigurationException e1) {
      // continue;
      // //Just skip show
      // }
    }
    return showIDs;
  }

  /**
   * Gets the IMBD id for a TV episode.
   *
   * @param e
   *          the episode
   * @return the episode's IMDB id.
   * @throws IOException
   *           connection to URL error.
   */
  public String epIMDB(Episode e) throws IOException {
    /*
     * Searches IMDB for episode name aired on air date. Could potentially give
     * faulty results if two episodes of different TV shows with the same neame
     * aired on the same day. Wasn't able to determine how to specify show.
     */
    if (e == null) {
      return null;
    }
    Document doc =
      Jsoup.connect(
        "http://www.imdb.com/search/" + "title?release_date=" + e.getAirDate()
        + "," + e.getAirDate() + "&title=" + e.getTitle()
        + "&title_type=tv_episode").get();
    Elements titles = doc.select("td.title").select("span[data-tconst]");
    if (titles.first() == null) {
      String trimmedRelease = e.getAirDate().substring(0, 7);
      doc =
          Jsoup.connect(
            "http://www.imdb.com/search/" + "title?release_date=" + trimmedRelease
            + "," + trimmedRelease + "&title=" + e.getTitle()
            + "&title_type=tv_episode").get();
      titles = doc.select("td.title").select("span[data-tconst]");
      if (titles.first() == null) {
        return null;
      }
    }
    // TODO null things?
    // System.out.println(e);
    // System.out.println(titles);
    // System.out.println(titles.first().attr("data-tconst"));
    return titles.first().attr("data-tconst");
  }

  // TODO delete
  public static void main(String[] args) {
    XMLParser test = new XMLParser();
    try {
      List<Show> top = test.loadTop();
      int count = 0;
      for (Show s : top) {
        count++;
        System.out.println(test.epRecap(s.getEp(2)));
        break;
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    } try {
      // Show testShow = test.parseShow("24493");
      // System.out.println(test.upcoming(testShow));
      // System.out.println();
      // System.out.println(test.search("airbender"));
      test.repl();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void repl() throws IOException, ParserConfigurationException,
    SAXException {
    System.out.println("Welcome!");
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = reader.readLine()) != null) {
      System.out.println();
      if (line.equals("help")) {
        System.out
          .println("Show search: \"show <show ID>\"\n"
            + "Next episode: \"nextEp <show ID>\"\n"
            + "Latest episode: \"latestEp <show ID>\"\n"
            + "Search: \"<search term>\"\n"
            + "Episode description: \"ep <show ID> <season num>x<episode num>\"\n"
            + "Episode recap: \"recap <show ID> <season num> <episode num>\"");
        continue;
      }
      String[] command = line.split(" ");

      switch (command[0]) {
        case "show":
          Show lookup = parseShow(command[1]);
          if (lookup == null) {
            System.out.println("Not found.");
          } else {
            System.out.println(lookup);
          }
          break;
        case "nextEp":
          Show found = parseShow(command[1]);
          if (found == null) {
            System.out.println("Show not found");
            break;
          } else if (found.isEnded()) {
            System.out.println(found.getName() + " has no upcoming episodes");
            break;
          }
          Episode ep = upcoming(found);
          if (ep == null) {
            System.out.println("No upcoming episodes found");
            break;
          }
          System.out.println(found.getName());
          System.out.println(ep);
          break;
        case "latestEp":
          found = parseShow(command[1]);
          if (found == null) {
            System.out.println("Show not found");
            break;
          }
          ep = latest(found);
          if (ep == null) {
            System.out.println("No latest episode found");
            break;
          }
          System.out.println(found.getName());
          System.out.println(ep);
          break;
        case "recent":
          System.out.println("Searching for recent shows...");
          Set<String> ids = recentEps();
          System.out.println("Recent shows: ");
          for (String i : ids) {
            System.out.println(i);
          }
          break;
        case "ep":
          if (command.length < 4) {
            System.out.println("USAGE: ep <showID> <seasonNum> <epNum>");
            break;
          }
          System.out.println("Searching for episode...");
          Show lookup2 = parseShow(command[1]);
          try {
            int seasonNum = Integer.parseInt(command[2]);
            int epNum = Integer.parseInt(command[3]);

            Episode e = lookup2.getEpInSeason(seasonNum, epNum);
            if (e == null) {
              System.out.println("Could not find that episode in "
                + lookup2.getName());
              break;
            }
            String description = epDescription(e);
            if (e == null || e.equals("")) {
              System.out.println("No description found for " + e.getTitle());
              break;
            }
            System.out.println(epDescription(e));
          } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid number");
            break;
          }
          break;
        case "recap":
          if (command.length < 4) {
            System.out.println("USAGE: recap <showID> <seasonNum> <epNum>");
            break;
          }
          System.out.println("Retrieving recap...");
          lookup2 = parseShow(command[1]);
          try {
            int seasonNum = Integer.parseInt(command[2]);
            int epNum = Integer.parseInt(command[3]);

            Episode e = lookup2.getEpInSeason(seasonNum, epNum);
            if (e == null) {
              System.out.println("Could not find that episode in "
                + lookup2.getName());
              break;
            }
            System.out.println(epRecap(e));
          } catch (NumberFormatException e) {
            System.out.println("ERROR: invalid number");
            break;
          }
          break;
        default:
          System.out.println("Searching: \"" + line + "\"");
          Map<String, String> result = search(line);
          if (!result.isEmpty()) {
            System.out.println(search(line));
          } else {
            System.out.println("The search produced no results");
          }
      }
    }
    System.out.println("END");
  }

}

/**
 * A SAX event handler for retrieving search results from the TVRage Api. Used
 * to produce a mapping of resulting show names to their IDs.
 */
class SearchResultsHandler extends DefaultHandler {
  private LinkedHashMap<String, String> showNameID;
  private String showName;
  private String showID;

  private String tmp;
  private String nameString; // needed because of issues with splitting...

  /**
   * Constructs a new handler.
   */
  public SearchResultsHandler() {
    showNameID = new LinkedHashMap<String, String>();
  }

  @Override
  public void startElement(String uri, String localName, String qName,
    Attributes attributes) throws SAXException {
    tmp = "";
    if (qName.equals("name")) {
      nameString = "";
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    tmp = new String(ch, start, length);
    nameString += tmp;
    // System.out.println("TEMP " + nameString);
  }

  @Override
  public void endElement(String uri, String localName, String qName)
    throws SAXException {

    switch (qName) {
      case "name":
        showName = nameString;
        break;
      case "showid":
        showID = tmp;
        break;
      case "show":
        showNameID.put(showName.toLowerCase(), showID);
        break;
    }
  }

  /**
   * Returns the mapping of show name to id created from parsing the query
   * result.
   *
   * @return the Name-ID mapping resulting from query.
   */
  public Map<String, String> getResults() {
    return showNameID;
  }

  /**
   * Gets the first show ID resulting from the search query.
   *
   * @return the ID of the first show result.
   */
  public String getTop() {
    for (String id : showNameID.keySet()) {
      return id; // return first id
    }
    return null;
  }
}

/**
 * A SAX event handler for retrieving next and latest episode information. Finds
 * the next episode to be premiered of a given show.
 */
class LatestNextEpHandler extends DefaultHandler {

  private Episode nextEp;
  private Episode latestEp;
  private String tmp;
  private Show show;
  private String seasonEpNum;

  public LatestNextEpHandler(Show show) {
    Preconditions.checkNotNull(show);
    this.show = show;
  }

  @Override
  public void startElement(String uri, String localName, String qName,
    Attributes attributes) throws SAXException {
    tmp = "";
  }

  @Override
  public void endElement(String uri, String localName, String qName)
    throws SAXException {

    switch (qName) {

      // case "Show":
      // show = new Show(name, numSeasons, ended, image, id, runtime);
      // break;
      case "number":
        seasonEpNum = tmp;
        break;
      case "nextepisode":
        String[] numSplit = seasonEpNum.split("x");
        assert (numSplit.length == 2);
        int season = Integer.parseInt(numSplit[0]);
        int ep = Integer.parseInt(numSplit[1]);
        nextEp = show.getEpInSeason(season, ep);
        break;
      case "latestepisode":
        numSplit = seasonEpNum.split("x");
        assert (numSplit.length == 2);
        season = Integer.parseInt(numSplit[0]);
        ep = Integer.parseInt(numSplit[1]);
        latestEp = show.getEpInSeason(season, ep);
        break;

    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    tmp = new String(ch, start, length);
  }

  /**
   * Retrieves the parsed next episode of this handler.
   *
   * @return the next episode.
   */
  public Episode getNextEp() {
    return nextEp;
  }

  /**
   * Retrieves the latest episode of this handler.
   *
   * @return the latest episode.
   */
  public Episode getLatestEp() {
    return latestEp;
  }

  /**
   * Gets the show associated with this handler.
   *
   * @return the show.
   */
  public Show getShow() {
    return show;
  }

}

// handles primary show parsing
class SAXHandler extends DefaultHandler {

  private boolean found;

  private Show show;
  private String name;
  private int numSeasons;
  private boolean ended;
  private String image;
  private String status;
  private String id;
  private String showURL;
  private List<String> genres;
  private int runtime;

  private Season season;
  private int seasonNum; // TODO needed?

  private int epNum;
  // private int seasonnum;
  // private String epID;
  private String title;
  private String screencap;
  private String airDate;
  private String epURL;
  private boolean special;
  // a bit clunky

  private String tmp;

  public SAXHandler() {
    found = false;
    show = null;
    name = null;
    numSeasons = 0;
    ended = false;
    image = null;
    genres = new ArrayList<String>();
    id = null;
    runtime = 0; // TODO who cares?
    special = false;
  }

  @Override
  public void startDocument() throws SAXException {
  }

  @Override
  public void endDocument() throws SAXException {
  }

  @Override
  public void startElement(String uri, String localName, String qName,
    Attributes attributes) throws SAXException {
    tmp = "";
    if (qName.equals("Show")) {
      found = true;
    } else if (qName.equals("Episodelist")) {
      show = new Show(name, numSeasons, ended, image, id, showURL, runtime, genres);
    } else if (qName.equals("Season")) {
      seasonNum = Integer.parseInt(attributes.getValue("no"));
      season = new Season(seasonNum, show);
    } else if (qName.equals("Special")) {
      special = true;
      epNum = 0;
    } /*
       * else if (qName.equals("ended")) { System.out.println("HI"); ended =
       * true; }
       */
  }

  @Override
  public void endElement(String uri, String localName, String qName)
    throws SAXException {

    switch (qName) {
      case "name":
        name = tmp;
        break;
      case "showid":
        id = tmp;
        break;
      case "showlink":
        showURL = tmp;
        break;
      case "totalseasons":
        try {
          numSeasons = Integer.parseInt(tmp);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "image":
        image = tmp;
        break;
      case "status":
        status = tmp;
        // if (tmp.equals("Ended")) {
        // ended = true; // TODO "ended" status can vary, use ended tag instead.
        // }
        break;
      case "ended":
        if (!tmp.equals("")) {
          ended = true;
        }
        break;
      case "genre":
        genres.add(tmp);
        break;
      case "runtime":
        runtime = Integer.parseInt(tmp);
        break;
      case "Season":
        show.addSeason(season);
        break;
      case "episode":
        if (epNum != 0) { // TODO Ignoring special episodes (for now?)
          season.addEpisode(new Episode(show, epNum, seasonNum, title,
            screencap, epURL, airDate));
        }
        break;
      case "epnum":
        if (special) {
          epNum = 0;
          break;
        }
        epNum = Integer.parseInt(tmp);
        break;
      case "seasonnum":
        seasonNum = Integer.parseInt(tmp);
        break;
      case "airdate":
        airDate = tmp;
        break;
      // case prodnum?
      case "link":
        epURL = tmp;
        break;
      case "title":
        title = tmp;
        break;
      case "screencap":
        screencap = tmp;
        break;
      case "Special":
        special = false;
        break;
        // TODO ignore special episodes
        // may want other ep info

    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    tmp = new String(ch, start, length);
  }

  /**
   * Retrieves the show object created from parsing.
   *
   * @return the show parsed from the given URL.
   */
  public Show getShow() {
    if (found) {
      return show;
    } else {
      return null;
    }
  }

}
