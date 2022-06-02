/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import montiarc.util.ArcAutomataError;
import com.google.common.base.Preconditions;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

class NoInputPortsInInitialOutputDeclarationTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "behavior/noInputPortsInInitialOutputDeclaration";

  @BeforeEach
  public void prepareModels() {
    this.parseAndCreateAndCompleteSymbols("helpers/Parent.arc");
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new NoInputPortsInInitialOutputDeclaration());
  }

  @ParameterizedTest
  @ValueSource(strings = {"WithoutInputPortRef.arc"})
  void shouldCorrectlyBindConfigurationParameters(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("WithInputPortRef.arc",
          ArcAutomataError.INPUT_PORT_IN_INITIAL_OUT_DECL, ArcAutomataError.INPUT_PORT_IN_INITIAL_OUT_DECL
      )
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldIncorrectlyBindConfigurationParameters(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }
}
