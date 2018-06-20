package montiarc.cocos;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTConstantsMontiArc;
import montiarc._ast.ASTIOAssignment;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.PortSymbol;

import java.util.Optional;

/**
 * CoCo which checks that no assignment assigns some value to an incoming port.
 * 
 * @implements [Wor16] AR2: Inputs, outputs, and variables are used correctly.
 * @implements [RRW14a] T6: The direction of ports has to be respected.
 * (p.103, Lst 5.20)
 * @author Michael Mutert
 */
public class AutomatonNoAssignmentToIncomingPort implements MontiArcASTIOAssignmentCoCo {
  
  @Override
  public void check(ASTIOAssignment node) {
    
    // It's an assignment
    final Optional<String> identifier = node.getName();
    final Optional<? extends Scope> enclosingScope = node.getEnclosingScope();
    if (enclosingScope.isPresent() && identifier.isPresent()) {
      final Optional<Symbol> resolvedSymbol = enclosingScope.get().resolve(identifier.get(),
          PortSymbol.KIND);
      if (resolvedSymbol.isPresent() && resolvedSymbol.get() instanceof PortSymbol) {
        final PortSymbol symbol = (PortSymbol) resolvedSymbol.get();
        if (symbol.isIncoming()) {
          Log.error("0xMA034 Writing to incoming ports is not allowed.",
              node.get_SourcePositionStart());
        }
      }
    }
  }
}
