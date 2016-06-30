/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._cocos.MontiArcCoCoChecker;
import de.se_rwth.commons.logging.Log;

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
    checkValid("arc4/coco", "valid.SimpleConnectorSourceExists");
  }
  
  @Test
  public void testValidExternal() {
    checkValid("arc4/coco", "valid.SimpleConnectorSourceExistsExternal");
  }
  
  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SimpleConnectorSourceExists()),
        getAstNode("arc4/coco", "invalid.SimpleConnectorSourceNonExistant"),
        new ExpectedErrorInfo(1, "xF4D71"));
  }
}
