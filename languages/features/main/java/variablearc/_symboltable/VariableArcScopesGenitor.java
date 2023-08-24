/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPort;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcVarIf;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.expressions.Expression;
import variablearc.evaluation.expressions.NegatedExpression;

import java.util.Collections;
import java.util.Optional;
import java.util.Stack;

public class VariableArcScopesGenitor extends VariableArcScopesGenitorTOP
  implements arcbasis._visitor.ArcBasisVisitor2 {

  protected Stack<IVariableArcComponentTypeSymbol> componentStack = new Stack<>();
  protected Stack<VariableArcVariationPoint> variationPointStack = new Stack<>();

  protected Stack<IVariableArcComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected Optional<IVariableArcComponentTypeSymbol> getCurrentComponent() {
    if (!this.getComponentStack().isEmpty()) {
      return Optional.ofNullable(this.getComponentStack().peek());
    }
    return Optional.empty();
  }

  protected void removeCurrentComponent() {
    this.getComponentStack().pop();
  }

  protected void putOnStack(@Nullable IVariableArcComponentTypeSymbol symbol) {
    this.getComponentStack().push(symbol);
  }

  protected Stack<VariableArcVariationPoint> getVariationPointStack() {
    return this.variationPointStack;
  }

  protected Optional<VariableArcVariationPoint> getCurrentVariationPoint() {
    if (!this.getVariationPointStack().isEmpty()) {
      return Optional.ofNullable(this.getVariationPointStack().peek());
    }
    return Optional.empty();
  }

  protected void removeCurrentVariationPoint() {
    if (!this.getVariationPointStack().isEmpty()) {
      this.getVariationPointStack().pop();
    }
  }

  protected void putOnStack(@Nullable VariableArcVariationPoint variationPoint) {
    Preconditions.checkState(this.getCurrentScope().isPresent());
    if (getCurrentComponent().isPresent()) {
      this.getCurrentComponent().get().add(variationPoint);
    }
    this.getVariationPointStack().push(variationPoint);
  }

  @Override
  public void traverse(@NotNull ASTArcVarIf node) {
    node.getCondition().accept(this.getTraverser());

    putOnStack(new VariableArcVariationPoint(new Expression(node.getCondition()), this.getCurrentVariationPoint().orElse(null)));
    node.getThen().accept(this.getTraverser());
    removeCurrentVariationPoint();

    if (node.isPresentOtherwise()) {
      putOnStack(new VariableArcVariationPoint(
        new NegatedExpression(node.getCondition()),
        this.getCurrentVariationPoint().orElse(null))
      );
      node.getOtherwise().accept(this.getTraverser());
      removeCurrentVariationPoint();
    }
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    this.putOnStack((IVariableArcComponentTypeSymbol) node.getSymbol());
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    Preconditions.checkState(this.getCurrentComponent().get().getTypeInfo().isPresentAstNode());
    Preconditions.checkState(this.getCurrentComponent().get().getTypeInfo().getAstNode().equals(node));
    this.removeCurrentComponent();
  }

  @Override
  public void endVisit(@NotNull ASTPort node) {
    Preconditions.checkNotNull(node.getSymbol());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    if (this.getCurrentVariationPoint().isPresent()) {
      this.getCurrentVariationPoint().get().add(node.getSymbol());
    }
  }

  @Override
  public void endVisit(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node.getSymbol());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    if (this.getCurrentVariationPoint().isPresent()) {
      this.getCurrentVariationPoint().get().add(node.getSymbol());
    }
  }

  @Override
  public void endVisit(@NotNull ASTConnector node) {
    Preconditions.checkNotNull(node);
    if (this.getCurrentVariationPoint().isPresent()) {
      this.getCurrentVariationPoint().get().add(node);
    }
  }

  @Override
  public void endVisit(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);
    if (this.getCurrentComponent().isEmpty()) return;
    this.getCurrentComponent().get().getLocalConstraints().add(new ExpressionSet(Collections.singletonList(new Expression(node.getExpression()))));
  }
}
