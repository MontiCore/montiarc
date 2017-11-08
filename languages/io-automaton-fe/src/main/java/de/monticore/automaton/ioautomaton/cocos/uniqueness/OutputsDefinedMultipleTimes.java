package de.monticore.automaton.ioautomaton.cocos.uniqueness;

import java.util.HashSet;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an output field is defined multiple times,
 * but with different stereotypes.
 * 
 * @author Gerrit Leonhardt
 */
public class OutputsDefinedMultipleTimes implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    HashSet<String> names = new HashSet<>();   
    for (ASTOutputDeclaration decl : node.getOutputDeclarations()) {
      for (ASTVariable var : decl.getVariables()) {
        if (names.contains(var.getName())) {
          Log.error("0xAA320 Output " + var.getName() + " is defined more than once.", var.get_SourcePositionStart());
        } else {
          names.add(var.getName());
        }
      }
    }
  }
  
}
