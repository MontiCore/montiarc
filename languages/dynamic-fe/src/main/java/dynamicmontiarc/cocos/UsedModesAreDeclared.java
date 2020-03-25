/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._ast.ASTModeTransition;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeAutomatonCoCo;
import dynamicmontiarc.helper.DynamicMontiArcHelper;

import java.util.Set;

/**
 * Checks that modes used in transitions are defined in the component
 *
 */
public class UsedModesAreDeclared implements DynamicMontiArcASTModeAutomatonCoCo {

  @Override
  public void check(ASTModeAutomaton node) {

    final Set<String> modeNames = node.getModeNames();

    for (ASTModeTransition transition : node.getModeTransitionList()) {
      final String startMode = transition.getSource();
      final String targetMode = transition.getTarget();

      if(!modeNames.contains(startMode)){
        Log.error(
            String.format("0xMA211 The start mode %s used in transition " +
                              "%s -> %s does not exist.",
                startMode, startMode, targetMode),
            transition.get_SourcePositionStart());
      }
      if(!modeNames.contains(targetMode)){
        Log.error(
            String.format("0xMA212 The target mode %s used in transition " +
                              "%s -> %s does not exist.",
                startMode, startMode, targetMode),
            transition.get_SourcePositionStart());
      }
    }
  }
}
