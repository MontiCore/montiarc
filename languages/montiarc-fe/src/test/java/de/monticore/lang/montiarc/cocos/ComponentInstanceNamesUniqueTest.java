/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._cocos.MontiArcCoCoChecker;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robert Heim, Crispin Kirchner
 */
public class ComponentInstanceNamesUniqueTest extends AbstractCoCoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValid() {
    checkValid("arc4/coco", "valid.ComponentInstanceNamesUnique");
    // runCheckerWithSymTab("arc4/coco", "valid.ComponentInstanceNamesUnique");
    // assertEquals(
    // Log.getFindings().stream().map(f -> f.buildMsg()).collect(Collectors.joining("\n")),
    // 0, Log.getFindings().size());
  }
  
  @Test
  public void testInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentInstanceNamesAreUnique()),
        getAstNode("arc4/coco", "invalid.ComponentInstanceNamesNotUnique"),
        new ExpectedErrorInfo(2, "xAC010"));
    
//    runCheckerWithSymTab("arc4/coco", "invalid.ComponentInstanceNamesNotUnique");
//    assertEquals(
//        Log.getFindings().stream().map(f -> f.buildMsg()).collect(Collectors.joining("\n")),
//        2, Log.getFindings().size());
  }
}
