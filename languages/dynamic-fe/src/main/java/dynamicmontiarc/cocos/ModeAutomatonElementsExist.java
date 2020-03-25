/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeAutomatonCoCo;

/**
 * If the component is dynamic, it needs to have exactly one mode controller
 * and at least one mode declaration.
 *
 */
public class ModeAutomatonElementsExist implements DynamicMontiArcASTModeAutomatonCoCo {

  @Override
  public void check(ASTModeAutomaton node) {

    final int modeCount = node.getModeDeclarationList().size();

    if (modeCount == 0) {
      Log.error(
          "0xMA210 There are no mode declarations in the " +
              "mode automaton!",
          node.get_SourcePositionStart());
    }
    if (modeCount == 1) {
      Log.warn(
          "0xMA207 There is only one mode and therefore the component " +
              "should be modelled with a static C&C configuration!",
          node.get_SourcePositionStart());
    }

  }
}
