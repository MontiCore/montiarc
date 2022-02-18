/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.*;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests whether the original state-chart cocos of TriggeredStatecharts work in statecharts of MontiArc.
 * Those are:
 * {@link UniqueStates}, {@link TransitionSourceTargetExists},
 * {@link CapitalStateNames}
 */
public class OriginalStatechartCoCosTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "originalStateChartCoCos";
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return Pattern.compile("0xCC\\d{3}");
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("LowercaseStateName.arc", new Error[] {ExternalErrors.CAPITAL_STATE_NAMES}),
      Arguments.of("NoUniqueStates.arc", new Error[] {ExternalErrors.UNIQUE_STATES}),
      Arguments.of("TransitionSourceMissing.arc", new Error[] {ExternalErrors.TRANSITION_SOURCE_EXISTS}),
      Arguments.of("TransitionTargetMissing.arc", new Error[] {ExternalErrors.TRANSITION_TARGET_EXISTS})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"ValidStatechart.arc", "TransStateTransition.arc", "NoStatechart.arc"})
  public void succeed(@NotNull String model) {
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
  public void fail(@NotNull String model, @NotNull Error... errors) {
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

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new UniqueStates());
    checker.addCoCo(new TransitionSourceTargetExists());
    checker.addCoCo(new CapitalStateNames());
  }

  /**
   * wraps the error messages into enum values, so they can be used in combination with the existing test infrastructure
   */
  enum ExternalErrors implements Error {
    UNIQUE_STATES(UniqueStates.ERROR_CODE),
    TRANSITION_SOURCE_EXISTS(TransitionSourceTargetExists.SOURCE_ERROR_CODE),
    TRANSITION_TARGET_EXISTS(TransitionSourceTargetExists.TARGET_ERROR_CODE),
    CAPITAL_STATE_NAMES(CapitalStateNames.ERROR_CODE),
    PACKAGE_CORRESPONDS_TO_FOLDERS(PackageCorrespondsToFolders.ERROR_CODE),
    SC_FILE_EXTENSION(SCFileExtension.ERROR_CODE),
    SC_NAME_IS_ARTIFACT_NAME(SCNameIsArtifactName.ERROR_CODE);

    ExternalErrors(String code){
      this.code = code;
    }

    final String code;

    @Override
    public String getErrorCode() {
      return code;
    }

    @Override
    public String printErrorMessage() {
      return "this method is not used for this test";
    }
  }
}