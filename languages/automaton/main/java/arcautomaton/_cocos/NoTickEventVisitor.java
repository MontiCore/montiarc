/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._visitor.ArcAutomatonVisitor2;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoTickEventVisitor implements ArcAutomatonVisitor2 {

  @Override
  public void visit(@NotNull ASTMsgEvent node) {
    Preconditions.checkNotNull(node);
    if (node.getEventSymbol() != null && ArcAutomatonMill.TICK.equals(node.getEventSymbol().getName())) {
      Log.error(ArcAutomataError.TICK_EVENT_IN_UNTIMED_AUTOMATON.format(),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
