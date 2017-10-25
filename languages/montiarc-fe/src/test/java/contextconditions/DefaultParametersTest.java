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
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.DefaultParametersHaveCorrectOrder;

/**
 * Test checks the cocos ${DefaultParameterReferencingVariable} and
 * ${DefaultParametersHaveCorrectOrder}
 *
 * @author Robert Heim
 */
public class DefaultParametersTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testDefaultParametersHaveCorrectOrder() {  
    checkValid("contextconditions", "valid.DefaultParametersHaveRightOrder");
  }

  @Test
  public void testDefaultParameterHaveWrongOrder() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersHaveCorrectOrder()),
        getAstNode("contextconditions", "invalid.DefaultParametersHaveWrongOrder"),
        new ExpectedErrorInfo(1, "xMA056"));
  }
  
  @Ignore("TODO Ticket #56: Coco schreiben, CoCo im Parameter initialisieren, Fehlercode anpassen.")
  @Test
  public void testWrongDefaultParameterType() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersHaveCorrectOrder()),
        getAstNode("contextconditions", "invalid.WrongDefaultParameterType"),
        new ExpectedErrorInfo(1, "xMA056"));
    
  }
  
  @Test
  public void testComplexInvalidParametersOrder() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersHaveCorrectOrder()),
        getAstNode("contextconditions", "invalid.ComplexInvalidParametersOrder"),
        new ExpectedErrorInfo(1, "xMA056"));
  }

}
