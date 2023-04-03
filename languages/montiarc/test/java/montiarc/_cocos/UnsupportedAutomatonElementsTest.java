/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link UnsupportedAutomatonElements}.
 */
public class UnsupportedAutomatonElementsTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component Comp1 { }",
    // no states
    "component Comp2 {" +
      "automaton { } " +
      "}",
    // no final state
    "component Comp3 { " +
      "automaton { " +
      "initial state s1; " +
      "state s2; " +
      "state s3; " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());

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
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // one final state
      arg("component Comp1 { " +
          "automaton { " +
          "final state s; " +
          "} " +
          "}",
        ArcError.UNSUPPORTED_MODEL_ELEMENT),
      // multiple states, one final state
      arg("component Comp2 { " +
          "automaton { " +
          "initial state s1; " +
          "state s2; " +
          "state s3; " +
          "final state s4; " +
          "} " +
          "}",
        ArcError.UNSUPPORTED_MODEL_ELEMENT),
      // multiple final states
      arg("component Comp3 { " +
          "automaton { " +
          "final state s1; " +
          "final state s2; " +
          "} " +
          "}",
        ArcError.UNSUPPORTED_MODEL_ELEMENT,
        ArcError.UNSUPPORTED_MODEL_ELEMENT)
    );
  }
}
