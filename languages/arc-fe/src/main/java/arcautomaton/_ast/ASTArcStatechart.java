/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._ast;

import de.monticore.scbasis._ast.ASTSCSAnte;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTAnteAction;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Stream;

public class ASTArcStatechart extends ASTArcStatechartTOP {

  /**
   * @return all transitions that occur in this statechart, probably in the order in which they are given
   */
  public Stream<ASTSCTransition> streamTransitions() {
    return streamSCStatechartElements()
      .filter(ASTSCTransition.class::isInstance)
      .map(ASTSCTransition.class::cast);
  }

  /**
   * @return all initial states together with their init-action. Note that if one initial state is declared multiple
   * times (probably by mistake) then all its declarations are contained in the stream, together with their
   * corresponding init action.
   */
  public Stream<Pair<ASTSCState, ASTSCSAnte>> streamInitialOutput() {
    return streamInitialStates().map(state -> Pair.of(state, state.getSCSAnte()));
  }

  /**
   * @return the behavior of this component, if it has a statechart
   */
  public Stream<ASTMCBlockStatement> streamInitialStatements(){
    return streamInitialOutput()
        .map(Pair::getRight)
        .filter(ASTAnteAction.class::isInstance)
        .map(ASTAnteAction.class::cast)
        .map(ASTAnteAction::getMCBlockStatementList)
        .flatMap(Collection::stream);
  }

  /**
   * @return all states that are contained in this statechart
   */
  public Stream<ASTSCState> streamStates() {
    return streamSCStatechartElements()
      .filter(ASTSCState.class::isInstance)
      .map(ASTSCState.class::cast);
  }

  /**
   * @return a stream containing all initial states of this statechart. Note that if one initial state is declared
   * multiple times (probably by mistake) then all its declarations are contained in the stream.
   */
  public Stream<ASTSCState> streamInitialStates() {
    return streamStates().filter(s -> s.getSCModifier().isInitial());
  }
}