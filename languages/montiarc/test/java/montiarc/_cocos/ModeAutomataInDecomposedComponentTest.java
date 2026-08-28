/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.ModeAutomataInDecomposedComponent;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ModesError.MODE_AUTOMATON_IN_ATOMIC_COMPONENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ModeAutomataInDecomposedComponent}.
 */
class ModeAutomataInDecomposedComponentTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponent() {
    compile("package a.b; component A { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // decomposed component without a mode automaton
    "component ValidComp1 { a.b.A a; }",
    // decomposed component with a mode automaton
    "component ValidComp2 { a.b.A a; mode automaton { } }",
    // nested decomposed component type with a mode automaton
    "component ValidComp3 { component Inner { a.b.A a; mode automaton { } } }",
    // two nested decomposed component types, each with a mode automaton
    "component ValidComp4 { component Inner1 { a.b.A a; mode automaton { } } component Inner2 { a.b.A a; mode automaton { } } }",
    // decomposed component with both a mode automaton and a regular automaton
    "component ValidComp5 { a.b.A a; mode automaton { } automaton { } }",
    // atomic component without a mode automaton
    "component ValidComp6 { }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ModeAutomataInDecomposedComponent());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ModeAutomataInDecomposedComponent());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // atomic component with a mode automaton
      arg("component InvalidComp1 { mode automaton { } }",
        MODE_AUTOMATON_IN_ATOMIC_COMPONENT),
      // nested atomic component type with a mode automaton
      arg("component InvalidComp2 { component Inner { mode automaton { } } }",
        MODE_AUTOMATON_IN_ATOMIC_COMPONENT),
      // atomic component with two mode automata
      arg("component InvalidComp3 { mode automaton { } mode automaton { } }",
        MODE_AUTOMATON_IN_ATOMIC_COMPONENT, MODE_AUTOMATON_IN_ATOMIC_COMPONENT)
    );
  }
}
