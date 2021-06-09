/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.FieldsInReactionsExist;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

/**
 * Tests whether the variables used in Actions and Reactions exist.
 * Does not search for type-miss-matches
 */
public class ActionVariablesTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "fieldsInActionsExist";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("MissingVariableInAction.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING}),
      Arguments.of("MissingVariableInReaction.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING}),
      Arguments.of("ReadFromDeeplyNestedMissingPort.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING}),
      Arguments.of("OnlyOneMissing.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NoActions.arc",
      "UsedParameter.arc",
      "UsedVariable.arc",
      "UsedVariablePortAndParameter.arc",
      "ReadFromDeeplyNestedPort.arc",
      "ValidStatechart.arc",
      "ValidWithSubstates.arc"
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
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new FieldsInReactionsExist());
  }
}