/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.io.IOException;
import java.util.stream.Stream;

import static montiarc.util.ArcError.CONNECTOR_TYPE_MISMATCH;
import static montiarc.util.ArcError.IN_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.MISSING_SUBCOMPONENT;
import static montiarc.util.ArcError.OUT_PORT_UNUSED;
import static montiarc.util.ArcError.SOURCE_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.TARGET_DIRECTION_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

class ModeCoCosTest extends MontiArcTestBase {

  @BeforeEach
  void setUpComponents() {
    compile("package a.b; component A { port in int i; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port in int i1, i2; port out int o; }");
    compile("package a.b; component D { port out boolean o; }");
    compile("package a.b; component E { port in int i; port out int o; automaton { initial state S; S -> S i / { o = i; }}}");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic component, no variability
    "component ValidComp1 { }",
    // in port forward
    """
      component ValidComp2 {
        port in int i;
        a.b.A sub;
        i -> sub.i;
      }""",
    // out port forward
    """
      component ValidComp3 {
        port out int o;
        a.b.B sub;
        sub.o -> o;
      }""",
    // hidden channel
    """
      component ValidComp4 {
        a.b.A sub1;
        a.b.B sub2;
        sub2.o -> sub1.i;
      }""",
    // in port forward
    """
      component ValidComp5 {
        port in int i;
        mode automaton {
          initial mode M1 {
            a.b.A sub;
            i -> sub.i;
          }
        }
      }""",
    // in port forward and complex forwards and hidden channel
    """
      component ValidComp6 {
        port in int i;
        mode automaton {
          initial mode M1 {
            a.b.A sub;
            i -> sub.i;
          }
          mode M2 {
            a.b.B sub1;
            a.b.C sub2;
            a.b.A sub3;
            i -> sub2.i1;
            sub1.o -> sub2.i2;
            sub2.o -> sub3.i;
          }
        }
      }"""
  })
  void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = MontiArcCoCos.afterSymTab2(true);

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @DisableIfDisplayName(contains = {
    "InvalidComp7",
    "InvalidComp8",
    "InvalidComp9"
  })
  void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = MontiArcCoCos.afterSymTab2(true);

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // in port forward, source direction mismatch
      arg(
        """
          component InvalidComp1 {
            port out int o;
            a.b.A sub;
            o -> sub.i;
          }
          """, SOURCE_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch
      arg(
        """
          component InvalidComp2 {
            port in int i;
            a.b.B sub;
            i -> sub.o;
          }""", TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch
      arg(
        """
          component InvalidComp3 {
            port out int o;
            a.b.A sub;
            sub.i -> o;
          }""", SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch
      arg(
        """
          component InvalidComp4 {
            port in int i;
            a.b.B sub;
            sub.o -> i;
          }""", TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch
      arg(
        """
          component InvalidComp5 {
            a.b.A sub1, sub2;
            sub2.i -> sub1.i;
          }""", SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch
      arg(
        """
          component InvalidComp6 {
            a.b.B sub1, sub2;
            sub2.o -> sub1.o;
          }""", TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, connector type mismatch, in mode
      arg(
        """
          component InvalidComp7 {
            port out int o;
            mode automaton {
              initial mode M1 {
                a.b.D sub;
                sub.o -> o;
              }
            }
          }""", CONNECTOR_TYPE_MISMATCH
      ),
      // out port forward, connector source direction mismatch, in mode
      arg(
        """
          component InvalidComp8 {
            port out int o;
            mode automaton {
              initial mode M1 {
                a.b.A sub;
                sub.i -> o;
              }
            }
          }""", SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, connector missing subcomponent & port not connected
      arg(
        """
          component InvalidComp9 {
            port out int o;
            mode automaton {
              initial mode M1 {
                sub.i -> o;
              }
              mode M2 {
                a.b.A sub;
              }
            }
          }""",
        MISSING_SUBCOMPONENT,
        OUT_PORT_UNUSED,
        IN_PORT_NOT_CONNECTED
      )
    );
  }
}
