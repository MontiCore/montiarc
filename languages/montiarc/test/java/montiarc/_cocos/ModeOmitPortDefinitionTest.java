/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.ModeOmitPortDefinition;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ModesError.MODE_CONTAINS_PORT_DEFINITION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ModeOmitPortDefinition}.
 */
class ModeOmitPortDefinitionTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // component without a mode automaton
    "component ValidComp1 { }",
    // empty mode automaton
    "component ValidComp2 { mode automaton { } }",
    // nested component type with a mode automaton containing one mode
    "component ValidComp3 { component Inner { mode automaton { mode m1 { } } } }",
    // two nested component types, each with a mode automaton
    "component ValidComp4 { component Inner1 { mode automaton { mode m1 { } } } component Inner2 { mode automaton { mode m1 { } } } }",
    // mode automaton with a mode, alongside a regular automaton
    "component ValidComp5 { mode automaton { mode m1 {} } automaton { } }",
    // two mode automata, each containing a mode
    "component ValidComp6 { mode automaton { mode m1 {} } mode automaton { mode m1 {} } }",
    // mode with a connector and a nested component type with its own port (not a direct port definition)
    "component ValidComp7 { mode automaton { mode m1 { port1 -> port2; } mode m2 { component A { port in int pIn; } A a; } } }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ModeOmitPortDefinition());

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
    checker.addCoCo(new ModeOmitPortDefinition());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // mode containing a direct port definition
      arg("component InvalidComp1 { mode automaton { mode m1 { port in int pIn; } } }",
        MODE_CONTAINS_PORT_DEFINITION),
      // two modes, each containing a direct port definition
      arg("component InvalidComp2 { mode automaton { mode m1 { port in int pIn; } mode m2 { port out double pOut; } } }",
        MODE_CONTAINS_PORT_DEFINITION, MODE_CONTAINS_PORT_DEFINITION)
    );
  }
}
