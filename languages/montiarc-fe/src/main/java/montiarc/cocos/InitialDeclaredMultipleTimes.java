package montiarc.cocos;

import java.util.HashSet;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if a state has been defined as an initial
 * state multiple times. This looks like: state A; initial A; initial A;
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class InitialDeclaredMultipleTimes implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
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
