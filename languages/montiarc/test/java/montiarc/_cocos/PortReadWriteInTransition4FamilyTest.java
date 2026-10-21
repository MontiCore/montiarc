/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.READ_FROM_OUTGOING_PORT;
import static montiarc.util.ArcError.WRITE_TO_INCOMING_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortReadWriteInTransition4Family}.
 */
class PortReadWriteInTransition4FamilyTest extends PortReadWriteInTransition4MontiArcTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInTransition4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @Override
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @Override
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // write to in input in transition action, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            automaton {
              state S;
              S -> S / { i = 0; }
            }
          }
        }
        """
      ),
      // read from output in transition action, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          port out int o;
          varif (false) {
            automaton {
              state S;
              S -> S / { int v = o; }
            }
          }
        }
        """
      ),
      // write to in input in transition action, dead feature
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          port in int i;
          varif (f) {
            automaton {
              state S;
              S -> S / { i = 0; }
            }
          }
          constraint (!f);
        }
        """
      ),
      // read from output in transition action, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          port out int o;
          varif (f) {
            automaton {
              state S;
              S -> S / { int v = o; }
            }
          }
          constraint (!f);
        }
        """
      ),
      // write to conditional input in transition action
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
              state S;
              S -> S / { io = 0; }
            }
          }
          constraint (!(f1 && f2));
        }
        """
      ),
      // read from conditional output in transition action
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
              state S;
              S -> S / { int v = io; }
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
      // write to in input in transition action, tautological variation point
      arg("""
        component InvalidCompWithVariability1 {
          port in int i;
          varif (true) {
            automaton {
              state S;
              S -> S / { i = 0; }
            }
          }
        }
        """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output in transition action, tautological variation point
      arg("""
        component InvalidCompWithVariability2 {
          port out int o;
          varif (true) {
            automaton {
              state S;
              S -> S / { int v = o; }
            }
          }
        }
        """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to in input in transition action, core feature
      arg("""
        component InvalidCompWithVariability3 {
          feature f;
          port in int i;
          varif (f) {
            automaton {
              state S;
              S -> S / { i = 0; }
            }
          }
          constraint (f);
        }
        """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output in transition action, core feature
      arg("""
        component InvalidCompWithVariability4 {
          feature f;
          port out int o;
          varif (f) {
            automaton {
              state S;
              S -> S / { int v = o; }
            }
          }
          constraint (f);
        }
        """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to conditional input in transition action
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
              state S;
              S -> S / { io = 0; }
            }
          }
        }
        """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from conditional output in transition action
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
              state S;
              S -> S / { int v = io; }
            }
          }
        }
        """,
        READ_FROM_OUTGOING_PORT
      )
    );
  }
}
