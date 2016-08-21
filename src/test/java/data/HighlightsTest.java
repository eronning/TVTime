package data;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Highlights;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.XMLParser;

public class HighlightsTest {

  private static XMLParser parser;
  private static List<Show> top;

  @BeforeClass
  public static void setUpClass() {
    parser = new XMLParser();
    try {
      top = parser.loadTop();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void basic() {
    Show dd = top.get(0);
    assertTrue(dd.getName().equals("Daredevil"));
    List<Episode> highlighted = Highlights.skippable(dd, 1, dd.getNumEpsides()-1, 1);
    assertTrue(!highlighted.isEmpty());
  }

  @Test
  public void skip10() {
    Show got = top.get(1);
    assertTrue(got.getName().equals("Game of Thrones"));
    List<Episode> highlighted = Highlights.getLowest(got, 1, got.getNumEpsides()-1, 10);
    assertTrue(!highlighted.isEmpty());
    assertTrue(highlighted.size() == 10);
  }

}
