/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
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

import static montiarc.util.ArcError.SOURCE_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.TARGET_DIRECTION_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorDirectionsFit}.
 */
class ConnectorDirectionsFitTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUp() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; }");
    compile("package a.b; component C { port out int o; }");
    compile("package a.b; component D { port in int i; port out int o; }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorPortsExist());
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
    checker.addCoCo(new ConnectorPortsExist());
    checker.addCoCo(new ConnectorDirectionsFit());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
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
      // output forward
      arg("""
        component ValidComp3 {
          port out int o;
          a.b.C sub;
          sub.o -> o;
        }"""
      ),
      // hidden channel
      arg("""
        component ValidComp4 {
          a.b.B sub1;
          a.b.C sub2;
          sub2.o -> sub1.i;
        }"""
      ),
      // full composition
      arg("""
        component ValidComp5 {
          port in int i;
          port out int o;
          a.b.D sub1, sub2;
          i -> sub1.i;
          sub1.o -> sub2.i;
          sub2.o -> o;
        }"""
      ),
      // port forward, inner component
      arg("""
        component ValidComp6 {
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
      // input forward, multiple targets
      arg("""
        component ValidComp7 {
          port in int i;
          a.b.B sub1, sub2;
          i -> sub1.i, sub2.i;
        }"""
      ),
      // output forward, multiple targets
      arg("""
        component ValidComp8 {
          port out int o1, o2;
          a.b.C sub;
          sub.o -> o1, o2;
        }"""
      ),
      // message pass through
      arg("""
        component ValidComp9 {
          port in int i;
          port out int o;
          i -> o;
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // input forward, target direction mismatch, output port of subcomponent is not a valid target
      arg("""
          component InvalidComp1 {
            port in int i1;
            port in int i2;
            a.b.D sub;
            i1 -> sub.i;
            i2 -> sub.o;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // output forward, source direction mismatch, input port of subcomponent is not a valid source
      arg("""
          component InvalidComp2 {
            port out int o1;
            port out int o2;
            a.b.D sub;
            sub.i -> o1;
            sub.o -> o2;
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // message pass through, target direction mismatch, input port of component is not a valid target
      arg("""
          component InvalidComp3 {
            port in int i1, i2;
            i1 -> i2;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // message pass through, source direction mismatch, output port of component is not a valid source
      arg("""
          component InvalidComp4 {
            port out int o1, o2;
            o1 -> o2;
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // message pass through, target direction mismatch, multiple targets, first position
      arg("""
          component InvalidComp5 {
            port in int i1, i2, out int o;
            i1 -> i2, o;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // message pass through, target direction mismatch, multiple targets, second position
      arg("""
          component InvalidComp6 {
            port in int i1, i2, out int o;
            i1 -> o, i2;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // message pass through, multiple target direction mismatch, multiple targets
      arg("""
          component InvalidComp7 {
            port in int i1, i2, i3;
            i1 -> i2, i3;
          }""",
        TARGET_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH
      ),
      // message pass through, multiple target direction mismatch, multiple targets, multiple connectors
      arg("""
          component InvalidComp8 {
            port in int i1, i2, i3, i4;
            i1 -> i3;
            i2 -> i4;
          }""",
        TARGET_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch
      arg("""
          component InvalidComp9 {
            a.b.B sub1, sub2;
            sub1.i -> sub2.i;
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch
      arg("""
          component InvalidComp10 {
            a.b.C sub1, sub2;
            sub1.o -> sub2.o;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source and target direction mismatch
      arg("""
          component InvalidComp11 {
            a.b.B sub1;
            a.b.C sub2;
            sub1.i -> sub2.o;
          }""",
        SOURCE_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel and message forward, all direction mismatch
      arg("""
          component InvalidComp12 {
            port in int i;
            port out int o;
            a.b.D sub1, sub2;
            o -> sub1.o;
            sub1.i -> sub2.o;
            sub2.i -> i;
          }""",
        SOURCE_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH,
        SOURCE_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH,
        SOURCE_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, inner component, source and target direction mismatch
      arg("""
          component InvalidComp13 {
            port in int i;
            port out int o;
            component Inner {
              port in int i;
              port out int o;
            }
            Inner sub;
            i -> sub.o;
            o -> sub.i;
          }""",
        SOURCE_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH
      )
    );
  }
}
