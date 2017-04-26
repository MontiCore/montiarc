/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * Symboltable entry for variables.
 *
 * @author  Jerome Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
public class ComponentVariableSymbol extends CommonSymbol{
  
  public static final ComponentVariableKind KIND = ComponentVariableKind.INSTANCE;  
  
  private JTypeReference<? extends JTypeSymbol> typeReference;

  
  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol
   * @param name
   * @param kind
   */
  public ComponentVariableSymbol(String name) {
    super(name, KIND);
  }

  /**
   * @return typeReference reference to the type from this port
   */
  public JTypeReference<? extends JTypeSymbol> getTypeReference() {
    return this.typeReference;
  }
  
  /**
   * @param typeReference reference to the type from this port
   */
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }
  
}
