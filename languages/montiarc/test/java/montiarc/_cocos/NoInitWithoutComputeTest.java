/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arccompute._cocos.NoInitWithoutCompute;
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

import static montiarc.util.ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoInitWithoutCompute}.
 */
class NoInitWithoutComputeTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoInitWithoutCompute());

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
    checker.addCoCo(new NoInitWithoutCompute());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // init block alongside a compute block
      arg("""
        component ValidComp1 {
          init { int x = 1; }
          compute { }
        }
        """
      ),
      // automaton and no init block
      arg("""
        component ValidComp2 {
          automaton {
            initial state Init;
          }
        }
        """
      ),
      // compute block and no init block
      arg("""
        component ValidComp3 {
          compute { }
        }
        """
      ),
      // component without any elements
      arg("component ValidComp4 { }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // init block without a compute block, alongside an automaton
      arg("""
        component InvalidComp1 {
          init { int x = 0; }
          automaton {
            initial state Init;
          }
        }
        """,
        INIT_BLOCK_WITHOUT_COMPUTE
      ),
      // nested component type with an init block without a compute block
      arg("""
        component InvalidComp2 {
          component Inner {
            init { int y = 2; }
            automaton {
              initial state S;
            }
          }
        }
        """,
        INIT_BLOCK_WITHOUT_COMPUTE
      ),
      // init block without a compute block, and no other elements
      arg("""
        component InvalidComp3 {
          init { int y = 2; }
        }
        """,
        INIT_BLOCK_WITHOUT_COMPUTE
      )
    );
  }
}
