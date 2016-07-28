package de.monticore.automaton.ioautomaton.cocos.uniqueness;

import java.util.HashSet;
import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a state has been defined as an initial
 * state multiple times. This looks like: state A; initial A; initial A;
 * 
 * @author Gerrit
 */
public class InitialDeclaredMultipleTimes implements IOAutomatonASTAutomatonContentCoCo {
  
  @Override
  public void check(ASTAutomatonContent node) {
    HashSet<String> names = new HashSet<>();   
    for (ASTInitialStateDeclaration decl : node.getInitialStateDeclarations()) {
      for (String name : decl.getNames()) {
        if (names.contains(name)) {
          Log.error("0xAA300 The state " + name + " is defined multiple times as initial state.", node.get_SourcePositionStart());
        } else {
          names.add(name);
        }
      }
    }
  }
  
}
