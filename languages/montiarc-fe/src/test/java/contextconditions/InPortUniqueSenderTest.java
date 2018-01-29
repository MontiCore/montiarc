/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.InPortUniqueSender;

/**
 * @author Crispin Kirchner
 */
public class InPortUniqueSenderTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.InPortUniqueSender");
  }
  
  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InPortUniqueSender()),
        getAstNode("contextconditions", "invalid.InPortAmbiguousSender"),
        new AbstractCoCoTestExpectedErrorInfo(2, "xMA005"));
  }
}
