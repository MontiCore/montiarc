/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import de.montiarc.simplegenerator.MontiArcConfiguration;
import de.montiarc.simplegenerator.MontiArcScript;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ComponentGeneratorTest extends  AbstractSymtabTest {
  private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/arc/codegen");
  
  private static ComponentGeneratorTest theInstance = new ComponentGeneratorTest();
  
  // set once in doSetup
  private static List<JavaTypeSymbol> generatedComponentClasses = new ArrayList<>();
  
  private static Scope symTab = null;
  
  private void generateModel(String modelName, List<String> expectedNames) {
    // 1. load model: arc/codegen/a/ComplexComponent.arc
    generatedComponentClasses = new ArrayList<>();
    symTab = createSymTab(modelPath);
    ComponentSymbol comp = symTab
        .<ComponentSymbol> resolve(modelName, ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals(modelName, comp.getFullName());
    
    // 2. execute generator
    new MontiArcScript().generate(Arrays.asList(modelPath.toFile()), modelName,
        outputDirectory.toFile(), Optional.of("src/test/java/"));
    
    // 3. load generated java class
    symTab = createJavaSymTab(outputDirectory);
    for (String expectedName : expectedNames) {
      JavaTypeSymbol generatedComponentClass = symTab.<JavaTypeSymbol> resolve(
          expectedName, JavaTypeSymbol.KIND).orElse(null);
      generatedComponentClasses.add(generatedComponentClass);
      assertNotNull(generatedComponentClass);
    }
  }
  
  
  @Test
  public void testDeploy() {
    List<String> expectedNames = Arrays.asList("a.DeployableComponent", "a.DeployDeployableComponent");
    generateModel("a.DeployableComponent",expectedNames);
  }
  
  @Test
  public void testHWCIntegration(){
    List<String> expectedNames = Arrays.asList("a.ComponentWithExistingHWCTOP");
    generateModel("a.ComponentWithExistingHWC",expectedNames);
  }
  
  
  @Test
  public void testComposedComponent() {
    List<String> expectedNames = Arrays.asList("a.ComponentWithSubComponent");
    generateModel("a.ComponentWithSubComponent",expectedNames);
   }
  
  @Test
  public void testComponentWithInnerComponent(){
    generateModel("a.CompWithInnerComp", Collections.emptyList());
  }
  
  @Test
  public void testAtomicComponent() {
    List<String> expectedNames = Arrays.asList("a.SubComponent");
    generateModel("a.SubComponent",expectedNames);
  }

  
}
