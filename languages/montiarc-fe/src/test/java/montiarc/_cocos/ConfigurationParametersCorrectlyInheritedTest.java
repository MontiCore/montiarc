/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConfigurationParametersCorrectlyInherited;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcMill;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ConfigurationParametersCorrectlyInheritedTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "configurationParametersCorrectlyInherited";

  @Override
  @BeforeEach
  public void init() {
    super.init();
    loadOOTypes();
    loadUtilityComponents();
  }

  public void loadOOTypes() {
    loadString();
    loadList();
    loadPersonAndStudent();
  }

  public void loadString() {
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("String")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());
  }

  public void loadList() {
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("List")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());
  }

  public void loadPersonAndStudent() {
    OOTypeSymbol personSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Person")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(personSym);
    MontiArcMill.globalScope().addSubScope(personSym.getSpannedScope());

    OOTypeSymbol studentSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(personSym))
      .build();
    MontiArcMill.globalScope().add(studentSym);
    MontiArcMill.globalScope().addSubScope(studentSym.getSpannedScope());
  }

  public void loadUtilityComponents() {
    // loading super components into the symboltable
    this.parseAndCreateAndCompleteSymbols("superComponents/NoParameters.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/OneMandatoryString.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/OneMandatoryStringAndOneMandatoryInt.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/OneOptionalString.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/OneOptionalStringAndOneOptionalInt.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/ThreeMandatoryAndThreeOptionalStrings.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/ThreeMandatoryStrings.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/ThreeOptionalStrings.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/WithVariousTypesAsParameters.arc");
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("MandatoryInheritedParameterOmitted.arc",
        ArcError.TOO_FEW_CONFIGURATION_PARAMETER),
      arg("MandatoryInheritedParametersUnordered.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("MandatoryInheritedParametersWithSomeTypesChanged.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("MandatoryInheritedParameterWithTypeChange.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("MultipleInheritedParametersAllOmitted.arc",
        ArcError.TOO_FEW_CONFIGURATION_PARAMETER),
      arg("MultipleInheritedParametersAndAdditionalParametersFirst.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("MultipleInheritedParametersAndAdditionalParametersInBetween.arc",
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("MultipleInheritedParametersPartiallyOmitted.arc",
        ArcError.TOO_FEW_CONFIGURATION_PARAMETER),
      arg("MultipleInheritedParametersUnordered.arc",
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("MultipleInheritedParametersWithSomeTypesChanged.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("OptionalInheritedParameterBecomesMandatoryNewName.arc",
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("OptionalInheritedParameterBecomesMandatoryNewType.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("OptionalInheritedParameterBecomesMandatorySameName.arc",
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("OptionalInheritedParameterOmitted.arc",
        ArcError.TOO_FEW_CONFIGURATION_PARAMETER),
      arg("OptionalInheritedParametersWithSomeTypesChanged.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("OptionalInheritedParametersWithSomeMandatoryAndTypesChanged.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("OptionalInheritedParameterWithTypeChange.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("TypeOverwritingIncorrectly.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH, ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH, ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH, ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH /*,
         // TODO: fix when monticores typecheck on generics is fixed
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH, ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH */)
    );
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "MandatoryInheritedParameterBecomesOptionalNewName.arc",
    "MandatoryInheritedParameterBecomesOptionalSameName.arc",
    "MandatoryInheritedParameterNewName.arc",
    "MandatoryInheritedParameterNoChanges.arc",
    "MultipleInheritedParametersAndAdditionalOptionalParametersLast.arc",
    "MultipleInheritedParametersNoChanges.arc",
    "NoInheritanceAndNoParametersInComponent.arc",
    "NoInheritanceButParametersInComponent.arc",
    "NoInheritedParametersAndNoneInComponent.arc",
    "NoInheritedParametersButParametersInComponent.arc",
    "OptionalInheritedParameterNoChanges.arc",
    "OptionalInheritedParameterDifferentNameDifferentValue.arc",
    "OptionalInheritedParameterDifferentNameSameValue.arc",
    "OptionalInheritedParameterSameNameDifferentValue.arc",
    "OptionalInheritedParametersUnordered.arc",
    "TypeOverwritingCorrectly.arc",
    "TypeOverwritingWithoutChange.arc"
  })
  public void shouldCorrectlyInheritConfigurationParameters(@NotNull String model) {
    Preconditions.checkNotNull(model);
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void testInvalidModelHasErrors(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ConfigurationParametersCorrectlyInherited());
  }
}