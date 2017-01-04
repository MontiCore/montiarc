/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava._symboltable;

import java.util.List;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * Represents the AJava definition within a component definition. Is scope
 * spanning as it contains several java statements.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class AJavaDefinitionSymbol extends CommonScopeSpanningSymbol {
  
  public static final AJavaDefinitionKind KIND = new AJavaDefinitionKind();
  
  /**
   * Constructor for
   * de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol
   * 
   * @param name
   * @param kind
   */
  public AJavaDefinitionSymbol(String name) {
    super(name, KIND);
  }
  
  private List<JTypeReference<JTypeSymbol>> typeReferences;
  
}
