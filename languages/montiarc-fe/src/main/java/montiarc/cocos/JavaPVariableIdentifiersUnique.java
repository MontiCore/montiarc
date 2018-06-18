/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.VariableSymbol;

/**
 * Checks whether variables with same name are defined multiple times in JavaP behavior.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class JavaPVariableIdentifiersUnique implements MontiArcASTJavaPBehaviorCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTJavaPBehaviorCoCo#check(montiarc._ast.ASTJavaPBehavior)
   */
  @Override
  public void check(ASTJavaPBehavior node) {
    List<String> names = new ArrayList<>();
    if (node.getEnclosingScope().isPresent()) {
      for(Scope sub : node.getEnclosingScope().get().getSubScopes()) {
        Collection<VariableSymbol> vars = sub
            .<VariableSymbol> resolveLocally(VariableSymbol.KIND);
        for (VariableSymbol v : vars) {
          if (names.contains(v.getName())) {
            Log.error("0xMA016 Duplicate local variable names "+ v.getName()+".", v.getSourcePosition());
          }
          else {
            names.add(v.getName());
          }
        }
      }
    }
    
  }
  
}
