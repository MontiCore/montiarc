package components.head.inheritance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import montiarc.MontiArcTool;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

/**
 * This class checks all context conditions related to component inheritance
 *
 * @author Andreas Wortmann
 */
public class InheritanceTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "components.head.inheritance";
  
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
  
  @Test
  public void testSuperComponents() {
    ComponentSymbol subB = new MontiArcTool().getComponentSymbol(
        PACKAGE + "." + "SubB", Paths.get("src/test/resources/").toFile()).orElse(null);
    assertNotNull(subB);

    assertNotNull(subB);
    assertTrue(subB.getIncomingPort("anotherIn").isPresent());
    assertTrue(subB.getOutgoingPort("anotherOut").isPresent());
    assertTrue(subB.getOutgoingPort("anotherOut2").isPresent());

    // inherited
    assertNotNull(subB.getSpannedScope().resolve("myInput", PortSymbol.KIND));
    assertEquals(2, subB.getAllIncomingPorts().size());

    assertNotNull(subB.getSpannedScope().resolve("myOutput", PortSymbol.KIND));
    assertNotNull(subB.getSpannedScope().resolve("myOutput519", PortSymbol.KIND));
    assertEquals(4, subB.getAllOutgoingPorts().size());
  }

}