package montiarc.cocos;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTConstantsMontiArc;
import montiarc._ast.ASTIOAssignment;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.PortSymbol;

import java.util.Optional;

public class AutomatonNoAssignmentToIncomingPort implements MontiArcASTIOAssignmentCoCo {

  @Override
  public void check(ASTIOAssignment node) {

    if(node.getOperator() == ASTConstantsMontiArc.SINGLE){
      // It's an assignment
      final Optional<String> identifier = node.getName();
      final Optional<? extends Scope> enclosingScope = node.getEnclosingScope();
      if(enclosingScope.isPresent() && identifier.isPresent()){
        final Optional<Symbol> resolvedSymbol = enclosingScope.get().resolve(identifier.get(), PortSymbol.KIND);
        if(resolvedSymbol.isPresent() && resolvedSymbol.get() instanceof PortSymbol){
          final PortSymbol symbol = (PortSymbol) resolvedSymbol.get();
          if(symbol.isIncoming()){
            Log.error("0xMA093 Writing to incoming ports is not allowed.",
                node.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
