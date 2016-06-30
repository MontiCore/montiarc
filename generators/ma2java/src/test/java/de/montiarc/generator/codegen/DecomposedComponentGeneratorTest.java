/**
 * 
 */
package de.montiarc.generator.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import de.montiarc.generator.MontiArcScript;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;

/**
 * Tests for decomposed components code generation. <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @date 02.07.2010
 */
public class DecomposedComponentGeneratorTest extends AbstractSymtabTest {
  
  protected static JavaTypeSymbol generatedComponentClass = null;
  
  protected static DecomposedComponentGeneratorTest theInstance = new DecomposedComponentGeneratorTest();
  
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    theInstance.doSetup();
  }
  
  private void doSetup() {
    // 1. load model: arc/codegen/a/ComplexComponent.arc
    String[] models = { "a.ArchitecturalComponent1", "c.Generic", "timeproc.MixedTiming" };
    symTab = createSymTab(modelPath);
    for (String model : models) {
      ComponentSymbol comp = symTab
          .<ComponentSymbol> resolve(model, ComponentSymbol.KIND).orElse(null);
      assertNotNull(comp);
      
      // 2. execute generator
      new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), model,
          outputDirectory.toFile(), Optional.empty());
    }
    // 3. load generated java class
    symTab = createJavaSymTab(outputDirectory);
    generatedComponentClass = symTab.<JavaTypeSymbol> resolve(
        "a.ArchitecturalComponent1", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(generatedComponentClass);
  }
  
  // protected void doSetUpTest() {
  // handler.clearErrorList();
  // // generate code
  // MontiArcGeneratorTestTool tool = createTestToolWithTransformations(new
  // String[] {
  // INPUT_TEST_FOLDER + "/arc/codegen/a/ArchitecturalComponent1.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/timeproc/MixedTiming.arc",
  // INPUT_TEST_FOLDER + "/arc/codegen/c/Generic.arc" },
  // new String[] {INPUT_TEST_FOLDER + "/arc/codegen/"},
  // "-generator", "mc.umlp.arc.ComponentMain", "montiarccomponent",
  // "-generator", "mc.umlp.arc.ComponentInterfaceMain", "montiarccomponent",
  // "-generator", "mc.umlp.arc.ComponentFactoryMain", "montiarccomponent");
  // // setup CoCos
  //
  // assertTrue(tool.run());
  // assertEquals(0, handler.getErrors().size());
  // assertEquals(0, handler.getWarnings().size());
  //
  // // process generated file to create symtab entry
  // MontiArcGeneratorTestTool javaTool = createTestTool(
  // new String[]{OUTPUT_TEST_FOLDER + "/a/gen/ArchitecturalComponent1.java"},
  // INPUT_TEST_FOLDER + "/arc/codegen/",
  // OUTPUT_TEST_FOLDER);
  // assertTrue(javaTool.run());
  //
  // // get java root and load symbol entry
  // JavaDSLRoot javaRoot = (JavaDSLRoot) initSymtabForRoot(javaTool,
  // "a.gen.ArchitecturalComponent1");
  // try {
  // generatedComponentClass = (JavaTypeEntry)
  // resolver.resolve("a.gen.ArchitecturalComponent1", JavaTypeEntry.KIND,
  // getNameSpaceFor(javaRoot.getAst()));
  // }
  // catch (AmbigousException e) {
  // fail(e.getMessage());
  // }
  // assertNotNull(generatedComponentClass);
  //
  // try {
  // generatedComponentClass.loadFullVersion(modelLoader, deserializers);
  // }
  // catch (EntryLoadingErrorException e) {
  // fail(e.getMessage());
  // }
  // }
  
  @Test
  public void testParseGeneratedCode() {
    JavaDSLParser parser = new JavaDSLParser();
    try {
      parser.parse(outputDirectory.toString() + File.separator + "a" + File.separator
          + "ArchitecturalComponent1.java");
      parser.parse(outputDirectory.toString() + File.separator + "timeproc" + File.separator
          + "MixedTiming.java");
      
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testArchitecturalPort() {
    boolean foundNormalIn = false;
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      String fieldName = field.getName();
      // check, if a field has been generated for the architectural port
      // normalIn that has more than one receiver
      if (fieldName.equals("normalIn")) {
        foundNormalIn = true;
        assertEquals("sim.port.IForwardPort", field.getType().getName());
      }
      // check, if no field has been generated for the architectural port
      // optimizedIn that has only one receiver
      if (fieldName.equals("optimizedIn")) {
        fail("No field must be generated for the incoming port optimizedIn, as it only has one receiver!");
      }
      
      if (fieldName.equals("output1")) {
        fail("No field must be generated for the outgoing port output1, as no fields must be generated for outgoing ports!");
      }
      
      if (fieldName.equals("output2")) {
        fail("No field must be generated for the outgoing port output2, as no fields must be generated for outgoing ports!");
      }
      
    }
    if (!foundNormalIn) {
      fail("A field for the port normalIn must exist but was not found!");
    }
  }
  
  @Test
  public void testReferencesFields() {
    boolean foundGen1 = false;
    boolean foundGen2 = false;
    boolean foundGen3 = false;
    boolean foundGen4 = false;
    
    for (JavaFieldSymbol field : generatedComponentClass.getFields()) {
      String fieldName = field.getName();
      
      if (fieldName.equals("gen1")) {
        foundGen1 = true;
        assertEquals("c.interfaces.IGeneric", field.getType().getName());
        // TODO revert this, if java symtab supports generic parameters
        // assertEquals(1,
        // field.getType().getTypeParameters().size());
        // assertEquals("java.lang.String",
        // field.getType().getTypeParameters().get(0).getName());
      }
      
      if (fieldName.equals("gen2")) {
        foundGen2 = true;
        assertEquals("c.interfaces.IGeneric", field.getType().getName());
        // TODO revert this, if java symtab supports generic parameters
        // assertEquals(1,
        // field.getType().getTypeParameters().size());
        // assertEquals("java.lang.String",
        // field.getType().getTypeParameters().get(0).getName());
      }
      
      if (fieldName.equals("gen3")) {
        foundGen3 = true;
        assertEquals("c.interfaces.IGeneric", field.getType().getName());
        // TODO revert this, if java symtab supports generic parameters
        // assertEquals(1,
        // field.getType().getTypeParameters().size());
        // assertEquals("java.lang.String",
        // field.getType().getTypeParameters().get(0).getName());
      }
      if (fieldName.equals("gen4")) {
        foundGen4 = true;
        assertEquals("c.interfaces.IGeneric", field.getType().getName());
        // TODO revert this, if java symtab supports generic parameters
        // assertEquals(1,
        // field.getType().getTypeParameters().size());
        // assertEquals("java.lang.String",
        // field.getType().getTypeParameters().get(0).getName());
      }
    }
    if (!foundGen1 || !foundGen2 || !foundGen3 || !foundGen4) {
      fail("Missing field for reference(s) " + (!foundGen1 ? "gen1 " : "")
          + (!foundGen2 ? "gen2 " : "") + (!foundGen3 ? "gen3 " : "") +
          (!foundGen4 ? "gen4 " : ""));
    }
  }
  
  @Test
  public void testReferencesGetters() {
    
    boolean foundGen1 = false;
    boolean foundGen2 = false;
    boolean foundGen3 = false;
    boolean foundGen4 = false;
    
    for (JavaMethodSymbol method : generatedComponentClass.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("getGen1")) {
        foundGen1 = true;
        
        assertEquals("c.interfaces.IGeneric",
            method.getReturnType().getName());
        assertEquals(1, method.getReturnType().getActualTypeArguments().size());
        assertEquals("java.lang.String",
            method.getReturnType().getActualTypeArguments().get(0)
                .getType().getName());
        assertEquals(0, method.getParameters().size());
      }
      if (methodName.equals("getGen2")) {
        foundGen2 = true;
        
        assertEquals("c.interfaces.IGeneric",
            method.getReturnType().getName());
        assertEquals(1, method.getReturnType().getActualTypeArguments().size());
        assertEquals("java.lang.String",
            method.getReturnType().getActualTypeArguments().get(0)
                .getType().getName());
        assertEquals(0, method.getParameters().size());
      }
      if (methodName.equals("getGen3")) {
        foundGen3 = true;
        
        assertEquals("c.interfaces.IGeneric",
            method.getReturnType().getName());
        assertEquals(1, method.getReturnType().getActualTypeArguments().size());
        assertEquals("java.lang.String",
            method.getReturnType().getActualTypeArguments().get(0)
                .getType().getName());
        assertEquals(0, method.getParameters().size());
      }
      if (methodName.equals("getGen4")) {
        foundGen4 = true;
        
        assertEquals("c.interfaces.IGeneric",
            method.getReturnType().getName());
        assertEquals(1, method.getReturnType().getActualTypeArguments().size());
        assertEquals("java.lang.String",
            method.getReturnType().getActualTypeArguments().get(0)
                .getType().getName());
        assertEquals(0, method.getParameters().size());
      }
    }
    if (!foundGen1 || !foundGen2 || !foundGen3 || !foundGen4) {
      fail("Missing protected getter method for reference(s) " + (!foundGen1 ?
          "gen1 " : "")
          + (!foundGen2 ? "gen2 " : "") + (!foundGen3 ? "gen3 " : "") + (!foundGen4 ?
              "gen4 " : ""));
    }
  }
  
  @Test
  public void testMixedTiming() {
    File generatedClass = new File(outputDirectory.toString() +
        File.separator + "timeproc" + File.separator + "MixedTiming.java");
    assertTrue(generatedClass.exists());
    try {
      String content = FileUtils.readFileToString(generatedClass);
      String matcher = "public int getLocalTime() {";
      content = content.substring(content.indexOf(matcher) + matcher.length());
      content = content.substring(0, content.indexOf("}")).trim();
      assertFalse(content.contains("utc.getLocalTime()"));
      assertFalse(content.contains("utsc.getLocalTime()"));
      assertTrue(content.contains("s1.getLocalTime()"));
      assertTrue(content.contains("s2.getLocalTime()"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
