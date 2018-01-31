package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._cocos.MontiArcASTVariableDeclarationCoCo;

/**
 * Context condition for checking, if all fields of an IO-Automaton start with a
 * lower case letter.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class VariableNameIsLowerCase implements MontiArcASTVariableDeclarationCoCo {

  @Override
  public void check(ASTVariableDeclaration varDecl) {
    for (String name : varDecl.getNames())
      if (!Character.isLowerCase(name.charAt(0))) {
        Log.warn("0xMA018 The name of variable " + name + " should start with a lowercase letter.", varDecl.get_SourcePositionStart());
      }
  }
}

