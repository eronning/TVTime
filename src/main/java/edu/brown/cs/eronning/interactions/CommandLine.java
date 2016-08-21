package edu.brown.cs.eronning.interactions;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.brown.cs.denj.data.XMLParser;

/**
 * CommandLine class is for interacting with the user via terminal.
 *
 * @author eronning
 *
 */
public class CommandLine implements Interaction {

  private XMLParser replParser;

  /**
   * CommandLine constructor.
   *
   * @param replParser
   *          the parser for the database
   */
  public CommandLine(XMLParser replParser) {
    this.replParser = replParser;
    run();
  }

  @Override
  public void run() {
    try {
      replParser.repl();
    } catch (IOException | ParserConfigurationException | SAXException e) {
      System.out
      .println("ERROR: there was an error in starting the command line");
      return;
    }
  }

}
