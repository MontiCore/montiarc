/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.body.subcomponents;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.SubcomponentParametersCorrectlyAssigned;

/**
 * This class checks all context conditions related to subcomponents
 *
 * @author Andreas Wortmann
 *
 */
public class SubComponentsTest extends AbstractCoCoTest{
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSubcomponentParametersCorrectlyAssigned() {
    ASTMontiArcNode node = getAstNode("contextconditions",
        "invalid.SubcomponentParametersNotCorrectlyAssigned");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SubcomponentParametersCorrectlyAssigned()), node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA064"));
  }
}
