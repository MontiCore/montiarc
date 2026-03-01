/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;
import variablearc._cocos.ConnectorPortsExist4Family;
import variablearc._cocos.ConnectorTimingsFit4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.CONNECTOR_TIMING_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorTimingsFit4Family}.
 */
class ConnectorTimingsFit4FamilyTest extends ConnectorTimingsFitTest {

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
    compile("package a.b; component E { feature ff; varif (ff) { port in int i; } else { port sync in int i; } }");
    compile("package a.b; component F { feature ff; varif (ff) { port out int o; } else { port sync out int o; } }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability7 ",
    "ValidCompWithVariability10 "
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorTimingsFit4Family());
    checker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest

  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability8 ",
    "InvalidCompWithVariability9 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorTimingsFit4Family());
    checker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // input forward, timing mismatch, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            i -> sub.i;
          }
          a.b.C sub;
        }"""
      ),
      // output forward, timing mismatch, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          port sync out int o;
          varif (false) {
            sub.o -> o;
          }
          a.b.B sub;
        }"""
      ),
      // hidden channel, timing mismatch, dead variation point
      arg("""
        component ValidCompWithVariability3 {
          varif (false) {
            sub1.o -> sub2.i;
          }
          a.b.B sub1;
          a.b.C sub2;
        }
        """
      ),
      // input forward, timing mismatch, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          port in int i;
          varif (f) {
            i -> sub.i;
          }
          a.b.C sub;
          constraint (!f);
        }"""
      ),
      // output forward, timing mismatch, dead feature
      arg("""
        component ValidCompWithVariability5 {
          feature f;
          port sync out int o;
          varif (f) {
            sub.o -> o;
          }
          a.b.B sub;
          constraint (!f);
        }"""
      ),
      // hidden channel, timing mismatch, dead feature
      arg("""
        component ValidCompWithVariability6 {
          feature f;
          varif (f) {
            sub1.o -> sub2.i;
          }
          a.b.B sub1;
          a.b.C sub2;
          constraint (!f);
        }
        """
      ),
      // input forward, subcomponent interface with variable timing
      arg("""
        component ValidCompWithVariability7 {
          port in int i;
          a.b.E sub;
          i -> sub.i;
          constraint (sub.ff);
        }"""
      ),
      // output forward, subcomponent interface with variable timing
      arg("""
        component ValidCompWithVariability8 {
          port sync out int o;
          a.b.F sub;
          sub.o -> o;
          constraint (!sub.ff);
        }"""
      ),
      // hidden channel, subcomponent interfaces with variable timing
      arg("""
        component ValidCompWithVariability9 {
          a.b.F sub1;
          a.b.E sub2;
          sub1.o -> sub2.i;
          constraint (sub1.ff == sub2.ff);
        }"""
      ),
      // input forward, component interface with variable timing
      arg("""
        component ValidCompWithVariability10 {
          feature f;
          varif (f) {
            port in int i;
          } else {
            port sync in int i;
          }
          a.b.C sub;
          i -> sub.i;
          constraint (!f);
        }"""
      ),
      // output forward, component interface with variable timing
      arg("""
        component ValidCompWithVariability11 {
          feature f;
          varif (f) {
            port out int o;
          } else {
            port sync out int o;
          }
          a.b.B sub;
          sub.o -> o;
          constraint (f);
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // input forward, timing mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            port in int i;
            varif (true) {
              i -> sub.i;
            }
            a.b.C sub;
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // output forward, timing mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            port sync out int o;
            varif (true) {
              sub.o -> o;
            }
            a.b.B sub;
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // hidden channel, timing mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability3 {
            varif (true) {
              sub1.o -> sub2.i;
            }
            a.b.B sub1;
            a.b.C sub2;
          }
          """,
        CONNECTOR_TIMING_MISMATCH
      ),
      // input forward, timing mismatch, core feature
      arg("""
          component InvalidCompWithVariability4 {
            feature f;
            port in int i;
            varif (f) {
              i -> sub.i;
            }
            a.b.C sub;
            constraint (f);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // output forward, timing mismatch, core feature
      arg("""
          component InvalidCompWithVariability5 {
            feature f;
            port sync out int o;
            varif (f) {
              sub.o -> o;
            }
            a.b.B sub;
            constraint (f);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // hidden channel, timing mismatch, core feature
      arg("""
          component InvalidCompWithVariability6 {
            feature f;
            varif (f) {
              sub1.o -> sub2.i;
            }
            a.b.B sub1;
            a.b.C sub2;
            constraint (f);
          }
          """,
        CONNECTOR_TIMING_MISMATCH
      ),
      // input forward, subcomponent interface with variable timing
      arg("""
          component InvalidCompWithVariability7 {
            port in int i;
            a.b.E sub;
            i -> sub.i;
            constraint (!sub.ff);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // output forward, subcomponent interface with variable timing
      arg("""
          component InvalidCompWithVariability8 {
            port sync out int o;
            a.b.F sub;
            sub.o -> o;
            constraint (sub.ff);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // hidden channel, subcomponent interfaces with variable timing
      arg("""
          component InvalidCompWithVariability9 {
            a.b.F sub1;
            a.b.E sub2;
            sub1.o -> sub2.i;
            constraint (sub1.ff != sub2.ff);
          }""",
        CONNECTOR_TIMING_MISMATCH
      )
    );
  }
}
