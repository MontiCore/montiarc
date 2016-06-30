/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import java.util.List;

import de.monticore.lang.montiarc.common._ast.ASTParameter;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentHeadCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures that parameters in the component's head are defined in the right order.
 * It is not allowed to define a normal parameter after a declaration of a default parameter.
 * E.g.: Wrong: A[int x = 5, int y]
 * Right: B[int x, int y = 5]
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class DefaultParametersHaveCorrectOrder
    implements MontiArcASTComponentHeadCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentHeadCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead)
   */
  @Override
  public void check(ASTComponentHead node) {
    List<ASTParameter> params = node.getParameters();
    boolean foundDefaultParameter = false;
    for (ASTParameter param : params) {

      if (!foundDefaultParameter) {
        foundDefaultParameter = param.getDefaultValue().isPresent();
      }
      else {
        if (foundDefaultParameter && !param.getDefaultValue().isPresent()) {
          Log.error("0xAC005 There are non default parameters after a default parameter",
              node.get_SourcePositionStart());
        }
      }
    }

  }

}
