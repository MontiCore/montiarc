/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.TypeParameterNamesUnique;


/**
 * @author (last commit) Crispin Kirchner
 */
public class TypeParameterNamesUniqueTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValidModel() {
    checkValid("contextconditions", "valid.TypeParameterNamesUnique");
  }
  
  @Test
  public void testInvalidModel() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new TypeParameterNamesUnique()),
        getAstNode("contextconditions", "invalid.TypeParameterNamesNotUnique"),
        new ExpectedErrorInfo(1, "x35F1A"));
  }
}
