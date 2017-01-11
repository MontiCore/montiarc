/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava.cocos;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CorrectnessTest extends AbstractCocoTest {
  
  @Before
  public void setup(){
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testUsedPortsExist() {
    checkValid("src/test/resources/", "cocos.correctness.valid.UsedPortsExist");
  }
  
  @Test
  public void testNewVariableDecl() {    
    checkValid("src/test/resources/", "cocos.correctness.valid.NewVarDecl");
  }
  
  @Test
  public void testUsedPortsNotExist() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "cocos.correctness.invalid.UsedPortsNotExist");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA330"));
  }
  
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    ASTMontiArcNode node = getAstNode("src/test/resources/","cocos.correctness.invalid.ComponentWithAJavaAndAutomaton");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAB140"));
  }
  
}
