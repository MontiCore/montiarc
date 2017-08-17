/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.Before;
import org.junit.Test;

import de.monticore.lang.montiarc.cocos.ComponentWithTypeParametersHasInstance;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcCoCoChecker;
import de.se_rwth.commons.logging.Log;

/**
 * @author (last commit) Crispin Kirchner
 */
public class ComponentWithTypeParametersHasInstanceTest
    extends AbstractCoCoTest {
  
  @Before
  public void setup() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.ComponentWithTypeParametersHasInstance");
  }
  
  public void testInvalid(String componentName) {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentWithTypeParametersHasInstance()),
        getAstNode("contextconditions", "invalid." + componentName), new ExpectedErrorInfo(1, "x79C00"));
    
//    runCheckerWithSymTab("contextconditions", "invalid." + componentName);
//    String findings = Log.getFindings().stream().map(f -> f.buildMsg())
//        .collect(Collectors.joining("\n"));
//    assertEquals(findings, 1, Log.getFindings().size());
//    assertTrue(findings.contains("x79C00"));
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
