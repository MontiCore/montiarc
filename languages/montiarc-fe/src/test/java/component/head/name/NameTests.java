/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.head.name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import montiarc.MontiArcTool;

/**
 * This class checks all context conditions related to component names
 *
 * @author (last commit) kirchhof
 * @version 4.2.0, 30.01.2018
 * @since 4.2.0
 */
public class NameTests extends AbstractCoCoTest {
  private static final String MP = "src/test/resources/";
  
  private static final String PACKAGE = "component.head.name";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testNameClash() {
    Log.getFindings().clear();
    
    // given
    MontiArcTool tool = new MontiArcTool();
    String qualifiedName = PACKAGE + "." + "NameClashB";
    
    // when
    try {
      tool.parse(MP + PACKAGE.replace(".", "/") + "/NameClashB.arc");
      assertEquals(1, Log.getErrorCount());
      return;
    }
    catch (Exception e) {
      fail(e.toString());
    }

    // then
    fail("NameClashB.arc should not be parseable because '" + qualifiedName + "' is ambiguous.");
  }
}
