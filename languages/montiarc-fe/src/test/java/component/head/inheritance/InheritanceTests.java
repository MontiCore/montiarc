package component.head.inheritance;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import de.se_rwth.commons.logging.Log;
import montiarc._parser.MontiArcParser;

/**
 * This class checks all context conditions related to component inheritance
 *
 * @author Andreas Wortmann
 */
public class InheritanceTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "component.head.inheritance";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMutipleInheritance() {
    MontiArcParser parser = new MontiArcParser();
    try {
      parser.parse(PACKAGE + "." + "MultipleInheritance");
    }
    catch (Exception e) {
        return;
    }
    fail("Component " + PACKAGE + ".invalid.MultipleInheritance should not be parseable.");
  }

}