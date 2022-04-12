/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.behavior;

import com.google.common.base.Preconditions;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scactions._visitor.SCActionsVisitor2;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._visitor.SCBasisVisitor2;
import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.monticore.scdoactions._visitor.SCDoActionsVisitor2;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcstatementsbasis._visitor.MCStatementsBasisVisitor2;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;

import java.util.*;

public class StateWrapper {
  protected final ASTSCState state;
  protected final Map<ActionType, List<ASTMCBlockStatement>> map;

  public StateWrapper(ASTSCState state) {
    this.state = Preconditions.checkNotNull(state);
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    ActionCollector collector = new ActionCollector();
    traverser.add4SCBasis(collector);
    traverser.add4SCActions(collector);
    traverser.add4SCDoActions(collector);
    traverser.add4MCStatementsBasis(collector);
    state.accept(traverser);
    map = collector.getStatements();
  }

  public String getName() {
    return state.getName();
  }

  public List<ASTMCBlockStatement> getEntryStatements() {
    return map.getOrDefault(ActionType.ENTRY, Collections.emptyList());
  }

  public List<ASTMCBlockStatement> getExitStatements() {
    return map.getOrDefault(ActionType.EXIT, Collections.emptyList());
  }

  public List<ASTMCBlockStatement> getDoStatements() {
    return map.getOrDefault(ActionType.DO, Collections.emptyList());
  }

  protected enum ActionType {
    ENTRY, EXIT, DO, NONE;
  }

  protected class ActionCollector implements SCBasisVisitor2, SCActionsVisitor2, SCDoActionsVisitor2, MCStatementsBasisVisitor2 {
    protected Deque<ASTSCState> states = new LinkedList<>(Collections.singleton(StateWrapper.this.state));
    protected Deque<ActionType> type = new LinkedList<>(Collections.singleton(ActionType.NONE));
    protected ASTMCBlockStatement top = null;
    protected Map<ASTSCState, Map<ActionType, List<ASTMCBlockStatement>>> map = new HashMap<>();

    public Map<ActionType, List<ASTMCBlockStatement>> getStatements() {
      return map.getOrDefault(StateWrapper.this.state, Collections.emptyMap());
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
      type.addLast(ActionType.ENTRY);
    }

    @Override
    public void endVisit(ASTSCEntryAction node) {
      type.removeLast();
    }

    @Override
    public void visit(ASTSCExitAction node) {
      type.addLast(ActionType.EXIT);
    }

    @Override
    public void endVisit(ASTSCExitAction node) {
      type.removeLast();
    }

    @Override
    public void visit(ASTSCDoAction node) {
      type.addLast(ActionType.DO);
    }

    @Override
    public void endVisit(ASTSCDoAction node) {
      type.removeLast();
    }

    @Override
    public void visit(ASTSCTransition node) {
      type.addLast(ActionType.NONE);
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
}
