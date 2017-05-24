/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import de.monticore.lang.montiarc.montiarc._ast.ASTPort;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTPortCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * This CoCo checks, whether a in going port has a initial value. 
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
public class InPortHasNoInitialValue implements MontiArcASTPortCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTPortCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTPort)
   */
  @Override
  public void check(ASTPort node) {
    if(node.isIncoming()) {
      if(node.getInitialValue().isPresent()) {
        Log.error(String.format("0x2BD85 Ingoing port "+node.getName()+" must not have a initial value"),
            node.getInitialValue().get().get_SourcePositionStart());
      }
    }
  }
  
}
