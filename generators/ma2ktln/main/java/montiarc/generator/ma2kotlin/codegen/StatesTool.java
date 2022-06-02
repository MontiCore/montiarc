/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.codegen;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTPortListEvent;
import arcautomaton._visitor.ArcAutomatonVisitor2;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scactions._visitor.SCActionsVisitor2;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._visitor.SCBasisVisitor2;
import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.monticore.scdoactions._visitor.SCDoActionsVisitor2;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeVisitor2;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcstatementsbasis._visitor.MCStatementsBasisVisitor2;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * contains some functionality related to the states of the statechart language
 */
public class StatesTool {

  /**
   * @return the behavior of the component, if it has a statechart
   */
  public Optional<ASTArcStatechart> getStatechart(ComponentTypeSymbol component){
    Preconditions.checkNotNull(component);
    return component.getAstNode().getBody()
        .streamArcElements()
        .filter(ASTArcStatechart.class::isInstance)
        .map(ASTArcStatechart.class::cast)
        .reduce((a, b) -> {throw new IllegalStateException(component.getName()+" has multiple statecharts");});
  }

  /**
   * @return transitions of the atomic-behavior-statechart of this component
   */
  public List<ASTSCTransition> getTransitions(ASTArcStatechart chart){
    return chart.streamTransitions().collect(Collectors.toList());
  }

  /**
   * @return whether the given transition starts at this state
   */
  public boolean startsAt(ASTSCState state, ASTSCTransition transition){
    return transition.getSourceName().equals(state.getName());
  }

  /**
   * @return states of the atomic-behavior-statechart of this component
   */
  public List<ASTSCState> getStates(ASTArcStatechart chart){
    return chart.streamStates().collect(Collectors.toList());
  }

  /**
   * @param state any state (sub-states are ignored)
   * @return the content of the entry-block of the given state
   */
  public List<ASTMCBlockStatement> getEntryStatements(ASTSCState state) {
    return collectActions(state, StatesTool.ActionType.ENTRY);
  }

  /**
   * @param state any state (sub-states are ignored)
   * @return the content of the exit-block of the given state
   */
  public List<ASTMCBlockStatement> getExitStatements(ASTSCState state) {
    return collectActions(state, StatesTool.ActionType.EXIT);
  }

  /**
   * @param state any state (sub-states are ignored)
   * @return the content of the do-block of the given state
   */
  public List<ASTMCBlockStatement> getDoStatements(ASTSCState state) {
    return collectActions(state, StatesTool.ActionType.DO);
  }

  public List<ASTPortAccess> getTriggers(ASTSCTransition transition){
    return Optional.ofNullable(collectProperties(transition).triggers).orElse(Collections.emptyList());
  }

  public Optional<ASTExpression> getGuard(ASTSCTransition transition){
    return Optional.ofNullable(collectProperties(transition).guard);
  }

  public Optional<ASTMCBlockStatement> getReaction(ASTSCTransition transition){
    return Optional.ofNullable(collectProperties(transition).reaction);
  }

  /**
   * traverses the state to find statements that declare its behavior
   * @param state any state (sub-states are ignored)
   * @param filter type of action to find
   * @return a list of found statements
   */
  protected List<ASTMCBlockStatement> collectActions(ASTSCState state, ActionType filter) {
    Preconditions.checkNotNull(state);
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    StatesTool.ActionCollector collector = new StatesTool.ActionCollector();
    traverser.add4SCBasis(collector);
    traverser.add4SCActions(collector);
    traverser.add4SCDoActions(collector);
    traverser.add4MCStatementsBasis(collector);
    state.accept(traverser);
    return collector.getStatements(state).getOrDefault(filter, Collections.emptyList());
  }

  public TransitionStuffCollector collectProperties(ASTSCTransition transition) {
    Preconditions.checkNotNull(transition);
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    TransitionStuffCollector collector = new TransitionStuffCollector();
    traverser.add4SCBasis(collector);
    traverser.add4ArcAutomaton(collector);
    traverser.add4SCTransitions4Code(collector);
    transition.accept(traverser);
    return collector;
  }

  protected enum ActionType {
    ENTRY, EXIT, DO, NONE
  }

  protected static class ActionCollector implements SCBasisVisitor2, SCActionsVisitor2, SCDoActionsVisitor2, MCStatementsBasisVisitor2 {
    protected final Deque<ASTSCState> states = new LinkedList<>();
    protected final Deque<StatesTool.ActionType> type = new LinkedList<>(Collections.singleton(StatesTool.ActionType.NONE));

    /**
     * block statements can be contained in themselves, but we always only need the top-level-statement,
     * and thus need this variable to differentiate between them
     */
    protected ASTMCBlockStatement top = null;

    /**
     * map in which found results are stored
     */
    protected final Map<ASTSCState, Map<StatesTool.ActionType, List<ASTMCBlockStatement>>> map = new HashMap<>();

    public Map<StatesTool.ActionType, List<ASTMCBlockStatement>> getStatements(ASTSCState state) {
      return map.getOrDefault(state, Collections.emptyMap());
    }

    @Override
    public void visit(ASTSCState node) {
      states.addLast(node);
    }

    @Override
    public void endVisit(ASTSCState node) {
      states.removeLast();
    }

    @Override
    public void visit(ASTSCEntryAction node) {
      type.addLast(StatesTool.ActionType.ENTRY);
    }

    @Override
    public void endVisit(ASTSCEntryAction node) {
      type.removeLast();
    }

    @Override
    public void visit(ASTSCExitAction node) {
      type.addLast(StatesTool.ActionType.EXIT);
    }

    @Override
    public void endVisit(ASTSCExitAction node) {
      type.removeLast();
    }

    @Override
    public void visit(ASTSCDoAction node) {
      type.addLast(StatesTool.ActionType.DO);
    }

    @Override
    public void endVisit(ASTSCDoAction node) {
      type.removeLast();
    }

    @Override
    public void visit(ASTSCTransition node) {
      type.addLast(StatesTool.ActionType.NONE);
    }

    @Override
    public void endVisit(ASTSCTransition node) {
      type.removeLast();
    }

    @Override
    public void visit(ASTMCBlockStatement node) {
      if (top == null) {
        top = node;
        map.putIfAbsent(states.getLast(), new HashMap<>());
        map.get(states.getLast()).putIfAbsent(type.getLast(), new ArrayList<>());
        map.get(states.getLast()).get(type.getLast()).add(top);
      }
    }

    @Override
    public void endVisit(ASTMCBlockStatement node) {
      if (top == node) {
        top = null;
      }
    }
  }

  protected static class TransitionStuffCollector implements SCBasisVisitor2, ArcAutomatonVisitor2, SCTransitions4CodeVisitor2 {
    protected final Deque<ASTSCTransition> transitions = new LinkedList<>();
    protected List<ASTPortAccess> triggers = null;
    protected ASTExpression guard = null;
    protected ASTMCBlockStatement reaction = null;

    @Override
    public void visit(ASTSCTransition node) {
      transitions.addLast(node);
    }

    @Override
    public void endVisit(ASTSCTransition node) {
      transitions.removeLast();
    }

    @Override
    public void visit(ASTPortListEvent node) {
      // only care about the top transition
      // transitions usually do not contain other transitions
      if(transitions.getLast() != transitions.getFirst()){
        return;
      }
      triggers = node.getTriggerList();
    }

    @Override
    public void visit(ASTTransitionBody node) {
      if(transitions.getLast() != transitions.getFirst()){
        return;
      }
      if(node.isPresentPre()){
        guard = node.getPre();
      }
      if(node.isPresentTransitionAction() && node.getTransitionAction().isPresentMCBlockStatement()){
        reaction = node.getTransitionAction().getMCBlockStatement();
      }
    }
  }
}