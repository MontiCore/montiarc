/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.ajava.generator.helper;

import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentVariableSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class AJavaHelper {
  
  private final ComponentSymbol component;

  public AJavaHelper(ComponentSymbol component) {
    this.component = component;
  }
  
  public String getPortTypeName(PortSymbol port) {
    return printFqnTypeName(port.getTypeReference());
  }
  
  public String printVariableTypeName(ComponentVariableSymbol var) {
    return printFqnTypeName(var.getTypeReference());
  }
  
  /**
   * Prints the type of the reference including dimensions.
   * 
   * @param ref
   * @return
   */
  protected String printFqnTypeName(JTypeReference<? extends JTypeSymbol> ref) {
    String name = ref.getName();
    for (int i = 0; i < ref.getDimension(); ++i) {
      name += "[]";
    }
    return name;
  }
  
}
