/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.ReferencedSubComponentExists;

/**
 * @author Crispin Kirchner
 */
public class ReferencedSubComponentExistsTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.ReferencedSubComponentExists");
  }
  
  @Test
  @Ignore("Symbol table already throws an exception, therefore the coco is never checked. A fix" + 
          " would be to stop the symbol table from theowing the exception, in order to have a" +
          " better error message")
  public void testInvalid() {
    MontiArcCoCoChecker coco = new MontiArcCoCoChecker()
        .addCoCo(new ReferencedSubComponentExists());
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.NonExistantReferencedSubComponent");
    
    checkInvalid(coco, node, new ExpectedErrorInfo(2, "xMA004"));
  }
}
