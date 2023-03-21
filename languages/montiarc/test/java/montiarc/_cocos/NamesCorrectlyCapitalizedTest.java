/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ComponentNameCapitalization;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.SubcomponentNameCapitalization;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.PortNameCapitalization;
import com.google.common.base.Preconditions;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc.util.ArcError;
import montiarc.util.Error;
import montiarc.util.GenericArcError;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.FeatureNameCapitalization;

import java.util.stream.Stream;

/**
 * Contains tests for {@link ComponentNameCapitalization},
 * {@link arcbasis._cocos.FieldNameCapitalization}, {@link
 * SubcomponentNameCapitalization}, {@link arcbasis._cocos.ParameterNameCapitalization},
 * {@link PortNameCapitalization}, {@link
 * genericarc._cocos.GenericTypeParameterNameCapitalization}
 */
public class NamesCorrectlyCapitalizedTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "namesCorrectlyCapitalized";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("allNamesIncorrectlyCapitalized.arc",
        ArcError.COMPONENT_LOWER_CASE, GenericArcError.TYPE_PARAMETER_UPPER_CASE_LETTER, ArcError.PARAMETER_UPPER_CASE,
        ArcError.COMPONENT_LOWER_CASE, ArcError.SUBCOMPONENT_UPPER_CASE, ArcError.PORT_UPPER_CASE,
        ArcError.PORT_UPPER_CASE, ArcError.FIELD_UPPER_CASE, VariableArcError.FEATURE_LOWER_CASE),
      arg("componentNameLowerCase.arc", ArcError.COMPONENT_LOWER_CASE),
      arg("InnerComponentTypeLowerCase.arc", ArcError.COMPONENT_LOWER_CASE),
      arg("InstanceNameUpperCase.arc", ArcError.SUBCOMPONENT_UPPER_CASE),
      arg("ParameterNameUpperCase.arc", ArcError.PARAMETER_UPPER_CASE),
      arg("PortNameUpperCase.arc", ArcError.PORT_UPPER_CASE, ArcError.PORT_UPPER_CASE),
      arg("TypeParameterLowerCaseLetter.arc", GenericArcError.TYPE_PARAMETER_UPPER_CASE_LETTER),
      arg("VariableNameUpperCase.arc", ArcError.FIELD_UPPER_CASE),
      arg("FeatureNameUpperCase.arc", VariableArcError.FEATURE_LOWER_CASE)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = { "AllNamesCorrectlyCapitalized.arc", "NothingToCapitalize.arc" })
  public void allNamesCorrectlyCapitalized(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldDetectIncorrectCapitalization(@NotNull String model,
                                                  @NotNull Error... errors) {
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new GenericTypeParameterNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new FeatureNameCapitalization());
  }
}