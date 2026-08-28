/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arccompute._cocos.MaxOneInit;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ArcComputeError.MULTIPLE_INIT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MaxOneInit}.
 */
class MaxOneInitTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneInit());

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
    checker.addCoCo(new MaxOneInit());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // single init block followed by a compute block
      arg("""
        component ValidComp1 {
          init { int x = 1; }
          compute { }
        }
        """
      ),
      // no init block, only an automaton
      arg("""
        component ValidComp2 {
          automaton {
            initial state Init;
          }
        }
        """
      ),
      // no init block, only a compute block
      arg("""
        component ValidComp3 {
          compute { }
        }
        """
      ),
      // single init block declared after the compute block
      arg("""
        component ValidComp4 {
          compute { }
          init { int x = 1; }
        }
        """
      ),
      // component without any elements
      arg("component ValidComp5 { }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // two init blocks in the same component
      arg("""
        component InvalidComp1 {
          init { int x = 0; }
          init { }
        }
        """,
        MULTIPLE_INIT
      ),
      // outer component with a single init block, nested component type with two init blocks
      arg("""
        component InvalidComp2 {
          component Inner {
            init { int y = 2; }
            init { }
          }
          init { }
        }
        """,
        MULTIPLE_INIT
      ),
      // two init blocks separated by a compute block
      arg("""
        component InvalidComp3 {
          init { int y = 2; }
          compute { }
          init { }
        }
        """,
        MULTIPLE_INIT
      )
    );
  }
}
