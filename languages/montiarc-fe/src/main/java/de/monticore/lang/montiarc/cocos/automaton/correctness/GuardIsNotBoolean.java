package de.monticore.lang.montiarc.cocos.automaton.correctness;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.lang.montiarc.helper.TypeCompatibilityChecker;
import de.monticore.lang.montiarc.montiarc._ast.ASTGuardExpression;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if every guard of a transition can be
 * evaluated to a True or False value.
 *
 * @author Andreas Wortmann
 */
public class GuardIsNotBoolean implements MontiArcASTGuardExpressionCoCo {

  @Override
  public void check(ASTGuardExpression node) {
    Optional<? extends JavaTypeSymbolReference> typeRef = TypeCompatibilityChecker.getExpressionType((ASTExpression) node);
    if (typeRef.isPresent()) {
      if (!typeRef.get().getName().equalsIgnoreCase("boolean")) {
        Log.error("0xAA400 Guard does not evaluate to a boolean, but instead to " + typeRef.get().getName() + ".", node.get_SourcePositionStart());
      }
    }
    else {
      Log.error("0xAA401 Could not resolve type of guard.", node.get_SourcePositionStart());
    }
  }
  
 
}
