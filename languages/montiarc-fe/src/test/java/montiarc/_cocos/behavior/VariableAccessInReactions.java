/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.FieldReadWriteAccessFitsInReactions;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class VariableAccessInReactions extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "variableAccessInReactions";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        Arguments.of("DeepIncrement.arc", new Error[] {BehaviorError.WRITE_TO_INCOMING_PORT}),
        Arguments.of("IncrementPorts.arc", new Error[] {BehaviorError.WRITE_TO_INCOMING_PORT, BehaviorError.READ_FROM_OUTGOING_PORT}),
        Arguments.of("InvalidAssignment.arc", new Error[] {BehaviorError.WRITE_TO_INCOMING_PORT}),
        Arguments.of("ReadOutgoingPort.arc", new Error[] {BehaviorError.READ_FROM_OUTGOING_PORT})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"SerialAssignment.arc", "UsedAll.arc", "ValidIncrement.arc"})
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
    checker.addCoCo(new FieldReadWriteAccessFitsInReactions());
  }
}