/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.behavior;

import arcautomaton._ast.ASTPortListEvent;
import arcautomaton._visitor.ArcAutomatonVisitor2;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._visitor.SCBasisVisitor2;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeVisitor2;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;

import java.util.*;

public class TransitionWrapper {
  protected final ASTSCTransition transition;
  protected final List<ASTPortAccess> trigger;
  protected final ASTExpression guard;
  protected final ASTTransitionAction reaction;

  public TransitionWrapper(ASTSCTransition transition) {
    this.transition = Preconditions.checkNotNull(transition);
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    TransitionStuffCollector collector = new TransitionStuffCollector();
    traverser.add4SCBasis(collector);
    traverser.add4ArcAutomaton(collector);
    traverser.add4SCTransitions4Code(collector);
    transition.accept(traverser);
    trigger = collector.triggers;
    guard = collector.guard;
    reaction = collector.reaction;
  }

  public ASTSCTransition getNode(){
    return transition;
  }

  public StateWrapper getSource(){
    return new StateWrapper(transition.getSourceNameDefinition());
  }

  public StateWrapper getTarget(){
    return new StateWrapper(transition.getTargetNameDefinition());
  }

  public String getTargetName(){
    return transition.getTargetName();
  }

  public String getSourceName(){
    return transition.getSourceName();
  }

  public List<ASTPortAccess> getTriggers(){
    return trigger;
  }

  public Optional<ASTExpression> getGuard(){
    return Optional.ofNullable(guard);
  }

  public Optional<ASTTransitionAction> getReaction(){
    return Optional.ofNullable(reaction);
  }

  public boolean startsAt(StateWrapper state){
    return getSource().getName().equals(Preconditions.checkNotNull(state).getName());
  }

  protected class TransitionStuffCollector implements SCBasisVisitor2, ArcAutomatonVisitor2, SCTransitions4CodeVisitor2 {
    protected Deque<ASTSCTransition> transitions = new LinkedList<>(Collections.singleton(TransitionWrapper.this.transition));
    protected List<ASTPortAccess> triggers = new ArrayList<>();
    protected ASTExpression guard = null;
    protected ASTTransitionAction reaction = null;

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
      if(transitions.getLast() != TransitionWrapper.this.transition){
        return;
      }
      triggers.addAll(node.getTriggerList());
    }

    @Override
    public void visit(ASTTransitionBody node) {
      if(transitions.getLast() != TransitionWrapper.this.transition){
        return;
      }
      if(node.isPresentPre()){
        guard = node.getPre();
      }
      if(node.isPresentTransitionAction() && node.getTransitionAction().isPresentMCBlockStatement()){
        reaction = node.getTransitionAction();
      }
    }
  }
}
