/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.FieldReadWriteAccessFitsInGuards;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class VariableAccessInGuards extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "variableAccessInGuards";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        Arguments.of("DeepIncrement.arc", new Error[] {BehaviorError.WRITE_TO_INCOMING_PORT}),
        Arguments.of("IncrementPort.arc", new Error[] {BehaviorError.WRITE_TO_INCOMING_PORT}),
        Arguments.of("InvalidAssignment.arc", new Error[] {BehaviorError.WRITE_TO_INCOMING_PORT}),
        Arguments.of("ReadOutgoingPort.arc", new Error[] {BehaviorError.READ_FROM_OUTGOING_PORT})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"StrangeAssignment.arc", "UsedAllInGuard.arc", "ValidIncrement.arc"})
  public void succeed(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(new Error[0]);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void fail(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    try {
      this.getChecker().checkAll(ast);
    } catch(de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e){
      // continue, as this error is kind of expected in some models
    }

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new FieldReadWriteAccessFitsInGuards());
  }
}