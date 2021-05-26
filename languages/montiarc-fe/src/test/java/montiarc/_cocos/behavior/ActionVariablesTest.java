/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.FieldsInReactionsExist;
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

/**
 * Tests whether the variables used in Actions and Reactions exist.
 * Does not search for type-miss-matches
 */
@Disabled(value = "This coco is not implemented yet")
public class ActionVariablesTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "fieldsInActionsExist";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("MissingVariableInAction.arc", new Error[] {BehaviorError.FIELD_IN_ACTION_MISSING}),
      Arguments.of("MissingVariableInReaction.arc", new Error[] {BehaviorError.FIELD_IN_ACTION_MISSING}),
      Arguments.of("ReadFromOutPort.arc", new Error[] {BehaviorError.FIELD_IN_ACTION_MISSING}),
      Arguments.of("WriteToInPort.arc", new Error[] {BehaviorError.FIELD_IN_ACTION_MISSING}),
      Arguments.of("OnlyOneMissing.arc", new Error[] {BehaviorError.FIELD_IN_ACTION_MISSING})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NoActions.arc",
      "UsedParameter.arc",
      "UsedVariable.arc",
      "UsedVariablePortAndParameter.arc",
      "ValidStatechart.arc",
      "ValidWithSubstates.arc"
  })
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
    this.getChecker().addCoCo(new FieldsInReactionsExist());
  }
}