/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.javap;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit;
import de.monticore.lang.montiarc.montiarc._parser.MontiArcParser;
import de.se_rwth.commons.logging.Log;

public class ParserTest {
  
  private static final String MODELPATH = "src/test/resources";
  
  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAJava() {
    test("/javap/valid/Foo.arc", true);
  }
  
  
  @Test
  public void testAutomaton() {
    test("/javap/valid/bumperbot/BumpControl.arc", true);
  }
  
  private void test(String modelName, boolean containsAJava){
    MontiArcParser parser = new MontiArcParser();
    try {
      Optional<ASTMACompilationUnit> cu = parser.parseMACompilationUnit(MODELPATH+modelName);
      assertTrue(cu.isPresent());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  @Test
  public void testCompWithInitMethod() {
    test("/javap/valid/CompWithVariableAndPortInit.arc", true);
  }
  
}
