/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.ExpressionStatementWellFormedness;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;


class ExpressionStatementWellFormednessTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "expressionStatementWellFormedness";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new ExpressionStatementWellFormedness(new MontiArcTypeCalculator()));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // "WithComputeBlockAndCorrectExpressions.arc", // Not included as ArcCompute (currently) is not part of MontiArc
    "WithAutomatonAndCorrectExpressions.arc"
  })
  void shouldFindValidExpressions(@NotNull String model) {
    Preconditions.checkNotNull(model);
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorProvider() {
    return Stream.of(
      // arg("WithComputeBlockAndIncorrectExpressions.arc", ArcError.MALFORMED_EXPRESSION, ArcError.MALFORMED_EXPRESSION), // Not included as ArcCompute (currently) is not part of MontiArc
      arg("WithAutomatonAndIncorrectExpressions.arc",
        ArcError.MALFORMED_EXPRESSION, ArcError.MALFORMED_EXPRESSION)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorProvider")
  void shouldFindInvalidExpressions(@NotNull String model, @NotNull montiarc.util.Error... errors) {
    testModel(model, errors);
  }

}
