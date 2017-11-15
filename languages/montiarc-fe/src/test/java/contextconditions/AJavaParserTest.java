/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AJavaParserTest {
  
  private static final String MODELPATH = "src/test/resources/";
  
  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAJava() {
//    test("/valid/Foo.maa", true);
  }
  
  
  @Test
  public void testAutomaton() {
    test("contextconditions/valid/BumpControl.arc", true);
  }
  
  private void test(String modelName, boolean containsAJava){
    MontiArcParser parser = new MontiArcParser();
    try {
      Optional<ASTMACompilationUnit> cu = parser.parseMACompilationUnit(MODELPATH+modelName);
      assertTrue(cu.isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  @Test
  public void testCompWithInitMethod() {
    test("contextconditions/valid/CompWithVariableAndPortInit.arc", true);
  }
  
}
