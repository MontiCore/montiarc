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
import montiarc.cocos.ParameterNamesAreUnique;

/**
 * This class tests all context conditions related to parameters
 * 
 * @author Crispin Kirchner, Andreas Wortmann
 */
public class ParametersTest extends AbstractCoCoTest {
	
  private static final String MP = "";
  private static final String PACKAGE = "component.head.parameters";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testParameterNamesUniqueTestValid() {
    checkValid(MP, PACKAGE + "." + "ParameterNamesUnique");
  }

  @Test
  public void testParameterNamesUniqueTestInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ParameterNamesAreUnique()),
        getAstNode(MP, PACKAGE + "." + "ParameterAmbiguous"),
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA069"));
    
  }
}
