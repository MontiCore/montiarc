package de.monticore.lang.montiarc.automaton.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

public class BumperbotTest extends AutomatonAbstractCocoTest {
 
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBumperbot() {
    // bumperbot must be valid
    checkValid("src/test/resources/", "automaton.valid.bumperbot.BumpControl");
  }
  
  @Test
  public void testBumperbotSpeed() {
    // bumperbot must be valid
    checkValid("src/test/resources/", "automaton.valid.bumperbot.BumpSpeed");
  }
}
