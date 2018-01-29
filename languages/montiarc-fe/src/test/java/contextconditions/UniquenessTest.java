/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import java.io.IOException;

import montiarc.cocos.IdentifiersAreUnique;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;

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
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()),
        getAstNode("arc/coco/uniqueness", "a.E1"), new AbstractCoCoTestExpectedErrorInfo(2, "xMA053"));
  }
  
  @Test
  public void testPortImplicitNamingOneExplicitNamed()
      throws RecognitionException, IOException {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()),
        getAstNode("arc/coco/uniqueness", "a.E1_2"), new AbstractCoCoTestExpectedErrorInfo(1, "xMA053"));
  }
  
  @Test
  public void testUniqueConstraints() throws RecognitionException, IOException {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()),
        getAstNode("arc/coco/uniqueness", "a.UniqueConstraint"),
        new AbstractCoCoTestExpectedErrorInfo(2, "xMA052"));
  }
  
}
