/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CircularInheritance implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol compSym = (ComponentSymbol) node.getSymbol().get();
    
    if (compSym.getSuperComponent().isPresent()) {
      Optional<ComponentSymbolReference> superComp = compSym.getSuperComponent();
      if (superComp.isPresent()) {
        Optional<ComponentSymbolReference> superSuper = superComp.get().getSuperComponent();
        if (superSuper.isPresent()) {
          String name = superSuper.get().getName();
          if (node.getName().equals(name)) {
            Log.error("0xMA090 Circular inheritance detected between components "
                + compSym.getName() + " and " + superComp.get().getName());
          }
        }
      }
      
    }
  }
  
}
