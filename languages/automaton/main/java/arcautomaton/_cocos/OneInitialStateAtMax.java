/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import montiarc.util.ArcAutomataError;
import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTSCState;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;


/**
 * ensures that there is exactly one initial state
 */
public class OneInitialStateAtMax implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(@NotNull ASTArcStatechart automaton) {
    Preconditions.checkNotNull(automaton);
    // Look at top-level states only
    List<ASTSCState> initialStates = automaton.streamSCStatechartElements()
        .filter(ASTSCState.class::isInstance)
        .map(ASTSCState.class::cast)
        .filter(s -> s.getSCModifier().isInitial())
        .collect(Collectors.toList());
    int count = initialStates.size();
    if (count > 1) {
      Log.error(ArcAutomataError.MANY_INITIAL_STATES.format(
          automaton.getEnclosingScope().getName(),
          count,
          String.join(", ", initialStates.subList(0, count - 1).stream().map(ASTSCState::getName).toArray(String[]::new)),
          initialStates.get(count - 1).getName()
                                                           ),
        initialStates.get(1).get_SourcePositionStart(), initialStates.get(1).get_SourcePositionEnd()
      );
    }
  }
}