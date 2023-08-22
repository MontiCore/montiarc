/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.ModeAutomataInDecomposedComponent;
import montiarc.MontiArcAbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.ModesError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link ModeAutomataInDecomposedComponent}.
 */
public class ModeAutomataInDecomposedComponentTest extends MontiArcAbstractTest {

  @BeforeEach
  public void setUp() {
    super.setUp();
    compile("package a.b; component A { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // decomposed no mode
    "component Comp1 { " +
      "a.b.A a;" +
      "}",
    // decomposed with mode
    "component Comp2 { " +
      "a.b.A a;" +
      "mode automaton { } " +
      "}",
    // inner with mode
    "component Comp3 { " +
      "component Inner { " +
      "a.b.A a;" +
      "mode automaton { } " +
      "} " +
      "}",
    // two inner with modes
    "component Comp4 { " +
      "component Inner1 { " +
      "a.b.A a;" +
      "mode automaton { } " +
      "} " +
      "component Inner2 { " +
      "a.b.A a;" +
      "mode automaton { } " +
      "} " +
      "}",
    // decomposed component with automaton and mode
    "component Comp5 { " +
      "a.b.A a;" +
      "mode automaton { } " +
      "automaton { } " +
      "}",
    // atomic component with no mode
    "component Comp6 { }",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ModeAutomataInDecomposedComponent());

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
    checker.addCoCo(new ModeAutomataInDecomposedComponent());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // atomic with mode automaton
      arg("component Comp1 { " +
          "mode automaton { } " +
          "}",
        ModesError.MODE_AUTOMATON_IN_ATOMIC_COMPONENT),
      // atomic inner with mode automaton
      arg("component Comp2 { " +
          "component Inner { " +
          "mode automaton { } " +
          "} " +
          "}",
        ModesError.MODE_AUTOMATON_IN_ATOMIC_COMPONENT),
      // atomic with two mode automata
      arg("component Comp1 { " +
          "mode automaton { } " +
          "mode automaton { } " +
          "}",
        ModesError.MODE_AUTOMATON_IN_ATOMIC_COMPONENT, ModesError.MODE_AUTOMATON_IN_ATOMIC_COMPONENT)
    );
  }
}
