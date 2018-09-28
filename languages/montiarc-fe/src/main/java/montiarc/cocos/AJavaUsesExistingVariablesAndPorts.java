/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor.ExpressionKind;

/**
 * This coco checks whether only declared variables are used in ajava.
 *
 * @author Pfeiffer
 * @version $Revision$, $Date$
 */
public class AJavaUsesExistingVariablesAndPorts implements MontiArcASTJavaPBehaviorCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTJavaPBehaviorCoCo#check(montiarc._ast.ASTJavaPBehavior)
   */
  @Override
  public void check(ASTJavaPBehavior node) {
    Scope componentScope = node.getEnclosingScope().get();
    
    for (ASTBlockStatement blockStmnt : node.getBlockStatementList()) {
      NamesInExpressionsDelegatorVisitor visitor = new NamesInExpressionsDelegatorVisitor();
      blockStmnt.accept(visitor);
      Map<ASTNameExpression, ExpressionKind> foundNames = visitor.getFoundNames();
      
      for (Entry<ASTNameExpression, ExpressionKind> entry : foundNames.entrySet()) {
        // check whether used name is declared as component variable or port or
        // is a static type
        Collection<VariableSymbol> vars = componentScope
            .<VariableSymbol> resolveDownMany(entry.getKey().getName(), VariableSymbol.KIND);
        Collection<PortSymbol> ports = componentScope
            .<PortSymbol> resolveDownMany(entry.getKey().getName(), PortSymbol.KIND);
        Optional<JTypeSymbol> type = componentScope.resolve(entry.getKey().getName(),
            JTypeSymbol.KIND);
        
        // check whether used name is local ajava variable
        Collection<VariableSymbol> localVar = Collections.emptyList();
        if (entry.getKey().getEnclosingScope().isPresent()) {
          localVar = entry.getKey().getEnclosingScopeOpt().get().resolveMany(entry.getKey().getName(),
              VariableSymbol.KIND);
        }
        
        if (vars.isEmpty() && ports.isEmpty() && !type.isPresent() && localVar.isEmpty()) {
          Log.error(
              "0xMA107 used variable " + entry.getKey().getName() + " is used but not declared.",
              entry.getKey().get_SourcePositionStart());
        }
      }
    }
    
  }
  
}
