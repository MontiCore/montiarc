/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.types3.SymTypeRelations;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._cocos.ConstraintIsBoolean;

import java.util.stream.Stream;

public class ConstraintIsBooleanTest extends MontiArcAbstractTest {

  protected static Stream<Arguments> provideConstraintAndError() {
    return Stream.of(
      Arguments.of(MontiArcMill.arcConstraintDeclarationBuilder().setExpression(
            MontiArcMill
              .literalExpressionBuilder()
              .setLiteral(MontiArcMill.booleanLiteralBuilder()
                .setSource(ASTConstantsMCCommonLiterals.FALSE).build())
              .build())
          .build(),
        new Error[]{}
      ),
      Arguments.of(MontiArcMill.arcConstraintDeclarationBuilder().setExpression(
            MontiArcMill
              .literalExpressionBuilder()
              .setLiteral(MontiArcMill.basicLongLiteralBuilder().setDigits("5")
                .build())
              .build())
          .build(),
        new Error[]{ VariableArcError.CONSTRAINT_EXPRESSION_WRONG_TYPE }
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideConstraintAndError")
  public void testConstraintType(ASTArcConstraintDeclaration constraint,
                                 Error[] errorList) {
    // Given
    ConstraintIsBoolean coco = new ConstraintIsBoolean(new MontiArcTypeCalculator(), new SymTypeRelations());

    // When
    coco.check(constraint);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }
}
