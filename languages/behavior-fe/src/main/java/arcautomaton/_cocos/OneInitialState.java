/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbehaviorbasis.BehaviorError;
import de.monticore.ast.ASTNode;
import de.monticore.scbasis._ast.ASTSCState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OneInitialState implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(ASTArcStatechart automaton) {
    check(automaton, automaton.streamSCStatechartElements(), BehaviorError.ONE_INITIAL_STATE);
  }

  /**
   * ensures that there is exactly one initial state
   * @param source object that should contain a state
   * @param elements all elements amongst which initial states may be hiding
   * @param error error to log, if there is not exactly one state
   */
  public static void check(ASTNode source, Stream<?> elements, BehaviorError error) {
    List<ASTSCState> initialStates = elements
      .filter(element -> element instanceof ASTSCState)
      .map(state -> (ASTSCState) state)
      .filter(state -> state.getSCModifier().isInitial())
      .collect(Collectors.toList());
    if(initialStates.size() != 1){
      error.logAt(
          initialStates.size() >= 2?initialStates.get(1):source,
          source.getEnclosingScope().getName(),
          initialStates.stream().map(ASTSCState::getName).reduce((a, b) -> a + " and " + b).orElse("none"));
    }
  }
}