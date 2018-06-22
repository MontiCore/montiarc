/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.head.name;

import org.junit.BeforeClass;

import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import montiarc._ast.ASTMontiArcNode;

/**
 * This class checks all context conditions related to component names
 *
 * @author (last commit) kirchhof
 * @version 4.2.0, 30.01.2018
 * @since 4.2.0
 */
public class NameTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.head.name";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Ignore("Not Resolvable in Symboltable. TODO catch error in Symboltable and enable Test again.")
  @Test
  public void testNameClash() {
    Log.getFindings().clear();
    
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "NameClashB");
    // checkInvalid(new MontiArcCoCoChecker, node, expectedErrors);
  }

}
