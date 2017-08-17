/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

// XXX: https://git.rwth-aachen.de/montiarc/core/issues/41


/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CorrectMAASubComponentInstantiation implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * Checks whether the arguments of the passed subcomponent are consistent
   */
  private void checkArguments(ComponentSymbol cmp) {
    //TODO @JP: Was soll diese Klasse?
    
  }
  
}
