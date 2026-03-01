/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorTimingsFit;
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

import java.util.stream.Stream;

import static montiarc.util.ArcError.CONNECTOR_TIMING_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorTimingsFit}.
 */
class ConnectorTimingsFitTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUp() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i, out int o; }");
    compile("package a.b; component C { port sync in int i, sync out int o; }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorTimingsFit());
    checker.addCoCo(new ConnectorDirectionsFit());

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
    checker.addCoCo(new ConnectorTimingsFit());
    checker.addCoCo(new ConnectorDirectionsFit());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // decomposed component, no ports or connectors
      arg("""
        component ValidComp1 {
          a.b.A sub;
        }"""
      ),
      // input forward
      arg("""
        component ValidComp2 {
          port in int i;
          a.b.B sub;
          i -> sub.i;
        }"""
      ),
      // sync input forward
      arg("""
        component ValidComp3 {
          port sync in int i;
          a.b.C sub;
          i -> sub.i;
        }"""
      ),
      // sync (to event) input forward
      arg("""
        component ValidComp4 {
          port sync in int i;
          a.b.B sub;
          i -> sub.i;
        }"""
      ),
      // output forward
      arg("""
        component ValidComp5 {
          port out int o;
          a.b.B sub;
          sub.o -> o;
        }"""
      ),
      // sync output forward
      arg("""
        component ValidComp6 {
          port sync out int o;
          a.b.C sub;
          sub.o -> o;
        }"""
      ),
      // sync (to event) output forward
      arg("""
        component ValidComp7 {
          port out int o;
          a.b.C sub;
          sub.o -> o;
        }"""
      ),
      // hidden channel
      arg("""
        component ValidComp8 {
          a.b.B sub1;
          a.b.B sub2;
          sub1.o -> sub2.i;
        }"""
      ),
      // sync hidden channel
      arg("""
        component ValidComp9 {
          a.b.C sub1;
          a.b.C sub2;
          sub1.o -> sub2.i;
        }"""
      ),
      // sync (to event) hidden channel
      arg("""
        component ValidComp9 {
          a.b.C sub1;
          a.b.B sub2;
          sub1.o -> sub2.i;
        }"""
      ),
      // port forward, inner component
      arg("""
        component ValidComp10 {
          port in int i;
          port out int o;
          component Inner {
            port in int i;
            port out int o;
          }
          Inner sub;
          i -> sub.i;
          sub.o -> o;
        }"""
      ),
      // sync port forward, inner component
      arg("""
        component ValidComp11 {
          port sync in int i;
          port sync out int o;
          component Inner {
            port sync in int i;
            port sync out int o;
          }
          Inner sub;
          i -> sub.i;
          sub.o -> o;
        }"""
      ),
      // message pass through
      arg("""
        component ValidComp12 {
          port in int i;
          port out int o;
          i -> o;
        }"""
      ),
      // sync message pass through
      arg("""
        component ValidComp13 {
          port sync in int i;
          port sync out int o;
          i -> o;
        }"""
      ),
      // sync (to event) message pass through
      arg("""
        component ValidComp14 {
          port sync in int i;
          port out int o;
          i -> o;
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // input forward, timing mismatch
      arg("""
          component InvalidComp1 {
            port in int i;
            a.b.C sub;
            i -> sub.i;
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // output forward, timing mismatch
      arg("""
          component InvalidComp2 {
            port sync out int o;
            a.b.B sub;
            sub.o -> o;
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // hidden channel, timing mismatch
      arg("""
          component InvalidComp3 {
            a.b.B sub1;
            a.b.C sub2;
            sub1.o -> sub2.i;
          }
          """,
        CONNECTOR_TIMING_MISMATCH
      ),
      // input forward, timing mismatch, multiple targets
      arg("""
          component InvalidComp4 {
            port in int i;
            a.b.B sub1;
            a.b.C sub2;
            i -> sub1.i, sub2.i;
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // input forward, multiple timing mismatch, multiple targets
      arg("""
          component InvalidComp5 {
            port in int i;
            a.b.C sub1;
            a.b.C sub2;
            i -> sub1.i, sub2.i;
          }""",
        CONNECTOR_TIMING_MISMATCH, CONNECTOR_TIMING_MISMATCH
      ),
      // output forward, timing mismatch, multiple targets
      arg("""
          component InvalidComp6 {
            port out int o1, sync out int o2;
            a.b.B sub;
            sub.o -> o1, o2;
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // output forward, multiple timing mismatch, multiple targets
      arg("""
          component InvalidComp6 {
            port sync out int o1, sync out int o2;
            a.b.B sub;
            sub.o -> o1, o2;
          }""",
        CONNECTOR_TIMING_MISMATCH, CONNECTOR_TIMING_MISMATCH
      )
    );
  }
}
