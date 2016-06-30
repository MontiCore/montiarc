/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.simplegenerator.codegen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.io.paths.IterablePath;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbolReference;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.Names;
import jline.internal.Log;

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
  
  public static String printType(ComponentSymbolReference type) {
    return type.getFullName();
  }
  
  public static String printInnerComponent(ComponentSymbolReference typeRef, String simpleCompName) {
    String fullName = typeRef.getFullName();
    if(fullName.contains(simpleCompName)){
      return fullName.replace(simpleCompName+".", "");
    }
    return fullName;
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
  
  public static Map<String, List<String>> getConnectors(ComponentSymbol symbol) {
    Map<String, List<String>> connectors = new HashMap<>();
    Collection<ConnectorSymbol> c = symbol.getConnectors();
    for (ConnectorSymbol co : c) {
      co.getSource();
      co.getTarget();
    }
    return connectors;
  }
  
  public static String getConnectorPortName(String connectorEnd) {
    if (connectorEnd.contains(".")) {
      return connectorEnd.substring(connectorEnd.lastIndexOf(".") + 1);
    }
    return connectorEnd;
  }
  
  public static boolean containsDot(String s) {
    return s.contains(".");
  }
  
  public static String getConnectorPortType(String connectorEnd) {
    if (connectorEnd.contains(".")) {
      return connectorEnd.substring(0, connectorEnd.lastIndexOf("."));
    }
    return connectorEnd;
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
  
}
