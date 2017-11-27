/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.SubcomponentParametersCorrectlyAssigned;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class SubcomponentParametersCorrectlyAssignedTest extends AbstractCoCoTest{
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    
  }
  
  @Test
  public void testInvalid() {
    ASTMontiArcNode node = getAstNode("contextconditions",
        "invalid.SubcomponentParametersNotCorrectlyAssigned");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SubcomponentParametersCorrectlyAssigned()), node, new ExpectedErrorInfo(1, "xMA064"));
  }
}
