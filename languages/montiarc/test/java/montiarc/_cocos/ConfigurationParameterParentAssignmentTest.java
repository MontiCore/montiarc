/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConfigurationParameterParentAssignment;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.ArcError;
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

/**
 * Tests for {@link ConfigurationParameterParentAssignment}
 */
public class ConfigurationParameterParentAssignmentTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "configurationParameterParentAssignment";

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
      arg("DirectBindingParentParametersIncorrectly.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("MandatoryInheritedParameterOmitted.arc",
        ArcError.TOO_FEW_PARENT_INSTANTIATION_ARGUMENTS),
      arg("MandatoryInheritedParametersUnordered.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("MandatoryInheritedParametersWithSomeTypesChanged.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("MandatoryInheritedParameterWithTypeChange.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("MultipleInheritedParametersAllOmitted.arc",
        ArcError.TOO_FEW_PARENT_INSTANTIATION_ARGUMENTS),
      arg("MultipleInheritedParametersWithSomeTypesChanged.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("OptionalInheritedParameterBecomesMandatoryNewType.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("OptionalInheritedParametersWithSomeTypesChanged.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("OptionalInheritedParametersWithSomeMandatoryAndTypesChanged.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("OptionalInheritedParameterWithTypeChange.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH),
      arg("TypeOverwritingIncorrectly.arc",
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH, ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH, ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH, ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH,
        ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH, ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH)
    );
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "DirectBindingParentParametersCorrectly.arc",
    "MandatoryInheritedParameterBecomesOptionalNewName.arc",
    "MandatoryInheritedParameterBecomesOptionalSameName.arc",
    "MandatoryInheritedParameterNewName.arc",
    "MandatoryInheritedParameterNoChanges.arc",
    "MultipleInheritedParametersAndAdditionalOptionalParametersLast.arc",
    "MultipleInheritedParametersAndAdditionalParametersFirst.arc",
    "MultipleInheritedParametersAndAdditionalParametersInBetween.arc",
    "MultipleInheritedParametersNoChanges.arc",
    "MultipleInheritedParametersPartiallyOmitted.arc",
    "MultipleInheritedParametersUnordered.arc",
    "NoInheritanceAndNoParametersInComponent.arc",
    "NoInheritanceButParametersInComponent.arc",
    "NoInheritedParametersAndNoneInComponent.arc",
    "NoInheritedParametersButParametersInComponent.arc",
    "OptionalInheritedParameterNoChanges.arc",
    "OptionalInheritedParameterOmitted.arc",
    "OptionalInheritedParameterBecomesMandatoryNewName.arc",
    "OptionalInheritedParameterBecomesMandatorySameName.arc",
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
    checker.addCoCo(new ConfigurationParameterParentAssignment(new MontiArcTypeCalculator()));
  }
}