/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTInitialOutputDeclaration;
import com.google.common.base.Preconditions;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArcAutomatonHelper {

  final ASTArcStatechart automaton;

  public ArcAutomatonHelper(@NotNull ASTArcStatechart automaton) {
    this.automaton = Preconditions.checkNotNull(automaton);
  }

  public List<ASTSCState> getAutomatonStates() {
    Preconditions.checkNotNull(automaton);
    return automaton.streamStates().collect(Collectors.toList());
  }

  public Optional<ASTInitialOutputDeclaration> getInitialOutputDecl() {
    Preconditions.checkNotNull(automaton);
    return automaton.streamInitialOutput().findFirst();
  }

  public List<ASTSCTransition> getTransitionsFrom(@NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(srcState);
    Preconditions.checkArgument(automaton.streamStates().anyMatch(state -> state.equals(srcState)));

    return automaton.streamTransitions()
      .filter(tr -> tr.getSourceName().equals(srcState.getName()))
      .collect(Collectors.toList());
  }

  public ASTTransitionAction scABodyToTransitionAction(@NotNull ASTSCABody action) {
    Preconditions.checkNotNull(action);
    return (ASTTransitionAction) action;
  }

  public boolean hasTransitionWithoutGuardFrom(@NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(srcState);
    Preconditions.checkArgument(automaton.streamStates().anyMatch(state -> state.equals(srcState)));
    Preconditions.checkArgument(
      automaton.streamTransitions()
        .map(ASTSCTransition::getSCTBody)
        .allMatch(trB -> trB instanceof ASTTransitionBody)
    );

    return automaton.streamTransitions()
      .filter(tr -> tr.getSourceName().equals(srcState.getName()))
      .map(tr -> (ASTTransitionBody) tr.getSCTBody())
      .anyMatch(tr -> !tr.isPresentPre());
  }
}
