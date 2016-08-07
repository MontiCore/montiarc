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
    checkValid("src/test/resources/", "valid.bumperbot.BumpControl");
  }
}
