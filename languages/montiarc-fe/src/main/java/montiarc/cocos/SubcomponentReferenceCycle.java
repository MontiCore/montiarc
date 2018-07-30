/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;

/**
 * @implements [Hab16] R13: Subcomponent reference cycles are forbidden. (p. 68,
 * lst. 3.48)
 * 
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class SubcomponentReferenceCycle implements MontiArcASTComponentCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    for(ComponentInstanceSymbol subcomp : comp.getSubComponents()) {
      for(ComponentInstanceSymbol subsubcomp : subcomp.getComponentType().getReferencedComponent().get().getSubComponents()) {
        if(comp.getName().equals(subsubcomp.getComponentType().getName())) {
          Log.error("0xMA086 Subcomponent "+subcomp.getName()+" declares "+comp.getName()+" as subcomponent as well.",  subcomp.getAstNode().get().get_SourcePositionStart());
        }
      }
    }
  }
  
}
