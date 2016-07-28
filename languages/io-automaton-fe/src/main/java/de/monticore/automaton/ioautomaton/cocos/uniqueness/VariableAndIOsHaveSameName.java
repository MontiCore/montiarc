package de.monticore.automaton.ioautomaton.cocos.uniqueness;

import java.util.ArrayList;
import java.util.List;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._ast.ASTVariableDeclaration;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a lokal field has the same name as an
 * output or input field. Since inputs and outputs can always be told apart from
 * their position (stimulus or reaction), it is not the case for lokal fields.
 * 
 * @author Gerrit
 */
public class VariableAndIOsHaveSameName implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    // Collect Variables
    List<ASTVariable> variables = new ArrayList<ASTVariable>();
    if (node.getVariableDeclarations() != null) {
      for (ASTVariableDeclaration decl : node.getVariableDeclarations()) {
        variables.addAll(decl.getVariables());
      }
    }
    
    // Check all inputs
    if (node.getInputDeclarations() != null) {
      for (ASTInputDeclaration decl : node.getInputDeclarations()) {
        for (ASTVariable input : decl.getVariables()) {
          for (ASTVariable var : variables) {
            if (var.getName().equals(input.getName())) {
              Log.error("0xAA350 The variable " + var.getName() + " has the same name as the input " + input.getName(), var.get_SourcePositionStart());
            }
          }
        }
      }
    }
    
    // Check all outputs
    if (node.getOutputDeclarations() != null) {
      for (ASTOutputDeclaration decl : node.getOutputDeclarations()) {
        for (ASTVariable output : decl.getVariables()) {
          for (ASTVariable var : variables) {
            if (var.getName().equals(output.getName())) {
              Log.error("0xAA351 The variable " + var.getName() + " has the same name as the output " + output.getName(), var.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
  
}
