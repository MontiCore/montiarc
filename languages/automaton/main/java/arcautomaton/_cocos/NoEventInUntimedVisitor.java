/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeVisitor2;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoEventInUntimedVisitor implements SCTransitions4CodeVisitor2 {

  @Override
  public void visit(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentSCEvent()) {
      Log.error(ArcAutomataError.NO_EVENT_IN_UNTIMED_AUTOMATON.format(),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}