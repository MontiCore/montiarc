/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;


/**
 * ensures that there is exactly one initial state
 */
public class OneInitialStateAtLeast implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(@NotNull ASTArcStatechart automaton) {
    Preconditions.checkNotNull(automaton);
    if(automaton.streamInitialStates().count() == 0L){
      Log.error(ArcError.NO_INITIAL_STATE.format(automaton.getEnclosingScope().getName()),
        automaton.get_SourcePositionStart(), automaton.get_SourcePositionEnd()
      );
    }
  }
}