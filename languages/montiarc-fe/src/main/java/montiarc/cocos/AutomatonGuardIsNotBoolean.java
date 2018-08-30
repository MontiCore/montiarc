package montiarc.cocos;

import java.util.Optional;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTGuardExpression;
import montiarc._ast.ASTJavaGuardExpression;
import montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Context condition for checking, if every guard of a transition can be
 * evaluated to a True or False value.
 *
 * @implements [Wor16] AT1: Guard expressions evaluate to a Boolean truth
 *  value. (p.105, Lst. 5.23)
 * @author Andreas Wortmann
 */
public class AutomatonGuardIsNotBoolean implements MontiArcASTGuardExpressionCoCo {
  
  @Override
  public void check(ASTGuardExpression node) {
    if (node instanceof ASTJavaGuardExpression) {
      doCheck((ASTJavaGuardExpression) node);
    }
    else {
      Log.error("0xMA037 Could not resolve type of guard.", node.get_SourcePositionStart());
    }
  }
  
  public void doCheck(ASTJavaGuardExpression node) {
    try {
      Optional<? extends JavaTypeSymbolReference> typeRef = TypeCompatibilityChecker
          .getExpressionType(node.getExpression());
      
      if (typeRef.isPresent()) {
        if (!typeRef.get().getName().equalsIgnoreCase("boolean")) {
          Log.error("0xMA036 Guard does not evaluate to a boolean, but instead to "
              + typeRef.get().getName() + ".", node.get_SourcePositionStart());
        }
      }
      else {
        Log.error(
            "0xMA037 Could not resolve type of guard. Do all types of used variables/ports fit?",
            node.get_SourcePositionStart());
      }
    }
    catch (Exception e) {
      Log.error("0xMA037 Could not resolve type of guard.", node.get_SourcePositionStart());
    }
  }
  
}
