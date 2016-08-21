package edu.brown.cs.eronning.tvtime;

import java.sql.SQLException;

import edu.brown.cs.denj.data.TVDatabase;
import edu.brown.cs.denj.data.XMLParser;
import edu.brown.cs.eronning.interactions.CommandLine;
import edu.brown.cs.eronning.interactions.Gui;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Main is the main class.
 *
 * @author eronning
 */
public final class Main {

  private final int defaultPort = 4567;
  private String[] args;

  /**
   * main is the main method to run the program.
   *
   * @param args
   *          is the arguments from the user
   */
  public static void main(String[] args) {
    try {
      new Main(args).run();
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println("ERROR: Failed startup.");
    }
  }

  /**
   * Main constructor.
   *
   * @param args
   *          arguments input
   */
  private Main(String[] args) {
    this.args = args;
  }

  /**
   * run starts the program.
   *
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  private void run() throws SQLException, ClassNotFoundException {
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg();
    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      int port = defaultPort;
      if (options.has("port")) {
        String portString = (String) options.valueOf("port");
        port = Integer.parseInt(portString);
      }
      new Gui(port, new TVDatabase(true));
    } else {
      new CommandLine(new XMLParser());
    }
  }

}