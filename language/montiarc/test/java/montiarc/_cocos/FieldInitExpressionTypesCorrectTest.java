/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.FieldInitExpressionTypesCorrect;
import arcbasis._symboltable.SymbolService;
import montiarc.util.ArcError;
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

import java.nio.file.Paths;
import java.util.stream.Stream;

class FieldInitExpressionTypesCorrectTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "expressionMatchesType/fields";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new FieldInitExpressionTypesCorrect(new MontiArcTypeCalculator()));
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    this.provideTypes();
  }

  protected void provideTypes() {
    TypeSymbol string = MontiArcMill.typeSymbolBuilder()
      .setName("String")
      .setEnclosingScope(MontiArcMill.globalScope())
      .build();

    TypeSymbol person = MontiArcMill.typeSymbolBuilder()
      .setName("Person")
      .setEnclosingScope(MontiArcMill.globalScope())
      .build();

    SymbolService.link(MontiArcMill.globalScope(), string);
    SymbolService.link(MontiArcMill.globalScope(), person);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("IncorrectFieldInitializations.arc",
        ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE,
        ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE),
      arg("IncompatibleAndTypeRef.arc",
        ArcError.FIELD_INITIALIZATION_IS_TYPE_REF,
        ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE,
        ArcError.FIELD_INITIALIZATION_IS_TYPE_REF,
        ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE),
      arg("TypeRefAsFieldInitialization.arc",
        ArcError.FIELD_INITIALIZATION_IS_TYPE_REF,
        ArcError.FIELD_INITIALIZATION_IS_TYPE_REF)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "CorrectFieldInitializations.arc",
      "FieldInitializationWithConstructor.arc",
      "ParameterShadowing.arc"
  })
  void shouldApproveValidTypeAssignments(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    this.getCLI().loadSymbols(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, getPackage(), "Datatypes.sym"));
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldFindInvalidTypeAssignments(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }
}