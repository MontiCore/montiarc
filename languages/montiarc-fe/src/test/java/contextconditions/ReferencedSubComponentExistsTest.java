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
/**
Symbol table already throws an exception, therefore the coco is never checked. A fix" +
would be to stop the symbol table from throwing the exception, in order to have a" +
better error message. For now we just check that we give out the rudimentary error
xA1038, which tells us that the non-existant component could not be loaded, but doesn't
provide more detail.
 */
  public void testInvalid() {
    Log.getFindings().clear();
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.NonExistantReferencedSubComponent");
    ExpectedErrorInfo errors = new ExpectedErrorInfo(2,"xA1038");
    errors.checkExpectedPresent(Log.getFindings(),"No errors found!");
  }
}
