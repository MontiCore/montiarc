/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ConfigurationParametersCorrectlyInherited;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ConfigurationParametersCorrectlyInheritedTest extends AbstractCoCoTest {

  protected static final String MODEL_PATH = "montiarc/cocos/";

  protected static final String PACKAGE = "configurationParametersCorrectlyInherited";

  protected static Arguments arg(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    return Arguments.of(model, errors);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("MandatoryInheritedParameterOmitted.arc",
        ArcError.TO_FEW_CONFIGURATION_PARAMETER),
      arg("MandatoryInheritedParametersUnordered.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("MandatoryInheritedParametersWithSomeTypesChanged.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("MandatoryInheritedParameterWithTypeChange.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("MultipleInheritedParametersAllOmitted.arc",
        ArcError.TO_FEW_CONFIGURATION_PARAMETER),
      arg("MultipleInheritedParametersAndAdditionalParametersFirst.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("MultipleInheritedParametersAndAdditionalParametersInBetween.arc",
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("MultipleInheritedParametersPartiallyOmitted.arc",
        ArcError.TO_FEW_CONFIGURATION_PARAMETER),
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
        ArcError.TO_FEW_CONFIGURATION_PARAMETER),
      arg("OptionalInheritedParametersWithSomeTypesChanged.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH),
      arg("OptionalInheritedParametersWithSomeMandatoryAndTypesChanged.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH,
        ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("OptionalInheritedParameterWithTypeChange.arc",
        ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH)
    );
  }

  protected ASTMACompilationUnit parseAndLoadSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    Path pathToModel = Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, PACKAGE, model);
    ASTMACompilationUnit ast = this.getTool().parse(pathToModel).orElse(null);
    this.getTool().createSymbolTable(ast);
    return ast;
  }

  protected ASTComponentType parseAndLoadAllSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    this.getTool().createSymbolTable(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH));
    Preconditions.checkState(MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).isPresent());
    return MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).get().getAstNode();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "MandatoryInheritedParameterBecomesOptionalNewName.arc",
    "MandatoryInheritedParameterBecomesOptionalSameName.arc",
    "MandatoryInheritedParameterNewName.arc",
    "MandatoryInheritedParameterNoChanges.arc",
    "MultipleInheritedParametersAndAdditionalParametersLast.arc",
    "MultipleInheritedParametersNoChanges.arc",
    "NoInheritanceAndNoParametersInComponent.arc",
    "NoInheritanceButParametersInComponent.arc",
    "NoInheritedParametersAndNoneInComponent.arc",
    "NoInheritedParametersButParametersInComponent.arc",
    "OptionalInheritedParameterNoChanges.arc",
    "OptionalInheritedParameterDifferentNameDifferentValue.arc",
    "OptionalInheritedParameterDifferentNameSameValue.arc",
    "OptionalInheritedParameterSameNameDifferentValue.arc",
    "OptionalInheritedParametersUnordered.arc"
  })
  public void shouldCorrectlyInheritConfigurationParameters(@NotNull String model) {
    Preconditions.checkNotNull(model);
    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void testInvalidModelHasErrors(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);

    //Given
    ASTComponentType ast = this.parseAndLoadAllSymbols(PACKAGE + "." + model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new ConfigurationParametersCorrectlyInherited());
  }
}