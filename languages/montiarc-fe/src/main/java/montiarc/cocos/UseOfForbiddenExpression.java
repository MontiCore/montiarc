package montiarc.cocos;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTExpressionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all expressions used are allowed
 * expressions.
 * 
 * @implements [Wor16] AC5: The automaton’s valuations and assignments use only
 * allowed Java/P modeling elements (Lst. 5.15, p. 101).
 * @author Gerrit Leonhardt
 */
public class UseOfForbiddenExpression implements JavaDSLASTExpressionCoCo {
  
  @Override
  public void check(ASTExpression node) {
    boolean isError = false;
    String errorMessage = "";
    if (node.instanceofTypeIsPresent()) {
      isError = true;
      errorMessage = "instanceOf";
    }
    else if (node.binaryAndOpIsPresent()) {
      isError = true;
      errorMessage = "binary AND";
    }
    else if (node.binaryOrOpIsPresent()) {
      isError = true;
      errorMessage = "binary OR";
    }
    else if(node.binaryXorOpIsPresent()) {
      isError = true;
      errorMessage = "binary XOR";
    }
    else if(node.booleanAndOpIsPresent() || node.booleanNotIsPresent() || node.booleanOrOpIsPresent()) {
      isError = true;
      errorMessage = "boolean AND/NOT/OR";
    }
    
    if (isError) {
      Log.error("0xMA023 Expression contains forbidden expression: " + errorMessage,
          node.get_SourcePositionStart());
    }
  }
}
