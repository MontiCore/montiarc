/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.GuardIsBoolean;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeCheckResult;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.check.MontiArcDerive;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class GuardIsBooleanTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "guardIsBoolean";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("InvalidBraceExpression.arc", new Error[]{BehaviorError.GUARD_IS_NO_BOOLEAN}),
      Arguments.of("WrongLiteral.arc", new Error[]{BehaviorError.GUARD_IS_NO_BOOLEAN}),
      Arguments.of("WrongParameter.arc", new Error[]{BehaviorError.GUARD_IS_NO_BOOLEAN}),
      Arguments.of("WrongPort.arc", new Error[]{BehaviorError.GUARD_IS_NO_BOOLEAN}),
      Arguments.of("WrongVariable.arc", new Error[]{BehaviorError.GUARD_IS_NO_BOOLEAN})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "JustLiteral.arc",
    "JustParameter.arc",
    "JustPort.arc",
    "JustVariable.arc",
    "SimpleComparison.arc",
    "StrangeAssignment.arc",
    "ValidBraceExpression.arc"
  })
  public void succeed(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void fail(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new GuardIsBoolean(new MontiArcDerive(new TypeCheckResult())));
  }
}