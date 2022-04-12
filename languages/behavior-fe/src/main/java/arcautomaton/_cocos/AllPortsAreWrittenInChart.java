package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcautomaton._visitor.StatechartNameResolver;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.scactions._ast.ASTSCAction;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCStateElement;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * checks that every output-port occurs
 * (which means is written to,
 * since {@link FieldReadWriteAccessFitsInStatements} checks that output-ports are not read)
 * once per time step in time-synchronous models,
 * not including initial outputs
 */
public class AllPortsAreWrittenInChart implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType component) {
    Preconditions.checkNotNull(component);
    Preconditions.checkArgument(component.isPresentSymbol(), "Symbol-table not created yet");
    component.getBody().streamArcElements().filter(e -> e instanceof ASTArcStatechart)
        .forEach(e -> checkStatechart((ASTArcStatechart) e, component));
  }

  /**
   * checks whether a given statechart of a given component matches this cocos condition
   * @param chart a statechart that describes the behavior of an atomic component
   * @param component the component that owns that statechart
   */
  protected void checkStatechart(ASTArcStatechart chart, ASTComponentType component) {
    // finds all ports that occur in every transition
    Map<ASTSCTransition, Set<PortSymbol>> writtenInTransitions = new HashMap<>();
    chart.streamTransitions().forEach(t -> writtenInTransitions.put(t, getPortsOfTransition(t)));

    // firstly finds states that may have no transitions and then finds ports that occur in their do actions
    Predicate<ASTSCState> hasUnguardedTransition = chart.streamTransitions().filter(t -> getBody(t).map(ASTTransitionBody::isPresentPre).orElse(false)).map(ASTSCTransition::getSourceNameDefinition).collect(Collectors.toSet())::contains;
    Map<ASTSCState, Set<PortSymbol>> writtenInStates = new HashMap<>();
    chart.streamStates().filter(hasUnguardedTransition.negate()).forEach(s -> writtenInStates.put(s, getPortsOfState(s)));

    // checks whether the found sets of ports are complete
    component.getSymbol().getOutgoingPorts().forEach(port -> {
      findMissedValues(writtenInTransitions, port).forEach(transition -> Log.error(ArcError.PORT_NOT_WRITTEN_IN_TRANSITION.format(port.getName()), transition.get_SourcePositionStart()));
      findMissedValues(writtenInStates, port).forEach(state -> Log.error(ArcError.PORT_NOT_WRITTEN_IN_STATE.format(state.getName(), port.getName()), state.get_SourcePositionStart()));
    });
  }

  /**
   * finds all keys of the map, whose respective sets do not contain the given reference
   * @param map sets that have to contain a given value
   * @param reference an object that has to be contained in every set
   * @param <K> key-type
   * @param <V> value-type
   * @return the keys for which the reference value is missing
   */
  protected <K, V> Stream<K> findMissedValues(Map<K, Set<V>> map, V reference){
    return map.keySet().stream().filter(key -> !map.get(key).contains(reference));
  }

  /**
   * @param transition a transition of a statechart
   * @return output-ports that occur in the transition's reaction, the exit-action of the left state or the entry action of the entered state
   */
  protected Set<PortSymbol> getPortsOfTransition(ASTSCTransition transition){
    Set<PortSymbol> ports = new HashSet<>();
    ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
    traverser.add4ExpressionsBasis(new PortFinder(ports));
    Consumer<ASTNode> search = action -> action.accept(traverser);
    getBody(transition).filter(ASTTransitionBody::isPresentTransitionAction).map(ASTTransitionBody::getTransitionAction).ifPresent(search);
    getActions(transition.getSourceNameDefinition(), ENTRY).forEach(search);
    getActions(transition.getTargetNameDefinition(), EXIT).forEach(search);
    return ports;
  }

  /**
   * Note that this method will only return empty sets,
   * since DoActions are currently not extended by MontiArc.
   * @param state any state
   * @return all output-ports that occur in the do-action of this state
   */
  protected Set<PortSymbol> getPortsOfState(ASTSCState state){
    Set<PortSymbol> ports = new HashSet<>();
    ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
    traverser.add4ExpressionsBasis(new PortFinder(ports));
    getActions(state, DO).forEach(d -> d.accept(traverser));
    return ports;
  }

  protected static class PortFinder implements ExpressionsBasisVisitor2 {
    protected final Set<PortSymbol> outputPorts;

    /**
     * creates a visitor that resolves name-expressions for output-ports
     * @param outputPorts bucket for all found output-ports
     */
    public PortFinder(Set<PortSymbol> outputPorts) {
      this.outputPorts = outputPorts;
    }

    @Override
    public void visit(ASTNameExpression node) {
      new StatechartNameResolver(node.getEnclosingScope())
          .resolvePort(node.getName())
          .filter(PortSymbol::isOutgoing)
          .ifPresent(outputPorts::add);
    }
  }

  protected Optional<ASTTransitionBody> getBody(ASTSCTransition transition){
    return transition.getSCTBody() instanceof ASTTransitionBody
        ? Optional.of((ASTTransitionBody) transition.getSCTBody())
        : Optional.empty();
  }

  protected final int ENTRY = 1_01_1973;
  protected final int EXIT = 31_01_2020;
  protected final int DO = 0xD0;

  protected Stream<ASTSCAction> getActions(ASTSCState state, int entry){
    Stream<ASTSCStateElement> stream = Stream.of(state).map(ASTSCState::getSCSBody)
        .filter(ASTSCHierarchyBody.class::isInstance)
        .map(ASTSCHierarchyBody.class::cast)
        .flatMap(ASTSCHierarchyBody::streamSCStateElements);
    if(entry == ENTRY){
      stream = stream.filter(e -> e instanceof ASTSCEntryAction);
    } else if(entry == EXIT){
      stream = stream.filter(e -> e instanceof ASTSCExitAction);
    } else if(entry == DO){
      stream = stream.filter(e -> e instanceof ASTSCDoAction);
    } else {
      stream = Stream.empty();
    }
    return stream.map(a -> (ASTSCAction) a);
  }
}