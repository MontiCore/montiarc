/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTAnteAction;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTAnteActionCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoStatechartAnteAction implements SCTransitions4CodeASTAnteActionCoCo {

  @Override
  public void check(@NotNull ASTAnteAction node) {
    Preconditions.checkNotNull(node);
    Log.error(ArcAutomataError.STATECHART_ANTE_ACTION_NOT_SUPPORTED.format(),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }
}
