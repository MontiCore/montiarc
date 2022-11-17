/* (c) https://github.com/MontiCore/monticore */

package variablearc._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._symboltable.ComponentInstanceSymbol;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;

import java.util.List;
import java.util.stream.Stream;

public class FeatureConfigurationParametersLastTest extends AbstractTest {

  protected static Stream<Arguments> provideInstanceAndError() {
    return Stream.of(
      Arguments.of(
        VariableArcMill.componentInstanceBuilder().setName("i1")
          .setArgumentsAbsent().build(),
        new Error[]{}
      ),
      Arguments.of(
        VariableArcMill.componentInstanceBuilder().setName("i2").setArguments(
          VariableArcMill.argumentsBuilder()
            .setExpressionsList(List.of(
              VariableArcMill.nameExpressionBuilder().setName("a").build(),
              AssignmentExpressionsMill.assignmentExpressionBuilder()
                .setLeft(VariableArcMill.nameExpressionBuilder().setName("b")
                  .build())
                .setRight(VariableArcMill
                  .literalExpressionBuilder()
                  .setLiteral(VariableArcMill.booleanLiteralBuilder()
                    .setSource(ASTConstantsMCCommonLiterals.FALSE).build())
                  .build())
                .build()
            ))
            .build()
        ).build(),
        new Error[]{}
      ),
      Arguments.of(
        VariableArcMill.componentInstanceBuilder().setName("i3").setArguments(
          VariableArcMill.argumentsBuilder()
            .setExpressionsList(List.of(
              AssignmentExpressionsMill.assignmentExpressionBuilder()
                .setLeft(VariableArcMill.nameExpressionBuilder().setName("b")
                  .build())
                .setRight(VariableArcMill
                  .literalExpressionBuilder()
                  .setLiteral(VariableArcMill.booleanLiteralBuilder()
                    .setSource(ASTConstantsMCCommonLiterals.FALSE).build())
                  .build())
                .build(),
              VariableArcMill.nameExpressionBuilder().setName("a").build()
            ))
            .build()
        ).build(),
        new Error[]{ VariableArcError.NAMED_ARGUMENTS_LAST }
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideInstanceAndError")
  public void testInstance(ASTComponentInstance instance, Error[] errorList) {
    // Given
    FeatureConfigurationParametersLast coco = new FeatureConfigurationParametersLast();
    ComponentInstanceSymbol symbol = VariableArcMill.componentInstanceSymbolBuilder()
      .setName(instance.getName())
      .build();
    instance.setSymbol(symbol);

    // When
    coco.check(instance);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }
}
