/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTGuard;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTTransition;
import montiarc._cocos.MontiArcASTTransitionCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor.ExpressionKind;

/**
 * Checks whether input and output ports are used correctly in automaton
 * transitions.
 * 
 * @implements [Wor16] AR2: Inputs, outputs, and variables are used correctly.
 * (p. 103, Lst. 520)
 * @implements [RRW14a] T6: The direction of ports has to be respected.
 */
public class AutomatonUsesCorrectPortDirection implements MontiArcASTTransitionCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTTransitionCoCo#check(montiarc._ast.ASTTransition)
   */
  @Override
  public void check(ASTTransition node) {
    Optional<ASTGuard> guard = node.getGuardOpt();
    NamesInExpressionsDelegatorVisitor ev = new NamesInExpressionsDelegatorVisitor();
    Scope componentScope = node.getEnclosingScope().get().getEnclosingScope().get();
    // check guard
    if (guard.isPresent()) {
      ASTExpression guardExpr = guard.get().getGuardExpression().getExpression();
      guardExpr.accept(ev);
      Map<ASTNameExpression, ExpressionKind> foundNames = ev.getFoundNames();
      for (Entry<ASTNameExpression, ExpressionKind> entry : foundNames.entrySet()) {
        Optional<PortSymbol> port = componentScope.resolve(entry.getKey().getName(),
            PortSymbol.KIND);
        if (port.isPresent()) {
          if (port.get().isOutgoing() && !entry.getValue().equals(ExpressionKind.DEFAULT)) {
            Log.error(
                "0xMA022 Outgoing port " + entry.getKey().getName()
                    + " must not be used in guards.",
                entry.getKey().get_SourcePositionStart());
          }
        }
      }
    }
    
    // check reaction
    if (node.isPresentReaction()) {
      for (ASTIOAssignment reactionExpr : node.getReaction().getIOAssignmentList()) {
        ev = new NamesInExpressionsDelegatorVisitor();
        if (reactionExpr.isAssignment()) {
          // check left hand side of assignment
          Optional<PortSymbol> port = componentScope.resolve(reactionExpr.getName(),
              PortSymbol.KIND);
          if (port.isPresent() && port.get().isIncoming()) {
            Log.error(
                "0xMA102 Incoming port " + reactionExpr.getName()
                    + " must not be used in assignments of reaction.",
                reactionExpr.get_SourcePositionStart());
          }
        }
        
        if (reactionExpr.isPresentValueList() && reactionExpr.getValueList().isPresentValuation()) {
          reactionExpr.getValueList().getValuation().getExpression().accept(ev);
          Map<ASTNameExpression, ExpressionKind> foundNames = ev.getFoundNames();
          for (ASTNameExpression entry : foundNames.keySet()) {
            Optional<PortSymbol> port = componentScope.resolve(entry.getName(), PortSymbol.KIND);
            if (port.isPresent()) {
              if (port.get().isOutgoing()) {
                Log.error(
                    "0xMA103 Outgoing port " + entry.getName()
                        + " must not be used in call expressions of reaction.",
                    entry.get_SourcePositionStart());
              }
            }
          }
        }
      }
    }
    
  }
  
}
