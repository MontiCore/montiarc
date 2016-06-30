/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;


import static de.montiarc.generator.codegen.GeneratorTestConstants.INPORT_ATTRIBUTE_TYPE;

import static de.montiarc.generator.codegen.GeneratorTestConstants.INPORT_GETTER_RETURN_TYPE;
import static de.montiarc.generator.codegen.GeneratorTestConstants.OUTPORT_ATTRIBUTE_TYPE;
import static de.montiarc.generator.codegen.GeneratorTestConstants.OUTPORT_GETTER_RETURN_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.montiarc.generator.MontiArcScript;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.Scope;

/**
 * The test-setup does the following:<br/>
 * 1. load the model arc/codegen/a/ComplexComponent.arc using the symbol-table.<br/>
 * 2. execute the generator<br/>
 * 3. load the generated java-implementation of the component<br/>
 * The different test-methods then check specific aspects of the java-implementation.
 * 
 * @author Robert Heim
 */
public class AtomicComponentGeneratorTest extends AbstractSymtabTest {
  
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  private static AtomicComponentGeneratorTest theInstance = new AtomicComponentGeneratorTest();
  
  // set once in doSetup
  private static JavaTypeSymbol generatedComponentClass = null;
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    theInstance.doSetup();
  }
  
  private void doSetup() {
    // 1. load model: arc/codegen/a/ComplexComponent.arc
    String model = "a.ComplexComponent";
    symTab = createSymTab(modelPath);
    ComponentSymbol comp = symTab
        .<ComponentSymbol> resolve(model, ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("a.ComplexComponent", comp.getFullName());
    
    // 2. execute generator
    new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), model,
        outputDirectory.toFile(),Optional.empty());
        
    // 3. load generated java class
    symTab = createJavaSymTab(outputDirectory);
    generatedComponentClass = symTab.<JavaTypeSymbol> resolve(
        "a.AComplexComponent", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(generatedComponentClass);
  }
  
  
  @Test
  public void testSendMethods() {
    JavaMethodSymbol met = generatedComponentClass.getMethod("sendStrOut1").orElse(null);
    assertNotNull(met);
    assertEquals("void", met.getReturnType().getName());
    assertEquals(1, met.getParameters().size());
    assertEquals("java.lang.String", met.getParameters().get(0).getType().getName());
        
    met = generatedComponentClass.getMethod("sendVOut").orElse(null);
    assertNotNull(met);
    assertEquals("void", met.getReturnType().getName());
    assertEquals(1, met.getParameters().size());
    assertEquals("V", met.getParameters().get(0).getType().getName());
  }
  
  @Test
  public void testTreatMethods() {
    JavaMethodSymbol met = generatedComponentClass.getMethod("treatStrIn1").orElse(null);
    assertNotNull(met);
    assertTrue(met.isAbstract());
    assertEquals("void", met.getReturnType().getName());
    assertEquals(1, met.getParameters().size());
    assertEquals("java.lang.String", met.getParameters().get(0).getType().getName());
    
    met = generatedComponentClass.getMethod("treatKIn").orElse(null);
    assertNotNull(met);
    assertTrue(met.isAbstract());
    assertEquals("void", met.getReturnType().getName());
    assertEquals(1, met.getParameters().size());
    assertEquals("K", met.getParameters().get(0).getType().getName());
  }
  
  @Test
  public void testSetMethods() {
    JavaMethodSymbol met = generatedComponentClass.getMethod("setStrOut1").orElse(null);
    assertNotNull(met);
    assertEquals("void", met.getReturnType().getName());
    assertEquals(1, met.getParameters().size());
    assertEquals("sim.port.IPort", met.getParameters().get(0).getType().getName());
    assertEquals(1, met.getParameters().get(0).getType().getActualTypeArguments().size());
    assertEquals("java.lang.String",
        met.getParameters().get(0).getType().getActualTypeArguments().get(0).getType().getName());
        
    met = generatedComponentClass.getMethod("setVOut").orElse(null);
    assertNotNull(met);
    assertEquals("void", met.getReturnType().getName());
    assertEquals(1, met.getParameters().size());
    assertEquals("sim.port.IPort", met.getParameters().get(0).getType().getName());
    assertEquals(1, met.getParameters().get(0).getType().getActualTypeArguments().size());
    assertEquals("V",
        met.getParameters().get(0).getType().getActualTypeArguments().get(0).getType().getName());
        
  }
  
  
  @Test
  public void testGetMethods() {
    JavaMethodSymbol met = generatedComponentClass.getMethod("getStrOut1").orElse(null);
    assertNotNull(met);
    assertEquals(OUTPORT_GETTER_RETURN_TYPE, met.getReturnType().getName());
    assertEquals(1, met.getReturnType().getActualTypeArguments().size());
    assertEquals("java.lang.String",
        met.getReturnType().getActualTypeArguments().get(0).getType().getName());
    assertEquals(0, met.getParameters().size());
    
    met = generatedComponentClass.getMethod("getVOut").orElse(null);
    assertNotNull(met);
    assertEquals(OUTPORT_GETTER_RETURN_TYPE, met.getReturnType().getName());
    assertEquals(1, met.getReturnType().getActualTypeArguments().size());
    assertEquals("V", met.getReturnType().getActualTypeArguments().get(0).getType().getName());
    assertEquals(0, met.getParameters().size());
    
    met = generatedComponentClass.getMethod("getStrIn1").orElse(null);
    assertNotNull(met);
    assertEquals(INPORT_GETTER_RETURN_TYPE, met.getReturnType().getName());
    assertEquals(1, met.getReturnType().getActualTypeArguments().size());
    assertEquals("java.lang.String",
        met.getReturnType().getActualTypeArguments().get(0).getType().getName());
    assertEquals(0, met.getParameters().size());
    
    met = generatedComponentClass.getMethod("getKIn").orElse(null);
    assertNotNull(met);
    assertEquals(INPORT_GETTER_RETURN_TYPE, met.getReturnType().getName());
    assertEquals(1, met.getReturnType().getActualTypeArguments().size());
    assertEquals("K", met.getReturnType().getActualTypeArguments().get(0).getType().getName());
    assertEquals(0, met.getParameters().size());
  }
  
  @Test
  public void testPortFields() {
    JavaFieldSymbol field = generatedComponentClass.getField("strIn1").orElse(null);
    assertNotNull(field);
    assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
    assertEquals(1, field.getType().getActualTypeArguments().size());
    assertEquals("java.lang.String",
        field.getType().getActualTypeArguments().get(0).getType().getName());
    assertTrue(field.isPrivate());
    field = generatedComponentClass.getField("kIn").orElse(null);
    assertNotNull(field);
    assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
    assertEquals(1, field.getType().getActualTypeArguments().size());
    assertEquals("K", field.getType().getActualTypeArguments().get(0).getType().getName());
    assertTrue(field.isPrivate());
    field = generatedComponentClass.getField("strOut1").orElse(null);
    assertNotNull(field);
    assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
    assertEquals(1, field.getType().getActualTypeArguments().size());
    assertEquals("java.lang.String",
        field.getType().getActualTypeArguments().get(0).getType().getName());
    assertTrue(field.isPrivate());
    field = generatedComponentClass.getField("vOut").orElse(null);
    assertNotNull(field);
    assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
    assertEquals(1, field.getType().getActualTypeArguments().size());
    assertEquals("V", field.getType().getActualTypeArguments().get(0).getType().getName());
    assertTrue(field.isPrivate());
  }
  
  @Test
  public void testParameterFields() {
    JavaFieldSymbol field = generatedComponentClass.getField("parStr").orElse(null);
    assertNotNull(field);
    assertEquals("java.lang.String", field.getType().getName());
    assertEquals(0, field.getType().getActualTypeArguments().size());
    field = generatedComponentClass.getField("parInt").orElse(null);
    assertNotNull(field);
    assertEquals("java.lang.Integer", field.getType().getName());
    assertEquals(0, field.getType().getActualTypeArguments().size());
  }

  @Test
  public void testParameterMethods() {
    JavaMethodSymbol met = generatedComponentClass.getMethod("getParStr").orElse(null);
    assertNotNull(met);
    assertEquals("java.lang.String", met.getReturnType().getName());
    assertEquals(0, met.getReturnType().getActualTypeArguments().size());
    assertEquals(0, met.getParameters().size());
    met = generatedComponentClass.getMethod("getParInt").orElse(null);
    assertNotNull(met);
    assertEquals("java.lang.Integer", met.getReturnType().getName());
    assertEquals(0, met.getReturnType().getActualTypeArguments().size());
    assertEquals(0, met.getParameters().size());
  }
  
  @Test
  public void testComponentTypeParameters() {
    assertEquals(2, generatedComponentClass.getFormalTypeParameters().size());
    JavaTypeSymbol par1 = generatedComponentClass.getFormalTypeParameters().get(0);
    assertEquals("K", par1.getName());
    assertFalse(par1.getSuperClass().isPresent());
    assertEquals(0, par1.getSuperTypes().size());
    JavaTypeSymbol par2 = generatedComponentClass.getFormalTypeParameters().get(1);
    assertEquals("V", par2.getName());
    assertFalse(par2.getSuperClass().isPresent());
    assertEquals(1, par2.getSuperTypes().size());
    assertEquals("java.lang.Number", par2.getSuperTypes().get(0).getName());
  }
  
  @Test
  public void testCommentsInClass() {
    String comment = GeneratorHelper
        .getCommentAsString(generatedComponentClass.getAstNode().get().get_PreComments());
    assertTrue(comment.contains(
        "This is a component comment that has to occur in the generated class and interface."));
  }
  
  @Test
  public void testCommentsInInterface() {
    JavaTypeSymbol interfaceRoot = symTab.<JavaTypeSymbol> resolve(
        "a.interfaces.IComplexComponent", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(interfaceRoot);
    assertTrue(interfaceRoot.getAstNode().isPresent());
    
    String comment = GeneratorHelper
        .getCommentAsString(interfaceRoot.getAstNode().get().get_PreComments());
    assertTrue(comment.contains(
        "This is a component comment that has to occur in the generated class and interface."));
  }
  
  @Ignore
  @Test
  public void testCommentsOnPortsInInterface() {
    JavaTypeSymbol interfaceRoot = symTab.<JavaTypeSymbol> resolve(
        "a.interfaces.IComplexComponent", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(interfaceRoot);
    assertTrue(interfaceRoot.getAstNode().isPresent());
    
    String comment = GeneratorHelper
        .getCommentAsString(interfaceRoot.getAstNode().get().get_PreComments());
    assertTrue(comment.contains("This is a comment for port strIn1."));
    assertTrue(comment.contains("This is a comment for port vOut."));
    
  }
}
