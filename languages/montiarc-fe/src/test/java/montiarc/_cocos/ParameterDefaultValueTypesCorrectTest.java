/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterDefaultValueTypesCorrect;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcDeriveType;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ParameterDefaultValueTypesCorrectTest extends AbstractCoCoTest {

  protected static String PACKAGE = "expressionMatchesType.parameters";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ParameterDefaultValueTypesCorrect(new MontiArcDeriveType()));
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    MontiArcMill.globalScope()
      .add(MontiArcMill.typeSymbolBuilder().setName("String")
        .setEnclosingScope(MontiArcMill.globalScope())
        .build());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("IncorrectParamDefaultVals.arc",
        ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE,
        ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"CorrectParamDefaults.arc", "ParamWithoutDefaults.arc"})
  public void shouldApproveValidTypeAssignments(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindInvalidTypeAssignments(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }
}
