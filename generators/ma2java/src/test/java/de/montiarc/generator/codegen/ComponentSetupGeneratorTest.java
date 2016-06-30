package de.montiarc.generator.codegen;

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
import de.monticore.java.symboltable.JavaTypeSymbol.JavaTypeSymbolKind;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.Scope;

/**
 * Tests for the component setup code generator. <br>
 * <br>
 * Copyright (c) 2012 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $Author: ahaber $
 * @version $Date: 2014-03-07 14:28:09 +0100 (Fr, 07 Mrz 2014) $<br>
 * $Revision: 2728 $
 */
public class ComponentSetupGeneratorTest extends AbstractSymtabTest {
  
  private static final String INPUT_TEST_FOLDER = "src/test/resources";
  
  /**
   * Singleton instance.
   */
  protected static ComponentSetupGeneratorTest theInstance = new ComponentSetupGeneratorTest();
  
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  // set once in doSetup
//  private static JavaTypeSymbol generatedComponentClass = null;
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    theInstance.doSetup();
  }
  
  private void doSetup() {
    // 1. load model: arc/codegen/a/ComplexComponent.arc
    String[] models = { "setup.InteriorLightArbiter",
        "setup.NoInput",
        "setup.NoOutput",
        "setup.ExtendingGeneric1",
        "setup.ExtendingGeneric3",
        "setup.ExtendingGeneric4",
        "setup.WithGenerics",
        "setup.WithParameters",
        "setup.GenericWithConfigNoIn",
        "setup.GenericWithConfigNoOut",
        "source.SourceSubcomponent",
        "setup.ExtendWithGenerics" };
    symTab = createSymTab(modelPath);
    for (String model : models) {
      ComponentSymbol comp = symTab
          .<ComponentSymbol> resolve(model, ComponentSymbol.KIND).orElse(null);
      assertNotNull(comp);
//      assertEquals("a.ComplexComponent", comp.getFullName());
      
      // 2. execute generator
      new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), model,
          outputDirectory.toFile(), Optional.empty());
    }
    // 3. load generated java class
    symTab = createJavaSymTab(outputDirectory);
//    generatedComponentClass = symTab.<JavaTypeSymbol> resolve(
//        "a.AComplexComponent", JavaTypeSymbol.KIND).orElse(null);
//    assertNotNull(generatedComponentClass);
  }
  
    
  /**
   * Mixed input and output ports test case.
   */
  @Ignore
  @Test
  public void testGenerateMixed() {
    JavaTypeSymbol generatedSetup = symTab.<JavaTypeSymbol>resolve("setup.AInteriorLightArbiter", JavaTypeSymbolKind.KIND).orElse(null);
    assertNotNull(generatedSetup);
    
    
    // check fields
    boolean foundTick = false;
    boolean foundGenericTickQueue = false;
    boolean foundAlarmStatusTick = false;
    boolean foundDoorStatusTick = false;
    boolean foundSwitchStatusTick = false;
    
    boolean foundAlarmStatus = false;
    boolean foundDoorStatus = false;
    boolean foundSwitchStatus = false;
    
    boolean foundAlarmStatusQueue = false;
    boolean foundDoorStatusQueue = false;
    boolean foundSwitchStatusQueue = false;
    
    for (JavaFieldSymbol field : generatedSetup.getFields()) {
      if (field.getName().equals("_tick")) {
        foundTick = true;
        continue;
      }
      if (field.getName().equals("_genericTickQueue")) {
        foundGenericTickQueue = true;
        continue;
      }
      if (field.getName().equals("_alarmStatusTick")
          &&
          field.getType().getName().equals("sim.generic.Tick")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("java.lang.String")) {
        foundAlarmStatusTick = true;
        continue;
      }
      if (field.getName().equals("_doorStatusTick")
          &&
          field.getType().getName().equals("sim.generic.Tick")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("java.lang.String")) {
        foundDoorStatusTick = true;
        continue;
      }
      if (field.getName().equals("_switchStatusTick")
          &&
          field.getType().getName().equals("sim.generic.Tick")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("java.lang.String")) {
        foundSwitchStatusTick = true;
        continue;
      }
      
      if (field.getName().equals("_alarmStatus")
          &&
          field.getType().getName().equals("sim.port.IInPort")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("java.lang.String")) {
        foundAlarmStatus = true;
        continue;
      }
      if (field.getName().equals("_doorStatus")
          &&
          field.getType().getName().equals("sim.port.IInPort")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("java.lang.String")) {
        foundDoorStatus = true;
        continue;
      }
      if (field.getName().equals("_switchStatus")
          &&
          field.getType().getName().equals("sim.port.IInPort")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("java.lang.String")) {
        foundSwitchStatus = true;
        continue;
      }
      
      if (field.getName().equals("_alarmStatusQueue")
          &&
          field.getType().getName().equals("java.util.Queue")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("sim.generic.TickedMessage")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getActualTypeArguments().get(0)
              .getType().getName().equals("java.lang.String")) {
        foundAlarmStatusQueue = true;
        continue;
      }
      if (field.getName().equals("_doorStatusQueue")
          &&
          field.getType().getName().equals("java.util.Queue")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("sim.generic.TickedMessage")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getActualTypeArguments().get(0)
              .getType().getName().equals("java.lang.String")) {
        foundDoorStatusQueue = true;
        continue;
      }
      if (field.getName().equals("_switchStatusQueue")
          &&
          field.getType().getName().equals("java.util.Queue")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getName()
              .equals("sim.generic.TickedMessage")
          &&
          field.getType().getActualTypeArguments().get(0).getType().getActualTypeArguments().get(0)
              .getType().getName().equals("java.lang.String")) {
        foundSwitchStatusQueue = true;
        continue;
      }
    }
    assertFalse(foundTick);
    assertFalse(foundGenericTickQueue);
    assertTrue(foundAlarmStatusTick);
    assertTrue(foundDoorStatusTick);
    assertTrue(foundSwitchStatusTick);
    
    assertTrue(foundAlarmStatus);
    assertTrue(foundDoorStatus);
    assertTrue(foundSwitchStatus);
    
    assertTrue(foundAlarmStatusQueue);
    assertTrue(foundDoorStatusQueue);
    assertTrue(foundSwitchStatusQueue);
    
    // check constructors
    boolean foundBasic = false;
    boolean foundWithScheduler = false;
    boolean foundWithSchedulerAndHandler = false;
    
    for (JavaMethodSymbol constr : generatedSetup.getConstructors()) {
      if (constr.getParameters().size() == 1 &&
          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
          constr.getParameters().get(0).getName().equals("cmdObserver")) {
        foundBasic = true;
        continue;
      }
      if (constr.getParameters().size() == 2 &&
          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
          constr.getParameters().get(0).getName().equals("cmdObserver") &&
          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")) {
        foundWithScheduler = true;
        continue;
      }
      if (constr.getParameters().size() == 3
          &&
          constr.getParameters().get(0).getType().getName().equals("java.util.Observer")
          &&
          constr.getParameters().get(0).getName().equals("cmdObserver")
          &&
          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")
          &&
          constr.getParameters().get(2).getType().getName()
              .equals("sim.error.ISimulationErrorHandler")) {
        foundWithSchedulerAndHandler = true;
        continue;
      }
    }
    
    assertTrue(foundBasic);
    assertTrue(foundWithScheduler);
    assertTrue(foundWithSchedulerAndHandler);
    
    // check send methods for incoming ports
    boolean foundSendAlarm = false;
    boolean foundSendDoor = false;
    boolean foundSendSwitch = false;
    
    for (JavaMethodSymbol method : generatedSetup.getMethods()) {
      if (method.getName().equals("sendAlarmStatus") &&
          method.getParameters().size() == 1 &&
          method.getParameters().get(0).getType().getName().equals("java.lang.String") &&
          method.isEllipsisParameterMethod()) {
        foundSendAlarm = true;
        continue;
      }
      if (method.getName().equals("sendDoorStatus") &&
          method.getParameters().size() == 1 &&
          method.getParameters().get(0).getType().getName().equals("java.lang.String") &&
          method.isEllipsisParameterMethod()) {
        foundSendDoor = true;
        continue;
      }
      if (method.getName().equals("sendSwitchStatus") &&
          method.getParameters().size() == 1 &&
          method.getParameters().get(0).getType().getName().equals("java.lang.String") &&
          method.isEllipsisParameterMethod()) {
        foundSendSwitch = true;
        continue;
      }
    }
    assertTrue(foundSendAlarm);
    assertTrue(foundSendDoor);
    assertTrue(foundSendSwitch);
  }
  
//  /**
//   * Test case without any input ports.
//   */
//  @Test
//  public void testGenerateNoInput() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "NoInputSetup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//    }
//    assertTrue(foundTick);
//    assertTrue(foundGenericTickQueue);
//  }
//  
//  /**
//   * Test case without any output ports.
//   */
//  @Test
//  public void testGenerateNoOutput() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "NoOutputSetup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//    }
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//    
//    boolean foundBasic = false;
//    boolean foundWithScheduler = false;
//    boolean foundWithSchedulerAndHandler = false;
//    
//    for (MethodEntry constr : generatedSetup.getConstructors()) {
//      if (constr.getName().equals("NoOutputSetup") && constr.isConstructor()) {
//        if (constr.getParameters().size() == 0) {
//          foundBasic = true;
//        }
//        if (constr.getParameters().size() == 1
//            && constr.getParameters().get(0).getType().getName().equals("sim.IScheduler")) {
//          foundWithScheduler = true;
//        }
//        if (constr.getParameters().size() == 2
//            && constr.getParameters().get(0).getType().getName().equals("sim.IScheduler")
//            &&
//            constr.getParameters().get(1).getType().getName()
//                .equals("sim.error.ISimulationErrorHandler")) {
//          foundWithSchedulerAndHandler = true;
//        }
//      }
//    }
//    
//    assertTrue(foundBasic);
//    assertTrue(foundWithScheduler);
//    assertTrue(foundWithSchedulerAndHandler);
//  }
//  
//  /**
//   * Test case with a generic component.
//   */
//  @Test
//  public void testGenerateGenerics() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "WithGenericsSetup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    boolean foundKIn = false;
//    boolean foundKInTick = false;
//    boolean foundKInQueue = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//      
//      if (field.getName().equals("_kInTick")
//          &&
//          field.getType().getName().equals("sim.generic.Tick")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKInTick = true;
//        continue;
//      }
//      
//      if (field.getName().equals("_kIn")
//          &&
//          field.getType().getName().equals("sim.port.IInPort")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKIn = true;
//        continue;
//      }
//      if (field.getName().equals("_kInQueue")
//          &&
//          field.getType().getName().equals("java.util.Queue")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("sim.generic.TickedMessage")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKInQueue = true;
//        continue;
//      }
//    }
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//    
//    assertTrue(foundKIn);
//    assertTrue(foundKInQueue);
//    assertTrue(foundKInTick);
//    
//  }
//  
//  /**
//   * Test case with a generic component.
//   */
//  @Test
//  public void testExtendsGenerateGenerics() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "ExtendWithGenericsSetup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    boolean foundKIn = false;
//    boolean foundKInTick = false;
//    boolean foundKInQueue = false;
//    
//    boolean foundComponent = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//      
//      if (field.getName().equals("_kInTick")
//          &&
//          field.getType().getName().equals("sim.generic.Tick")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKInTick = true;
//        continue;
//      }
//      
//      if (field.getName().equals("_kIn")
//          &&
//          field.getType().getName().equals("sim.port.IInPort")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKIn = true;
//        continue;
//      }
//      if (field.getName().equals("_kInQueue")
//          &&
//          field.getType().getName().equals("java.util.Queue")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("sim.generic.TickedMessage")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKInQueue = true;
//        continue;
//      }
//      if (field.getName().equals("_component") &&
//          field.getType().getName().equals("setup.gen.interfaces.IExtendWithGenerics") &&
//          field.getType().getReferenceParameters().size() == 2 &&
//          field.getType().getReferenceParameters().get(0).getType().getName().equals("V") &&
//          field.getType().getReferenceParameters().get(1).getType().getName().equals("K")) {
//        foundComponent = true;
//        continue;
//      }
//    }
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//    
//    assertTrue(foundKIn);
//    assertTrue(foundKInQueue);
//    assertTrue(foundKInTick);
//    
//    assertTrue(foundComponent);
//    
//  }
//  
//  /**
//   * Test case with a component that extends a generic component and sets
//   * generic type parameters with a concrete type See ticket #35.
//   */
//  @Test
//  public void testExtendsGenerateGenericsAndSetType() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "ExtendingGeneric1Setup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    boolean foundKIn = false;
//    boolean foundKInTick = false;
//    boolean foundKInQueue = false;
//    
//    boolean foundComponent = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//      
//      if (field.getName().equals("_kInTick")
//          &&
//          field.getType().getName().equals("sim.generic.Tick")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.lang.Integer")) {
//        foundKInTick = true;
//        continue;
//      }
//      
//      if (field.getName().equals("_kIn")
//          &&
//          field.getType().getName().equals("sim.port.IInPort")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.lang.Integer")) {
//        foundKIn = true;
//        continue;
//      }
//      if (field.getName().equals("_kInQueue")
//          &&
//          field.getType().getName().equals("java.util.Queue")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("sim.generic.TickedMessage")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.lang.Integer")) {
//        foundKInQueue = true;
//        continue;
//      }
//      if (field.getName().equals("_component") &&
//          field.getType().getName().equals("setup.gen.interfaces.IExtendingGeneric1") &&
//          field.getType().getReferenceParameters().size() == 0) {
//        foundComponent = true;
//        continue;
//      }
//      
//    }
//    
//    // check constructors
//    boolean foundBasic = false;
//    boolean foundWithScheduler = false;
//    boolean foundWithSchedulerAndHandler = false;
//    
//    for (MethodEntry constr : generatedSetup.getConstructors()) {
//      if (constr.getParameters().size() == 1 &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
//          constr.getParameters().get(0).getName().equals("vOutObserver")) {
//        foundBasic = true;
//        continue;
//      }
//      if (constr.getParameters().size() == 2 &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
//          constr.getParameters().get(0).getName().equals("vOutObserver") &&
//          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")) {
//        foundWithScheduler = true;
//        continue;
//      }
//      if (constr.getParameters().size() == 3
//          &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer")
//          &&
//          constr.getParameters().get(0).getName().equals("vOutObserver")
//          &&
//          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")
//          &&
//          constr.getParameters().get(2).getType().getName()
//              .equals("sim.error.ISimulationErrorHandler")) {
//        foundWithSchedulerAndHandler = true;
//        continue;
//      }
//    }
//    
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//    
//    assertTrue(foundKIn);
//    assertTrue(foundKInQueue);
//    assertTrue(foundKInTick);
//    assertTrue(foundComponent);
//    
//    assertTrue(foundBasic);
//    assertTrue(foundWithScheduler);
//    assertTrue(foundWithSchedulerAndHandler);
//    
//  }
//  
//  /**
//   * Test case with a component that extends a generic component and sets
//   * generic type parameters with a concrete type See ticket #35.
//   */
//  @Test
//  public void testExtendsGenerateGenericsAndPartiallySetTypeIncoming() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "ExtendingGeneric3Setup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    boolean foundKIn = false;
//    boolean foundKInTick = false;
//    boolean foundKInQueue = false;
//    
//    boolean foundComponent = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//      
//      if (field.getName().equals("_kInTick")
//          &&
//          field.getType().getName().equals("sim.generic.Tick")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKInTick = true;
//        continue;
//      }
//      
//      if (field.getName().equals("_kIn")
//          &&
//          field.getType().getName().equals("sim.port.IInPort")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKIn = true;
//        continue;
//      }
//      if (field.getName().equals("_kInQueue")
//          &&
//          field.getType().getName().equals("java.util.Queue")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("sim.generic.TickedMessage")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getReferenceParameters().get(0)
//              .getType().getName().equals("K")) {
//        foundKInQueue = true;
//        continue;
//      }
//      if (field.getName().equals("_component") &&
//          field.getType().getName().equals("setup.gen.interfaces.IExtendingGeneric3") &&
//          field.getType().getReferenceParameters().size() == 1 &&
//          field.getType().getReferenceParameters().get(0).getType().getName().equals("K")) {
//        foundComponent = true;
//        continue;
//      }
//    }
//    
//    // check constructors
//    boolean foundBasic = false;
//    boolean foundWithScheduler = false;
//    boolean foundWithSchedulerAndHandler = false;
//    
//    for (MethodEntry constr : generatedSetup.getConstructors()) {
//      if (constr.getParameters().size() == 1 &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
//          constr.getParameters().get(0).getName().equals("vOutObserver")) {
//        foundBasic = true;
//        continue;
//      }
//      if (constr.getParameters().size() == 2 &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
//          constr.getParameters().get(0).getName().equals("vOutObserver") &&
//          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")) {
//        foundWithScheduler = true;
//        continue;
//      }
//      if (constr.getParameters().size() == 3
//          &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer")
//          &&
//          constr.getParameters().get(0).getName().equals("vOutObserver")
//          &&
//          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")
//          &&
//          constr.getParameters().get(2).getType().getName()
//              .equals("sim.error.ISimulationErrorHandler")) {
//        foundWithSchedulerAndHandler = true;
//        continue;
//      }
//    }
//    
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//    
//    assertTrue(foundKIn);
//    assertTrue(foundKInQueue);
//    assertTrue(foundKInTick);
//    assertTrue(foundComponent);
//    
//    assertTrue(foundBasic);
//    assertTrue(foundWithScheduler);
//    assertTrue(foundWithSchedulerAndHandler);
//    
//  }
//  
//  /**
//   * Test case with a component that extends a generic component and sets
//   * generic type parameters with a concrete type See ticket #35.
//   */
//  @Test
//  public void testExtendsGenerateGenericsAndPartiallySetTypeOutgoing() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "ExtendingGeneric4Setup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    boolean foundKIn = false;
//    boolean foundKInTick = false;
//    boolean foundKInQueue = false;
//    
//    boolean foundComponent = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//      
//      if (field.getName().equals("_kInTick")
//          &&
//          field.getType().getName().equals("sim.generic.Tick")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.lang.Integer")) {
//        foundKInTick = true;
//        continue;
//      }
//      
//      if (field.getName().equals("_kIn")
//          &&
//          field.getType().getName().equals("sim.port.IInPort")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.lang.Integer")) {
//        foundKIn = true;
//        continue;
//      }
//      if (field.getName().equals("_kInQueue")
//          &&
//          field.getType().getName().equals("java.util.Queue")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getName()
//              .equals("sim.generic.TickedMessage")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.util.List")
//          &&
//          field.getType().getReferenceParameters().get(0).getType().getReferenceParameters().get(0)
//              .getType().getReferenceParameters().get(0)
//              .getType().getName().equals("java.lang.Integer")) {
//        foundKInQueue = true;
//        continue;
//      }
//      if (field.getName().equals("_component") &&
//          field.getType().getName().equals("setup.gen.interfaces.IExtendingGeneric4") &&
//          field.getType().getReferenceParameters().size() == 1 &&
//          field.getType().getReferenceParameters().get(0).getType().getName().equals("V")) {
//        foundComponent = true;
//        continue;
//      }
//    }
//    
//    // check constructors
//    boolean foundBasic = false;
//    boolean foundWithScheduler = false;
//    boolean foundWithSchedulerAndHandler = false;
//    
//    for (MethodEntry constr : generatedSetup.getConstructors()) {
//      if (constr.getParameters().size() == 1 &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
//          constr.getParameters().get(0).getName().equals("vOutObserver")) {
//        foundBasic = true;
//        continue;
//      }
//      if (constr.getParameters().size() == 2 &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer") &&
//          constr.getParameters().get(0).getName().equals("vOutObserver") &&
//          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")) {
//        foundWithScheduler = true;
//        continue;
//      }
//      if (constr.getParameters().size() == 3
//          &&
//          constr.getParameters().get(0).getType().getName().equals("java.util.Observer")
//          &&
//          constr.getParameters().get(0).getName().equals("vOutObserver")
//          &&
//          constr.getParameters().get(1).getType().getName().equals("sim.IScheduler")
//          &&
//          constr.getParameters().get(2).getType().getName()
//              .equals("sim.error.ISimulationErrorHandler")) {
//        foundWithSchedulerAndHandler = true;
//        continue;
//      }
//    }
//    
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//    
//    assertTrue(foundKIn);
//    assertTrue(foundKInQueue);
//    assertTrue(foundKInTick);
//    assertTrue(foundComponent);
//    
//    assertTrue(foundBasic);
//    assertTrue(foundWithScheduler);
//    assertTrue(foundWithSchedulerAndHandler);
//    
//  }
//  
//  /**
//   * Test case with a component that has configuration parameters.
//   */
//  @Test
//  public void testGenerateParameters() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen", "WithParametersSetup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//    }
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//  }
//  
//  /**
//   * Test case with a component that has configuration parameters and generic
//   * types without incoming ports.
//   */
//  @Test
//  public void testGenerateGenericAndConfigParametersNoInput() {
//    JavaTypeEntry generatedSetup = super
//        .getSymbolForName("setup/gen", "GenericWithConfigNoInSetup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//    }
//    assertTrue(foundTick);
//    assertTrue(foundGenericTickQueue);
//  }
//  
//  /**
//   * Test case with a component that has configuration parameters and generic
//   * types without outgoing ports.
//   */
//  @Test
//  public void testGenerateGenericAndConfigParametersNoOutput() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("setup/gen",
//        "GenericWithConfigNoOutSetup");
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//    }
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//  }
//  
//  @Test
//  public void testGenerateSetupForSourceSubComponent() {
//    JavaTypeEntry generatedSetup = super.getSymbolForName("source/gen", "SourceSubcomponentSetup");
//    // TODO ausgehende ports immer noch fehlerhaft.
//    
//    assertNotNull(generatedSetup);
//    
//    boolean foundTick = false;
//    boolean foundGenericTickQueue = false;
//    
//    for (FieldEntry field : generatedSetup.getFields()) {
//      if (field.getName().equals("_tick")) {
//        foundTick = true;
//      }
//      if (field.getName().equals("_genericTickQueue")) {
//        foundGenericTickQueue = true;
//      }
//    }
//    assertFalse(foundTick);
//    assertFalse(foundGenericTickQueue);
//  }
  
}
