/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.visitor.IVisitor;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcVarIf;
import variablearc._ast.ASTVariableArcComponentType;
import variablearc._visitor.VariableArcInheritanceHandler;
import variablearc._visitor.VariableArcTraverser;
import variablearc._visitor.VariableArcVisitor2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates the {@link LocalPresence} of a component type and of all its
 * inner component types from the AST and stores it at their ASTs. The
 * presence condition of every node is the conjunction of the conditions of
 * the varif branches of its component type that enclose it.
 */
public class LocalPresenceCalculator implements IVisitor, ArcBasisVisitor2, VariableArcVisitor2 {

  protected final Deque<Frame> frames = new ArrayDeque<>();

  public void calculate(@NotNull ASTArcComponentType component) {
    Preconditions.checkNotNull(component);
    Preconditions.checkState(this.frames.isEmpty());
    component.accept(this.createTraverser());
  }

  protected VariableArcTraverser createTraverser() {
    VariableArcTraverser traverser = VariableArcMill.inheritanceTraverser();
    traverser.setVariableArcHandler(new VarIfHandler());
    traverser.add4ArcBasis(this);
    traverser.add4VariableArc(this);
    traverser.add4IVisitor(this);
    return traverser;
  }

  @Override
  public void visit(ASTNode node) {
    if (this.frames.isEmpty()) return;
    if (!this.currentCondition().isTop()) {
      this.frames.peek().nodes().put(node, this.currentCondition());
    }
    if (node instanceof ASTArcBehaviorElement behavior && behavior.isDelayed()) {
      this.frames.peek().delayedBehaviors().add(this.currentCondition());
    }
  }

  @Override
  public void visit(ASTArcComponentType node) {
    this.frames.push(Frame.empty());
  }

  @Override
  public void endVisit(ASTArcComponentType node) {
    Frame frame = this.frames.pop();
    if (node instanceof ASTVariableArcComponentType component) {
      component.setLocalPresence(new LocalPresence(frame.nodes(), frame.constraints(), frame.delayedBehaviors()));
    }
  }

  @Override
  public void endVisit(ASTArcConstraintDeclaration node) {
    this.frames.peek().constraints().add(new LocalPresence.ConditionalConstraint(this.currentCondition(), node.getExpression()));
  }

  protected PresenceCondition currentCondition() {
    return this.frames.peek().conditions().peek();
  }

  protected record Frame(@NotNull Deque<PresenceCondition> conditions,
                         @NotNull Map<ASTNode, PresenceCondition> nodes,
                         @NotNull List<LocalPresence.ConditionalConstraint> constraints,
                         @NotNull List<PresenceCondition> delayedBehaviors) {

    protected static Frame empty() {
      return new Frame(new ArrayDeque<>(List.of(PresenceCondition.top())), new IdentityHashMap<>(), new ArrayList<>(),
        new ArrayList<>());
    }
  }

  protected class VarIfHandler extends VariableArcInheritanceHandler {

    @Override
    public void traverse(ASTArcVarIf node) {
      node.getCondition().accept(this.getTraverser());
      this.traverseBranch(node.getThen(), new PresenceCondition.Literal(node.getCondition(), false));
      if (node.isPresentOtherwise()) {
        this.traverseBranch(node.getOtherwise(), new PresenceCondition.Literal(node.getCondition(), true));
      }
    }

    protected void traverseBranch(@NotNull ASTNode branch, @NotNull PresenceCondition.Literal condition) {
      Deque<PresenceCondition> conditions = frames.peek().conditions();
      conditions.push(conditions.peek().and(condition));
      branch.accept(this.getTraverser());
      conditions.pop();
    }
  }
}
