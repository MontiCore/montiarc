/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.FieldsInGuardsExist;
import arcbehaviorbasis.BehaviorError;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

/**
 * Tests whether the variables used in Guards exist.
 * Does not search for type-miss-matches
 */
public class GuardVariablesTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "fieldsInGuardsExist";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("MissingVariable.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING}),
      Arguments.of("OnlyOneMissing.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING}),
      Arguments.of("DeeplyNestedMissingVariable.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NoGuards.arc",
      "UsedParameter.arc",
      "UsedVariable.arc",
      "UsedPort.arc",
      "UsedVariablePortAndParameter.arc",
      "DeeplyNestedVariable.arc",
      "ValidWithSubstates.arc"})
  public void succeed(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void fail(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new FieldsInGuardsExist());
  }
}