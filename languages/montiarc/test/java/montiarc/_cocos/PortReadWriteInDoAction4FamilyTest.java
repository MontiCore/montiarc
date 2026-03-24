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

import static montiarc.util.ArcError.READ_FROM_OUTGOING_PORT;
import static montiarc.util.ArcError.WRITE_TO_INCOMING_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortReadWriteInDoAction4Family}.
 */
class PortReadWriteInDoAction4FamilyTest extends PortReadWriteInDoAction4MontiArcTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability1 ",
    "ValidCompWithVariability2 ",
    "ValidCompWithVariability3 ",
    "ValidCompWithVariability4 ",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInDoAction4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability5 ",
    "InvalidCompWithVariability6 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInDoAction4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // write to in input in do action, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            automaton {
              state S {
                do / { i = 0; }
              }
            }
          }
        }
        """
      ),
      // read from output in do action, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          port out int o;
          varif (false) {
            automaton {
              state S {
                do / { int v = o; }
              }
            }
          }
        }
        """
      ),
      // write to in input in do action, dead feature
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          port in int i;
          varif (f) {
            automaton {
              state S {
                do / { i = 0; }
              }
            }
          }
          constraint (!f);
        }
        """
      ),
      // read from output in do action, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          port out int o;
          varif (f) {
            automaton {
              state S {
                do / { int v = o; }
              }
            }
          }
          constraint (!f);
        }
        """
      ),
      // write to conditional input in do action
      arg("""
        component ValidCompWithVariability5 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int io;
          } else {
            port out int io;
          }
          varif (f2) {
            automaton {
              state S {
                do / { io = 0; }
              }
            }
          }
          constraint (!(f1 && f2));
        }
        """
      ),
      // read from conditional output in do action
      arg("""
        component ValidCompWithVariability6 {
          feature f1;
          feature f2;
          varif (f1) {
            port out int io;
          } else {
            port in int io;
          }
          varif (f2) {
            automaton {
              state S {
                do / { int v = io; }
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
      // write to in input in do action, tautological variation point
      arg("""
        component InvalidCompWithVariability1 {
          port in int i;
          varif (true) {
            automaton {
              state S {
                do / { i = 0; }
              }
            }
          }
        }
        """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output in do action, tautological variation point
      arg("""
        component InvalidCompWithVariability2 {
          port out int o;
          varif (true) {
            automaton {
              state S {
                do / { int v = o; }
              }
            }
          }
        }
        """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to in input in do action, core feature
      arg("""
        component InvalidCompWithVariability3 {
          feature f;
          port in int i;
          varif (f) {
            automaton {
              state S {
                do / { i = 0; }
              }
            }
          }
          constraint (f);
        }
        """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output in do action, core feature
      arg("""
        component InvalidCompWithVariability4 {
          feature f;
          port out int o;
          varif (f) {
            automaton {
              state S {
                do / { int v = o; }
              }
            }
          }
          constraint (f);
        }
        """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to conditional input in do action
      arg("""
        component InvalidCompWithVariability5 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int io;
          } else {
            port out int io;
          }
          varif (f2) {
            automaton {
              state S {
                do / { io = 0; }
              }
            }
          }
        }
        """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from conditional output in do action
      arg("""
        component InvalidCompWithVariability6 {
          feature f1;
          feature f2;
          varif (f1) {
            port out int io;
          } else {
            port in int io;
          }
          varif (f2) {
            automaton {
              state S {
                do / { int v = io; }
              }
            }
          }
        }
        """,
        READ_FROM_OUTGOING_PORT
      )
    );
  }
}
