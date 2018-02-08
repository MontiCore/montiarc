/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponentHead;
import montiarc._ast.ASTParameter;
import montiarc._cocos.MontiArcASTComponentHeadCoCo;

/**
 * @author Crispin Kirchner
 */
public class ParameterNamesAreUnique implements MontiArcASTComponentHeadCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentHeadCoCo#check(montiarc._ast.ASTComponentHead)
   */
  @Override
  public void check(ASTComponentHead node) {
    List<ASTParameter> parameters = node.getParameters();

    List<String> parameterNames = new ArrayList<>();
    for (ASTParameter parameter : parameters) {

      if (parameterNames.contains(parameter.getName())) {
        Log.error(String.format("0xMA069 Parameter name \"%s\" not unique", parameter.getName()),
            parameter.get_SourcePositionStart());
      }

      else {
        parameterNames.add(parameter.getName());
      }
    }
  }

}
