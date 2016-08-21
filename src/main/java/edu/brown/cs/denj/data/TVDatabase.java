package edu.brown.cs.denj.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

/**
 * Central backend class for TV data queries. Handles caching. TODO make an
 * interface.
 *
 */
public class TVDatabase implements TVTQuery {

  private XMLParser parser;
  private Map<String, Show> showCache;
  private List<Show> topShows;

  // private Thread parsingThread; //TODO threadsafe

  /**
   * Constructs a TVDatabase.
   *
   * @param parseTop
   *          if true, parses and caches top 50 shows upon initialization.
   */
  public TVDatabase(boolean parseTop) {
    parser = new XMLParser();
    showCache = new HashMap<String, Show>();
    if (parseTop) {
      try {
        topShows = parser.loadTop();
        for (Show s : topShows) {
          showCache.put(s.getId(), s);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * Constructs a TVDatabase without parsing top50.
   */
  public TVDatabase() {
    this(true);
  }

  @Override
  public List<Show> sampleByGenre(String genre) {
    // TODO add a limit?
    // checkThread();
    List<Show> results = new ArrayList<Show>();
    for (Show s : showCache.values()) {
      if (s.getGenres().contains(genre)) {
        results.add(s);
      }
    }
    return results;
  }

  private List<Show> parseTopShows() {
    if (topShows != null) {
      return topShows;
    }
    try {
      // topShows = parser.parseTopShows();
      topShows = parser.loadTop();
      for (Show s : topShows) {
        showCache.put(s.getId(), s);
      }
      return topShows;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public List<Show> topShows() {
    // checkThread();
    if (topShows != null) {
      return topShows;
    } else {
      return parseTopShows();
    }
  }

  @Override
  public Show getShow(String id) {
    try {
      Show s = showCache.get(id);
      if (s == null) { // query and cache show if not found
        s = parser.parseShow(id);
        if (s != null) {
          showCache.put(s.getId(), s);
        }
      }
      return s;
    } catch (ParserConfigurationException | SAXException | IOException e) {
      // TODO throw?
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Episode getNextEp(Show s) {
    Preconditions.checkNotNull(s);
    try {
      return parser.upcoming(s);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      // TODO throw?
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String description(Episode e) {
    try {
      return parser.epDescription(e);
    } catch (IOException e1) {
      return null;
    }
  }

  @Override
  public String recap(Episode e) {
    try {
      return parser.epRecap(e);
    } catch (IOException e1) {
      return null; // TODO return "not found"
    }
  }

  @Override
  public Episode mostRecentEp(Show s) {
    Preconditions.checkNotNull(s);
    try {
      return parser.latest(s);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      // TODO throw?
      e.printStackTrace();
    }
    return null;
  }

  // private void checkThread() {
  // if (parsingThread != null && parsingThread.isAlive()) {
  // try {
  // parsingThread.join();
  // } catch (InterruptedException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  // }

  /**
   * A thread that will parse the top shows.
   */
  private class TopShowsThread extends Thread {
    @Override
    public void run() {
      parseTopShows();
    }
  }

  public static void main(String[] args) {
    System.out.println("Hello.");
    TVDatabase db = new TVDatabase(true);
    List<Show> top = db.topShows();
    for (Show s : top) {
      System.out.println(s.getName() + " " + s.getImage());
    }
    // System.out.println(db.topShows());
    // System.out.println("getting dramatic.");
    // List<Show> drama = db.sampleByGenre("Drama");
    // List<Show> comedy = db.sampleByGenre("Comedy");
    // List<Show> crime = db.sampleByGenre("Crime");
    // System.out.println("DRAMA");
    // for (Show s : drama) {
    // System.out.println(s.getName());
    // }
    // System.out.println("\nCOMEDY");
    // for (Show s : comedy) {
    // System.out.println(s.getName());
    // }
    //
    // System.out.println("\nCRIME");
    // for (Show s : crime) {
    // System.out.println(s.getName());
    // }

  }

  @Override
  public Map<String, String> search(String entry) {
    try {
      return parser.search(entry);
    } catch (SAXException | IOException | ParserConfigurationException e) {
      return new HashMap<String, String> ();
    }
  }

}
