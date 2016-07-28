package de.monticore.automaton.ioautomaton.cocos.uniqueness;

import java.util.HashSet;
import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an input field has been defined multiple
 * times with different types.
 * 
 * @author Gerrit
 */
public class InputsDefinedMultipleTimes implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    HashSet<String> names = new HashSet<>();   
    for (ASTInputDeclaration decl : node.getInputDeclarations()) {
      for (ASTVariable var : decl.getVariables()) {
        if (names.contains(var.getName())) {
          Log.error("0xAA310 Input " + var.getName() + " is defined more than once.", var.get_SourcePositionStart());
        } else {
          names.add(var.getName());
        }
      }
    }
  }
  
}
