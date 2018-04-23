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

/**
 * This coco forbids the definition of inner components
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */

public class InnerComponentsNotAllowed implements MontiArcASTComponentCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol sym = (ComponentSymbol) node.getSymbol().get();
    if(sym.isInnerComponent()) {
      Log.error("0xMA095 Inner component definitions are forbidden.",node.get_SourcePositionStart());
    }
  }
  
}
