/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcElement;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc._ast.ASTArcVarIf;
import variablearc._cocos.VarIfIsBoolean;

import java.util.stream.Stream;

public class VarIfIsBooleanTest extends MontiArcAbstractTest {

  protected static Stream<Arguments> provideVarIfAndError() {
    return Stream.of(
      Arguments.of(MontiArcMill.arcVarIfBuilder().setCondition(
            MontiArcMill
              .literalExpressionBuilder()
              .setLiteral(MontiArcMill.booleanLiteralBuilder()
                .setSource(ASTConstantsMCCommonLiterals.FALSE).build())
              .build())
          .setThen(Mockito.mock(ASTArcElement.class))
          .setOtherwiseAbsent()
          .build(),
        new Error[]{}
      ),
      Arguments.of(MontiArcMill.arcVarIfBuilder().setCondition(
            MontiArcMill
              .literalExpressionBuilder()
              .setLiteral(MontiArcMill.basicLongLiteralBuilder().setDigits("5")
                .build())
              .build())
          .setThen(Mockito.mock(ASTArcElement.class))
          .setOtherwiseAbsent()
          .build(),
        new Error[]{ VariableArcError.IF_STATEMENT_EXPRESSION_WRONG_TYPE }
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideVarIfAndError")
  public void testVarIfType(ASTArcVarIf constraint,
                            Error[] errorList) {
    // Given
    VarIfIsBoolean coco = new VarIfIsBoolean(new MontiArcTypeCalculator());

    // When
    coco.check(constraint);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }
}
