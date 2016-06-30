/**
 * 
 */
package de.montiarc.generator.codegen;

import static de.montiarc.generator.MontiArcGeneratorConstants.COMPONENT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.PORT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.SIMPLE_IN_PORT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.TIMED_COMPONENT_INTERFACE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.montiarc.generator.MontiArcScript;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Scope;

/**
 * Tests the codegenerator for component interfaces. <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @date 25.06.2010
 */
public class ComponentInterfaceCodegenTest extends AbstractSymtabTest {
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  protected static ComponentInterfaceCodegenTest theInstance = new ComponentInterfaceCodegenTest();
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void generateCode() {
    theInstance.doGenerateCode();
  }
  
  private void doGenerateCode() {
    // TODO was for this test: MontiArcGeneratorConstants.optimizeSingleIn =
    // true;
    // generate code
    List<String> models = Arrays.asList(
        "a.SomeComp",
        "a.AutoConnectGeneric",
        "a.SimpleInComponent",
        "a.UntimedSimpleInComponent",
        "a.UntimedComponent",
        "a.ComplexComponent",
        "a.ComponentWithSuperComponent");
    
    for (String model : models) {
      new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), model,
          outputDirectory.toFile(), Optional.empty());
    }
    // "-generator", "mc.umlp.arc.ComponentInterfaceMain", "montiarccomponent");
  }
  
  /**
   * Load generated java class from outputDirectory.
   * 
   * @param qualifiedName qualified name of the model to load
   * @return the type symbol from the generated java interface
   */
  protected JavaTypeSymbol loadJavaSymbol(String qualifiedName) {
    symTab = createJavaSymTab(outputDirectory);
    JavaTypeSymbol sym = symTab.<JavaTypeSymbol> resolve(qualifiedName, JavaTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(sym);
    return sym;
  }
  
  @Test
  public void testMethods() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.ISomeComp");
    assertEquals(4, generatedInterface.getMethods().size());
    
    JavaMethodSymbol method = generatedInterface.getMethod("getMyIn").get();
    assertEquals(0, method.getParameters().size());
    JavaTypeSymbolReference returnType = method.getReturnType();
    assertEquals(IN_PORT_INTERFACE_NAME, returnType.getName());
    assertFalse(returnType.getActualTypeArguments().isEmpty());
    assertEquals("java.lang.String",
        returnType.getActualTypeArguments().get(0).getType().getName());
    
    method = generatedInterface.getMethod("getMyListIn").get();
    assertEquals(0, method.getParameters().size());
    returnType = method.getReturnType();
    assertEquals(IN_PORT_INTERFACE_NAME, returnType.getName());
    assertFalse(returnType.getActualTypeArguments().isEmpty());
    assertEquals("java.util.List",
        returnType.getActualTypeArguments().get(0).getType().getName());
    
    method = generatedInterface.getMethod("getMyOut").get();
    assertEquals(0, method.getParameters().size());
    returnType = method.getReturnType();
    assertEquals(OUT_PORT_INTERFACE_NAME, returnType.getName());
    assertFalse(returnType.getActualTypeArguments().isEmpty());
    assertEquals("java.lang.String",
        returnType.getActualTypeArguments().get(0).getType().getName());
    
    method = generatedInterface.getMethod("setMyOut").get();
    assertEquals(1, method.getParameters().size());
    assertEquals(PORT_INTERFACE_NAME, method.getParameters().get(0).getType().getName());
    returnType = method.getReturnType();
    assertEquals("void", returnType.getName());
  }
  
  @Test
  public void testSimpleOutComponentShape() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.ISomeComp");
    
    assertEquals(1, generatedInterface.getInterfaces().size());
    for (JavaTypeSymbolReference superInterface : generatedInterface.getInterfaces()) {
      String name = superInterface.getName();
      if (!name.equals(TIMED_COMPONENT_INTERFACE_NAME)) {
        fail("Unexpected super interface found!");
      }
    }
  }
  
  
  @Test
  public void testSimpleComponentShape() {
    try {
      FileUtils.copyFile(
          Paths.get("src/test/resources/arc/codegen/testtypes", "DBInterface.java").toFile(),
          Paths.get(outputDirectory.toString(), "testtypes", "DBInterface.java").toFile());
    }
    catch (IOException e1) {
      e1.printStackTrace();
      fail("error while copying test files");
    }
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.IAutoConnectGeneric");
    
    assertEquals(1, generatedInterface.getFormalTypeParameters().size());
    JavaTypeSymbol typeParameter = generatedInterface.getFormalTypeParameters().get(0);
    assertEquals("T", typeParameter.getName());
    assertEquals(1, typeParameter.getSuperTypes().size());
    JavaTypeSymbolReference dbInterfaceRef = typeParameter.getSuperTypes().get(0);
    assertEquals("testtypes.DBInterface", dbInterfaceRef.getName());
    assertTrue(dbInterfaceRef.existsReferencedSymbol());
    assertEquals("DBInterface", dbInterfaceRef.getReferencedSymbol().getName());
    assertEquals("testtypes.DBInterface", dbInterfaceRef.getReferencedSymbol().getFullName());
    
    // was 3 because of singleIn optimization
    assertEquals(1, generatedInterface.getInterfaces().size());
    
    // TODO remove port-interface-tests as singleIn optimization is not
    // implemented?
    for (JavaTypeSymbolReference superInterface : generatedInterface.getInterfaces()) {
      String name = superInterface.getName();
      if (name.equals(TIMED_COMPONENT_INTERFACE_NAME)) {
        
      }
      else if (name.equals(IN_PORT_INTERFACE_NAME)) {
        assertEquals(1, superInterface.getActualTypeArguments().size());
        assertEquals("T", superInterface.getActualTypeArguments().get(0).getType().getName());
      }
      else if (name.equals(SIMPLE_IN_PORT_INTERFACE_NAME)) {
        assertEquals(1, superInterface.getActualTypeArguments().size());
        assertEquals("T", superInterface.getActualTypeArguments().get(0).getType().getName());
      }
      else {
        fail("Unexpected super interface found: " + name);
      }
    }
  }
  
  @Test
  public void testSimpleInComponentShape() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.ISimpleInComponent");
    
    // was 3 because of singleIn optimization
    assertEquals(1, generatedInterface.getInterfaces().size());
    
    // TODO remove port-interface-tests as singleIn optimization is not
    // implemented?
    for (JavaTypeSymbolReference superInterface : generatedInterface.getInterfaces()) {
      String name = superInterface.getName();
      if (name.equals(TIMED_COMPONENT_INTERFACE_NAME)) {
        
      }
      else if (name.equals(IN_PORT_INTERFACE_NAME)) {
        assertEquals(1, superInterface.getActualTypeArguments().size());
        assertEquals("java.lang.String",
            superInterface.getActualTypeArguments().get(0).getType().getName());
      }
      else if (name.equals(SIMPLE_IN_PORT_INTERFACE_NAME)) {
        assertEquals(1, superInterface.getActualTypeArguments().size());
        assertEquals("java.lang.String",
            superInterface.getActualTypeArguments().get(0).getType().getName());
      }
      else {
        fail("Unexpected super interface found: " + name);
      }
    }
  }
  
  @Test
  public void testUntimedSimpleInComponentShape() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.IUntimedSimpleInComponent");
    
    // was 3 because of singleIn optimization
    assertEquals(1, generatedInterface.getInterfaces().size());
    
    // TODO remove port-interface-tests as singleIn optimization is not
    // implemented?
    for (JavaTypeSymbolReference superInterface : generatedInterface.getInterfaces()) {
      String name = superInterface.getName();
      if (name.equals(COMPONENT_INTERFACE_NAME)) {
        
      }
      else if (name.equals(IN_PORT_INTERFACE_NAME)) {
        assertEquals(1, superInterface.getActualTypeArguments().size());
        assertEquals("java.lang.String",
            superInterface.getActualTypeArguments().get(0).getType().getName());
      }
      else if (name.equals(SIMPLE_IN_PORT_INTERFACE_NAME)) {
        assertEquals(1, superInterface.getActualTypeArguments().size());
        assertEquals("java.lang.String",
            superInterface.getActualTypeArguments().get(0).getType().getName());
      }
      else {
        fail("Unexpected super interface found: " + name);
      }
    }
  }
  
  @Test
  public void testUntimedComponentShape() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.IUntimedComponent");
    
    assertEquals(1, generatedInterface.getInterfaces().size());
    for (JavaTypeSymbolReference superInterface : generatedInterface.getInterfaces()) {
      String name = superInterface.getName();
      if (name.equals(COMPONENT_INTERFACE_NAME)) {
        
      }
      else {
        fail("Unexpected super interface found: " + name);
      }
    }
  }
  
  
  @Test
  public void testComplexComponentsShape() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.IComplexComponent");
    
    assertEquals(1, generatedInterface.getInterfaces().size());
    for (JavaTypeSymbolReference superInterface : generatedInterface.getInterfaces()) {
      String name = superInterface.getName();
      if (name.equals(TIMED_COMPONENT_INTERFACE_NAME)) {
        
      }
      else {
        fail("Unexpected super interface found!");
      }
    }
  }
  
  @Test
  public void testSuperComponentInterface() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.IComponentWithSuperComponent");
    
    // check interface from component with super component
    assertEquals(1, generatedInterface.getInterfaces().size());
    JavaTypeSymbolReference superInterface = generatedInterface.getInterfaces().iterator().next();
    assertEquals("a.interfaces.IComplexComponent", superInterface.getName());
    assertEquals("IComplexComponent", superInterface.getReferencedSymbol().getName());
    assertEquals("a.interfaces.IComplexComponent",
        superInterface.getReferencedSymbol().getFullName());
    
    // check interface from component without super component
    assertEquals(1, superInterface.getReferencedSymbol().getInterfaces().size());
    assertEquals(TIMED_COMPONENT_INTERFACE_NAME,
        superInterface.getReferencedSymbol().getInterfaces().iterator().next().getName());
  }
  
  @Test
  public void testGenericComponentInterface() {
    JavaTypeSymbol generatedInterface = loadJavaSymbol("a.interfaces.IComplexComponent");
    assertEquals(2, generatedInterface.getFormalTypeParameters().size());
    
    JavaTypeSymbol par1 = generatedInterface.getFormalTypeParameters().get(0);
    assertEquals("K", par1.getName());
    assertFalse(par1.getSuperClass().isPresent());
    assertTrue(par1.getSuperTypes().isEmpty());
    
    JavaTypeSymbol par2 = generatedInterface.getFormalTypeParameters().get(1);
    assertEquals("V", par2.getName());
    assertFalse(par2.getSuperClass().isPresent());
    assertEquals(1, par2.getSuperTypes().size());
    assertEquals("java.lang.Number", par2.getSuperTypes().get(0).getName());
  }
}
