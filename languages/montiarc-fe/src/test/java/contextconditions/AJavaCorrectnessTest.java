/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.Before;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

public class AJavaCorrectnessTest extends AbstractCoCoTest {

  @Before
  public void setup(){
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testUsedPortsExist() {
    checkValid("", "contextconditions.valid.UsedPortsExist");
  }
  
  @Test
  public void testNewVariableDecl() {    
    checkValid("", "contextconditions.valid.NewVarDecl");
  }
  

  
  
}
