/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.List;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponentHead;
import montiarc._ast.ASTParameter;
import montiarc._cocos.MontiArcASTComponentHeadCoCo;

/**
 * Ensures that parameters in the component's head are defined in the right order.
 * It is not allowed to define a normal parameter after a declaration of a default parameter.
 * E.g.: Wrong: A[int x = 5, int y]
 * Right: B[int x, int y = 5]
 *
 * @author Andreas Wortmann
 */
public class DefaultParametersAssignentCorrectlyTyped
    implements MontiArcASTComponentHeadCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentHeadCoCo#check(montiarc._ast.ASTComponentHead)
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
          Log.error("0xMA056 There are non default parameters after a default parameter",
              node.get_SourcePositionStart());
        }
      }
    }

  }

}
