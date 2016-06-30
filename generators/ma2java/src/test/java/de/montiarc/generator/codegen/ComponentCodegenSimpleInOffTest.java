/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.montiarc.generator.MontiArcGeneratorConstants;
import de.montiarc.generator.MontiArcScript;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Scope;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author: ahaber $
 * @version $Revision: 2922 $, $Date: 2014-08-06 13:41:20 +0200 (Mi, 06 Aug
 * 2014) $
 * @since 2.5.0
 */
public class ComponentCodegenSimpleInOffTest extends AbstractSymtabTest {
  
  public static boolean hasCorrectModifier(JavaFieldSymbol field) {
    return field.isPrivate();
  }
  
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  private static ComponentCodegenSimpleInOffTest theInstance = new ComponentCodegenSimpleInOffTest();
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    MontiArcGeneratorConstants.optimizeSingleIn = false;
    theInstance.doSetup();
  }
  
  
  private void doSetup() {
    String[] models = {"a.SimpleInComponent2"};
    symTab = createJavaSymTab(outputDirectory);
    for (String model : models) {
      new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), model,
          outputDirectory.toFile(), Optional.empty());
    }
  }
  
  private void checkInterfaces(JavaTypeSymbol generatedComponent, String generatedInterfaceName) {
    boolean foundGeneratedInterfaze = false, foundSimInterface = false;
    for (JavaTypeSymbolReference interfaze : generatedComponent.getInterfaces()) {
      if (interfaze.getName().equals(generatedInterfaceName)) {
        foundGeneratedInterfaze = true;
        checkSimpleInInterfaces(interfaze.getReferencedSymbol());
      }
      if (interfaze.getName().equals(MontiArcGeneratorConstants.SIM_COMPONENT_INTERFACE_NAME)) {
        foundSimInterface = true;
      }
    }
    assertTrue(foundGeneratedInterfaze);
    assertTrue(foundSimInterface);
  }
  
  private void checkSimpleInInterfaces(JavaTypeSymbol generatedInterface) {
    // extends sim.generic.ITimedComponent, sim.port.IInPort<java.lang.String>,
    // sim.generic.SimpleInPortInterface<java.lang.String> {
    boolean foundPort = false;
    boolean foundSimplePort = false;
    boolean foundITimedComponent = false;
    
    for (JavaTypeSymbolReference implInterf : generatedInterface.getInterfaces()) {
      if (implInterf.getName().equals(MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME)) {
        foundPort = true;
      }
      else if (implInterf.getName().equals(
          MontiArcGeneratorConstants.TIMED_COMPONENT_INTERFACE_NAME)) {
        foundITimedComponent = true;
      }
      else if (implInterf.getName()
          .equals(MontiArcGeneratorConstants.SIMPLE_IN_PORT_INTERFACE_NAME)) {
        foundSimplePort = true;
      }
    }
    assertTrue("Interface " + MontiArcGeneratorConstants.TIMED_COMPONENT_INTERFACE_NAME
        + " has to be implemented.", foundITimedComponent);
    assertFalse("Interface " + MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME
        + " must not be implemented.", foundPort);
    assertFalse("Interface " + MontiArcGeneratorConstants.SIMPLE_IN_PORT_INTERFACE_NAME
        + " must not be implemented.", foundSimplePort);
  }
  
  @Test
  public void testSimpleInComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.ASimpleInComponent2");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    Collection<JavaTypeSymbolReference> interfaces = generatedComponent.getInterfaces();
    assertEquals(2, interfaces.size());
    
    checkInterfaces(generatedComponent, "a.interfaces.ISimpleInComponent2");
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_TIMED_COMPONENT_NAME, generatedComponent
        .getSuperClass().get().getName());
    
    // check methods
    // check (not-)existence of certain methods
    boolean messageReceivedExists = false;
    boolean sendTickExists = false;
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("treatStrIn")) {
        assertEquals(1, method.getParameters().size());
        assertTrue(method.isAbstract());
        messageReceivedExists = true;
      }
      else if (methodName.equals("handleTick")) {
        assertEquals(0, method.getParameters().size());
        sendTickExists = true;
      }
    }
    if (!messageReceivedExists) {
      fail("No treatStrIn()-method found in this SimpleInComponent2!");
    }
    if (!sendTickExists) {
      fail("No sendTick()-method found in this SimpleInComponent2!");
    }
    
    // check existence of fields for input and output ports
    boolean strInExists = false;
    boolean str1OutExists = false;
    boolean str2OutExists = false;
    
    // check fields
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("strIn")) {
        assertEquals(MontiArcGeneratorConstants.IN_SIM_PORT_INTERFACE_NAME, field.getType()
            .getName());
        strInExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str1Out")) {
        assertEquals(MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME, field.getType().getName());
        str1OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2Out")) {
        assertEquals(MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME, field.getType().getName());
        str2OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
    }
    if (!strInExists) {
      fail("No field for incoming port 'strIn' generated! This should happen, as simple in optimization is turned off.");
    }
    if (!str1OutExists) {
      fail("No field for outgoing port 'str1Out' generated!");
    }
    if (!str2OutExists) {
      fail("No field for outgoing port 'str2Out' generated!");
    }
  }
  
  private JavaTypeSymbol getSymbolForComponentName(String name) {
    return symTab.<JavaTypeSymbol> resolve(name, JavaTypeSymbol.KIND).orElse(null);
  }
}
