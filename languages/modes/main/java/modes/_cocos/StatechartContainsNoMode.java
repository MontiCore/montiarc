/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._cocos.ArcAutomatonASTArcStatechartCoCo;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._symboltable.IModesScope;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This context-condition checks that a behavior statechart has no mode description.
 */
public class StatechartContainsNoMode implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(@NotNull ASTArcStatechart node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getSpannedScope());

    if (!((IModesScope) node.getSpannedScope()).getLocalArcModeSymbols().isEmpty()) {
      Log.error(ModesError.STATECHART_CONTAINS_MODE.format(), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
