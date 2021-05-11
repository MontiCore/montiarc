/* (c) https://github.com/MontiCore/monticore */

package montiarc._cocos;

import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcDerive;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ConfigurationParameterAssignmentTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "configurationParameterAssignment";

  @BeforeEach
  public void loadComponentsToInstantiate() {
    // loading helper models into the symboltable
    ASTMACompilationUnit simple = this.parseAndLoadSymbols("subcomponentDefinitions/Simple.arc");
    ASTMACompilationUnit complex = this.parseAndLoadSymbols("subcomponentDefinitions/Complex.arc");
  }

  @ParameterizedTest
  @ValueSource(strings = {"CorrectParameterBindings.arc"})
  public void shouldCorrectlyBindConfigurationParameters(@NotNull String model) {
    Preconditions.checkNotNull(model);
    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("TooFewParameterBindings.arc",
        ArcError.CONFIG_PARAMETER_BINDING,
        ArcError.CONFIG_PARAMETER_BINDING,
        ArcError.CONFIG_PARAMETER_BINDING,
        ArcError.CONFIG_PARAMETER_BINDING),
      arg("TooManyParameterBindings.arc",
        ArcError.CONFIG_PARAMETER_BINDING,
        ArcError.CONFIG_PARAMETER_BINDING),
      arg("WrongTypeParameterBindings.arc",
        ArcError.CONFIG_PARAMETER_BINDING,
        ArcError.CONFIG_PARAMETER_BINDING,
        ArcError.CONFIG_PARAMETER_BINDING,
        ArcError.CONFIG_PARAMETER_BINDING)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldIncorrectlyBindConfigurationParameters(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);
    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @Override
  protected String getPackage() { return PACKAGE; }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new ConfigurationParameterAssignment(new MontiArcDerive(new TypeCheckResult())));
  }
}
