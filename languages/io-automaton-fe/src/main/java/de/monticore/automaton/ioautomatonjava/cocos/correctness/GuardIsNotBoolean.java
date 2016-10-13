package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import java.util.Optional;

import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTGuardExpressionExt;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTGuardExpressionExtCoCo;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if every guard of a transition can be
 * evaluated to a True or False value.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since $Version$
 */
public class GuardIsNotBoolean implements IOAutomatonASTGuardExpressionExtCoCo {

  @Override
  public void check(ASTGuardExpressionExt node) {
    Optional<? extends JavaTypeSymbolReference> typeRef = TypeCompatibilityChecker.getExpressionType(node.getExpression());
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
