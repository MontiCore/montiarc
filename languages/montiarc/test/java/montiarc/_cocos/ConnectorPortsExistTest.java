/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

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

import static montiarc.util.ArcError.MISSING_PORT;
import static montiarc.util.ArcError.MISSING_SUBCOMPONENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorPortsExist}.
 */
class ConnectorPortsExistTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUp() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; }");
    compile("package a.b; component C { port out int o; }");
    compile("package a.b; component D { port in int i; port out int o; }");
    compile("package a.b; component E { port in int i1, i2; port out int o; }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorPortsExist());

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
      // port forward, inner component
      arg("""
        component ValidComp5 {
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
      // output forward, multiple targets (component)
      arg("""
        component ValidComp6 {
          port out int o1, o2;
          a.b.C sub;
          sub.o -> o1, o2;
        }"""
      ),
      // input forward, multiple targets (subcomponents)
      arg("""
        component ValidComp7 {
          port in int i;
          a.b.B sub1, sub2;
          i -> sub1.i, sub2.i;
        }"""
      ),
      // input forward, multiple targets (subcomponent ports)
      arg("""
        component ValidComp8 {
          port in int i;
          a.b.E sub;
          i -> sub.i1, sub.i2;
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // input forward, missing source
      arg("""
          component InvalidComp1 {
            a.b.B sub;
            i -> sub.i;
          }""",
        MISSING_PORT
      ),
      // input forward, missing target (port)
      arg("""
          component InvalidComp2 {
            port in int i;
            a.b.A sub;
            i -> sub.i;
          }""",
        MISSING_PORT
      ),
      // input forward, missing target (subcomponent)
      arg("""
          component InvalidComp3 {
            port in int i;
            i -> sub.i;
          }""",
        MISSING_SUBCOMPONENT
      ),
      // output forward, missing target
      arg("""
          component InvalidComp4 {
            a.b.C sub;
            sub.o -> o;
          }""",
        MISSING_PORT
      ),
      // output forward, missing source (port)
      arg("""
          component InvalidComp5 {
            port out int o;
            a.b.A sub;
            sub.o -> o;
          }""",
        MISSING_PORT
      ),
      // output forward, missing source (subcomponent)
      arg("""
          component InvalidComp6 {
            port out int o;
            sub.o -> o;
          }""",
        MISSING_SUBCOMPONENT
      ),
      // hidden channel, missing source
      arg("""
          component InvalidComp7 {
            a.b.A sub1;
            a.b.C sub2;
            sub2.o -> sub1.i;
          }""",
        MISSING_PORT
      ),
      // hidden channel, missing target
      arg("""
          component InvalidComp8 {
            a.b.B sub1;
            a.b.A sub2;
            sub2.o -> sub1.i;
          }""",
        MISSING_PORT
      ),
      // input forward, multiple sources missing
      arg("""
          component InvalidComp9 {
            a.b.B sub1, sub2;
            i1 -> sub1.i;
            i2 -> sub2.i;
          }""",
        MISSING_PORT, MISSING_PORT
      ),
      // input forward, multiple targets missing (subcomponents)
      arg("""
          component InvalidComp10 {
            port in int i;
            i -> sub1.i, sub2.i;
          }""",
        MISSING_SUBCOMPONENT, MISSING_SUBCOMPONENT
      ),
      // input forward, multiple targets missing (ports)
      arg("""
          component InvalidComp11 {
            port in int i;
            a.b.A sub;
            i -> sub.i1, sub.i2;
          }""",
        MISSING_PORT, MISSING_PORT
      ),
      // output forward, multiple targets missing
      arg("""
          component InvalidComp12 {
            a.b.C sub;
            sub.o -> o1, o2;
          }""",
        MISSING_PORT, MISSING_PORT
      )
    );
  }
}
