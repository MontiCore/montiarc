/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.ModeOmitPortDefinition;
import montiarc.MontiArcAbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.ModesError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link ModeOmitPortDefinition}.
 */
public class ModeOmitPortDefinitionTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no mode automata
    "component Comp1 { }",
    // mode automaton with no modes
    "component Comp2 { " +
      "mode automaton { } " +
      "}",
    // inner with mode automaton and one mode
    "component Comp3 { " +
      "component Inner { " +
      "mode automaton { " +
      "mode m1 { } " +
      " } " +
      "} " +
      "}",
    // two inner with modes
    "component Comp4 { " +
      "component Inner1 { " +
      "mode automaton { " +
      "mode m1 { }" +
      " } " +
      "} " +
      "component Inner2 { " +
      "mode automaton { " +
      "mode m1 { }" +
      " } " +
      "} " +
      "}",
    // component with automaton and mode
    "component Comp5 { " +
      "mode automaton { mode m1 {} } " +
      "automaton { } " +
      "}",
    // component with two mode automata
    "component Comp6 { " +
      "mode automaton { mode m1 {} } " +
      "mode automaton { mode m1 {} } " +
      "}",
    // component with multiple modes and elements
    "component Comp6 { " +
      "mode automaton { " +
      "mode m1 { " +
      "port1 -> port2;" +
      "} " +
      "mode m2 { " +
      "component A { " +
      "port in int pIn;" +
      "} " +
      "A a;" +
      "} " +
      "} " +
      "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ModeOmitPortDefinition());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ModeOmitPortDefinition());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // mode with port definition
      arg("component Comp1 { " +
          "mode automaton { " +
          "mode m1 { port in int pIn; }" +
          "} " +
          "}",
        ModesError.MODE_CONTAINS_PORT_DEFINITION),
      // mode automaton with multiple states and modes
      arg("component Comp2 { " +
          "mode automaton { " +
          "mode m1 { port in int pIn; }" +
          "mode m2 { port out double pOut;}" +
          "} " +
          "}",
        ModesError.MODE_CONTAINS_PORT_DEFINITION, ModesError.MODE_CONTAINS_PORT_DEFINITION)
    );
  }
}
