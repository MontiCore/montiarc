/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava._symboltable;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.Symbols;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class SimpleVariableSymbol extends CommonSymbol{

  public static final SimpleVariableKind KIND = new SimpleVariableKind();

  
  /**
   * Constructor for de.monticore.lang.montiarc.ajava._symboltable.JavaExpressionVariableSymbol
   * @param name
   * @param kind
   */
  public SimpleVariableSymbol(String name) {
    super(name, KIND);
  }
  
}
