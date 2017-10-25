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
import montiarc.cocos.SimpleConnectorSourceExists;

/**
 * @author Crispin Kirchner
 */
public class SimpleConnectorSourceExistsTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.SimpleConnectorSourceExists");
  }
  
  @Test
  public void testValidExternal() {
    checkValid("contextconditions", "valid.SimpleConnectorSourceExistsExternal");
  }
  
  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SimpleConnectorSourceExists()),
        getAstNode("contextconditions", "invalid.SimpleConnectorSourceNonExistant"),
        new ExpectedErrorInfo(1, "xMA072"));
  }
}
