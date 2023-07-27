/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc.evaluation.expressions.Expression;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Tests for {@link ExpressionSet}
 */
public class ExpressionSetTest extends VariableArcAbstractTest {

  public static Stream<Arguments> expressionSetAndExpectedPrintProvider() {
    return Stream.of(
      Arguments.of(new ExpressionSet(), ""),
      Arguments.of(new ExpressionSet(List.of(provideExpression("a"), provideExpression("b"))), "a ∧ b"),
      Arguments.of(new ExpressionSet(
          Collections.singletonList(provideExpression("a")),
          List.of(List.of(provideExpression("b"), provideExpression("c")), Collections.singletonList(provideExpression("d")))
        ),
        "a ∧ ¬(b∧c) ∧ ¬(d)")
    );
  }

  protected static Expression provideExpression(String name) {
    return new Expression(VariableArcMill.nameExpressionBuilder().setName(name).build());
  }

  @ParameterizedTest
  @MethodSource("expressionSetAndExpectedPrintProvider")
  public void testPrint(ExpressionSet provided, String expected) {
    Assertions.assertEquals(expected, provided.print());
  }
}
