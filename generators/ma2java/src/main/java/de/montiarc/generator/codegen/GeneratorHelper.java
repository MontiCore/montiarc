/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jline.internal.Log;
import de.montiarc.generator.MontiArcGeneratorConstants;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.io.paths.IterablePath;
import de.monticore.lang.montiarc.helper.SymbolPrinter;
import de.monticore.lang.montiarc.helper.Timing;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbolReference;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

/**
 * Common helper methods for generator.
 *
 * @author Robert Heim
 */
public class GeneratorHelper {
  
  private static final String DEFAULT_FILE_EXTENSION = ".java";
  
  public static String getMontiArcVersion() {
    return "TODOMAVersion";
  }
  
  public static String getTimeNow() {
    return LocalDateTime.now().toString();
  }
  
  /**
   * @param comments comments that should be converted to a string.
   * @return the given comments as a string. Comment start- and end-markers
   * (e.g. '/**') are removed. '*' at the begining of a line are kept.
   */
  public static String getCommentAsString(List<Comment> comments) {
    StringBuilder sb = new StringBuilder();
    if (comments != null) {
      for (Comment c : comments) {
        sb.append(c.getText());
      }
    }
    String result = sb.toString().replace("/**", "").replace("/*", "").replace("*/", "");
    return result;
  }
  
  public static String printType(JTypeReference<?> typeRef) {
    // return ArcTypePrinter.printType((ASTType) typeRef.getAstNode().get());
    if (typeRef.getReferencedSymbol().isFormalTypeParameter()) {
      return typeRef.getName();
    }
    return typeRef.getReferencedSymbol().getFullName();
  }
  
  /**
   * TODO is there a helper in MC that can do it?<br/>
   * Recursively prints the type + its generics and the corresponding actual
   * type arguments.
   * 
   * @param type the type reference to print
   */
  public static String printType(TypeReference<? extends TypeSymbol> type) {
    StringBuilder sb = new StringBuilder();
    sb.append(type.getName());
    if (!type.getActualTypeArguments().isEmpty()) {
      String sep = "<";
      for (ActualTypeArgument subParam : type.getActualTypeArguments()) {
        sb.append(sep);
        sep = ", ";
        // recursively print the types arguments
        sb.append(printType(subParam.getType()));
      }
      sb.append(">");
    }
    return sb.toString();
  }
  
  public static String getSubcomponentInterface(ComponentInstanceSymbol subcomp) {
    String fqn = subcomp.getComponentType().getFullName();
    String simpleTypeName = subcomp.getComponentType().getName(); 
    String packageName = fqn.substring(0, fqn.indexOf(simpleTypeName));
    String interfaceName = packageName + "interfaces.I" + subcomp.getComponentType().getName();
    String genericTypeParams = SymbolPrinter.printTypeParameters(subcomp.getComponentType().getActualTypeArguments());
    return interfaceName + genericTypeParams;
  }
  
  /**
   * Revert an Java qualified name to a package name E.g. java.util.ArrayList ->
   * java.util.
   * 
   * @param in String to transform
   * @return
   */
  public static String getPackageWithDotFromComplexname(String in) {
    int posLess = in.indexOf("<");
    String withoutLess = in;
    if (posLess != -1) {
      withoutLess = withoutLess.substring(0, posLess - 1);
    }
    // find last .
    int pos = withoutLess.lastIndexOf('.');
    if (pos > 0) {
      return withoutLess.substring(0, pos + 1).intern();
    }
    return "";
  }
  
  public static boolean noIncomingPorts(ComponentSymbol comp) {
    return comp.getAllIncomingPorts().isEmpty();
  }
  
  public static String getAdditionalReceiverPort(ComponentSymbol comp) {
    boolean noIncomingPorts = comp.getAllIncomingPorts().isEmpty();
    if (noIncomingPorts && comp.isDecomposed()) {
      if (comp.getSubComponents().size() == 1) {
        return ((List<ComponentInstanceSymbol>) comp.getSubComponents()).get(0).getName();
      }
    }
    return null;
  }
  
  public static List<String> getSubWithoutPorts(ComponentSymbol comp) {
    List<String> subWithoutPorts = new ArrayList<>();
    for (ComponentInstanceSymbol sc : comp.getSubComponents()) {
      if (sc.getComponentType().getAllIncomingPorts().size() == 0) {
        subWithoutPorts.add(sc.getName());
      }
      else if (sc.getComponentType().getName()
          .equals(MontiArcGeneratorConstants.TICK_SOURCE_COMPONENT)
          && sc.getName().equals(MontiArcGeneratorConstants.TICK_SOURCE_SC_NAME)
          && comp.getAllIncomingPorts().isEmpty()) {
        subWithoutPorts.add(sc.getName());
      }
    }
    return subWithoutPorts;
  }
  
  public static String getSender(ComponentSymbol comp) {
    String sender = null;
    if (getSubWithoutPorts(comp).size() > 0) {
      if (comp.getIncomingPorts().size() == 0) {
        sender = "_" + MontiArcGeneratorConstants.CODEGEN_TIME_IN_PORTNAME;
      }
      else {
        sender = comp.getIncomingPorts().iterator().next().getName();
      }
    }
    return sender;
  }
  
  /**
   * Checks whether an additional in-port for time is required. This is the case
   * when there are no incoming ports in a decomposed component with exactly 1
   * subcomponent. TODO improve doc
   * 
   * @param symbol
   * @return
   */
  public static boolean requiresPortTimeIn(ComponentSymbol symbol) {
    return symbol.getAllIncomingPorts().isEmpty() && symbol.isDecomposed()
        && symbol.getSubComponents().size() == 1;
  }
  
  public static String printTypeParameters(ComponentSymbol symbol) {
    ASTComponent a = ((ASTComponent) symbol.getAstNode().get());
    return TypesPrinter.printTypeParameters(a.getHead().getGenericTypeParameters().get());
  }
  
  public static String printTypeParameters(JTypeSymbol symbol) {
    if (!symbol.getAstNode().isPresent()) {
      // TODO ?!
      return "";
    }
    ASTNode n = symbol.getAstNode().get();
    if (n instanceof ASTComponent) {
      ASTComponent a = (ASTComponent) n;
      return TypesPrinter.printTypeParameters(a.getHead().getGenericTypeParameters().get());
    }
    // TODO non-component types?
    Log.error("non-component types are not yet fully supported for generic-bindings");
    return "";
  }
  
  /**
   * help function for nested type arguments such as List<NewType<String,
   * List<String>>>
   */
  public static String printTypeParameters(ActualTypeArgument arg) {
    String ret = arg.getType().getName();
    if (arg.getType().getActualTypeArguments() != null
        && !arg.getType().getActualTypeArguments().isEmpty()) {
      ret += "<" + arg.getType().getActualTypeArguments().stream().
          map(a -> printTypeParameters(a)).collect(Collectors.joining(",")) + ">";
    }
    return ret;
  }
  
  public static String printTypeParameters(List<ActualTypeArgument> arg) {
    if (arg.isEmpty())
      return "";
    return "<" + arg.stream().map(a -> printTypeParameters(a)).
        collect(Collectors.joining(",")) + ">";
  }
  
  /**
   * help function for nested type arguments such as List<NewType<String,
   * List<String>>>
   */
  public static String printFormalTypeParameters(JTypeSymbol arg) {
    String ret = arg.getName();
    
    // if (!arg.getSuperTypes().isEmpty()) {
    // ret += " extends " + arg.getSuperTypes().stream()
    // .map(t -> t.getName() + printTypeParameters(t.getActualTypeArguments()))
    // .collect(Collectors.joining("&"));
    // }
    
    if (arg.getFormalTypeParameters() != null && !arg.getFormalTypeParameters().isEmpty()) {
      ret += "<" + arg.getFormalTypeParameters().stream().
          map(a -> printFormalTypeParameters(a)).collect(Collectors.joining(",")) + ">";
    }
    return ret;
  }
  
  /**
   * @return string representation of the type parameters associated with this
   * port.
   */
  public static String printFormalTypeParameters(List<JTypeSymbol> arg) {
    if (arg.isEmpty())
      return "";
    return "<" + arg.stream()
        .map(a -> printFormalTypeParameters(a))
        .collect(Collectors.joining(",")) + ">";
  }
  
  public static String printConfigParameters(List<JFieldSymbol> configParams) {
    String ret = "";
    for (JFieldSymbol p : configParams) {
      ret += printType(p.getType()) + " " + p.getName() + ", ";
    }
    
    if (ret.contains(",")) {
      ret = ret.substring(0, ret.lastIndexOf(","));
    }
    return ret;
  }
  
  public static String printConfigParametersNames(List<JFieldSymbol> configParams) {
    String ret = "";
    for (JFieldSymbol p : configParams) {
      ret += p.getName() + ", ";
    }
    
    if (ret.contains(",")) {
      ret = ret.substring(0, ret.lastIndexOf(","));
    }
    return ret;
  }
  
  public static String printSuperComponentConfigParameterNames(ComponentSymbol comp) {
    Optional<ComponentSymbolReference> superC = comp.getSuperComponent();
    String ret = "";
    if (superC.isPresent()) {
      ret = printConfigParametersNames(superC.get().getConfigParameters());
    }
    return ret;
  }
  
  public static String printSuperComponentConfigParametersNamesForSuperCall(ComponentSymbol comp) {
    Optional<ComponentSymbolReference> superC = comp.getSuperComponent();
    String ret = "";
    List<JFieldSymbol> toPrint = new ArrayList<>();
    if (superC.isPresent()) {
      List<JFieldSymbol> compConfigParams = comp.getConfigParameters();
      for (int i = 0; i < superC.get().getConfigParameters().size(); i++) {
        toPrint.add(compConfigParams.get(i));
      }
      ret = printConfigParametersNames(toPrint);
    }
    return ret;
  }
  
  public static String getSubComponentFactoryName(ComponentInstanceSymbol subcomponent) {
    StringBuilder type = new StringBuilder();
    String pack = subcomponent.getComponentType().getReferencedSymbol().getPackageName();
    if (pack == null) {
      pack = subcomponent.getFullName().substring(0, subcomponent.getFullName().lastIndexOf('.'));
    }
    else if (!pack.isEmpty()) {
      pack += ".";
    }
    
    type.append(pack);
    // type.append("gen.");
    type.append("factories");
    if (subcomponent.getComponentType().isInnerComponent()) {
      List<String> compTypeAsList = Arrays.asList(subcomponent.getComponentType().getFullName()
          .split("."));
      String sep = "";
      for (String s : compTypeAsList) {
        type.append(sep);
        sep = "_";
        type.append(s);
      }
    }
    else {
      String simpleName = subcomponent.getComponentType().getFullName()
          .substring(subcomponent.getComponentType().getFullName().lastIndexOf("."));
      type.append(simpleName);
    }
    type.append("Factory");
    return type.toString();
  }
  
  /**
   * TODO replace this method with something from MC and do not store the
   * imports in ComponentSymbol anymore (see
   * {@link ComponentSymbol#setImports(List)}).
   * 
   * @param symbol
   * @return
   */
  public static List<String> getImports(ComponentSymbol symbol) {
    // TODO this probably should be implemented somewhere in MC
    List<String> statements = new ArrayList<>();
    for (ImportStatement is : symbol.getImports()) {
      String i = is.getStatement() + (is.isStar() ? ".*" : "");
      statements.add(i);
    }
    return statements;
  }
  
  public static String getLocalTime(ComponentSymbol compSym) {
    List<ComponentInstanceSymbol> timedSCs = new LinkedList<>();
    for (ComponentInstanceSymbol sc : compSym.getSubComponents()) {
      if (!sc.getComponentType().getBehaviorKind().equals(Timing.UNTIMED)) {
        timedSCs.add(sc);
      }
    }
    
    StringBuilder sb = new StringBuilder();
    if (timedSCs.size() > 1) {
      int closer = 0;
      String sep = ", ";
      for (ComponentInstanceSymbol sc : timedSCs) {
        closer++;
        if (closer != timedSCs.size()) {
          sb.append("Math.min(");
        }
        else {
          sep = "";
        }
        sb.append(sc.getName());
        sb.append(".getLocalTime()");
        sb.append(sep);
      }
      for (int i = 0; i < closer - 1; i++) {
        sb.append(")");
      }
    }
    else if (timedSCs.size() == 1) {
      sb.append(timedSCs.iterator().next().getName());
      sb.append(".getLocalTime()");
    }
    else {
      sb.append("-1");
    }
    
    return sb.toString();
  }
  
  /**
   * Checks if a handwritten class with the given qualifiedName (dot-separated)
   * exists on the target path
   * 
   * @param qualifiedName name of the class to search for
   * @return true if a handwritten class with the qualifiedName exists
   */
  public static boolean existsHandwrittenClass(IterablePath targetPath,
      String qualifiedName) {
    Path handwrittenFile = Paths.get(Names
        .getPathFromPackage(qualifiedName)
        + DEFAULT_FILE_EXTENSION);
    return targetPath.exists(handwrittenFile);
  }
  
  /**
   * Calculates a member and a getter for subcomponent instance sc
   * 
   * @param compAst
   * @param generator
   * @param superComp
   * @param sc
   * @return
   */
  private static String calcSubcomponentType(ComponentSymbol superComp, ComponentInstanceSymbol sc) {
    
    StringBuilder type = new StringBuilder();
    String pack = sc.getPackageName();
    if (pack == null) {
      pack = GeneratorHelper.getPackageWithDotFromComplexname(sc.getName());
    }
    else if (pack.isEmpty()) {
      pack += ".";
    }
    type.append(pack);
    type.append("interfaces.I");
    ComponentSymbolReference componentType = sc.getComponentType();
    if (componentType.isInnerComponent()) {
      type.append(superComp.getName());
      type.append("_");
      type.append(componentType.getName());
    }
    else {
      type.append(componentType.getName());
    }
    
    String typeArgs = GeneratorHelper.printTypeParameters(componentType.getReferencedSymbol());
    if (!typeArgs.isEmpty()) {
      type.append(typeArgs.substring(typeArgs.indexOf("<")));
    }
    return type.toString();
  }
  
}
