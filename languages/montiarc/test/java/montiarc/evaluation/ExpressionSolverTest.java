/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
  @Override
  public void init() {
    super.init();
    this.setUpTypeExpr();
  }

  protected void setUpTypeExpr() {
    IVariableArcScope scope = MontiArcMill.scope();

    ArcFeatureSymbol variableSymbol = MontiArcMill.arcFeatureSymbolBuilder()
      .setName("a").build();
    scope.add(variableSymbol);
    ASTExpression variableAssignmentExpr = MontiArcMill.assignmentExpressionBuilder()
      .setLeft(MontiArcMill.nameExpressionBuilder().setName("a").build())
      .setOperator(ASTConstantsAssignmentExpressions.DEFAULT)
      .setRight(MontiArcMill.literalExpressionBuilder()
        .setLiteral(MontiArcMill.booleanLiteralBuilder()
          .setSource(ASTConstantsMCCommonLiterals.TRUE).build())
        .build())
      .build();

    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    Mockito.when(astComponentType.getBody()).thenReturn(Mockito.mock(ASTComponentBody.class));

    ComponentTypeSymbol componentTypeSymbol = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Test")
      .setAstNode(astComponentType)
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
      .setLiteral(MontiArcMill.booleanLiteralBuilder()
        .setSource(ASTConstantsMCCommonLiterals.TRUE).build())
      .build();

    ExpressionSolver solver = new ExpressionSolver(this.typeExprOfVariableComponent);
    Optional<Boolean> res = solver.solve(expression);
    solver.close();

    Assertions.assertTrue(res.isPresent());
    Assertions.assertTrue(res.get());
  }

  @Test
  public void shouldSolveWithLocalVariable() {
    ASTExpression expression = MontiArcMill.nameExpressionBuilder().setName("a")
      .build();
    expression.setEnclosingScope(this.typeExprOfVariableComponent.getTypeInfo().getSpannedScope());

    ExpressionSolver solver = new ExpressionSolver(this.typeExprOfVariableComponent);
    Optional<Boolean> res = solver.solve(expression);
    solver.close();

    Assertions.assertTrue(res.isPresent());
    Assertions.assertTrue(res.get());
  }
}
