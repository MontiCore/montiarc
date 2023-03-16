/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcElement;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.types.check.TypeRelations;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc._ast.ASTArcIfStatement;
import variablearc._cocos.IfStatementIsBoolean;

import java.util.stream.Stream;

public class IfStatementIsBooleanTest extends AbstractTest {

  protected static Stream<Arguments> provideIfStatementAndError() {
    return Stream.of(
      Arguments.of(MontiArcMill.arcIfStatementBuilder().setCondition(
            MontiArcMill
              .literalExpressionBuilder()
              .setLiteral(MontiArcMill.booleanLiteralBuilder()
                .setSource(ASTConstantsMCCommonLiterals.FALSE).build())
              .build())
          .setThenStatement(Mockito.mock(ASTArcElement.class))
          .setElseStatementAbsent()
          .build(),
        new Error[]{}
      ),
      Arguments.of(MontiArcMill.arcIfStatementBuilder().setCondition(
            MontiArcMill
              .literalExpressionBuilder()
              .setLiteral(MontiArcMill.basicLongLiteralBuilder().setDigits("5")
                .build())
              .build())
          .setThenStatement(Mockito.mock(ASTArcElement.class))
          .setElseStatementAbsent()
          .build(),
        new Error[]{ VariableArcError.IF_STATEMENT_EXPRESSION_WRONG_TYPE }
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideIfStatementAndError")
  public void testIfStatementType(ASTArcIfStatement constraint,
                                  Error[] errorList) {
    // Given
    IfStatementIsBoolean coco = new IfStatementIsBoolean(new MontiArcTypeCalculator(), new TypeRelations());

    // When
    coco.check(constraint);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }
}
