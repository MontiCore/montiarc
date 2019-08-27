/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeDeclaration;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeDeclarationCoCo;

/**
 * Ensures that the names of the modes start with an uppercase character.
 *
 * @author (last commit) Fuerste
 */
public class ModeNameUppercase implements DynamicMontiArcASTModeDeclarationCoCo {

  @Override
  public void check(ASTModeDeclaration node) {
    for (String s : node.getModeList()) {
      if (!Character.isUpperCase(s.charAt(0))) {
        Log.error(
            String.format(
                "0xMA200 Name of mode '%s' needs to start with an " +
                    "uppercase character!", s),
            node.get_SourcePositionStart());
      }
    }

  }
}
