/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.OneInitialInHierarchicalStates;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.AbstractCoCoTest;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

@Disabled(value = "This coco does not have an error-code yet")
public class InitialStateInHierarchicalStates extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "oneInitialStateInComposedStates";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        Arguments.of("AllStatesInitial.arc", new Error[] {BehaviorError.FIELD_IN_GUARD_MISSING}),
        Arguments.of("LacksInitialState.arc", new Error[] {BehaviorError.FIELD_IN_GUARD_MISSING}),
        Arguments.of("TwoInitialStates.arc", new Error[] {BehaviorError.FIELD_IN_GUARD_MISSING})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NoSubstates.arc",
      "ValidHierarchicalStates.arc"})
  public void succeed(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), new Error[0]);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void fail(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    try {
      this.getChecker().checkAll(ast);
    } catch(de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e){
      // continue, as this error is kind of expected in some models
    }

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new OneInitialInHierarchicalStates());
  }

}