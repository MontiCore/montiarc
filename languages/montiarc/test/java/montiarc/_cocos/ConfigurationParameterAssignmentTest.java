/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
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

public class ConfigurationParameterAssignmentTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "configurationParameterAssignment";

  @BeforeEach
  public void prepareModels() {
    loadList();
    loadPerson();
    loadComponentsToInstantiate();
  }

  public void loadList() {
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("List")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    SymbolService.link(MontiArcMill.globalScope(), listSym);
  }

  public void loadPerson() {
    OOTypeSymbol personSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Person")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    SymbolService.link(MontiArcMill.globalScope(), personSym);
  }

  public void loadComponentsToInstantiate() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Simple.arc");
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Complex.arc");
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/ComplexWithTwoMandatoryParams.arc");
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Generic.arc");
  }

  @ParameterizedTest
  @ValueSource(strings = {"CorrectParameterBindings.arc", "CorrectGenericParameterBindings.arc"})
  void shouldCorrectlyBindConfigurationParameters(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("TooFewAndWrongBindings.arc",
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("TooFewParameterBindings.arc",
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS),
      arg("TooManyAndWrongBindings.arc",
        ArcError.TOO_MANY_INSTANTIATION_ARGUMENTS,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.TOO_MANY_INSTANTIATION_ARGUMENTS,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.MULTIPLE_VALUES_FOR_ARGUMENT),
      arg("TooManyParameterBindings.arc",
        ArcError.TOO_MANY_INSTANTIATION_ARGUMENTS,
        ArcError.TOO_MANY_INSTANTIATION_ARGUMENTS),
      arg("WrongTypeParameterBindings.arc",
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("WrongGenericParameterBindings.arc",
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("BindsTypeReference.arc",
        ArcError.CONFIG_PARAM_BINDING_IS_TYPE_REF,
        ArcError.CONFIG_PARAM_BINDING_IS_TYPE_REF),
      arg("TooFewParameterBindingsAndTypeRef.arc",
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.CONFIG_PARAM_BINDING_IS_TYPE_REF,
        ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS,
        ArcError.CONFIG_PARAM_BINDING_IS_TYPE_REF),
      arg("IncompatibleAndBindsTypeRef.arc",
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.CONFIG_PARAM_BINDING_IS_TYPE_REF,
        ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.CONFIG_PARAM_BINDING_IS_TYPE_REF),
      arg("UnknownParameterKeyword.arc",
        ArcError.ASSIGNMENT_KEYWORD_NOT_A_PARAMETER,
        ArcError.ASSIGNMENT_KEYWORD_NOT_A_PARAMETER),
      arg("InvalidAssignmentSyntax.arc",
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEYWORD_ASSIGNMENT,
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEYWORD_ASSIGNMENT)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldIncorrectlyBindConfigurationParameters(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    testModel(model, errors);
  }

  @Override
  protected String getPackage() { return PACKAGE; }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ConfigurationParameterAssignment(new MontiArcTypeCalculator(), new TypeRelations()));
  }
}