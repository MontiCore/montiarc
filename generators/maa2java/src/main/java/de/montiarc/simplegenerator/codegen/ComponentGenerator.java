/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.simplegenerator.codegen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import de.montiarc.simplegenerator.MontiArcGeneratorConstants;
import de.monticore.common.common._ast.ASTStereotype;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.lang.montiarc.helper.Timing;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Generates components.
 *
 * @author Robert Heim
 */
public class ComponentGenerator {
  
  private final static String LOGGER_NAME = ComponentGenerator.class.getName();
  
  public static final String JAVA_EXTENSION = "java";
  
  public static final Set<String> HWC_EXTENSIONS = Sets.newHashSet(JAVA_EXTENSION);
  
  private final static IterablePath HANDWRITTEN_CODE_PATH = IterablePath.from(
      Paths.get("src/main/java/").toFile(), HWC_EXTENSIONS);
  
  private static String calcSuperComponent(Timing timing, ComponentSymbol compSym) {
    if (!compSym.getSuperComponent().isPresent()) {
      return (timing == Timing.UNTIMED)
          ? MontiArcGeneratorConstants.ABSTRACT_COMPONENT_NAME
          : MontiArcGeneratorConstants.ABSTRACT_TIMED_COMPONENT_NAME;
    }
    else {
      ComponentSymbolReference superComponent = compSym.getSuperComponent().get();
      ComponentSymbol superComponentType = superComponent.getReferencedComponent().get();
      
      StringBuilder superComp = new StringBuilder();
      // inherit from super component implementation, if the super
      // component is a behavioral component
      if (superComponentType.getSubComponents().isEmpty()) {
        superComp.append(superComponentType.getFullName());
        superComp.append(MontiArcGeneratorConstants.DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX);
      }
      else {
        superComp.append(superComponentType.getFullName());
      }
      superComp.append(printTypeArguments(superComponent));
      return superComp.toString();
    }
  }
  
  private static String getSuperInterface(ComponentSymbol compSym) {
    Timing timing = Timing.getBehaviorKind((ASTComponent) compSym.getAstNode().get());
    
    if (!compSym.getSuperComponent().isPresent()) {
      return Timing.UNTIMED == timing
          ? MontiArcGeneratorConstants.COMPONENT_INTERFACE_NAME
          : MontiArcGeneratorConstants.TIMED_COMPONENT_INTERFACE_NAME;
    }
    else {
      // superComponent does exist
      ComponentSymbolReference superCompRef = compSym.getSuperComponent().get();
      ComponentSymbol superComponentType = superCompRef.getReferencedSymbol();
      
      StringBuilder superInterface = new StringBuilder();
      superInterface.append(superComponentType.getPackageName());
      superInterface.append("." + MontiArcGeneratorConstants.INTERFACES_PACKAGE + ".I");
      superInterface.append(superComponentType.getName());
      superInterface.append(printTypeArguments(superCompRef));
      return superInterface.toString();
    }
  }
  
  private static String printTypeArguments(ComponentSymbolReference superCompRef) {
    StringBuilder superInterface = new StringBuilder();
    // print potential generic-bindings
    if (!superCompRef.getActualTypeArguments().isEmpty()) {
      String sep = "<";
      for (ActualTypeArgument subParam : superCompRef.getActualTypeArguments()) {
        superInterface.append(sep);
        sep = ", ";
        // recursively print the types arguments
        superInterface.append(GeneratorHelper.printType(subParam.getType()));
      }
      superInterface.append(">");
    }
    return superInterface.toString();
  }
  
  /**
   * Prints only the names of the formal type parameters (i.e., without their
   * bounds). This is helpful for chaining the formal type parameters of e.g. a
   * class to its interface: <br/>
   * <br/>
   * {@code AComplexComponent<K, V extends Number> implements IComplexComponent<K, V>}
   * <br/>
   * <br/>
   * The latter use ({@code <K, V>}) is printed by this method.
   * 
   * @param compSym
   * @return
   */
  private static String printFormalTypeParametersWithoutBounds(ComponentSymbol compSym) {
    StringBuilder superInterface = new StringBuilder();
    // print potential generic-bindings
    if (!compSym.getFormalTypeParameters().isEmpty()) {
      String sep = "<";
      for (JTypeSymbol subParam : compSym.getFormalTypeParameters()) {
        superInterface.append(sep);
        sep = ", ";
        superInterface.append(subParam.getName());
      }
      superInterface.append(">");
    }
    return superInterface.toString();
  }
  
  public static void generate(GlobalExtensionManagement glex, ASTComponent compAst,
      ComponentSymbol compSym, File outputDirectory, Optional<String> hwcPath) {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory);
    setup.setGlex(glex);
    GeneratorHelper helper = new GeneratorHelper();
    final GeneratorEngine generator = new GeneratorEngine(setup);
    
    // trafos
    generateComponent(generator, compAst, compSym, helper, hwcPath);
    generateDeployer(generator, compAst, compSym, helper);
  }
  
  
  private static String calcInterfaceName(ComponentSymbol compSym) {
    return "I" + compSym.getName();
  }
  
  private static String calcInterfacePackage(ComponentSymbol compSym) {
    return compSym.getPackageName() + "." + MontiArcGeneratorConstants.INTERFACES_PACKAGE;
  }
  
  private static void generateComponent(GeneratorEngine generator, ASTComponent compAst,
      ComponentSymbol compSym, GeneratorHelper helper, Optional<String> hwcPath) {
    final String comments = GeneratorHelper.getCommentAsString(compAst.get_PreComments());
    final String _package = compSym.getPackageName();
    final String path = Names.getPathFromPackage(_package);
    
    String prefix = "";
    IterablePath handwrittenPath = HANDWRITTEN_CODE_PATH;
    if (hwcPath.isPresent()) {
      handwrittenPath = IterablePath.from(
          Paths.get(hwcPath.get()).toFile(), HWC_EXTENSIONS);
    }
    boolean existHWC = GeneratorHelper.existsHandwrittenClass(handwrittenPath,
        compSym.getFullName());
    String compName = prefix + compSym.getName();
    if (existHWC) {
      compName += "TOP";
    }
   
    String formalTypeParams = printFormalTypeParametersWithoutBounds(compSym);
    final Path filePath = Paths.get(path, compName + ".java");
    System.out.println(compSym.getFormalTypeParameters());
    generator.generate("templates.component.Component", filePath, compAst,
        compSym, _package, comments,
        helper, existHWC, formalTypeParams);
    Log.trace(LOGGER_NAME,
        String.format("Generated java class %s for component-model %s.", compName,
            compSym.getFullName()));
  }
  
  private static void generateDeployer(GeneratorEngine generator, ASTComponent compAst,
      ComponentSymbol compSym, GeneratorHelper helper) {
    final String _package = compSym.getPackageName();
    final String path = Names.getPathFromPackage(_package);
    
    String prefix = "";
    Optional<ASTStereotype> stereotype = compAst.getStereotype();
    boolean isDeploy = false;
    if (stereotype.isPresent()) {
      if (stereotype.get().containsStereoValue("deploy")) {
        prefix = "Deploy";
        if (compSym.isAtomic()) {
          // prefix += "A";
        }
        isDeploy = true;
      }
    }
    
    final String compName = prefix + compSym.getName();
    final Path filePath = Paths.get(path, compName + ".java");
    
    if (isDeploy) {
      generator.generate("templates.component.Deployer", filePath, compAst, _package, compSym);
    }
    Log.trace(LOGGER_NAME,
        String.format("Generated java class %s for component-model %s.", compName,
            compSym.getFullName()));
  }
  
  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().collect(Collectors.mapping(file -> new File(file), Collectors.toList()));
  }
}
