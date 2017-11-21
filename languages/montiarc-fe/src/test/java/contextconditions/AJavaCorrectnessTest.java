/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;

public class AJavaCorrectnessTest extends AJavaCocoTest {

  protected final String MODEL_PATH = "src/test/resources/";

  @Before
  public void setup(){
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testUsedPortsExist() {
    checkValid(MODEL_PATH, "contextconditions.valid.UsedPortsExist");
  }
  
  @Test
  public void testNewVariableDecl() {    
    checkValid(MODEL_PATH, "contextconditions.valid.NewVarDecl");
  }
  
  @Test
  public void testUsedPortsNotExist() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.UsedPortsNotExist");
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA030"));
  }
  
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,"contextconditions.invalid.ComponentWithAJavaAndAutomaton");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA050"));
  }

  @Test
  public void testChangeOfIncomingPort() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions", "invalid.ChangesIncomingPortInCompute");
    checkInvalid(node, new ExpectedErrorInfo(4, "xMA078"));
  }
  
}
