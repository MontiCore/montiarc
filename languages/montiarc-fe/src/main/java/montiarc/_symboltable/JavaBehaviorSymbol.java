/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

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
public class JavaBehaviorSymbol extends CommonScopeSpanningSymbol {
  
  public static final JavaBehaviorKind KIND = new JavaBehaviorKind();
  
  /**
   * Constructor for
   * de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol
   * 
   * @param name
   * @param kind
   */
  public JavaBehaviorSymbol(String name) {
    super(name, KIND);
  }
  
}
