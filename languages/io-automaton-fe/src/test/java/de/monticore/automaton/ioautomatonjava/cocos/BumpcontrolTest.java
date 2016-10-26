package de.monticore.automaton.ioautomatonjava.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

public class BumpcontrolTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBumpcontrol() {
    checkValid("src/test/resources/", "valid.bumperbot.BumpControl");
  }
}
