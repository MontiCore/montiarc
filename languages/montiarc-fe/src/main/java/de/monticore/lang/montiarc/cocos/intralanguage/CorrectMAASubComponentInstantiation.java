/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos.intralanguage;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CorrectMAASubComponentInstantiation implements MontiArcASTComponentCoCo {
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * Checks whether the arguments of the passed subcomponent are consistent
   */
  private void checkArguments(ComponentSymbol cmp) {
    //XXX @JP: Was soll diese Klasse?
    
  }
  
}
