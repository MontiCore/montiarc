/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import java.io.IOException;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.PortNamesAreUnique;
import montiarc.cocos.UniqueConstraint;

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
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new PortNamesAreUnique()),
        getAstNode("arc/coco/uniqueness", "a.E1"), new ExpectedErrorInfo(2, "xMA053"));
  }
  
  @Test
  public void testPortImplicitNamingOneExplicitNamed()
      throws RecognitionException, IOException {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new PortNamesAreUnique()),
        getAstNode("arc/coco/uniqueness", "a.E1_2"), new ExpectedErrorInfo(1, "xMA053"));
  }
  
  @Test
  public void testUniqueConstraints() throws RecognitionException, IOException {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new UniqueConstraint()),
        getAstNode("arc/coco/uniqueness", "a.UniqueConstraint"),
        new ExpectedErrorInfo(2, "xMA052"));
  }
  
}
