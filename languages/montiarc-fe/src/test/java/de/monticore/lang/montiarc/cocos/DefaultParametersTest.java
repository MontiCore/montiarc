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
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class DefaultParametersTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testDefaultParametersHaveCorrectOrder() {
    runCheckerWithSymTab("arc4/coco", "valid.DefaultParametersHaveRightOrder");
    assertEquals(0, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xAC005"))
        .count());
  }

  @Test
  public void testDefaultParameterHaveWrongOrder() {
    runCheckerWithSymTab("arc4/coco", "invalid.DefaultParametersHaveWrongOrder");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xAC005"))
        .count());
  }

}
