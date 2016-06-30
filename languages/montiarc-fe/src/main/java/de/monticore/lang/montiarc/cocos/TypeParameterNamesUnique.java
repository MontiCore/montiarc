/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentHeadCoCo;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.logging.Log;

/**
 * @author (last commit) Crispin Kirchner
 */
public class TypeParameterNamesUnique implements MontiArcASTComponentHeadCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponentHead node) {
    ASTTypeParameters typeParameters = node.getGenericTypeParameters().orElse(null);
    if (typeParameters == null) {
      return;
    }

    List<String> typeParameterNames = new ArrayList<>();
    for (ASTTypeVariableDeclaration typeParameter : typeParameters.getTypeVariableDeclarations()) {

      if (typeParameterNames.contains(typeParameter.getName())) {
        Log.error(String.format(
            "0x35F1A The formal type parameter name \"%s\" is not unique",
            typeParameter.getName()), typeParameter.get_SourcePositionStart());
      }

      else {
        typeParameterNames.add(typeParameter.getName());
      }
    }
  }

}
