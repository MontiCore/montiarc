/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

/**
 * Tests various forms of component uniqueness
 *
 * @author Robert Heim
 */
public class UniquenessTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testPortNamingUnique() throws RecognitionException, IOException {
    runCheckerWithSymTab("arc/coco/uniqueness", "a.E1");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xAC002"))
        .count());
  }

  @Test
  public void testPortImplicitNamingOneExplicitNamed()
      throws RecognitionException, IOException {
    runCheckerWithSymTab("arc/coco/uniqueness", "a.E1_2");
    assertEquals(1,
        Log.getFindings().stream().filter(f -> f.buildMsg().contains("xAC002")).count());
  }

  @Test
  public void testUniqueConstraints() throws RecognitionException, IOException {
    runCheckerWithSymTab("arc/coco/uniqueness", "a.UniqueConstraint");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xAC001"))
        .count());
  }

}
