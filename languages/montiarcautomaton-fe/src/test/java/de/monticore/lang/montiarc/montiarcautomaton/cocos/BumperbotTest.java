package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

public class BumperbotTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBumperbot() {
    // bumperbot must be valid
    checkValid("src/test/resources/", "valid.bumperbot.BumpControl");
  }
  
  @Test
  public void testBumperbotSpeed() {
    // bumperbot must be valid
    checkValid("src/test/resources/", "valid.bumperbot.BumpSpeed");
  }
}
