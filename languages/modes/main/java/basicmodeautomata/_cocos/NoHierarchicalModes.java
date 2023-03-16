/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import basicmodeautomata._ast.ASTModeAutomaton;
import basicmodeautomata._ast.ASTModeDeclaration;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.BasicModeAutomataError;

public class NoHierarchicalModes implements BasicModeAutomataASTModeDeclarationCoCo {
  @Override
  public void check(ASTModeDeclaration statement) {
    Preconditions.checkNotNull(statement)
        .getBody()
        .streamArcElements()
        .filter(x -> x instanceof ASTModeDeclaration || x instanceof ASTModeAutomaton)
        .forEach(x -> Log.error(BasicModeAutomataError.HIERARCHICAL_MODE_ELEMENTS.format(), x.get_SourcePositionStart()));
  }
}
