/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcautomaton._visitor.SCSuperStateResolver;
import com.google.common.base.Preconditions;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scbasis._ast.ASTSCSAnte;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.sctransitions4code._ast.ASTAnteAction;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArcAutomatonHelper {
  public ASTSCTransition getFirstTransitionWithoutGuardFrom(@NotNull ASTArcStatechart automaton, @NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(srcState);
    Preconditions.checkArgument(automaton.streamStates().anyMatch(state -> state.equals(srcState)));
    Preconditions.checkArgument(hasTransitionWithoutGuardFrom(automaton, srcState));

    List<ASTSCTransition> transitions = automaton.streamTransitions().filter(tr ->
        tr.getSourceName().equals(srcState.getName()) &&
          !((ASTTransitionBody) tr.getSCTBody()).isPresentPre())
      .collect(Collectors.toList());

    return transitions.get(0);
  }

  public List<ASTSCTransition> getAllTransitionsWithGuardFrom(@NotNull ASTArcStatechart automaton, @NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(srcState);
    Preconditions.checkArgument(automaton.streamStates().anyMatch(state -> state.equals(srcState)));

    return automaton.streamTransitions()
      .filter(tr -> tr.getSourceName().equals(srcState.getName()) && ((ASTTransitionBody) tr.getSCTBody()).isPresentPre())
      .collect(Collectors.toList());
  }

  public ASTTransitionAction scABodyToTransitionAction(@NotNull ASTSCABody action) {
    Preconditions.checkNotNull(action);
    return (ASTTransitionAction) action;
  }

  public boolean isAnteAction(@NotNull ASTSCSAnte ante) {
    Preconditions.checkNotNull(ante);
    return ante instanceof ASTAnteAction;
  }

  public ASTAnteAction asAnteAction(@NotNull ASTSCSAnte ante) {
    Preconditions.checkNotNull(ante);
    return (ASTAnteAction) ante;
  }

  public boolean hasTransitionWithoutGuardFrom(@NotNull ASTArcStatechart automaton, @NotNull ASTSCState srcState) {
    Preconditions.checkNotNull(automaton);
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

  public boolean hasEntryAction(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);

    if (!(state.getSCSBody() instanceof ASTSCHierarchyBody))
      return false;
    ASTSCHierarchyBody stateBody = (ASTSCHierarchyBody) state.getSCSBody();
    return stateBody.getSCStateElementList().stream()
      .anyMatch(elem -> elem instanceof ASTSCEntryAction);
  }

  public ASTMCBlockStatement getEntryActionBlockStatement(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    Preconditions.checkArgument(hasEntryAction(state));

    ASTSCHierarchyBody stateBody = (ASTSCHierarchyBody) state.getSCSBody();
    ASTSCEntryAction entryAction = stateBody.getSCStateElementList().stream()
      .filter(elem -> elem instanceof ASTSCEntryAction).map(elem -> (ASTSCEntryAction) elem)
      .findFirst().get();
    return scABodyToTransitionAction(entryAction.getSCABody()).getMCBlockStatement();
  }

  public boolean hasExitAction(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);

    if (!(state.getSCSBody() instanceof ASTSCHierarchyBody))
      return false;
    ASTSCHierarchyBody stateBody = (ASTSCHierarchyBody) state.getSCSBody();
    return stateBody.getSCStateElementList().stream()
      .anyMatch(elem -> elem instanceof ASTSCExitAction);
  }

  public ASTMCBlockStatement getExitActionBlockStatement(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    Preconditions.checkArgument(hasExitAction(state));

    ASTSCHierarchyBody stateBody = (ASTSCHierarchyBody) state.getSCSBody();
    ASTSCExitAction exitAction = stateBody.getSCStateElementList().stream()
      .filter(elem -> elem instanceof ASTSCExitAction).map(elem -> (ASTSCExitAction) elem)
      .findFirst().get();
    return scABodyToTransitionAction(exitAction.getSCABody()).getMCBlockStatement();
  }

  public boolean hasInitAction(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    return state.getSCModifier().isInitial() && isAnteAction(state.getSCSAnte());
  }

  public List<ASTMCBlockStatement> getInitActionStatementList(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    Preconditions.checkArgument(hasInitAction(state));
    return asAnteAction(state.getSCSAnte()).getMCBlockStatementList();
  }

  public boolean hasSubStates(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    return state.getSCSBody() instanceof ASTSCHierarchyBody
      && ((ASTSCHierarchyBody) state.getSCSBody()).getSCStateElementList().stream().anyMatch(ASTSCState.class::isInstance);
  }

  public Stream<ASTSCState> getSubStatesStream(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    if (state.getSCSBody() instanceof ASTSCHierarchyBody) {
      return ((ASTSCHierarchyBody) state.getSCSBody()).getSCStateElementList().stream().filter(ASTSCState.class::isInstance)
        .map(ASTSCState.class::cast);
    }
    return Stream.empty();
  }

  public Stream<ASTSCState> getInitialSubStatesStream(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    Stream<ASTSCState> substates = getSubStatesStream(state);
    if (substates.anyMatch(s -> s.getSCModifier().isInitial())) { // if at least one is marked initial
      return getSubStatesStream(state).filter(s -> s.getSCModifier().isInitial()); // return marked ones
    }
    return getSubStatesStream(state); // otherwise, all are initial
  }

  public boolean hasSuperState(@NotNull ASTArcStatechart automaton, @NotNull ASTSCState state) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(state);
    return getSuperState(automaton, state) != null;
  }

  public ASTSCState getSuperState(@NotNull ASTArcStatechart automaton, @NotNull ASTSCState state) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(state);
    final ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
    final SCSuperStateResolver resolver = new SCSuperStateResolver(state);
    traverser.add4SCBasis(resolver);
    automaton.accept(traverser);
    return resolver.getResult();
  }

  public boolean isFinalState(@NotNull ASTArcStatechart automaton, @NotNull ASTSCState state) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(state);
    if (state.getSCModifier().isFinal())
      return true; // has final keyword
    ASTSCState superState = getSuperState(automaton, state);
    if (superState == null)
      return false;
    return getSubStatesStream(superState).noneMatch(s -> s.getSCModifier().isFinal()); // or is substate and none have
  }

  // This includes nested sub states
  public List<ASTSCState> getAllSubStatesStream(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    ArrayDeque<ASTSCState> next = getSubStatesStream(state).collect(Collectors.toCollection(ArrayDeque::new));
    List<ASTSCState> result = new ArrayList<>(next.size());
    while (!next.isEmpty()) {
      result.add(next.peek());
      next.addAll(getSubStatesStream(next.pop()).collect(Collectors.toList()));
    }
    return result;
  }

  public List<ASTSCState> getLeavingParentStatesFromWith(@NotNull ASTArcStatechart automaton,
                                                         @NotNull ASTSCState from,
                                                         @NotNull ASTSCTransition transition) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(from);
    Preconditions.checkNotNull(transition);
    ASTSCState to = from.getEnclosingScope().resolveSCState(transition.getTargetName()).orElseThrow().getAstNode();
    return getDifferingParents(automaton, from, to);
  }

  public List<ASTSCState> getEnteringParentStatesFromWith(@NotNull ASTArcStatechart automaton,
                                                          @NotNull ASTSCState from,
                                                          @NotNull ASTSCTransition transition) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(from);
    Preconditions.checkNotNull(transition);
    ASTSCState to = from.getEnclosingScope().resolveSCState(transition.getTargetName()).orElseThrow().getAstNode();
    List<ASTSCState> res = getDifferingParents(automaton, to, from);
    Collections.reverse(res);
    return res;
  }

  public List<ASTSCState> getDifferingParents(@NotNull ASTArcStatechart automaton,
                                              @NotNull ASTSCState start,
                                              @NotNull ASTSCState end) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(start);
    Preconditions.checkNotNull(end);
    List<ASTSCState> parents = new ArrayList<>();
    ASTSCState next = start;
    while (!getAllSubStatesStream(next).contains(end)) {
      if (!next.equals(start))
        parents.add(next);
      if (hasSuperState(automaton, next)) {
        next = getSuperState(automaton, next);
      } else {
        return parents;
      }
    }
    return parents;
  }
}
