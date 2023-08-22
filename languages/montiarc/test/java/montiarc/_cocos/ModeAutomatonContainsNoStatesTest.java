/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.ModeAutomatonContainsNoStates;
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
 * The class under test is {@link ModeAutomatonContainsNoStates}.
 */
public class ModeAutomatonContainsNoStatesTest extends MontiArcAbstractTest {

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
    // component with multiple modes
    "component Comp6 { " +
      "mode automaton { " +
      "mode m1 {} " +
      "mode m2 {}" +
      "} " +
      "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ModeAutomatonContainsNoStates());

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
    checker.addCoCo(new ModeAutomatonContainsNoStates());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // mode automaton with a state
      arg("component Comp1 { " +
          "mode automaton { state s1; } " +
          "}",
        ModesError.MODE_AUTOMATON_CONTAINS_STATE),
      // mode automaton with multiple states and modes
      arg("component Comp2 { " +
          "mode automaton { " +
          "state s1; " +
          "mode m1 { }" +
          "state s2; " +
          "} " +
          "}",
        ModesError.MODE_AUTOMATON_CONTAINS_STATE),
      // inner with two mode automata where one has a state
      arg("component Comp3 { " +
          "component Inner { " +
          "mode automaton { mode m1 {} } " +
          "mode automaton { state s1; mode m1 { } } " +
          "} " +
          "}",
        ModesError.MODE_AUTOMATON_CONTAINS_STATE)
    );
  }
}
