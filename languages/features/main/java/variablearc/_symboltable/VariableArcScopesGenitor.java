/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTPort;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._ast.ASTArcIfStatement;

import java.util.Optional;
import java.util.Stack;

public class VariableArcScopesGenitor extends VariableArcScopesGenitorTOP
  implements arcbasis._visitor.ArcBasisVisitor2 {

  protected Stack<VariableArcVariationPoint> variationPointStack = new Stack<>();

  protected Stack<VariableArcVariationPoint> getVariationPointStack() {
    return this.variationPointStack;
  }

  protected Optional<VariableArcVariationPoint> getCurrentVariationPoint() {
    if (!this.getVariationPointStack().isEmpty()) {
      return Optional.ofNullable(this.getVariationPointStack().peek());
    }
    return Optional.empty();
  }

  protected Optional<VariableArcVariationPoint> removeCurrentVariationPoint() {
    if (!this.getVariationPointStack().isEmpty()) {
      return Optional.ofNullable(this.getVariationPointStack().pop());
    }
    return Optional.empty();
  }

  protected void putOnStack(@Nullable VariableArcVariationPoint variationPoint) {
    Preconditions.checkState(this.getCurrentScope().isPresent());
    if (getCurrentVariationPoint().isEmpty()) {
      this.getCurrentScope().get().add(variationPoint);
    }
    this.getVariationPointStack().push(variationPoint);
  }

  @Override
  public void traverse(@NotNull ASTArcIfStatement node) {
    node.getCondition().accept(this.getTraverser());

    putOnStack(new VariableArcVariationPoint(node.getCondition(), this.getCurrentVariationPoint()));
    node.getThenStatement().accept(this.getTraverser());
    removeCurrentVariationPoint();

    if (node.isPresentElseStatement()) {
      putOnStack(new VariableArcVariationPoint(
        CommonExpressionsMill.logicalNotExpressionBuilder().setExpression(
          CommonExpressionsMill.bracketExpressionBuilder().setExpression(
            node.getCondition()
          ).build()
        ).build(),
        this.getCurrentVariationPoint())
      );
      node.getElseStatement().accept(this.getTraverser());
      removeCurrentVariationPoint();
    }
  }

  @Override
  public void endVisit(ASTPort node) {
    Preconditions.checkNotNull(node.getSymbol());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    if (this.getCurrentVariationPoint().isPresent()) {
      this.getCurrentVariationPoint().get().add(node.getSymbol());
    }
  }
}
