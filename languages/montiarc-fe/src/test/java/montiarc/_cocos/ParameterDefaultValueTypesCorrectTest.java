/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterDefaultValueTypesCorrect;
import arcbasis._symboltable.SymbolService;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

class ParameterDefaultValueTypesCorrectTest extends AbstractCoCoTest {

  protected static String PACKAGE = "expressionMatchesType.parameters";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ParameterDefaultValueTypesCorrect(new MontiArcTypeCalculator()));
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    this.provideTypes();
  }

  protected void provideTypes() {
    TypeSymbol string = MontiArcMill.typeSymbolBuilder().setName("String")
      .setEnclosingScope(MontiArcMill.globalScope())
      .build();
    TypeSymbol person = MontiArcMill.typeSymbolBuilder().setName("Person")
      .setEnclosingScope(MontiArcMill.globalScope())
      .build();

    SymbolService.link(MontiArcMill.globalScope(), string);
    SymbolService.link(MontiArcMill.globalScope(), person);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("IncorrectParamDefaultVals.arc",
        ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE,
        ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE),
      arg("TypeRefAsDefaultValue.arc",
        ArcError.PARAM_DEFAULT_VALUE_IS_TYPE_REF,
        ArcError.PARAM_DEFAULT_VALUE_IS_TYPE_REF),
      arg("IncompatibleAndTypeRef.arc",
        ArcError.PARAM_DEFAULT_VALUE_IS_TYPE_REF,
        ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE,
        ArcError.PARAM_DEFAULT_VALUE_IS_TYPE_REF,
        ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"CorrectParamDefaults.arc", "ParamWithoutDefaults.arc"})
  void shouldApproveValidTypeAssignments(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldFindInvalidTypeAssignments(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }
}
