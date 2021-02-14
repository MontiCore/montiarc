/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._cocos.*;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

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

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldDetectIncorrectCapitalization(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new ComponentTypeNameCapitalization())
      .addCoCo(new FieldNameCapitalization())
      .addCoCo(new GenericTypeParameterNameCapitalization())
      .addCoCo(new InstanceNameCapitalisation())
      .addCoCo(new ParameterNameCapitalization())
      .addCoCo(new PortNameCapitalisation());
  }
}