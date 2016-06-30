/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import de.montiarc.simplegenerator.MontiArcScript;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class GeneratedComponentsTest extends AbstractSymtabTest {
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen/deployments");
  
  // set once in doSetup
  private static List<JavaTypeSymbol> generatedComponentClasses = new ArrayList<>();
  
  private static Scope symTab = null;
  
  private void generateModels(List<String> models, List<String> expectedNames) {
    // 1. load model: arc/codegen/a/ComplexComponent.arc
    generatedComponentClasses = new ArrayList<>();
    symTab = createSymTab(modelPath);
    for (String model : models) {
      System.out.println("[GeneratedComponentsTest] resolving model: " + model);
      ComponentSymbol comp = symTab
          .<ComponentSymbol> resolve(model, ComponentSymbol.KIND).orElse(null);
      assertNotNull(comp);
      assertEquals(model, comp.getFullName());
      
      // 2. execute generator
      new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), model,
          outputDirectory.toFile(), Optional.of("src/test/java/"));
    }
    // 3. load generated java class
    symTab = createJavaSymTab(outputDirectory);
    for (String expectedName : expectedNames) {
      JavaTypeSymbol generatedComponentClass = symTab.<JavaTypeSymbol> resolve(
          expectedName, JavaTypeSymbol.KIND).orElse(null);
      generatedComponentClasses.add(generatedComponentClass);
    }
  }
  
  @Test
  public void testSimpleDeployment() {
    String packageName = "simpleDeployment.";
    List<String> models = Arrays.asList(packageName + "A", packageName + "BWithSubcomponentA",
        packageName + "C", packageName + "ComplexComponent", packageName + "ValueGenerator",
        packageName + "Deployment");
    List<String> expectedClassNames = Arrays.asList(packageName + "ATOP", packageName + "C",
        packageName + "ComplexComponent", packageName + "DeployDeployment",
        packageName + "Deployment", packageName + "ValueGenerator");
    generateModels(models, expectedClassNames);
    
    // SETUP
    // Deployment cmp = new Deployment();
    // cmp.setUp();
    // cmp.init();
    // ///////
    //
    // cmp.getGenerator().getValue().setNextValue(5);
    //
    // int tick = 0;
    // while(null == cmp.getCComponent().getCCStringOut().getCurrentValue() && tick <= 20){
    // System.out.println("Tick: " + tick);
    // cmp.update();
    // cmp.compute();
    // tick++;
    // }
    // assertEquals("5", cmp.getCComponent().getCCStringOut().getCurrentValue());
  }
  
  @Test
  public void testBumperBot() {
    String packageName = "bumperbot.";
    List<String> models = Arrays.asList(packageName + "BumperBot", packageName + "BumpControl",
        packageName + "library.DistanceSensor", packageName + "library.Motor",
        packageName + "library.Timer", packageName + "library.Ultrasonic", packageName + "Logging",
        packageName + "library.Logger");
    generateModels(models, new ArrayList<>());
  }
  
  @Test
  public void testCDIntegration() {
    Scope symtab = createSymTab(modelPath);
    ComponentSymbol compSymbol = symtab
        .<ComponentSymbol> resolve("bumperbot.library.Motor", ComponentSymbol.KIND).orElse(null);
    assertNotNull(compSymbol);
    Optional<PortSymbol> speedPort = compSymbol.getIncomingPort("speed");
    assertTrue(speedPort.isPresent());
    JTypeReference<?> speedType = speedPort.get().getTypeReference();
    assertEquals("MotorCmd", speedType.getName());
    assertEquals("bumperbot.library.Types.MotorCmd", speedType.getReferencedSymbol().getFullName());
  }
  
}
