/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.TransitionSourceTargetExists;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.SCError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test ist {@link TransitionSourceTargetExists}
 */
public class TransitionSourceTargetExistsTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component Comp1 { }",
    // no transition
    "component Comp2 { " +
      "automaton { } " +
      "}",
    // self-loop
    "component Comp3 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s; " +
      "} " +
      "}",
    // single transition
    "component Comp4 { " +
      "automaton { " +
      "initial state s1; " +
      "state s2; " +
      "s1 -> s2; " +
      "} " +
      "}",
    // multiple transitions
    "component Comp5 { " +
      "automaton { " +
      "initial state s1; " +
      "state s2; " +
      "state s3; " +
      "s1 -> s2; " +
      "s2 -> s3; " +
      "s3 -> s1; " +
      "} " +
      "}",
    // single transition before state declaration
    "component Comp6 { " +
      "automaton { " +
      "s1 -> s2; " +
      "initial state s1; " +
      "state s2; " +
      "} " +
      "}",
    // multiple transitions before state declaration
    "component Comp7 { " +
      "automaton { " +
      "s1 -> s2; " +
      "s2 -> s3; " +
      "s3 -> s1; " +
      "initial state s1; " +
      "state s2; " +
      "state s3; " +
      "} " +
      "}",
    // single transition in hierarchical state
    "component Comp8 { " +
      "automaton { " +
      "initial state s1 { " +
      "state s2; " +
      "state s3; " +
      "s2 -> s3; " +
      "}; " +
      "} " +
      "}",
    // single transition up hierarchy
    "component Comp9 { " +
      "automaton { " +
      "initial state s1 { " +
      "state s2; " +
      "}; " +
      "s1 -> s2; " +
      "} " +
      "}",
    // single transition down hierarchy
    "component Comp10 { " +
      "automaton { " +
      "initial state s1 { " +
      "state s2; " +
      "}; " +
      "s2 -> s1; " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TransitionSourceTargetExists());

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
    checker.addCoCo(new TransitionSourceTargetExists());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // single transition missing source state
      arg("component Comp1 { " +
          "automaton { " +
          "state s2; " +
          "s1 -> s2; " +
          "} " +
          "}",
        SCError.MISSING_SOURCE_STATE),
      // single transition missing target state
      arg("component Comp2 { " +
        "automaton { " +
        "initial state s1; " +
        "s1 -> s2; " +
        "} " +
        "}",
        SCError.MISSING_TARGET_STATE),
      // single transition missing source and target states
      arg("component Comp3 { " +
          "automaton { " +
          "s1 -> s2; " +
          "} " +
          "}",
        SCError.MISSING_SOURCE_STATE,
        SCError.MISSING_TARGET_STATE),
      // single-loop missing state
      arg("component Comp4 { " +
          "automaton { " +
          "s -> s; " +
          "} " +
          "}",
        SCError.MISSING_SOURCE_STATE,
        SCError.MISSING_TARGET_STATE),
      // multiple transitions missing source state
      arg("component Comp5 { " +
          "automaton { " +
          "state s2; " +
          "s1 -> s2; " +
          "s1 -> s2; " +
          "} " +
          "}",
        SCError.MISSING_SOURCE_STATE,
        SCError.MISSING_SOURCE_STATE),
      // multiple transitions missing target state
      arg("component Comp6 { " +
          "automaton { " +
          "initial state s1; " +
          "s1 -> s2; " +
          "s1 -> s2; " +
          "} " +
          "}",
        SCError.MISSING_TARGET_STATE,
        SCError.MISSING_TARGET_STATE)
    );
  }
}

