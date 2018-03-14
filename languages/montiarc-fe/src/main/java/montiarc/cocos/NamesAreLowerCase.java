package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * @implements [RRW14a] C2: The names of variables and ports start with lowercase letters. (p. 31,
 * Lst. 6.5) Context condition for checking, if all fields of an IO-Automaton start with a lower
 * case letter.
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class NamesAreLowerCase implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    for (ASTVariableDeclaration varDecl : node.getVariables()) {
      for (String name : varDecl.getNameList())
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.warn(
              "0xMA018 The name of variable " + name + " should start with a lowercase letter.",
              varDecl.get_SourcePositionStart());
        }
    }
    
    for (ASTPort port : node.getPorts()) {
      for (String name : port.getNameList()) {
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.error("0xMA077: The name of the port should start with a lowercase letter.",
              node.get_SourcePositionStart());
        }
      }
    }
    
    // TODO: Auch für Parameter nachziehen -> Neue CoCo NamesCorrectlyCapitalized
    
  }
}
