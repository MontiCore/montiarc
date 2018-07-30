/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;

/**
 * @implements [Hab16] R11: Inheritance cycles of component types are forbidden. (p. 67, lst. 3.46)
 *
 * @author Pfeiffer
 * @version $Revision$, $Date$
 */
public class CircularInheritance implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol compSym = (ComponentSymbol) node.getSymbolOpt().get();
    List<String> superCompNames = new ArrayList<>();
    superCompNames.add(compSym.getPackageName()+ "."+ node.getName());
    if (compSym.getSuperComponent().isPresent()) {
      Optional<ComponentSymbolReference> superComp = compSym.getSuperComponent();
      while (superComp.isPresent()) {
            String name = superComp.get().getPackageName()+"."+superComp.get().getName();
            if (superCompNames.contains(name)) {
              Log.error("0xMA017 Circular inheritance detected between components "
                  + compSym.getName() + " and " + superComp.get().getName());
              return;
            }else {
              superCompNames.add(name);
            }
        superComp = superComp.get().getSuperComponent();
      }
      
    }
  }
  
}
