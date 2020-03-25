/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor.ExpressionKind;

/**
 * Checks whether ports and component parameters are correctly used regarding
 * their direction.
 *
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
                  + " must not be used on the left side of an assignment.",
                  found.getKey().get_SourcePositionStart());
              break;
            case POSTFIX_EXPR:
              Log.error("0xMA110 Ingoing port " + port.get().getName()
                  + " must not be used in postfix expressions.",
                  found.getKey().get_SourcePositionStart());
              break;
            case PREFIX_EXPR:
              Log.error("0xMA109 Ingoing port " + port.get().getName()
                  + " must not be used in method prefix expressions.",
                  found.getKey().get_SourcePositionStart());
              break;
            default:
              break;
          }
        }
        else if (port.get().isOutgoing()) {
          switch (found.getValue()) {
            case ASSIGNMENT_RIGHT:
              Log.error("0xMA105 Outgoing port " + port.get().getName()
                  + " must not be used on the right side of an assignment.",
                  found.getKey().get_SourcePositionStart());
              break;
            case CALL:
              Log.error("0xMA106 Outgoing port " + port.get().getName()
                  + " must not be used in method call statements.",
                  found.getKey().get_SourcePositionStart());
              break;
            case POSTFIX_EXPR:
              Log.error("0xMA107 Outgoing port " + port.get().getName()
                  + " must not be used in method postfix expressions.",
                  found.getKey().get_SourcePositionStart());
              break;
            case PREFIX_EXPR:
              Log.error("0xMA108 Outgoing port " + port.get().getName()
                  + " must not be used in method prefix expressions.",
                  found.getKey().get_SourcePositionStart());
              break;
            default:
              break;
          }
        }
      }
      else {
        Optional<JFieldSymbol> compParameter = ((Optional<ComponentSymbol>) componentScope
            .getSpanningSymbol()).get().getConfigParameters().stream()
              .filter(p -> p.getName().equals(found.getKey().getName())).findAny();
        if (compParameter.isPresent()) {
          switch (found.getValue()) {
            case ASSIGNMENT_LEFT:
              Log.error("0xMA111 component parameter " + compParameter.get().getName()
                  + " must not be used on the left side of an assignment",
                  found.getKey().get_SourcePositionStart());
              break;
            case POSTFIX_EXPR:
              Log.error("0xMA112 component parameter " + compParameter.get().getName()
                  + " must not be used in method postfix expressions.",
                  found.getKey().get_SourcePositionStart());
              break;
            case PREFIX_EXPR:
              Log.error("0xMA113 component parameter " + compParameter.get().getName()
                  + " must not be used in method prefix expressions.",
                  found.getKey().get_SourcePositionStart());
              break;
            default:
              break;
          }
          
          if (found.getValue().equals(ExpressionKind.ASSIGNMENT_LEFT)) {
            
          }
        }
      }
    }
    
  }
  
}
