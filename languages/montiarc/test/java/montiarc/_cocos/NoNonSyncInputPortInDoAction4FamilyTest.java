/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.util.stream.Stream;

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoNonSyncInputPortInDoAction4Family}.
 */
class NoNonSyncInputPortInDoAction4FamilyTest extends NoNonSyncInputPortInDoActionTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability1 ",
    "ValidCompWithVariability2 "
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoNonSyncInputPortInDoAction4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability3 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoNonSyncInputPortInDoAction4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // read value from non-synchronous input port in do action, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            automaton {
              initial state S {
                do / { int x = i; }
              }
            }
          }
        }
        """
      ),
      // read value from non-synchronous input port in do action, dead feature
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          port in int i;
          varif (f) {
            automaton {
              initial state S {
                do / { int x = i; }
              }
            }
          }
          constraint (!f);
        }
        """
      ),
      // read value from conditional non-synchronous input port in do action
      arg("""
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int i;
          } else {
            port sync in int i;
          }
          varif (f2) {
            automaton {
              initial state S {
                do / { int x = i; }
              }
            }
          }
          constraint (!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // read value from non-synchronous input port in do action, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            port in int i;
            varif (true) {
              automaton {
                initial state S {
                  do / { int x = i; }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from non-synchronous input port in do action, core feature
      arg("""
          component InvalidCompWithVariability2 {
            feature f;
            port in int i;
            varif (f) {
              automaton {
                initial state S {
                  do / { int x = i; }
                }
              }
            }
            constraint (f);
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from conditional non-synchronous input port in do action
      arg("""
          component InvalidCompWithVariability3 {
            feature f1;
            feature f2;
            varif (f1) {
              port in int i;
            } else {
              port sync in int i;
            }
            varif (f2) {
              automaton {
                initial state S {
                  do / { int x = i; }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      )
    );
  }
}
