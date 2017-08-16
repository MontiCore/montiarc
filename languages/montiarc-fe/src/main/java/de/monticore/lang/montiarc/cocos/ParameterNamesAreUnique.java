/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.lang.montiarc.common._ast.ASTParameter;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentHeadCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * @author Crispin Kirchner
 */
public class ParameterNamesAreUnique implements MontiArcASTComponentHeadCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentHeadCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead)
   */
  @Override
  public void check(ASTComponentHead node) {
    List<ASTParameter> parameters = node.getParameters();

    List<String> parameterNames = new ArrayList<>();
    for (ASTParameter parameter : parameters) {

      if (parameterNames.contains(parameter.getName())) {
        Log.error(String.format("0xC4A61 Parameter name \"%s\" not unique", parameter.getName()),
            parameter.get_SourcePositionStart());
      }

      else {
        parameterNames.add(parameter.getName());
      }
    }
  }

}
