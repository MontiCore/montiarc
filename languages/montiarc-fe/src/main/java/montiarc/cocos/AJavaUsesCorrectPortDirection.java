/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor.ExpressionKind;

/**
 * Checks whether ports are correctly used regarding their direction.
 *
 * @author Pfeiffer
 * @version $Revision$, $Date$
 */
public class AJavaUsesCorrectPortDirection implements MontiArcASTJavaPBehaviorCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTJavaPBehaviorCoCo#check(montiarc._ast.ASTJavaPBehavior)
   */
  @Override
  public void check(ASTJavaPBehavior node) {
    Scope componentScope = node.getEnclosingScopeOpt().get();
    NamesInExpressionsDelegatorVisitor ev = new NamesInExpressionsDelegatorVisitor();
    for (ASTBlockStatement block : node.getBlockStatementList()) {
      block.accept(ev);
    }
    Map<ASTNameExpression, ExpressionKind> foundNames = ev.getFoundNames();
    
    for (Entry<ASTNameExpression, ExpressionKind> found : foundNames.entrySet()) {
      Optional<PortSymbol> port = componentScope.<PortSymbol> resolve(found.getKey().getName(),
          PortSymbol.KIND);
      if (port.isPresent()) {
        if (port.get().isIncoming()) {
          switch (found.getValue()) {
            case ASSIGNMENT_LEFT:
              Log.error("0xMA104 Incoming port " + port.get().getName()
                  + " must not be used on the left side of an assignment.");
              break;
            default:
              break;
          }
        }
        else if (port.get().isOutgoing()) {
          switch (found.getValue()) {
            case ASSIGNMENT_RIGHT:
              Log.error("0xMA105 Outgoing port " + port.get().getName()
                  + " must not be used on the right side of an assignment.");
              break;
            case CALL:
              Log.error("0xMA106 Outgoing port " + port.get().getName()
                  + " must not be used in method call statements.");
            default:
              break;
          }
        }
      }
    }
    
  }
  
}
