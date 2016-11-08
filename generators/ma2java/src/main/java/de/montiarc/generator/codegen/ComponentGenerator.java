/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import _templates._setup.GeneratorConfig;
import _templates.mc.umlp.arc.factory.ComponentFactory;
import _templates.mc.umlp.arc.implementation.Component;
import _templates.mc.umlp.arc.interfaces.ComponentInterface;

import com.google.common.collect.Sets;

import de.montiarc.generator.MontiArcGeneratorConstants;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.ExtendedGeneratorEngine;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.lang.montiarc.helper.SymbolPrinter;
import de.monticore.lang.montiarc.helper.Timing;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbolReference;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.types.JFieldSymbol;
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
      if (timing == Timing.UNTIMED) {
        if (PortHelper.isSingleIn(compSym)) {
          return MontiArcGeneratorConstants.ABSTRACT_SINGLE_IN_COMPONENT_NAME;
        }
        else {
          return MontiArcGeneratorConstants.ABSTRACT_COMPONENT_NAME;
        }
      }
      else {
        if (PortHelper.isSingleIn(compSym)) {
          return MontiArcGeneratorConstants.ABSTRACT_TIMED_SINGLE_IN_COMPONENT_NAME;
        }
        else {
          return MontiArcGeneratorConstants.ABSTRACT_TIMED_COMPONENT_NAME;
        }
      }
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
  
  private static String getPortInterfaces(ComponentSymbol compSym) {
    StringBuilder sb = new StringBuilder();
    if (compSym.isAtomic()) {
      String incomingPortType = "";
      for (PortSymbol ps : compSym.getPorts()) {
        if (ps.isIncoming()) {
          incomingPortType = GeneratorHelper.printType(ps.getTypeReference());
        }
      }
      boolean singleIn = PortHelper.isSingleIn(compSym);
      if (singleIn) {
        sb.append(", ");
        sb.append(MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME);
        sb.append("<");
        sb.append(incomingPortType);
        sb.append(">, ");
        sb.append(MontiArcGeneratorConstants.SIMPLE_IN_PORT_INTERFACE_NAME);
        sb.append("<");
        sb.append(incomingPortType);
        sb.append(">");
      }
    }
    return sb.toString();
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
    GeneratorHelper helper = new GeneratorHelper();
    glex.setGlobalValue(MontiArcGeneratorConstants.TIME_PARADIGM_STORAGE_KEY,
        compSym.getBehaviorKind());
    setup.setGlex(glex);
    GeneratorConfig.init(setup);
    generateComponentInterface(compAst, compSym, helper);
    generateComponent(compAst, compSym, helper);
    generateComponentFactory(compAst, compSym, helper, hwcPath);
  }
  
  /**
   * Recursively generates the components interface as well as the interfaces
   * for its inner components
   * 
   * @param generator
   * @param compAst
   * @param compSym
   * @param helper
   */
  private static void generateComponentInterface(ASTComponent compAst,
      ComponentSymbol compSym, GeneratorHelper helper) {
    final String comments = GeneratorHelper.getCommentAsString(compAst.get_PreComments());
    
    final String _package = calcInterfacePackage(compSym);
    final String interfaceName = calcInterfaceName(compSym);
    final Path filePath = Paths.get(Names.getPathFromPackage(_package), interfaceName + ".java");
    String superInterface = getSuperInterface(compSym);
    String portInterfaces = getPortInterfaces(compSym);
    List<PortSymbol> ports = (List<PortSymbol>) compSym.getPorts();
    String formalTypeParams = SymbolPrinter.printFormalTypeParameters(compSym
        .getFormalTypeParameters());
    // component needs an additional port for receiving ticks
    boolean needsAdditionalPort = compSym.getAllIncomingPorts().isEmpty();
    ComponentInterface.generate(filePath, compAst, _package,
        interfaceName, ports, superInterface, portInterfaces, needsAdditionalPort,
        formalTypeParams, helper,
        comments);
    Log.trace(LOGGER_NAME, String.format("Generated java interface %s for component-model %s.",
        interfaceName, compSym.getFullName()));
    for (ComponentSymbol inner : compSym.getInnerComponents()) {
      generateComponentInterface((ASTComponent) inner.getAstNode().get(), inner, helper);
    }
  }
  
  private static String calcInterfaceName(ComponentSymbol compSym) {
    return "I" + compSym.getName();
  }
  
  private static String calcInterfacePackage(ComponentSymbol compSym) {
    return compSym.getPackageName() + "." + MontiArcGeneratorConstants.INTERFACES_PACKAGE;
  }
  
  private static void generateComponent(ASTComponent compAst,
      ComponentSymbol compSym, GeneratorHelper helper) {
    String comments = GeneratorHelper.getCommentAsString(compAst.get_PreComments());
    final String _package = compSym.getPackageName();
    final String path = Names.getPathFromPackage(_package);
    
    String modifier = "";
    String prefix = "";
    if (compSym.isAtomic()) {
      modifier = "abstract ";
      prefix = "A";
    }
    final String compName = prefix + compSym.getName();
    final Path filePath = Paths.get(path, compName + ".java");
    final Timing timingParadigm = Timing.getBehaviorKind(compAst);
    final String superComponent = calcSuperComponent(timingParadigm, compSym);
    final String fqCompInterface = Names
        .getQualifiedName(Arrays.asList(calcInterfacePackage(compSym), calcInterfaceName(compSym)));
    final String fqCompInterfaceWithTypeParameters = fqCompInterface
        + printFormalTypeParametersWithoutBounds(compSym);
    String formalTypeParams = SymbolPrinter.printFormalTypeParameters(compSym
        .getFormalTypeParameters());
    Component.generate(filePath, compAst,
        compSym, _package, comments, modifier, prefix, superComponent,
        fqCompInterfaceWithTypeParameters, helper, new PortHelper(), timingParadigm,
        formalTypeParams);
    Log.trace(LOGGER_NAME,
        String.format("Generated java class %s for component-model %s.", compName,
            compSym.getFullName()));
    for (ComponentSymbol inner : compSym.getInnerComponents()) {
      generateComponent((ASTComponent) inner.getAstNode().get(), inner, helper);
    }
  }
  
  private static void generateComponentFactory(ASTComponent compAst,
      ComponentSymbol compSym, GeneratorHelper helper, Optional<String> hwcPath) {
    final String comments = GeneratorHelper.getCommentAsString(compAst.get_PreComments());
    final String _package = compSym.getPackageName() + "."
        + MontiArcGeneratorConstants.FACTORIES_PACKAGE;
    final String path = Names.getPathFromPackage(_package);
    final String compName = compSym.getName();
    final String factoryName = compName + "Factory";
    final Path filePath = Paths.get(path, factoryName + ".java");
    List<JFieldSymbol> configParameters = compSym.getConfigParameters();
    IterablePath handwrittenPath = HANDWRITTEN_CODE_PATH;
    if (hwcPath.isPresent()) {
      handwrittenPath = IterablePath.from(
          Paths.get(hwcPath.get()).toFile(), HWC_EXTENSIONS);
    }
    boolean existHWC = GeneratorHelper.existsHandwrittenClass(handwrittenPath,
        compSym.getFullName());
    
    ComponentFactory.generate(filePath, compAst, _package, factoryName,
        compSym, configParameters, helper, existHWC);
    
    for (ComponentSymbol inner : compSym.getInnerComponents()) {
      generateComponentFactory((ASTComponent) inner.getAstNode().get(), inner, helper,
          hwcPath);
    }
  }
}
