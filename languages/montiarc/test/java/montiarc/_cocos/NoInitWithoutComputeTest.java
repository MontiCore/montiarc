/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arccompute._cocos.NoInitWithoutCompute;
import de.se_rwth.commons.logging.Log;
import montiarc.ATestBase;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcComputeError;
import montiarc.util.Error;

import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NoInitWithoutCompute} CoCo:
 * Forbids 'init' blocks in components that also declare an automaton or that doesn't contain a compute block.
 */
public class NoInitWithoutComputeTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNotReportError(@NotNull String model) throws IOException {
    // parse & compile model into AST
    ASTMACompilationUnit ast = MontiArcTestBase.compile(model);

    // register CoCo and run checker
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoInitWithoutCompute());
    checker.checkAll(ast);

    // expect no findings
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error[] expectedErrors) throws IOException {
    ASTMACompilationUnit ast = MontiArcTestBase.compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoInitWithoutCompute());
    checker.checkAll(ast);

    // expect findings and exact error codes (order-insensitive)
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(ATestBase.getLoggedErrorCodes()).containsExactlyInAnyOrder(ATestBase.getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of("component comp1 {  init { int x = 1; } compute { } }"),
      Arguments.of("component comp2 { automaton { initial state Init; } }"),
      Arguments.of("component comp3 { compute { } }"),
      Arguments.of("component comp4 { }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      Arguments.of(
        "component comp5 { init { int x = 0; } automaton { initial state Init; } }",
        new Error[]{ ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE }
      ),
      Arguments.of(
        "component comp6 { component comp7 { init { int y = 2; } automaton { initial state S; } } }",
        new Error[]{ ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE }
      ),
      Arguments.of(
        "component comp8 { init { int y = 2; } }",
        new Error[]{ ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE }
      )
    );
  }
}
