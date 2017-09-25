/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.javap;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit;
import de.monticore.lang.montiarc.montiarc._parser.MontiArcParser;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ParserTest {
  
  private static final String MODELPATH = "src/test/resources/javap";
  
  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAJava() {
    test("/valid/Foo.maa", true);
  }
  
  
  @Test
  public void testAutomaton() {
    test("/valid/bumperbot/Bumpcontrol.maa", true);
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
    test("/valid/CompWithVariableAndPortInit.maa", true);
  }
  
}
