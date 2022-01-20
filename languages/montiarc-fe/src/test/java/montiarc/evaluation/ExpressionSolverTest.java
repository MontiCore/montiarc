/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.check.TypeExprOfVariableComponent;
import variablearc.evaluation.ExpressionSolver;

import java.util.Collections;
import java.util.Optional;

public class ExpressionSolverTest extends AbstractTest {

  protected TypeExprOfVariableComponent typeExprOfVariableComponent;

  protected MontiArcFullPrettyPrinter prettyPrinter;

  @BeforeEach
  public void SetUpTypeExpr() {
    IVariableArcScope scope = MontiArcMill.scope();

    ArcFeatureSymbol variableSymbol = MontiArcMill.arcFeatureSymbolBuilder().setName("a").build();
    scope.add(variableSymbol);
    ASTExpression variableAssignmentExpr = MontiArcMill.assignmentExpressionBuilder()
      .setLeft(MontiArcMill.nameExpressionBuilder().setName("a").build())
      .setOperator(0)
      .setRight(MontiArcMill.literalExpressionBuilder()
        .setLiteral(MontiArcMill.booleanLiteralBuilder().setSource(3).build())
        .build())
      .build();

    ComponentTypeSymbol componentTypeSymbol = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Test")
      .setSpannedScope(scope)
      .build();
    scope.setSpanningSymbol(componentTypeSymbol);
    prettyPrinter = new MontiArcFullPrettyPrinter();
    typeExprOfVariableComponent = new TypeExprOfVariableComponent(componentTypeSymbol,
      Collections.singletonList(variableAssignmentExpr));
  }

  @Test
  public void shouldSolveConstantExpression() {
    ASTExpression expression = MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.booleanLiteralBuilder().setSource(3).build())
      .build();

    Optional<Boolean> res = ExpressionSolver.solve(expression, this.typeExprOfVariableComponent,
      this.prettyPrinter::prettyprint);

    Assertions.assertTrue(res.isPresent());
    Assertions.assertTrue(res.get());
  }

  @Test
  public void shouldSolveWithLocalVariable() {
    ASTExpression expression = MontiArcMill.nameExpressionBuilder().setName("a").build();

    Optional<Boolean> res = ExpressionSolver.solve(expression, this.typeExprOfVariableComponent,
      this.prettyPrinter::prettyprint);

    Assertions.assertTrue(res.isPresent());
    Assertions.assertTrue(res.get());
  }
}
