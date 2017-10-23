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
  public void testValid() {
    checkValid("contextconditions", "valid.TypeParameterNamesUnique");
    
    // runCheckerWithSymTab("contextconditions", "valid.TypeParameterNamesUnique");
    // assertEquals(
    // Log.getFindings().stream().map(f -> f.buildMsg()).collect(Collectors.joining("\n")),
    // 0, Log.getFindings().size());
  }
  
  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new TypeParameterNamesUnique()),
        getAstNode("contextconditions", "invalid.TypeParameterNamesNotUnique"),
        new ExpectedErrorInfo(1, "xMA006"));
    
//    runCheckerWithSymTab("contextconditions", "invalid.TypeParameterNamesNotUnique");
//    String findings = Log.getFindings().stream().map(f -> f.buildMsg())
//        .collect(Collectors.joining("\n"));
//    assertEquals(findings, 1, Log.getFindings().size());
//    assertTrue(findings.contains("xMA006"));
  }
}
