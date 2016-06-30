/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcCoCoChecker;
import de.se_rwth.commons.logging.Log;

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
    checkValid("arc4/coco", "valid.ReferencedSubComponentExists");
  }
  
  @Test
  @Ignore("Symbol table already throws an exception, therefore the coco is never checked. A fix" + 
          " would be to stop the symbol table from theowing the exception, in order to have a" +
          " better error message")
  public void testInvalid() {
    MontiArcCoCoChecker coco = new MontiArcCoCoChecker()
        .addCoCo(new ReferencedSubComponentExists());
    ASTMontiArcNode node = getAstNode("arc4/coco", "invalid.NonExistantReferencedSubComponent");
    
    checkInvalid(coco, node, new ExpectedErrorInfo(2, "x069B7"));
  }
}
