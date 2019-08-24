/* (c) https://github.com/MontiCore/monticore */
package components.body.timing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;

/**
 * This class checks all context conditions related to the timing configuration
 * and undelayed message cycles.
 *
 * @author Andreas Wortmann
 */
public class TimingTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.body.timing";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentEntryIsDelayed() {
    ComponentSymbol parent = this.loadComponentSymbol(PACKAGE, "Timing");
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());

    ComponentSymbol child = parent.getInnerComponent("TimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());

    child = parent.getInnerComponent("TimedDelayingInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());

    child = parent.getInnerComponent("TimeSyncInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());

    child = parent.getInnerComponent("TimeCausalSyncInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());

    child = parent.getInnerComponent("UntimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());
  }

  @Test
  public void testDelayedComponentWithTwoPorts() {
    checkValid(PACKAGE + "." + "DelayedComponentWithTwoPorts");
    ComponentSymbol parent = this.loadComponentSymbol(PACKAGE, "DelayedComponentWithTwoPorts");
    assertTrue(parent.hasDelay());
  }

  @Test
  public void testUsingCompsWithTwoPortsAndWithAndWithoutDelay() {
    checkValid(PACKAGE + "." + "UsingCompsWithTwoPortsAndWithAndWithoutDelay");
  }
}
