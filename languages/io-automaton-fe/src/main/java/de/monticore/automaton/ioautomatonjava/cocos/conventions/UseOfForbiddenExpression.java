package de.monticore.automaton.ioautomatonjava.cocos.conventions;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTExpressionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all expressions used are allowed
 * expressions.
 * 
 * @author Gerrit
 */
public class UseOfForbiddenExpression implements JavaDSLASTExpressionCoCo {

  @Override
  public void check(ASTExpression node) {
    // if (node.getCreator().isPresent()) {
    // Log.error("0xAA10C Expression contains forbidden expression: class instantiation expression", exp.get_SourcePositionStart());
    // }
    if (node.instanceofTypeIsPresent()) {
      Log.error("0xAA1B0 Expression contains forbidden expression: instanceOf expression", node.get_SourcePositionStart());
    }
  }
}
