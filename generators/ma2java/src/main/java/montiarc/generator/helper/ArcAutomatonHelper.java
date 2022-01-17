/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcautomaton._ast.ASTStateBody;
import com.google.common.base.Preconditions;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scbasis._ast.ASTSCSBody;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import org.codehaus.commons.nullanalysis.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
  
  public boolean existTransitionsFrom(@NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(srcState);
    return automaton.streamStates().anyMatch(state -> state.equals(srcState));
  }
  
  public ASTSCTransition getFirstTransitionWithoutGuardFrom(@NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(srcState);
    Preconditions.checkArgument(automaton.streamStates().anyMatch(state -> state.equals(srcState)));
    Preconditions.checkArgument(hasTransitionWithoutGuardFrom(srcState));
    
    List<ASTSCTransition> transitions = automaton.streamTransitions().filter(tr ->
        tr.getSourceName().equals(srcState.getName()) &&
        !((ASTTransitionBody) tr.getSCTBody()).isPresentPre())
      .collect(Collectors.toList());
    
    return  transitions.get(0);
  }
  
  public List<ASTSCTransition> getAllTransitionsWithGuardFrom(@NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(srcState);
    Preconditions.checkArgument(automaton.streamStates().anyMatch(state -> state.equals(srcState)));
    Preconditions.checkArgument(
      automaton.streamTransitions()
        .map(ASTSCTransition::getSCTBody)
        .allMatch(trB -> trB instanceof ASTTransitionBody)
    );
    
    return automaton.streamTransitions().filter(tr ->
      tr.getSourceName().equals(srcState.getName()) &&
        ((ASTTransitionBody) tr.getSCTBody()).isPresentPre())
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
  
  public boolean hasTransitionWithGuardFrom(@NotNull ASTSCState srcState) {
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
      .anyMatch(ASTTransitionBody::isPresentPre);
  }
  
  public boolean hasEntryAction(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    
    if(!(state.getSCSBody() instanceof ASTStateBody)) return false;
    ASTStateBody stateBody = (ASTStateBody) state.getSCSBody();
    return stateBody.getSCStateElementList().stream()
      .anyMatch(elem -> elem instanceof ASTSCEntryAction);
  }
  
  public ASTMCBlockStatement getEntryActionBlockStatement(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    Preconditions.checkArgument(hasEntryAction(state));
    
    ASTStateBody stateBody = (ASTStateBody) state.getSCSBody();
    ASTSCEntryAction entryAction = stateBody.getSCStateElementList().stream()
      .filter(elem -> elem instanceof ASTSCEntryAction).map(elem -> (ASTSCEntryAction) elem)
      .findFirst().get();
    return scABodyToTransitionAction(entryAction.getSCABody()).getMCBlockStatement();
  }
  
  public boolean hasExitAction(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
  
    if(!(state.getSCSBody() instanceof ASTStateBody)) return false;
    ASTStateBody stateBody = (ASTStateBody) state.getSCSBody();
    return stateBody.getSCStateElementList().stream()
      .anyMatch(elem -> elem instanceof ASTSCExitAction);
  }
  
  public ASTMCBlockStatement getExitActionBlockStatement(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    Preconditions.checkArgument(hasExitAction(state));
    
    ASTStateBody stateBody = (ASTStateBody) state.getSCSBody();
    ASTSCExitAction exitAction = stateBody.getSCStateElementList().stream()
      .filter(elem -> elem instanceof ASTSCExitAction).map(elem -> (ASTSCExitAction) elem)
      .findFirst().get();
    return scABodyToTransitionAction(exitAction.getSCABody()).getMCBlockStatement();
  }
  
}
