package montiarc.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.mcexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.mcexpressions._cocos.MCExpressionsASTGenericInvocationSuffixCoCo;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

/**
 * Checks whether output ports are used within a expression in a automaton
 * transition.
 * 
 * @implements [Wor16] AR2: Inputs, outputs, and variables are used correctly.
 * (p. 103, Lst. 520)
 * @implements [RRW14a] T6: The direction of ports has to be respected.
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AutomatonOutputInExpression implements MCExpressionsASTGenericInvocationSuffixCoCo {
  
  @Override
  public void check(ASTGenericInvocationSuffix node) {
    // TODO Check that nothing is writing on incoming ports
    
    // Transition Scope
    if (node.isPresentName() && node.getEnclosingScope().isPresent()) {
      
      // Automaton Scope
      if (node.getEnclosingScope().get().getEnclosingScope().isPresent()) {
        
        // Component Scope
        if (node.getEnclosingScope().get().getEnclosingScope().get().getEnclosingScope()
            .isPresent()) {
          Scope scope = node.getEnclosingScope().get().getEnclosingScope().get().getEnclosingScope()
              .get();
          Optional<? extends ScopeSpanningSymbol> scopeSymbol = scope.getSpanningSymbol();
          if (scopeSymbol.isPresent() && scopeSymbol.get().isKindOf(ComponentSymbol.KIND)) {
            Optional<ASTNode> nodeAST = scopeSymbol.get().getAstNode();
            if (nodeAST.isPresent()) {
              // Component that conatins the expression somewhere
              ASTComponent compAST = (ASTComponent) nodeAST.get();
              List<ASTElement> elements = compAST.getBody().getElementList();
              for (ASTElement elem : elements) {
                if (elem instanceof ASTAutomatonBehavior) {
                  Optional<PortSymbol> found = scope.resolve(node.getName(), PortSymbol.KIND);
                  if (found.isPresent() && found.get().isOutgoing()) {
                    Log.error(
                        "0xMA022 Field " + found.get().getName()
                            + " is an Ouput and not allowed in Expressions.",
                        node.get_SourcePositionStart());
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
}
