package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._ast.ASTVariableDeclaration;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInputDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTOutputDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTVariableDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all fields of an IO-Automaton start with a
 * lower case letter.
 * 
 * @author Gerrit Leonhardt
 */
public class DeclarationNamesLowerCase implements IOAutomatonASTInputDeclarationCoCo, IOAutomatonASTOutputDeclarationCoCo, IOAutomatonASTVariableDeclarationCoCo {

  @Override
  public void check(ASTVariableDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (!Character.isLowerCase(var.getName().charAt(0))) {
        Log.warn("0xAA160 The name of variable " + var.getName() + " should start with a lowercase letter.", var.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void check(ASTOutputDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (!Character.isLowerCase(var.getName().charAt(0))) {
        Log.warn("0xAA161 The name of output '" + var.getName() + "' should start with a lowercase letter.", var.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void check(ASTInputDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (!Character.isLowerCase(var.getName().charAt(0))) {
        Log.warn("0xAA162 The name of input '" + var.getName() + "' should start with a lowercase letter.", var.get_SourcePositionStart());
      }
    }
  }
  
}
