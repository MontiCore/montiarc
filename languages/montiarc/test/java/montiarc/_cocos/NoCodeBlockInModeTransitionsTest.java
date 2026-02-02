/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.se_rwth.commons.logging.Log;
import modes._cocos.NoCodeBlockInModeTransitions;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NoCodeBlockInModeTransitions}
 */
public class NoCodeBlockInModeTransitionsTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNotReportError(@NotNull String model) throws IOException {
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoCodeBlockInModeTransitions());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error[] expectedErrors) throws IOException {
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoCodeBlockInModeTransitions());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of(
        "component Comp1 { mode automaton { initial mode A { } mode B { } A -> B; } }"
      ),
      Arguments.of(
        "component Comp2 { mode automaton { initial mode Init { } mode Ready { } Init -> Ready; } }"
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      Arguments.of(
        "component Comp3 { port out int x; mode automaton { initial mode A { } mode B { } A -> B / { x = 1; } } }",
        new Error[]{ModesError.MODE_AUTOMATON_TRANSITION_CONTAINS_ACTION}
      ),
      Arguments.of(
        "component Comp4 { mode automaton { initial mode A { } mode B { } A -> B / { } } }",
        new Error[]{ModesError.MODE_AUTOMATON_TRANSITION_CONTAINS_ACTION}
      )
    );
  }
}
