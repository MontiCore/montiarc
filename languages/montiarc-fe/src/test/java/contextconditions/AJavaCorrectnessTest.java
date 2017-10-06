/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;

public class AJavaCorrectnessTest extends AJavaCocoTest {
  
  @Before
  public void setup(){
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testUsedPortsExist() {
    checkValid("src/test/resources/", "contextconditions.valid.UsedPortsExist");
  }
  
  @Test
  public void testNewVariableDecl() {    
    checkValid("src/test/resources/", "contextconditions.valid.NewVarDecl");
  }
  
  @Test
  public void testUsedPortsNotExist() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "contextconditions.invalid.UsedPortsNotExist");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA330"));
  }
  
  @Ignore
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    ASTMontiArcNode node = getAstNode("src/test/resources/","contextconditions.invalid.ComponentWithAJavaAndAutomaton");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAB140"));
  }
  
}
