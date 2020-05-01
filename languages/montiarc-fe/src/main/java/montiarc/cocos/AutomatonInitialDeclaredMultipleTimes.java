/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.HashSet;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if a state has been defined as an initial state multiple times.
 * This looks like: state A; initial A; initial A;
 * 
 * @implements [Wor16] AU2: Each state is declared initial at most once. (p. 97, Lst. 5.9)
 */
public class AutomatonInitialDeclaredMultipleTimes implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    HashSet<String> names = new HashSet<>();
    for (ASTInitialStateDeclaration decl : node.getInitialStateDeclarationList()) {
      for (String name : decl.getNameList()) {
        if (names.contains(name)) {
          Log.error("0xMA029 The state " + name + " is defined multiple times as initial state.",
              node.get_SourcePositionStart());
        }
        else {
          names.add(name);
        }
      }
    }
  }
  
}
