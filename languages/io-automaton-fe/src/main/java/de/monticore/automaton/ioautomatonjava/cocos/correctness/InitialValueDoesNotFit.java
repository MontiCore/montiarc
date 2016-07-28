package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._ast.ASTVariableDeclaration;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonCoCo;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if the type of a declared local
 * variable fits the type of the value it was assigned.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since $Version$
 */
public class InitialValueDoesNotFit implements IOAutomatonASTAutomatonCoCo {

  @Override
  public void check(ASTAutomaton node) {    
    ASTAutomatonContext context = node.getAutomatonContext();
    
    // Inputs
    if (context.getInputDeclarations() != null) {
      for (ASTInputDeclaration d : context.getInputDeclarations()) {
        ASTType t = d.getType();
        for (ASTVariable v : d.getVariables()) {
          if (v.getValuation().isPresent()) {
            // TODO
//            if (!ExpressionHelper.compareExpressionTypeWithType(
//                (ASTExpression) v.getValuation(), t, content,
//                this.getResolver())) {
//              Log.error("0xAA403 The value of input " + v.getName()
//                  + " does not fit its type.",
//                  v.get_SourcePositionStart());
//            }
          }
        }
      }
    }
    
    if (context.getVariableDeclarations() != null) {
      for (ASTVariableDeclaration d : context.getVariableDeclarations()) {
        ASTType t = d.getType();
        for (ASTVariable v : d.getVariables()) {
          if (v.getValuation().isPresent()) {
            // TODO
//            if (!ExpressionHelper.compareExpressionTypeWithType(
//                (ASTExpression) v.getValuation(), t, content,
//                this.getResolver())) {
//              Log.error("0xAA403 The value of variable "
//                      + v.getName()
//                      + " does not fit its type.",
//                  v.get_SourcePositionStart());
//            }
          }
        }
      }
    }
    
    if (context.getOutputDeclarations() != null) {
      for (ASTOutputDeclaration d : context.getOutputDeclarations()) {
        ASTType t = d.getType();
        for (ASTVariable v : d.getVariables()) {
          if (v.getValuation().isPresent()) {
            // TODO
//            if (!ExpressionHelper.compareExpressionTypeWithType(
//                (ASTExpression) v.getValuation(), t, content,
//                this.getResolver())) {
//              Log.error("0xAA403 The value of output "
//                      + v.getName()
//                      + " does not fit its type.",
//                  v.get_SourcePositionStart());
//            }
          }
        }
      }
    }
  }
  
}
