/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import montiarc.MontiArcMill;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ParameterOmitAssignmentExpressionsTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "configurationParameterAssignment";

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    this.loadComponents();
  }

  public void loadComponents() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Simple.arc");
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Complex.arc");
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Generic.arc");
  }

  @ParameterizedTest
  @ValueSource(strings = {"CorrectParameterBindings.arc", "CorrectGenericParameterBindings.arc"})
  void shouldCorrectlyBindConfigurationParameters(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("InvalidAssignmentSyntax.arc",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT,
        ArcError.COMP_ARG_MULTI_ASSIGNMENT)
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
    checker.addCoCo(new ParameterOmitAssignmentExpressions());
  }
}