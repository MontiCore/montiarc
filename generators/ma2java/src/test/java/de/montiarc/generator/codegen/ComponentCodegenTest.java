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
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.montiarc.generator.MontiArcGeneratorConstants;
import de.montiarc.generator.MontiArcScript;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Scope;

/**
 * Tests for component generation. <br>
 * <br>
 * Copyright (c) 2012 RWTH Aachen. All rights reserved.
 *
 * @author (last commit) $Author: ahaber $
 * @version $Date: 2015-03-19 14:43:21 +0100 (Do, 19 Mrz 2015) $<br>
 * $Revision: 3136 $
 */
public class ComponentCodegenTest extends AbstractSymtabTest {
  
  public static final String INPORT_ATTRIBUTE_TYPE = MontiArcGeneratorConstants.IN_SIM_PORT_INTERFACE_NAME;
  
  public static final String INPORT_GETTER_RETURN_TYPE = MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME;
  
  public static final String OUTPORT_ATTRIBUTE_TYPE = MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME;
  
  public static final String OUTPORT_GETTER_RETURN_TYPE = MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME;
  
  public static final String SIM_COMPONENT_INTERFACE_TYPE = MontiArcGeneratorConstants.SIM_COMPONENT_INTERFACE_NAME;
  
  private static final String OUTPUT_TEST_FOLDER = "target/generated-test-sources/gen";
  
  public static boolean hasCorrectModifier(JavaFieldSymbol field) {
    return field.isPrivate();
  }
  
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  private static ComponentCodegenTest theInstance = new ComponentCodegenTest();
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    MontiArcGeneratorConstants.optimizeSingleIn = true;
    theInstance.doSetup();
  }
  
  @AfterClass
  public static void tearDown() {
    MontiArcGeneratorConstants.optimizeSingleIn = false;
  }
  
  private void doSetup() {
    String[] models = { "a.ComplexComponent", "a.AutoConnectGeneric", "a.SimpleInComponent", "a.UntimedSimpleInComponent","a.UntimedComponent","a.TimeSync", "a.TimeSyncSimpleInComponent", "a.SubCompWithCfgSameNames", "a.SubCompWithCfgDifNames","a.SubCompWithCfgInheritance", "a.SubCompWithMoreCfgArgs", "a.SubComponentWithSuperCWithoutCfgArgs","a.UsingCompWithArgs","a.UsingCompWithInnerComp"};
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
      }
      if (interfaze.getName().equals(SIM_COMPONENT_INTERFACE_TYPE)) {
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
    assertTrue("Interface " + MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME
        + " has to be implemented.", foundPort);
    assertTrue("Interface " + MontiArcGeneratorConstants.TIMED_COMPONENT_INTERFACE_NAME
        + " has to be implemented.",
        foundITimedComponent);
    assertTrue("Interface " + MontiArcGeneratorConstants.SIMPLE_IN_PORT_INTERFACE_NAME
        + " has to be implemented.",
        foundSimplePort);
  }
  
  @Test
  public void testSimpleComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.AAutoConnectGeneric");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    assertEquals(2, generatedComponent.getInterfaces().size());
    checkInterfaces(generatedComponent, "a.interfaces.IAutoConnectGeneric");
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_TIMED_SINGLE_IN_COMPONENT_NAME,
        generatedComponent.getSuperClass().get()
            .getName());
    
    // check (not-)existence of certain methods
    boolean messageReceivedExists = false;
    
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("messageReceived")) {
        assertEquals(1, method.getParameters().size());
        messageReceivedExists = true;
      }
    }
    if (!messageReceivedExists) {
      fail("No messageReceived()-method found in this simple component!");
    }
    
    // check existence of fields for input ports and not-existence of fields for
    // output ports
    boolean myStrInExists = false;
    
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("myStrIn")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        myStrInExists = true;
        assertTrue(hasCorrectModifier(field));
      }
    }
    if (myStrInExists) {
      fail("Field for incoming port 'myStrIn' generated! This should be omitted for single in components.");
    }
    
  }
  
  private JavaTypeSymbol getSymbolForComponentName(String name) {
    return symTab.<JavaTypeSymbol> resolve(name, JavaTypeSymbol.KIND).orElse(null);
  }
  
  @Test
  public void testSimpleInComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.ASimpleInComponent");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    assertEquals(2, generatedComponent.getInterfaces().size());
    
    checkInterfaces(generatedComponent, "a.interfaces.ISimpleInComponent");
    checkSimpleInInterfaces(symTab.<JavaTypeSymbol> resolve("a.interfaces.ISimpleInComponent", JavaTypeSymbol.KIND).orElse(null));
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_TIMED_SINGLE_IN_COMPONENT_NAME,
        generatedComponent.getSuperClass().get()
            .getName());
    
    // check (not-)existence of certain methods
    boolean messageReceivedExists = false;
    boolean sendTickExists = false;
    
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("messageReceived")) {
        assertEquals(1, method.getParameters().size());
        messageReceivedExists = true;
      }
      else if (methodName.equals("handleTick")) {
        assertEquals(0, method.getParameters().size());
        sendTickExists = true;
      }
    }
    if (!messageReceivedExists) {
      fail("No messageReceived()-method found in this SimpleInComponent!");
    }
    if (!sendTickExists) {
      fail("No sendTick()-method found in this SimpleInComponent!");
    }
    
    // check existence of fields for input and output ports
    boolean strInExists = false;
    boolean str1OutExists = false;
    boolean str2OutExists = false;
    
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("strIn")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strInExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str1Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str1OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str2OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
    }
    if (strInExists) {
      fail("Field for incoming port 'strIn' generated! This should be omitted for single in components.");
    }
    if (!str1OutExists) {
      fail("No field for outgoing port 'str1Out' generated!");
    }
    if (!str2OutExists) {
      fail("No field for outgoing port 'str2Out' generated!");
    }
  }
  
  @Test
  public void testUntimedSimpleInComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.AUntimedSimpleInComponent");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    assertEquals(2, generatedComponent.getInterfaces().size());
    checkInterfaces(generatedComponent, "a.interfaces.IUntimedSimpleInComponent");
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_SINGLE_IN_COMPONENT_NAME, generatedComponent
        .getSuperClass().get().getName());
    
    // check (not-)existence of certain methods
    boolean messageReceivedExists = false;
    boolean sendTickExists = false;
    
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("messageReceived")) {
        assertEquals(1, method.getParameters().size());
        messageReceivedExists = true;
      }
      else if (methodName.equals("handleTick")) {
        assertEquals(0, method.getParameters().size());
        sendTickExists = true;
      }
    }
    if (!messageReceivedExists) {
      fail("No messageReceived()-method found in this UntimedSimpleInComponent!");
    }
    if (!sendTickExists) {
      fail("No sendTick()-method found in this UntimedSimpleInComponent!");
    }
    
    // check existence of fields for input and output ports
    boolean strInExists = false;
    boolean str1OutExists = false;
    boolean str2OutExists = false;
    
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("strIn")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strInExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str1Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str1OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str2OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
    }
    if (strInExists) {
      fail("Field for incoming port 'strIn' generated! This should be omitted for single in components.");
    }
    if (!str1OutExists) {
      fail("No field for outgoing port 'str1Out' generated!");
    }
    if (!str2OutExists) {
      fail("No field for outgoing port 'str2Out' generated!");
    }
  }
  
  @Test
  public void testUntimedAtomicComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.AUntimedComponent");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    assertEquals(2, generatedComponent.getInterfaces().size());
    checkInterfaces(generatedComponent, "a.interfaces.IUntimedComponent");
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_COMPONENT_NAME, generatedComponent
        .getSuperClass().get().getName());
    
    // check (not-)existence of certain methods
    boolean treatStr1In = false;
    boolean treatStr2In = false;
    boolean sendTickExists = false;
    
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("treatStr1In")) {
        assertEquals(1, method.getParameters().size());
        treatStr1In = true;
      }
      else if (methodName.equals("treatStr2In")) {
        assertEquals(1, method.getParameters().size());
        treatStr2In = true;
      }
      else if (methodName.equals("handleTick")) {
        assertEquals(0, method.getParameters().size());
        sendTickExists = true;
      }
    }
    if (!treatStr1In) {
      fail("No treatStr1In()-method found in this UntimedComponent!");
    }
    if (!treatStr2In) {
      fail("No treatStr1In()-method found in this UntimedComponent!");
    }
    if (!sendTickExists) {
      fail("No sendTick()-method found in this UntimedComponent!");
    }
    
    // check existence of fields for input and output ports
    boolean str1In = false;
    boolean str2In = false;
    boolean str1OutExists = false;
    boolean str2OutExists = false;
    
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("str1In")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str1In = true;
        assertTrue(hasCorrectModifier(field));
      }
      if (fieldName.equals("str2In")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str2In = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str1Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str1OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str2OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
    }
    if (!str1In) {
      fail("No field for incoming port 'str1In' generated!");
    }
    if (!str2In) {
      fail("No field for incoming port 'str2In' generated!");
    }
    if (!str1OutExists) {
      fail("No field for outgoing port 'str1Out' generated!");
    }
    if (!str2OutExists) {
      fail("No field for outgoing port 'str2Out' generated!");
    }
  }
  
  @Test
  public void testTimeSyncAtomicComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.ATimeSync");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    assertEquals(2, generatedComponent.getInterfaces().size());
    checkInterfaces(generatedComponent, "a.interfaces.ITimeSync");
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_TIMED_COMPONENT_NAME, generatedComponent
        .getSuperClass().get().getName());
    
    // check (not-)existence of certain methods
    boolean treatStr1In = false;
    boolean treatStr2In = false;
    boolean sendTickExists = false;
    boolean timeStepWOParams = false;
    boolean timeStepWithParams = false;
    boolean preTimeIncreased = false;
    
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("treatStr1In")) {
        assertEquals(1, method.getParameters().size());
        treatStr1In = true;
      }
      else if (methodName.equals("treatStr2In")) {
        assertEquals(1, method.getParameters().size());
        treatStr2In = true;
      }
      else if (methodName.equals("timeStep") && method.getParameters().isEmpty()) {
        timeStepWOParams = true;
        assertFalse(method.isAbstract());
      }
      else if (methodName.equals("preTimeIncreased") && method.getParameters().isEmpty()) {
        preTimeIncreased = true;
        assertFalse(method.isAbstract());
      }
      else if (methodName.equals("timeStep") && !method.getParameters().isEmpty()) {
        timeStepWithParams = true;
        assertEquals(2, method.getParameters().size());
        assertEquals(String.class.getName(), method.getParameters().get(0).getType().getName());
        assertEquals(String.class.getName(), method.getParameters().get(1).getType().getName());
        assertTrue(method.isAbstract());
      }
      else if (methodName.equals("handleTick")) {
        assertEquals(0, method.getParameters().size());
        sendTickExists = true;
      }
    }
    if (treatStr1In) {
      fail("treatStr1In()-method found in this TimeSync! This method is not expected in a time-synchronous component.");
    }
    if (treatStr2In) {
      fail("treatStr2In()-method found in this TimeSync! This method is not expected in a time-synchronous component.");
    }
    if (!sendTickExists) {
      fail("No sendTick()-method found in this TimeSync!");
    }
    if (!timeStepWOParams) {
      fail("Abstract method timIncreased() is not implemented but has to be for timed components.");
    }
    if (preTimeIncreased) {
      fail("Method preTimeIncreased() has been removed!!");
    }
    if (!timeStepWithParams) {
      fail("Not abstract timIncreased() method found with params for each port buffer.");
    }
    
    // check existence of fields for input and output ports
    boolean str1In = false;
    boolean str2In = false;
    boolean str1InBuffer = false;
    boolean str2InBuffer = false;
    boolean str1OutExists = false;
    boolean str2OutExists = false;
    boolean str1OutBuffer = false;
    boolean str2OutBuffer = false;
    
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("str1In")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str1In = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str1InBuffer")) {
        assertEquals(String.class.getName(), field.getType().getName());
        str1InBuffer = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2In")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str2In = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2InBuffer")) {
        assertEquals(String.class.getName(), field.getType().getName());
        str2InBuffer = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str1OutBuffer")) {
        assertEquals(String.class.getName(), field.getType().getName());
        str1OutBuffer = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2OutBuffer")) {
        assertEquals(String.class.getName(), field.getType().getName());
        str2OutBuffer = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str1Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str1OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("str2Out")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        str2OutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      
    }
    if (!str1In) {
      fail("No field for incoming port 'str1In' generated!");
    }
    if (!str2In) {
      fail("No field for incoming port 'str2In' generated!");
    }
    if (!str1InBuffer) {
      fail("No field as message buffer for incoming port 'str1In' generated!");
    }
    if (!str2InBuffer) {
      fail("No field as message buffer for incoming port 'str2In' generated!");
    }
    if (!str1OutBuffer) {
      fail("No field as message buffer for incoming port 'str1Out' generated!");
    }
    if (!str2OutBuffer) {
      fail("No field as message buffer for incoming port 'str2Out' generated!");
    }
    if (!str1OutExists) {
      fail("No field for outgoing port 'str1Out' generated!");
    }
    if (!str2OutExists) {
      fail("No field for outgoing port 'str2Out' generated!");
    }
  }
  
  @Test
  public void testTimeSyncSimpleInAtomicComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.ATimeSyncSimpleInComponent");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    assertEquals(2, generatedComponent.getInterfaces().size());
    checkInterfaces(generatedComponent, "a.interfaces.ITimeSyncSimpleInComponent");
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_TIMED_SINGLE_IN_COMPONENT_NAME,
        generatedComponent.getSuperClass().get()
            .getName());
    
    // check (not-)existence of certain methods
    boolean treatStr1In = false;
    boolean sendTickExists = false;
    boolean timeStepWOParams = false;
    boolean timeStepWithParams = false;
    boolean preTimeIncreased = false;
    
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("treatStrIn")) {
        assertEquals(1, method.getParameters().size());
        treatStr1In = true;
      }
      else if (methodName.equals("timeStep") && method.getParameters().isEmpty()) {
        timeStepWOParams = true;
        assertFalse(method.isAbstract());
      }
      else if (methodName.equals("preTimeIncreased") && method.getParameters().isEmpty()) {
        preTimeIncreased = true;
        assertFalse(method.isAbstract());
      }
      else if (methodName.equals("timeStep") && !method.getParameters().isEmpty()) {
        timeStepWithParams = true;
        assertEquals(1, method.getParameters().size());
        assertEquals(String.class.getName(), method.getParameters().get(0).getType().getName());
        assertTrue(method.isAbstract());
      }
      else if (methodName.equals("handleTick")) {
        assertEquals(0, method.getParameters().size());
        sendTickExists = true;
      }
    }
    if (treatStr1In) {
      fail("treatStrIn()-method found in this TimeSync! This method is not expected in a time-synchronous component.");
    }
    if (!sendTickExists) {
      fail("No sendTick()-method found in this TimeSync!");
    }
    if (!timeStepWOParams) {
      fail("Abstract method timIncreased() is not implemented but has to be for time sync single in components.");
    }
    if (preTimeIncreased) {
      fail("Method preTimeIncreased() has been removed!!");
    }
    if (!timeStepWithParams) {
      fail("Abstract timIncreased() method not found with params for each port buffer....must not exist in single in time sync component");
    }
    
    // check existence of fields for input and output ports
    boolean strIn = false;
    boolean strInBuffer = false;
    boolean strOutExists = false;
    boolean strOutBuffer = false;
    
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("strIn")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strIn = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("strInBuffer")) {
        assertEquals(String.class.getName(), field.getType().getName());
        strInBuffer = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("strOutBuffer")) {
        assertEquals(String.class.getName(), field.getType().getName());
        strOutBuffer = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("strOut")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strOutExists = true;
        assertTrue(hasCorrectModifier(field));
      }
      
    }
    if (strIn) {
      fail("Field for incoming port 'str1In' generated! This should be omitted for single in components.");
    }
    if (!strInBuffer) {
      fail("No field as message buffer for incoming port 'str1In' generated!");
    }
    if (!strOutBuffer) {
      fail("No field as message buffer for incoming port 'str1Out' generated!");
    }
    if (!strOutExists) {
      fail("No field for outgoing port 'str1Out' generated!");
    }
  }
  
  @Test
  public void testComplexComponent() {
    JavaTypeSymbol generatedComponent = getSymbolForComponentName("a.AComplexComponent");
    
    // check modifier, superclass and implemented interfaces
    assertTrue(generatedComponent.isAbstract());
    assertEquals(2, generatedComponent.getInterfaces().size());
    checkInterfaces(generatedComponent, "a.interfaces.IComplexComponent");
    assertEquals(MontiArcGeneratorConstants.ABSTRACT_TIMED_COMPONENT_NAME, generatedComponent
        .getSuperClass().get().getName());
    
    // check (not-)existence of certain methods
    boolean messageReceivedExists = false;
    boolean sendTickExists = false;
    boolean handleMessageExists = false;
    
    for (JavaMethodSymbol method : generatedComponent.getMethods()) {
      String methodName = method.getName();
      if (methodName.equals("messageReceived")) {
        messageReceivedExists = true;
      }
      else if (methodName.equals("handleTick")) {
        assertEquals(0, method.getParameters().size());
        sendTickExists = true;
      }
      else if (methodName.equals("handleMessage")) {
        assertEquals(2, method.getParameters().size());
        handleMessageExists = true;
      }
    }
    if (messageReceivedExists) {
      fail("A messageReceived()-method was generated for a complex component!");
    }
    if (!sendTickExists) {
      fail("No sendTick()-method found in this complex component!");
    }
    if (!handleMessageExists) {
      fail("No handleMessage()-method found in this complex component!");
    }
    
    // check existence of fields for input and output ports
    boolean strIn1Exists = false;
    boolean strIn2Exists = false;
    boolean strOut1Exists = false;
    boolean strOut2Exists = false;
    
    for (JavaFieldSymbol field : generatedComponent.getFields()) {
      String fieldName = field.getName();
      if (fieldName.equals("strIn1")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strIn1Exists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("kIn")) {
        assertEquals(INPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strIn2Exists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("strOut1")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strOut1Exists = true;
        assertTrue(hasCorrectModifier(field));
      }
      else if (fieldName.equals("vOut")) {
        assertEquals(OUTPORT_ATTRIBUTE_TYPE, field.getType().getName());
        strOut2Exists = true;
        assertTrue(hasCorrectModifier(field));
      }
    }
    if (!strIn1Exists) {
      fail("No field for incoming port 'strIn1' generated!");
    }
    if (!strIn2Exists) {
      fail("No field for incoming port 'kIn' generated!");
    }
    if (!strOut1Exists) {
      fail("No field for outgoing port 'strOut1' generated!");
    }
    if (!strOut2Exists) {
      fail("No field for outgoing port 'vOut' generated!");
    }
  }
  
  /**
   * Test for ticket #11.
   */
  @Test
  public void testComponentInheritanceWithCfgParamsSameNames() {
    
    JavaTypeSymbol sub = this.getSymbolForComponentName("a.ASubCompWithCfgSameNames");
    String expectedConst = "a, b);";
    
    assertNotNull(sub);
    
    try {
      List<String> lines = FileUtils.readLines(new File(OUTPUT_TEST_FOLDER,
          "a/ASubCompWithCfgSameNames.java"));
      
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("super(")) {
          assertTrue("expected super(" + expectedConst + " but was: " + line.trim(),
              line.contains(expectedConst));
          break;
        }
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test for ticket #11.
   */
  @Test
  public void testComponentInheritanceWithCfgParamsDifNames() {
    
    JavaTypeSymbol sub = this.getSymbolForComponentName("a.ASubCompWithCfgDifNames");
    String expectedConst = "c, d);";
    
    assertNotNull(sub);
    
    try {
      List<String> lines = FileUtils.readLines(new File(OUTPUT_TEST_FOLDER,
          "a/ASubCompWithCfgDifNames.java"));
      
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("super(")) {
          assertTrue("expected super(" + expectedConst + " but was: " + line.trim(),
              line.contains(expectedConst));
          break;
        }
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test for ticket #11.
   */
  @Test
  public void testComponentInheritanceWithCfgParamInheritance() {
    
    JavaTypeSymbol sub = this.getSymbolForComponentName("a.ASubCompWithCfgInheritance");
    String expectedConst = "b1, b2);";
    
    assertNotNull(sub);
    
    try {
      List<String> lines = FileUtils.readLines(new File(OUTPUT_TEST_FOLDER,
          "a/ASubCompWithCfgInheritance.java"));
      
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("super(")) {
          assertTrue("expected super(" + expectedConst + " but was: " + line.trim(),
              line.contains(expectedConst));
          break;
        }
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test for ticket #11.
   */
  @Test
  public void testComponentInheritanceWithMoreCfgArgsInSubComponent() {
    
    JavaTypeSymbol sub = this.getSymbolForComponentName("a.ASubCompWithMoreCfgArgs");
    String expectedConst = "a, b);";
    
    assertNotNull(sub);
    
    try {
      List<String> lines = FileUtils.readLines(new File(OUTPUT_TEST_FOLDER,
          "a/ASubCompWithMoreCfgArgs.java"));
      
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("super(")) {
          assertTrue("expected super(" + expectedConst + " but was: " + line.trim(),
              line.contains(expectedConst));
          break;
        }
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test for ticket #11.
   */
  @Test
  public void testComponentInheritanceOnlySubcomponentHasConfigArgs() {
    
    JavaTypeSymbol sub = this.getSymbolForComponentName("a.ASubComponentWithSuperCWithoutCfgArgs");
    String expectedConst = ");";
    
    assertNotNull(sub);
    
    try {
      List<String> lines = FileUtils.readLines(new File(OUTPUT_TEST_FOLDER,
          "a/ASubComponentWithSuperCWithoutCfgArgs.java"));
      
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("super(")) {
          assertTrue("expected super(" + expectedConst + " but was: " + line.trim(),
              line.contains(expectedConst));
          break;
        }
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test for ticket #36.
   */
  @Test
  public void testUsingSubcomponentsByPassingConstructorCallsAsArguments() {
    
    JavaTypeSymbol sub = this.getSymbolForComponentName("a.UsingCompWithArgs");
    String expectedConst = "new a.A(), new a.A(new a.C()));";
    
    assertNotNull(sub);
    
    try {
      List<String> lines = FileUtils.readLines(new File(OUTPUT_TEST_FOLDER,
          "a/UsingCompWithArgs.java"));
      
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line
            .contains("this.superCompWithCfg2 = a.gen.factories.SuperCompWithCfg2Factory.create(")) {
          assertTrue("expected ...create(" + expectedConst + " but was: " + line.trim(),
              line.contains(expectedConst));
          break;
        }
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  // /**
  // * TODO: Write me!
  // * @param string
  // * @return
  // */
  // private JavaTypeSymbol getSymbolForDecomposedComponentName(String string) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  
  @Test
  public void testUsingCompWithInnerComp() {
    JavaTypeSymbol sub = this.getSymbolForComponentName("a.UsingCompWithInnerComp");
    assertNotNull(sub);
  }
  
  
  
  /**
   * Not implemented in MontiArc yet
   */
  @Ignore
  @Test
  public void testUsingCompWithConstraint() {
    JavaTypeSymbol sub = this.getSymbolForComponentName("CompWithConst");
    assertNotNull(sub);
    
    boolean foundmyJavaInv = false, foundMyOclInv = false;
    for (JavaMethodSymbol m : sub.getMethods()) {
      if (m.getName().equals("_checkMyJavaInv")) {
        foundmyJavaInv = true;
      }
      else if (m.getName().equals("_checkMyOclInv")) {
        foundMyOclInv = true;
      }
    }
    assertTrue(foundmyJavaInv);
    assertTrue(foundMyOclInv);
  }
  
  /**
   * Not implemented in MontiArc yet
   */
  @Ignore
  @Test
  public void testUsingCompWithConstraintUntimed() {
    JavaTypeSymbol sub = this.getSymbolForComponentName("CompWithConstUntimed");
    assertNotNull(sub);
    
    boolean foundmyJavaInv = false, foundMyOclInv = false;
    for (JavaMethodSymbol m : sub.getMethods()) {
      if (m.getName().equals("_checkMyJavaInv")) {
        foundmyJavaInv = true;
      }
      else if (m.getName().equals("_checkMyOclInv")) {
        foundMyOclInv = true;
      }
    }
    assertTrue(foundmyJavaInv);
    assertTrue(foundMyOclInv);
  }
  
}
