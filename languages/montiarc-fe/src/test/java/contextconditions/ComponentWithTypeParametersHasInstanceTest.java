/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.Before;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.ComponentWithTypeParametersHasInstance;

/**
 * @author (last commit) Crispin Kirchner
 */
public class ComponentWithTypeParametersHasInstanceTest
    extends AbstractCoCoTest {
  
  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.ComponentWithTypeParametersHasInstance");
  }
  
  public void testInvalid(String componentName) {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentWithTypeParametersHasInstance()),
        getAstNode("contextconditions", "invalid." + componentName), new ExpectedErrorInfo(1, "xMA009"));
    
  }
  
  @Test
  public void testInvalidComponentWithTypeParametersLacksInstance() {
    testInvalid("ComponentWithTypeParametersLacksInstance");
  }
  
  @Test
  public void testInvalidNestedComponentWithTypeParameterLacksInstance() {
    testInvalid("NestedComponentWithTypeParameterLacksInstance");
  }
}
