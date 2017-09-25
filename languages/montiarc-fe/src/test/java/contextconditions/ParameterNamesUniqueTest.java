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
import montiarc.cocos.ParameterNamesAreUnique;

/**
 * @author Crispin Kirchner
 */
public class ParameterNamesUniqueTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.ParameterNamesUnique");
//    runCheckerWithSymTab("contextconditions", "valid.ParameterNamesUnique");
//
//    String findings = Log.getFindings().stream().map(f -> f.buildMsg())
//        .collect(Collectors.joining("\n"));
//
//    assertEquals(findings, 0, Log.getFindings().size());
  }

  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ParameterNamesAreUnique()),
        getAstNode("contextconditions", "invalid.ParameterNamesNotUnique"),
        new ExpectedErrorInfo(1, "xC4A61"));
    
//    runCheckerWithSymTab("contextconditions", "invalid.ParameterNamesNotUnique");
//
//    String findings = Log.getFindings().stream().map(f -> f.buildMsg())
//        .collect(Collectors.joining("\n"));
//
//    assertEquals(findings, 1, Log.getFindings().size());
//    assertTrue(findings.contains("xC4A61"));
  }
}
