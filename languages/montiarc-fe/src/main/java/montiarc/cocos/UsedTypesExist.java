/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Checks whether the used formal type parameters, e.g. as type of a port, are
 * declared in the head of the component.
 *
 * @author Pfeiffer
 * @version $Revision$, $Date$
 */
public class UsedTypesExist implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    for (PortSymbol p : comp.getPorts()) {
      if (!p.getTypeReference().existsReferencedSymbol()) {
        Log.error("0xMA101 Type " + p.getTypeReference().getName() + " is used but does not exist.",
            p.getSourcePosition());
      }
    }
    
    for (VariableSymbol v : comp.getVariables()) {
      if (!v.getTypeReference().existsReferencedSymbol()) {
        Log.error("0xMA101 Type " + v.getTypeReference().getName() + " is used but does not exist.",
            v.getSourcePosition());
        
      }
    }
    
  }
}
