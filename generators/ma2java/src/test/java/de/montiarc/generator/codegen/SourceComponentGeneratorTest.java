/**
 * 
 */
package de.montiarc.generator.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.montiarc.generator.MontiArcGeneratorConstants;
import de.montiarc.generator.MontiArcScript;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.StringTransformations;

/**
 * Tests for code generation of source components. <br>
 * <br>
 * Copyright (c) 2012 RWTH Aachen. All rights reserved.
 *
 * @author (last commit) $Author: ahaber $
 * @version $Date: 2015-02-02 14:49:50 +0100 (Mo, 02 Feb 2015) $<br>
 * $Revision: 3110 $
 */
public class SourceComponentGeneratorTest extends AbstractSymtabTest {
  /**
   * Name of time in ports.
   */
  public static final String CODEGEN_TIME_IN_PORTNAME = "sourceTickPort";
  
  // @BeforeClass
  // public static void beforeClassSetUp() {
  //
  // try {
  // FileUtils.copyFile(
  // new File(INPUT_TEST_FOLDER + "/arc/codegen/source/SuperComponentImpl.java")
  // ,
  // new File(OUTPUT_TEST_FOLDER + "/source/SuperComponentImpl.java"));
  // }
  // catch (IOException e) {
  // e.printStackTrace();
  // }
  // SourceComponentGeneratorTest test = new SourceComponentGeneratorTest();
  // test.handler.clearErrorList();
  // MontiArcGeneratorTestTool tool = test.createTestToolWithTransformations(new
  // String[] {
  // INPUT_TEST_FOLDER + "/arc/codegen/source/SourceSubcomponent.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/source/SuperComponent.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/source/TimedTrigger.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/source/TimedTriggerUsage0.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/source/TimedTriggerUsage1.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/source/TimedTriggerUsage2.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/source/TimedTriggerUsage3.arc"},
  // new String[] {INPUT_TEST_FOLDER + "/arc/codegen/"},
  // "-generator", "mc.umlp.arc.ComponentMain", "montiarccomponent",
  // "-generator", "mc.umlp.arc.ComponentInterfaceMain", "montiarccomponent",
  // "-generator", "mc.umlp.arc.ComponentFactoryMain", "montiarccomponent");
  //
  // assertTrue(tool.run());
  // assertEquals(0, test.handler.getErrors().size());
  // assertEquals(0, test.handler.getWarnings().size());
  // }
  
  public static boolean hasCorrectModifier(JavaFieldSymbol field) {
    return field.isPrivate();
  }
  
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  private static SourceComponentGeneratorTest theInstance = new SourceComponentGeneratorTest();
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    theInstance.doSetup();
  }
  
  private JavaTypeSymbol getSymbolForName(String name) {
    return symTab.<JavaTypeSymbol> resolve(name, JavaTypeSymbol.KIND).orElse(null);
  }
  
  private void doSetup() {
    String[] models = { "source.SourceSubcomponent", "source.SuperComponent",
        "source.TimedTrigger",
        "source.TimedTriggerUsage0", "source.TimedTriggerUsage1", "source.TimedTriggerUsage2",
        "source.TimedTriggerUsage3" };
    symTab = createJavaSymTab(outputDirectory);
    for (String model : models) {
      new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), model,
          outputDirectory.toFile(), Optional.empty());
    }
  }
  
  
  @Test
  public void testAtomarSourceGeneration() {
    JavaTypeSymbol generatedComponentClass = getSymbolForName("source.ATimedTrigger");
    
    boolean foundPortField = false;
    boolean foundPortMethod = false;
    boolean foundTriggerMethod = false;
    
    for (JavaMethodSymbol m : generatedComponentClass.getMethods()) {
      if (m.getName().equals("_get" + StringTransformations.capitalize(CODEGEN_TIME_IN_PORTNAME))) {
        assertEquals(MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME, m.getReturnType().getName());
        assertEquals("Object", m.getReturnType().getActualTypeArguments().iterator()
            .next().getType().getName());
        assertTrue(m.isPublic());
        foundPortMethod = true;
      }
      if (m.getName().equals("timeStep")) {
        assertEquals("void", m.getReturnType().getName());
        assertTrue(m.isProtected());
        foundTriggerMethod = true;
      }
    }
    
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      if (field.getName().equals("_" + CODEGEN_TIME_IN_PORTNAME)) {
        assertEquals(MontiArcGeneratorConstants.IN_SIM_PORT_INTERFACE_NAME, field.getType()
            .getName());
        assertEquals("Object", field.getType().getActualTypeArguments().iterator().next()
            .getType().getName());
        assertTrue(hasCorrectModifier(field));
        foundPortField = true;
        break;
      }
    }
    
    assertTrue(foundPortMethod);
    assertFalse(foundTriggerMethod);
    assertTrue(foundPortField);
  }
  
  @Test
  public void testDecomposedSourceOneInnerSourceGeneration() {
    
    JavaTypeSymbol generatedComponentClass = getSymbolForName("source.TimedTriggerUsage0");
    
    boolean foundPortField = false;
    boolean foundPortMethod = false;
    boolean foundTriggerMethod = false;
    
    for (JavaMethodSymbol m : generatedComponentClass.getMethods()) {
      if (m.getName().equals("_get" + StringTransformations.capitalize(CODEGEN_TIME_IN_PORTNAME))) {
        assertEquals(MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME, m.getReturnType().getName());
        assertEquals("Object", m.getReturnType().getActualTypeArguments().iterator()
            .next().getType().getName());
        assertTrue(m.isPublic());
        foundPortMethod = true;
      }
      if (m.getName().equals("timeStep")) {
        foundTriggerMethod = true;
      }
    }
    
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      if (field.getName().equals("_" + CODEGEN_TIME_IN_PORTNAME)) {
        foundPortField = true;
        assertTrue(hasCorrectModifier(field));
        break;
      }
    }
    
    assertTrue(foundPortMethod);
    assertFalse(foundPortField);
    assertTrue(foundTriggerMethod);
  }
  
  @Test
  public void testDecomposedSourceTwoInnerSourceGeneration() {
    
    JavaTypeSymbol generatedComponentClass = getSymbolForName("source.TimedTriggerUsage1");
    
    boolean foundPortField = false;
    boolean foundPortMethod = false;
    boolean foundTriggerMethod = false;
    
    for (JavaMethodSymbol m : generatedComponentClass.getMethods()) {
      if (m.getName().equals("_get" + StringTransformations.capitalize(CODEGEN_TIME_IN_PORTNAME))) {
        assertEquals(MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME, m.getReturnType().getName());
        assertEquals("Object", m.getReturnType().getActualTypeArguments().iterator()
            .next().getType().getName());
        assertTrue(m.isPublic());
        foundPortMethod = true;
      }
      if (m.getName().equals("timeStep")) {
        foundTriggerMethod = true;
      }
    }
    
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      if (field.getName().equals("_" + CODEGEN_TIME_IN_PORTNAME)) {
        assertEquals("sim.port.IForwardPort", field.getType().getName());
        assertEquals("Object", field.getType().getActualTypeArguments().iterator().next()
            .getType().getName());
        assertTrue(hasCorrectModifier(field));
        foundPortField = true;
        break;
      }
    }
    
    assertTrue(foundPortMethod);
    assertTrue(foundPortField);
    assertTrue(foundTriggerMethod);
  }
  
  @Test
  public void testDecomposedSourceInnerSourceMixedGeneration() {
    
    JavaTypeSymbol generatedComponentClass = getSymbolForName("source.TimedTriggerUsage2");
    
    boolean foundPortField = false;
    boolean foundPortMethod = false;
    boolean foundTriggerMethod = false;
    
    for (JavaMethodSymbol m : generatedComponentClass.getMethods()) {
      if (m.getName().equals("_get" + StringTransformations.capitalize(CODEGEN_TIME_IN_PORTNAME))) {
        foundPortMethod = true;
      }
      if (m.getName().equals("timeStep")) {
        foundTriggerMethod = true;
      }
    }
    
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      if (field.getName().equals("_" + CODEGEN_TIME_IN_PORTNAME)) {
        foundPortField = true;
        assertTrue(hasCorrectModifier(field));
        break;
      }
    }
    
    assertFalse(foundPortMethod);
    assertFalse(foundPortField);
    assertTrue(foundTriggerMethod);
  }
  
  @Test
  public void testDecomposedSourceInnerSourceMixed2Generation() {
    
    JavaTypeSymbol generatedComponentClass = getSymbolForName("source.TimedTriggerUsage3");
    
    boolean foundPortField = false;
    boolean foundPortMethod = false;
    boolean foundTriggerMethod = false;
    
    for (JavaMethodSymbol m : generatedComponentClass.getMethods()) {
      if (m.getName().equals("_get" + StringTransformations.capitalize(CODEGEN_TIME_IN_PORTNAME))) {
        foundPortMethod = true;
      }
      if (m.getName().equals("timeStep")) {
        foundTriggerMethod = true;
      }
    }
    
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      if (field.getName().equals("_" + CODEGEN_TIME_IN_PORTNAME)) {
        foundPortField = true;
        assertTrue(hasCorrectModifier(field));
        break;
      }
    }
    
    assertFalse(foundPortMethod);
    assertFalse(foundPortField);
    assertTrue(foundTriggerMethod);
  }
  
  @Test
  public void testSourceComponentExtendsOtherComponents() {
    
    JavaTypeSymbol generatedComponentClass = getSymbolForName("source.ASourceSubcomponent");
    
    boolean foundPortField = false;
    boolean foundPortMethod = false;
    boolean foundTriggerMethod = false;
    
    for (JavaMethodSymbol m : generatedComponentClass.getMethods()) {
      if (m.getName().equals("_get" + StringTransformations.capitalize(CODEGEN_TIME_IN_PORTNAME))) {
        foundPortMethod = true;
      }
      if (m.getName().equals("timeStep")) {
        foundTriggerMethod = true;
      }
    }
    
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      if (field.getName().equals("_" + CODEGEN_TIME_IN_PORTNAME)) {
        foundPortField = true;
        assertTrue(hasCorrectModifier(field));
        break;
      }
    }
    assertFalse(foundPortMethod);
    assertFalse(foundPortField);
    assertFalse(foundTriggerMethod);
  }
  
}
