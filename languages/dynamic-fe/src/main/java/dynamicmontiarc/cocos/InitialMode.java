/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTInitialModeDeclaration;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeAutomatonCoCo;
import dynamicmontiarc.helper.DynamicMontiArcHelper;

import java.util.List;

/**
 * If a component uses modes, the mode automaton must have exactly one initial
 * mode. AND A mode referenced by an initial mode declaration must correspond to
 * a mode declared in the current component type definition M1+R6
 *
 * @author (last commit) Fuerste
 */
public class InitialMode implements DynamicMontiArcASTModeAutomatonCoCo
{
  @Override
  public void check(ASTModeAutomaton node) {

    List<ASTInitialModeDeclaration> initialModes =
        node.getInitialModeDeclarationList();

    for (ASTInitialModeDeclaration mode : initialModes) {
      if (!node.getModeNames().contains(mode.getName())) {
        Log.error(String.format("0xMA201 The initial mode '%s' does not exist!",
                mode.getName()),
            mode.get_SourcePositionStart());
      }
    }

    if (initialModes.size() > 1) {
      for (ASTInitialModeDeclaration mode : initialModes) {
        Log.error(
            String.format(
                "0xMA202 The initial mode '%s' is ambiguous!",
                mode.getName()),
            mode.get_SourcePositionStart());
      }
    }

    if (initialModes.size() == 0) {
      Log.error(
          "0xMA203 There is no initial mode!",
          node.get_SourcePositionStart());
    }

  }
}
