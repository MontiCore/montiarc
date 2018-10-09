package montiarc.cocos;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._cocos.MontiArcASTParameterCoCo;
import montiarc._cocos.MontiArcASTPortCoCo;
import montiarc._cocos.MontiArcASTVariableDeclarationCoCo;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that in the model no prohibited identifiers are used.
 * The prohibited identifiers are those, which are used in the generation process
 * to circumvent issues with name clashes.
 */
public class UseOfProhibitedIdentifiers
    implements MontiArcASTParameterCoCo,
                   MontiArcASTVariableDeclarationCoCo,
                   MontiArcASTPortCoCo {

  public static List<String> prohibitedIdentifiers = Lists.newArrayList(
      "r__input",
      "r__result",
      "r__behaviorImpl",
      "r__currentState");

  @Override
  public void check(ASTParameter node) {
    if(prohibitedIdentifiers.contains(node.getName())){
      Log.error(
          String.format("0xMA046 The use of identifier '%s' is prohibited. " +
                            "Please use a different identifier.", node.getName()),
          node.get_SourcePositionStart()
      );
    }
  }

  @Override
  public void check(ASTPort node) {
    for (String name : node.getNameList()) {
      if(prohibitedIdentifiers.contains(name)){
        Log.error(
            String.format("0xMA046 The use of identifier '%s' is prohibited. " +
                              "Please use a different identifier.", name),
            node.get_SourcePositionStart()
        );
      }
    }
  }

  @Override
  public void check(ASTVariableDeclaration node) {
    for (String name : node.getNameList()) {
      if(prohibitedIdentifiers.contains(name)){
        Log.error(
            String.format("0xMA046 The use of identifier '%s' is prohibited. " +
                              "Please use a different identifier.", name),
            node.get_SourcePositionStart()
        );
      }
    }
  }
}
