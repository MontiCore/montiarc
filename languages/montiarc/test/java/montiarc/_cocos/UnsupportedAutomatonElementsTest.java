/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.UNSUPPORTED_MODEL_ELEMENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link UnsupportedAutomatonElements}.
 */
class UnsupportedAutomatonElementsTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component ValidComp1 { }",
    // automaton without states
    "component ValidComp2 { automaton { } }",
    // automaton without a final state
    "component ValidComp3 { automaton { initial state s1; state s2; state s3; } }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());

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
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // one final state
      arg("component InvalidComp1 { automaton { final state s; } }",
        UNSUPPORTED_MODEL_ELEMENT),
      // multiple states, one final state
      arg("component InvalidComp2 { automaton { initial state s1; state s2; state s3; final state s4; } }",
        UNSUPPORTED_MODEL_ELEMENT),
      // multiple final states
      arg("component InvalidComp3 { automaton { final state s1; final state s2; } }",
        UNSUPPORTED_MODEL_ELEMENT,
        UNSUPPORTED_MODEL_ELEMENT)
    );
  }
}
