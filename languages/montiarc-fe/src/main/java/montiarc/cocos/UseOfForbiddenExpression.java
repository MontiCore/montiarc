package montiarc.cocos;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTExpressionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all expressions used are allowed
 * expressions.
 * 
 * @implements [Wor16] AC5: The automaton’s valuations and assignments use only
 * allowed Java/P modeling elements.
 * @author Gerrit Leonhardt
 */
public class UseOfForbiddenExpression implements JavaDSLASTExpressionCoCo {
  
  @Override
  public void check(ASTExpression node) {
    boolean isError = false;
    String errorMessage = "";
    if (node.instanceofTypeIsPresent()) {
      isError = true;
      errorMessage = "instanceOf expression";
    }
    else if (node.arrayExpressionIsPresent()) {
      isError = true;
      errorMessage = "array access expression";
    }
    else if (node.typeCastTypeIsPresent()) {
      isError = true;
      errorMessage = "type cast expression";
    }
    else if (node.prefixOpIsPresent()) {
      String prefixOp = node.getPrefixOp().get();
      if (prefixOp.equals("--") || prefixOp.equals("++")) {
        isError = true;
        errorMessage = "prefix expression";
      }
    }
    
    if (isError) {
      Log.error("0xMA023 Expression contains forbidden expression: " + errorMessage,
          node.get_SourcePositionStart());
    }
  }
}
