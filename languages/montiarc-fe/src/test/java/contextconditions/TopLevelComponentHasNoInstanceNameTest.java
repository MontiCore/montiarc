/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.TopLevelComponentHasNoInstanceName;

/**
 * @author Crispin Kirchner
 */
public class TopLevelComponentHasNoInstanceNameTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.TopLevelComponentHasNoInstanceName");

  }

  /*
   * Checks that the outer component definition has no instance name.
   */
  @Test
  public void testInvalid() {

    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.OuterComponentWithInstanceName");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new TopLevelComponentHasNoInstanceName());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA007"));

    checkInvalid(cocos,
        getAstNode("contextconditions", "invalid.TopLevelComponentHasInstanceName"),
        new ExpectedErrorInfo(1, "xMA007"));
  }
}
