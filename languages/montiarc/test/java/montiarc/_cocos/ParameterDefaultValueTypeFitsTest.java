/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterDefaultValueTypeFits;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.TypeRelations;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ParameterDefaultValueTypeFitsTest extends AbstractCoCoTest {

  protected static String PACKAGE = "parameterDefaultValueTypesCorrect";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ParameterDefaultValueTypeFits(new MontiArcTypeCalculator(), new TypeRelations()));
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
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH,
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("TypeRefAsDefaultValue.arc",
        ArcError.TYPE_REF_DEFAULT_VALUE,
        ArcError.TYPE_REF_DEFAULT_VALUE),
      arg("IncompatibleAndTypeRef.arc",
        ArcError.TYPE_REF_DEFAULT_VALUE,
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH,
        ArcError.TYPE_REF_DEFAULT_VALUE,
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH)
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
