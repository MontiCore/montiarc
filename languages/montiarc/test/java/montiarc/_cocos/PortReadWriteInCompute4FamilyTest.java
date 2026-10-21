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
 * The class under test is {@link PortReadWriteInCompute4Family}.
 */
class PortReadWriteInCompute4FamilyTest extends PortReadWriteInCompute4MontiArcTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInCompute4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
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
      // write to local variable that hides an input port of the same name
      arg("""
        component ValidCompWithVariability7 {
          feature f;
          varif (f) {
            port in int x;
          }
          compute {
            int x = 0;
            x = 1;
          }
        }
        """
      ),
      // write to in input in compute block, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            compute {
              i = 0;
            }
          }
        }
        """
      ),
      // read from output in compute block, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          port out int o;
          varif (false) {
            compute {
              int v = o;
            }
          }
        }
        """
      ),
      // write to in input in compute block, dead feature
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          port in int i;
          varif (f) {
            compute {
              i = 0;
            }
          }
          constraint (!f);
        }
        """
      ),
      // read from output in compute block, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          port out int o;
          varif (f) {
            compute {
              int v = o;
            }
          }
          constraint (!f);
        }
        """
      ),
      // write to conditional input in compute block
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
            compute {
              io = 0;
            }
          }
          constraint (!(f1 && f2));
        }
        """
      ),
      // read from conditional output in compute block
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
            compute {
              int v = io;
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
      // write to input that is a field in another configuration
      arg("""
          component InvalidCompWithVariability7 {
            feature f;
            varif (f) {
              int x = 0;
            } else {
              port in int x;
            }
            compute {
              x = 1;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output that is a field in another configuration
      arg("""
          component InvalidCompWithVariability8 {
            feature f;
            varif (f) {
              int x = 0;
            } else {
              port out int x;
            }
            compute {
              int v = x;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to in input in compute block, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            port in int i;
            varif (true) {
              compute {
                i = 0;
              }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output in compute block, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            port out int o;
            varif (true) {
              compute {
                int v = o;
              }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to in input in compute block, core feature
      arg("""
          component InvalidCompWithVariability3 {
            feature f;
            port in int i;
            varif (f) {
              compute {
                i = 0;
              }
            }
            constraint (f);
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output in compute block, core feature
      arg("""
          component InvalidCompWithVariability4 {
            feature f;
            port out int o;
            varif (f) {
              compute {
                int v = o;
              }
            }
            constraint (f);
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to conditional input in compute block
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
              compute {
                io = 0;
              }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from conditional output in compute block
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
              compute {
                int v = io;
              }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      )
    );
  }
}
