/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.helper;

import java.util.Optional;

import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.Names;
import montiarc._ast.ASTValueInitialization;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

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
  
  public String printVariableTypeName(VariableSymbol var) {
    return printFqnTypeName(var.getTypeReference());
  }
  
  public String getParamTypeName(JFieldSymbol param) {
    return printFqnTypeName(param.getType());
  }
  
  public String printInit(ASTValueInitialization init) {
    String ret = "";
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    String name = Names.getQualifiedName(init.getQualifiedName().getParts());
    ret += name;
    ret+= " = ";
    ret+= printer.prettyprint(init.getValuation().getExpression());
    ret+= ";";
    
    return ret;
    
    
  }
  
  /**
   * Prints the type of the reference including dimensions.
   * 
   * @param ref
   * @return
   */
  protected String printFqnTypeName(JTypeReference<? extends JTypeSymbol> ref) {
    String name = ref.getName();
    Optional<JTypeSymbol> sym = ref.getEnclosingScope().<JTypeSymbol>resolve(ref.getName(), JTypeSymbol.KIND);
    if(sym.isPresent()){
      name = sym.get().getFullName();
    }
    for (int i = 0; i < ref.getDimension(); ++i) {
      name += "[]";
    }
    return name;
  }
  
}

