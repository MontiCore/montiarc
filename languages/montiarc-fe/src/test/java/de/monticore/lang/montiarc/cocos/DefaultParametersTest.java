/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

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
    runCheckerWithSymTab("contextconditions", "valid.DefaultParametersHaveRightOrder");
    assertEquals(0, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xAC005"))
        .count());
  }

  @Test
  public void testDefaultParameterHaveWrongOrder() {
    runCheckerWithSymTab("contextconditions", "invalid.DefaultParametersHaveWrongOrder");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xAC005"))
        .count());
  }

}
