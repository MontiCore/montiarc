/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPort;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._ast.ASTArcIfStatement;
import variablearc.evaluation.Expression;

import java.util.Optional;
import java.util.Stack;

public class VariableArcScopesGenitor extends VariableArcScopesGenitorTOP
  implements arcbasis._visitor.ArcBasisVisitor2 {

  protected Stack<VariableComponentTypeSymbol> componentStack = new Stack<>();
  protected Stack<VariableArcVariationPoint> variationPointStack = new Stack<>();

  protected Stack<VariableComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected Optional<VariableComponentTypeSymbol> getCurrentComponent() {
    if (!this.getComponentStack().isEmpty()) {
      return Optional.ofNullable(this.getComponentStack().peek());
    }
    return Optional.empty();
  }

  protected void removeCurrentComponent() {
    this.getComponentStack().pop();
  }

  protected void putOnStack(@Nullable VariableComponentTypeSymbol symbol) {
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
  public void traverse(@NotNull ASTArcIfStatement node) {
    node.getCondition().accept(this.getTraverser());

    putOnStack(new VariableArcVariationPoint(new Expression(node.getCondition()), this.getCurrentVariationPoint().orElse(null)));
    node.getThenStatement().accept(this.getTraverser());
    removeCurrentVariationPoint();

    if (node.isPresentElseStatement()) {
      putOnStack(new VariableArcVariationPoint(
        new Expression(node.getCondition(), true),
        this.getCurrentVariationPoint().orElse(null))
      );
      node.getElseStatement().accept(this.getTraverser());
      removeCurrentVariationPoint();
    }
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    this.putOnStack((VariableComponentTypeSymbol) node.getSymbol());
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    Preconditions.checkState(this.getCurrentComponent().get().isPresentAstNode());
    Preconditions.checkState(this.getCurrentComponent().get().getAstNode().equals(node));
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
}
