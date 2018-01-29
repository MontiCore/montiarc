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
import montiarc.cocos.ComponentInstanceNamesAreUnique;

/**
 * @author Robert Heim, Crispin Kirchner
 */
public class ComponentInstanceNamesUniqueTest extends AbstractCoCoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.ComponentInstanceNamesUnique");
      }
  
  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentInstanceNamesAreUnique()),
        getAstNode("contextconditions", "invalid.ComponentInstanceNamesNotUnique"),
        new AbstractCoCoTestExpectedErrorInfo(2, "xMA061"));
    
  }
}
