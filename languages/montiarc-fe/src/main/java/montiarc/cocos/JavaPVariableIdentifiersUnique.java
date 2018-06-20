/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableKind;
import montiarc._symboltable.VariableSymbol;

/**
 * Checks whether variables with same name are defined multiple times in JavaP
 * behavior.
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
      for (Scope sub : node.getEnclosingScope().get().getSubScopes()) {
        Collection<VariableSymbol> vars = sub
            .<VariableSymbol> resolveLocally(VariableSymbol.KIND);
        for (VariableSymbol v : vars) {
          if (names.contains(v.getName())) {
            Log.error("0xMA093 Duplicate local variable names " + v.getName() + ".",
                v.getSourcePosition());
          }
          else {
            names.add(v.getName());
            checkPortOrVariableWithNameAlreadyExists(v);
          }
        }
      }
    }
    
  }
  
  /**
   * Checks whether the component defines a component variable or port with the
   * same name of the Java/P variable. If so, it logs a warning.
   * 
   * @param v
   */
  private void checkPortOrVariableWithNameAlreadyExists(VariableSymbol v) {
    Optional<PortSymbol> port = v.getEnclosingScope().<PortSymbol> resolve(v.getName(),
        PortSymbol.KIND);
    Collection<VariableSymbol> var = v.getEnclosingScope().<VariableSymbol> resolveMany(v.getName(),
        VariableSymbol.KIND);
    
    if (port.isPresent()) {
      Log.warn(
          "0xMA094 There already exists a port with name " + v.getName()
              + " in the component definition.",
          v.getAstNode().get().get_SourcePositionStart());
    }
    
    if (var.size() > 1) {
      Log.warn(
          "0xMA095 There already exists a variable with name " + v.getName()
              + " in the component definition.",
          v.getAstNode().get().get_SourcePositionStart());
    }
    
  }
  
}
