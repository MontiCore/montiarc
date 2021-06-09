/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.*;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.AbstractCoCoTest;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

/**
 * Tests the cocos
 * {@link PrefixDecrementTargetsNamesOnly}
 * {@link PrefixIncrementTargetsNamesOnly}
 * {@link SuffixDecrementTargetsNamesOnly}
 * {@link SuffixIncrementTargetsNamesOnly}
 * and {@link OnlyAssignToNames}
 */
public class AssignmentExpressionsTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "onlyNamesInAssignments";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("AssignToIncrement.arc", new Error[] {BehaviorError.ASSIGN_TO_NOT_NAME}),
      Arguments.of("DecrementLiterals.arc", new Error[] {BehaviorError.ASSIGN_TO_NOT_NAME, BehaviorError.ASSIGN_TO_NOT_NAME}),
      Arguments.of("IncrementAnAddition.arc", new Error[] {BehaviorError.ASSIGN_TO_NOT_NAME})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "UsedVariableSomewhere.arc"
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

    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new OnlyAssignToNames());
    this.getChecker().addCoCo(new PrefixDecrementTargetsNamesOnly());
    this.getChecker().addCoCo(new PrefixIncrementTargetsNamesOnly());
    this.getChecker().addCoCo(new SuffixDecrementTargetsNamesOnly());
    this.getChecker().addCoCo(new SuffixIncrementTargetsNamesOnly());
  }
}