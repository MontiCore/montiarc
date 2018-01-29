/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.head.parameters;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.DefaultParametersCorrectlyAssigned;
import montiarc.cocos.DefaultParametersHaveCorrectOrder;

/**
 * This class checks all context conditions related to default parameters
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class DefaultParametersTest extends AbstractCoCoTest {

  private static final String MP = "";
  
  private static final String PACKAGE = "component.head.parameters";
  
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testDefaultParametersInCorrectOrder() {  
    checkValid(MP, PACKAGE + "." + "DefaultParametersInCorrectOrder");
  }

  @Test
  public void testDefaultParametersInIncorrectOrder() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersHaveCorrectOrder()),
        getAstNode(MP, PACKAGE + "." + "DefaultParametersInIncorrectOrder"),
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA056"));
  }
  
  @Test
  public void testWrongDefaultParameterType() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersCorrectlyAssigned()),
        getAstNode("", PACKAGE + "." + "DefaultParameterWrongType"),
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA061"));
  }

}
