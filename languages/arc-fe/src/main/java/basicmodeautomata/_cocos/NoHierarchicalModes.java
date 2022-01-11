/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import arcbasis.util.ArcError;
import basicmodeautomata._ast.ASTModeAutomaton;
import basicmodeautomata._ast.ASTModeDeclaration;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;

public class NoHierarchicalModes implements BasicModeAutomataASTModeDeclarationCoCo {
  @Override
  public void check(ASTModeDeclaration statement) {
    Preconditions.checkNotNull(statement)
        .getBody()
        .streamArcElements()
        .filter(x -> x instanceof ASTModeDeclaration || x instanceof ASTModeAutomaton)
        .forEach(x -> Log.error(ArcError.HIERARCHICAL_MODE_ELEMENTS.format(), x.get_SourcePositionStart()));
  }
}
