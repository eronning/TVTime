package data;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.XMLParser;

public class XMLParserTest {

  private static XMLParser parser;

  @BeforeClass
  public static void setUpClass() {
    parser = new XMLParser();
  }

  @Test
  public void parseShow() {
    try {
      Show s = parser.parseShow("24493");
      assertTrue(s.getName().equals("Game of Thrones"));
      assertTrue(s.getEp(1).getTitle().equals("Winter is Coming"));
    } catch (ParserConfigurationException | SAXException | IOException e) {
      System.out.println("BAD");
      assertTrue(false);
    }
  }

}
