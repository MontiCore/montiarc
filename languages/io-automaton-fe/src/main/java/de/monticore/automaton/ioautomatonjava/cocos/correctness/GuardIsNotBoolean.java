package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTGuard;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonCoCo;
import de.monticore.automaton.ioautomatonjava._ast.ASTGuardExpression;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.se_rwth.commons.SourcePosition;

/**
 * Context condition for checking, if every guard of a transition can be
 * evaluated to a True or False value.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since $Version$
 */
public class GuardIsNotBoolean implements IOAutomatonASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    if (node.getAutomatonContent().getTransitions() != null) {
      for (ASTTransition transition : node.getAutomatonContent().getTransitions()) {
        if (transition.getGuard().isPresent()) {
          ASTGuard guard = transition.getGuard().get();
          SourcePosition pos = guard.get_SourcePositionStart();
          ASTExpression expr = ((ASTGuardExpression)guard.getGuardExpression()).getExpression();
          
          // TODO
//          STEntry ctype = checker.getType(expr);
//          if (!(ctype instanceof JavaTypeEntry)) {
//            Log.error("0xAA400 The guard of transition '" + transition + "' can not be evaluated to a JavaTypeEntry.", pos);
//          }
//          else {
//            JavaTypeEntry jtype = (JavaTypeEntry) ctype;
//            if (!jtype.getName().equalsIgnoreCase("boolean")) {
//              Log.error("0xAA401 The guard of transition " + transition + " does not evaluate to a boolean, but instead to " + jtype.getName(), pos);
//            }
//          }
        }
      }
    }
  }
  
 
}
