/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ComponentTypeNameCapitalization;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.InstanceNameCapitalisation;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.PortNameCapitalisation;
import com.google.common.base.Preconditions;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

/**
 * Contains tests for
 * {@link arcbasis._cocos.ComponentTypeNameCapitalization}, {@link arcbasis._cocos.FieldNameCapitalization},
 * {@link arcbasis._cocos.InstanceNameCapitalisation}, {@link arcbasis._cocos.ParameterNameCapitalization},
 * {@link arcbasis._cocos.PortNameCapitalisation}, {@link genericarc._cocos.GenericTypeParameterNameCapitalization}
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
        ArcError.COMPONENT_NAME_UPPER_CASE, ArcError.TYPE_PARAMETER_UPPER_CASE_LETTER, ArcError.PARAMETER_LOWER_CASE,
        ArcError.COMPONENT_NAME_UPPER_CASE, ArcError.INSTANCE_NAME_LOWER_CASE, ArcError.PORT_LOWER_CASE,
        ArcError.PORT_LOWER_CASE, ArcError.VARIABLE_LOWER_CASE),
      arg("componentNameLowerCase.arc", ArcError.COMPONENT_NAME_UPPER_CASE),
      arg("InnerComponentTypeLowerCase.arc", ArcError.COMPONENT_NAME_UPPER_CASE),
      arg("InstanceNameUpperCase.arc", ArcError.INSTANCE_NAME_LOWER_CASE),
      arg("ParameterNameUpperCase.arc", ArcError.PARAMETER_LOWER_CASE),
      arg("PortNameUpperCase.arc", ArcError.PORT_LOWER_CASE, ArcError.PORT_LOWER_CASE),
      arg("TypeParameterLowerCaseLetter.arc", ArcError.TYPE_PARAMETER_UPPER_CASE_LETTER),
      arg("VariableNameUpperCase.arc", ArcError.VARIABLE_LOWER_CASE)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"AllNamesCorrectlyCapitalized.arc", "NothingToCapitalize.arc"})
  public void allNamesCorrectlyCapitalized(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldDetectIncorrectCapitalization(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ComponentTypeNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new GenericTypeParameterNameCapitalization());
    checker.addCoCo(new InstanceNameCapitalisation());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new PortNameCapitalisation());
  }
}