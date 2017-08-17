package montiarc.cocos;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTPrimaryExpressionCoCo;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueList;
import montiarc._cocos.MontiArcASTValueListCoCo;
import montiarc._symboltable.PortSymbol;

public class AutomatonOutputInExpression implements MontiArcASTValueListCoCo {
  
  @Override
  public void check(ASTValueList node) {
    for (ASTValuation val : node.getAllValuations()) {
      Optional<ASTPrimaryExpression> expr = val.getExpression().getPrimaryExpression();
      if (expr.isPresent()) {
        if (expr.get().nameIsPresent() && expr.get().getEnclosingScope().isPresent()) {
          Scope scope = expr.get().getEnclosingScope().get();
          Optional<PortSymbol> found = scope.resolve(expr.get().getName().get(), PortSymbol.KIND);
          if (found.isPresent() && found.get().isOutgoing()) {
            Log.error("0xAA1A0 Port " + found.get().getName()
                + " is an outgoing port and not allowed in expressions.", node.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
}
