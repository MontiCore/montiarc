package de.monticore.automaton.ioautomatonjava.cocos.integrity;

import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTIOAssignmentCoCo;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTPrimaryExpressionCoCo;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a variable is used inside an automaton
 * which has not been defined in an {@link ASTInputDeclaration}, {@link ASTVariableDeclaration} or {@link ASTOutputDeclaration}.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   $Version$
 *
 */
public class UseOfUndeclaredField implements JavaDSLASTPrimaryExpressionCoCo, IOAutomatonASTIOAssignmentCoCo {
  
  @Override
  public void check(ASTPrimaryExpression node) {
    if (node.nameIsPresent()) {
      Scope scope = node.getEnclosingScope().get();
      boolean found = scope.resolve(node.getName().get(), VariableSymbol.KIND).isPresent();
      if (!found) {
        Log.error("0xAA230 " + node.getName().get() + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void check(ASTIOAssignment node) {
    if (node.nameIsPresent()) {
      Scope scope = node.getEnclosingScope().get();
      boolean found = scope.resolve(node.getName().get(), VariableSymbol.KIND).isPresent();
      if (!found) {
        Log.error("0xAA231 " + node.getName().get() + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
      }
    }
  }
  
}
