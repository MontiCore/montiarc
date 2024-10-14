/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._ast;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcautomaton._visitor.SCTransitionsCollector;
import de.monticore.scbasis._ast.ASTSCEmptyBody;
import de.monticore.scbasis._ast.ASTSCSAnte;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCStateElement;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTArcStatechart extends ASTArcStatechartTOP {

  /**
   * @return all transitions that occur in this statechart, probably in the order in which they are given
   */
  public Stream<ASTSCTransition> streamTransitions() {
    final ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
    final SCTransitionsCollector collector = new SCTransitionsCollector();
    traverser.add4SCBasis(collector);
    this.accept(traverser);
    return collector.getTransitions().stream();
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
   * @return all states that are contained in this statechart
   */
  public Stream<ASTSCState> streamStates() {
    return getSpannedScope().getLocalSCStateSymbols().stream().map(SCStateSymbol::getAstNode);
  }

  public List<ASTSCState> getStates() {
    List<ASTSCState> list = new ArrayList<>();
    for (SCStateSymbol state : getSpannedScope().getLocalSCStateSymbols()) {
      list.add(state.getAstNode());
    }
    return list;
  }


  /**
   * @return a stream containing all initial states of this statechart. Note that if one initial state is declared
   * multiple times (probably by mistake) then all its declarations are contained in the stream.
   */
  public Stream<ASTSCState> streamInitialStates() {
    return streamStates().filter(s -> s.getSCModifier().isInitial());
  }

  public List<ASTSCState> listInitialStates() {
    return streamStates().filter(s -> s.getSCModifier().isInitial()).collect(Collectors.toList());
  }

  /**
   * @return a stream containing all initial outer states of this statechart. Note that if one initial state is declared
   * multiple times (probably by mistake) then all its declarations are contained in the stream.
   */
  public Stream<ASTSCState> streamInitialOuterStates() {
    return getSCStatechartElementList().stream()
      .filter(ASTSCState.class::isInstance)
      .map(ASTSCState.class::cast)
      .filter(s -> s.getSCModifier().isInitial());
  }

  protected Timing timing;

  @Override
  public Timing getTiming() {
    if (this.timing != null) {
      return this.timing;
    } else if (this.isPresentStereotype()) {
      this.timing = this.getStereotype().streamValues()
        .map(v -> Timing.of(v.getName()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst().orElse(Timing.DEFAULT);
      return this.timing;
    } else {
      this.timing = Timing.DEFAULT;
      return this.timing;
    }
  }

  public ASTSCState findCommonSuperstate(ASTSCState current, ASTSCState destination) {
    List<ASTSCState> pathCurrent = getAncestors(current);
    List<ASTSCState> pathDestination = getAncestors(destination);
    ASTSCState commonSuperstate = null;
    Collections.reverse(pathDestination);

    for (ASTSCState value : pathCurrent) {
      for (ASTSCState item : pathDestination) {
        if (value.getName().equals(item.getName())) {
          commonSuperstate = value;
        }
      }
    }
    return commonSuperstate;
  }

  /**
   * Returns all super states to which {@code state} belongs to, including {@code state} itself. <br>
   * The order in the list starts from {@code state} and moves up all ancestors until the root state.
   */
  public List<ASTSCState> getAncestors(ASTSCState state) {
    List<ASTSCState> path = new ArrayList<>();
    path.add(state);
    for (ASTSCState s : getStates()) {
      if (s.getSCSBody() instanceof ASTSCEmptyBody)
        continue;
      for (ASTSCStateElement body : ((ASTSCHierarchyBody) s.getSCSBody()).getSCStateElementList()) {
        if (ArcAutomatonMill.typeDispatcher().isSCBasisASTSCState(body) && ArcAutomatonMill.typeDispatcher().asSCBasisASTSCState(body).getName().equals(state.getName()))
          path.addAll(getAncestors(s));
      }
    }
    return path;
  }

  /**
   * Given that {@code ancestor} is an ancestor of {@code baseState}, this method returns the states that are in between
   * {@code ancestor} and {@code baseState} in the state hierarchy. <br>
   * {code ancestor} is not included in the returned list, but {@code baseState} is
   * (except if {@code ancestor == baseState}, then ancestor is in the list).
   * The <i>order</i> starts from the first child state of {@code ancestor} and moves downwards the state hierarchy,
   * until it reaches {@code baseState}.
   *
   * @param ancestor In the special case that ancestor is {@code null}, the method returns all states ancestors of
   *                 {@code baseState} in the state hierarchy in the order from the most outer state to
   *                 {@code baseState}.
   */
  public List<ASTSCState> getAncestorsInbetween(ASTSCState baseState, ASTSCState ancestor) {
    List<ASTSCState> path = getAncestors(baseState);
    Collections.reverse(path);
    if (ancestor != null && ancestor != baseState) {
      path = path.subList(path.indexOf(ancestor) + 1, path.size());
    }
    return path;
  }

}