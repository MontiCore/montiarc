package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInputDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTOutputDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all inputs/outputs do not use initial
 * value assignments.
 * 
 * @author Gerrit Leonhardt
 */
public class InitialInputOutputAssignments implements IOAutomatonASTInputDeclarationCoCo, IOAutomatonASTOutputDeclarationCoCo {

  @Override
  public void check(ASTOutputDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (var.valuationIsPresent()) {
        Log.warn("0xAA1C0 The output '" + var.getName() + "' must not use inital assignments.", var.get_SourcePositionStart());
      }
    }
  }

  
  @Override
  public void check(ASTInputDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (!Character.isLowerCase(var.getName().charAt(0))) {
        Log.warn("0xAA1C1 The input '" + var.getName() + "' must not use inital assignments.", var.get_SourcePositionStart());
      }
    }
  }
  
}
