/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.head.generics;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.TypeParameterNamesUnique;

/**
 * This class checks all context conditions related to the definition of generic type parameters
 *
 * @author Crispin Kirchner, Andreas Wortmann
 */
public class GenericsTest extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "component.head.generics";
  
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testTypeParameterNamesUniqueValid() {
    checkValid(MP, PACKAGE + "." + "TypeParameterNamesUnique");
  }
  
  @Test
  public void testTypeParameterNamesUniqueInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new TypeParameterNamesUnique()),
        getAstNode(MP, PACKAGE + "." + "TypeParameterNamesAbiguous"),
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA006"));
  }
}
