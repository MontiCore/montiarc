package montiarc.cocos;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTExpressionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all expressions used are allowed
 * expressions.
 * 
 * @implements [Wor16] AC5: The automaton’s valuations and assignments use only allowed Java/P modeling elements.
 * 
 * @author Gerrit Leonhardt
 */
public class UseOfForbiddenExpression implements JavaDSLASTExpressionCoCo {

  @Override
  public void check(ASTExpression node) {
    if (node.instanceofTypeIsPresent()) {
      Log.error("0xMA023 Expression contains forbidden expression: instanceOf expression", node.get_SourcePositionStart());
    }
  }
}
