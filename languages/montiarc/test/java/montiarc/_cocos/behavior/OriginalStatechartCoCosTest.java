/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.CapitalStateNames;
import de.monticore.scbasis._cocos.TransitionSourceTargetExists;
import de.monticore.scbasis._cocos.UniqueStates;
import de.monticore.sctransitions4code._cocos.AnteBlocksOnlyForInitialStates;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.Error;
import montiarc.util.StatechartError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests whether the original state-chart cocos of TriggeredStatecharts work in statecharts of MontiArc.
 * Those are:
 * {@link UniqueStates}, {@link TransitionSourceTargetExists},
 * {@link CapitalStateNames}, {@link AtLeastOneInitialState}, {@link TransitionPreconditionsAreBoolean}
 */
public class OriginalStatechartCoCosTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "originalStateChartCoCos";
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    addStringToGlobalScope();
  }

  protected static void addStringToGlobalScope() {
    MontiArcMill.globalScope()
      .add(MontiArcMill.typeSymbolBuilder().setName("String")
        .setEnclosingScope(MontiArcMill.globalScope())
        .build());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("LowercaseStateName.arc", new Error[] {StatechartError.CAPITAL_STATE_NAMES}),
      Arguments.of("NoUniqueStates.arc", new Error[] {StatechartError.UNIQUE_STATES}),
      Arguments.of("TransitionSourceMissing.arc", new Error[] {StatechartError.TRANSITION_SOURCE_EXISTS}),
      Arguments.of("TransitionTargetMissing.arc", new Error[] {StatechartError.TRANSITION_TARGET_EXISTS})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"ValidStatechart.arc", "TransStateTransition.arc", "NoStatechart.arc"})
  void succeed(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(Collections.emptyList(), Log.getFindings().stream().map(Finding::getMsg).filter(s -> !s.startsWith("0xA7000")).collect(Collectors.toList()));
    // ignore strange traverser stuff
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void fail(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    try {
      this.getChecker().checkAll(ast);
    } catch(de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e){
      // continue, as this error is kind of expected in some models
    }

    //Then
    this.checkOnlyExpectedErrorsPresent(errors, getPathToModel(model).toAbsolutePath());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "transitionPreconditionsAreBoolean/JustLiteral.arc",
    "transitionPreconditionsAreBoolean/JustParameter.arc",
    "transitionPreconditionsAreBoolean/JustPort.arc",
    "transitionPreconditionsAreBoolean/JustVariable.arc",
    "transitionPreconditionsAreBoolean/SimpleComparison.arc",
    "transitionPreconditionsAreBoolean/StrangeAssignment.arc",
    "transitionPreconditionsAreBoolean/ValidBraceExpression.arc"
  })
  void shouldSucceedBooleanPreconditions(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> booleanPreconditionsModelAndExpectedErrorProvider() {
    return Stream.of(
      Arguments.of("transitionPreconditionsAreBoolean/InvalidBraceExpression.arc",
        new Error[]{StatechartError.PRECONDITION_IS_NOT_BOOLEAN}),
      Arguments.of("transitionPreconditionsAreBoolean/WrongLiteral.arc",
        new Error[]{StatechartError.PRECONDITION_IS_NOT_BOOLEAN}),
      Arguments.of("transitionPreconditionsAreBoolean/WrongParameter.arc",
        new Error[]{StatechartError.PRECONDITION_IS_NOT_BOOLEAN}),
      Arguments.of("transitionPreconditionsAreBoolean/WrongPort.arc",
        new Error[]{StatechartError.PRECONDITION_IS_NOT_BOOLEAN}),
      Arguments.of("transitionPreconditionsAreBoolean/WrongVariable.arc",
        new Error[]{StatechartError.PRECONDITION_IS_NOT_BOOLEAN})
    );
  }

  @ParameterizedTest
  @MethodSource("booleanPreconditionsModelAndExpectedErrorProvider")
  void shouldFailNotBooleanPreconditions(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "atLeastOneInitialState/OneInitialState.arc",
    "atLeastOneInitialState/TwoInitialStates.arc"
  })
  void shouldSucceedAtLeastOneInitialState(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("atLeastOneInitialStateModelAndExpectedErrorProvider")
  void shouldFailAtLeastOneInitialState(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  protected static Stream<Arguments> atLeastOneInitialStateModelAndExpectedErrorProvider() {
    return Stream.of(
      Arguments.of("atLeastOneInitialState/NoInitialState.arc",
        new Error[]{StatechartError.MISSING_INITIAL_STATE})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "anteBlocksOnlyForInitialStates/AnteBeforeInitialState.arc"
  })
  void shouldSucceedWithAnteBlockBeforeInitialState(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("anteBlockAndNormalStateModelAndExpectedErrorProvider")
  void shouldFailWithAnteBlockBeforeNormalState(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  protected static Stream<Arguments> anteBlockAndNormalStateModelAndExpectedErrorProvider() {
    return Stream.of(
      Arguments.of("anteBlocksOnlyForInitialStates/AnteBeforeNormalStates.arc",
        new Error[]{StatechartError.ANTE_NOT_AT_INITIAL, StatechartError.ANTE_NOT_AT_INITIAL})
    );
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new UniqueStates());
    checker.addCoCo(new TransitionSourceTargetExists());
    checker.addCoCo(new CapitalStateNames());
    checker.addCoCo(new TransitionPreconditionsAreBoolean(new MontiArcTypeCalculator()));
    checker.addCoCo(new AtLeastOneInitialState());
    checker.addCoCo(new AnteBlocksOnlyForInitialStates());
  }
}