/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import de.monticore.lang.montiarc.montiarc._cocos.MontiArcCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

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
    checkValid("arc4/coco", "valid.ParameterNamesUnique");
//    runCheckerWithSymTab("arc4/coco", "valid.ParameterNamesUnique");
//
//    String findings = Log.getFindings().stream().map(f -> f.buildMsg())
//        .collect(Collectors.joining("\n"));
//
//    assertEquals(findings, 0, Log.getFindings().size());
  }

  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ParameterNamesUnique()),
        getAstNode("arc4/coco", "invalid.ParameterNamesNotUnique"),
        new ExpectedErrorInfo(1, "xC4A61"));
    
//    runCheckerWithSymTab("arc4/coco", "invalid.ParameterNamesNotUnique");
//
//    String findings = Log.getFindings().stream().map(f -> f.buildMsg())
//        .collect(Collectors.joining("\n"));
//
//    assertEquals(findings, 1, Log.getFindings().size());
//    assertTrue(findings.contains("xC4A61"));
  }
}
