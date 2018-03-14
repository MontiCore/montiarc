/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponentHead;
import montiarc._cocos.MontiArcASTComponentHeadCoCo;

/**
 * @author (last commit) Crispin Kirchner
 */
public class TypeParameterNamesUnique implements MontiArcASTComponentHeadCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponentHead node) {
    ASTTypeParameters typeParameters = node.getGenericTypeParametersOpt().orElse(null);
    if (typeParameters == null) {
      return;
    }
    
    List<String> typeParameterNames = new ArrayList<>();
    for (ASTTypeVariableDeclaration typeParameter : typeParameters
        .getTypeVariableDeclarationList()) {
      
      if (typeParameterNames.contains(typeParameter.getName())) {
        Log.error(String.format(
            "0xMA006 The formal type parameter name \"%s\" is not unique",
            typeParameter.getName()), typeParameter.get_SourcePositionStart());
      }
      
      else {
        typeParameterNames.add(typeParameter.getName());
      }
    }
  }
  
}
