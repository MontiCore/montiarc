/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;
import variablearc._cocos.PortsConnected4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.IN_PORT_UNUSED;
import static montiarc.util.ArcError.OUT_PORT_UNUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * The class under test is {@link PortsConnected4Family}.
 */
class PortsConnected4FamilyTest extends PortsConnectedTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability7",
    "ValidCompWithVariability8",
    "ValidCompWithVariability9"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortsConnected4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability1 ",
    "InvalidCompWithVariability2 ",
    "InvalidCompWithVariability3 ",
    "InvalidCompWithVariability4 ",
    "InvalidCompWithVariability5 ",
    "InvalidCompWithVariability6 ",
    "InvalidCompWithVariability10 ",
    "InvalidCompWithVariability11 ",
    "InvalidCompWithVariability12 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortsConnected4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // unconnected input port, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          varif (false) {
            port in int i;
          }
          a.b.A sub;
        }
        """
      ),
      // unconnected output port, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          varif (false) {
            port out int o;
          }
          a.b.A sub;
        }
        """
      ),
      // unconnected input and output port, dead variation point
      arg("""
        component ValidCompWithVariability3 {
          varif (false) {
            port in int i;
            port out int o;
          }
          a.b.A sub;
        }
        """
      ),
      // unconnected input port, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          varif (f) {
            port in int i;
          }
          a.b.A sub;
          constraint (!f);
        }
        """
      ),
      // unconnected output port, dead feature
      arg("""
        component ValidCompWithVariability5 {
          feature f;
          varif (f) {
            port out int o;
          }
          a.b.A sub;
          constraint (!f);
        }
        """
      ),
      // unconnected input and output port, dead feature
      arg("""
        component ValidCompWithVariability6 {
          feature f;
          varif (f) {
            port in int i;
            port out int o;
          }
          a.b.A sub;
          constraint (!f);
        }
        """
      ),
      // conditionally connected input port
      arg("""
        component ValidCompWithVariability7 {
          feature f;
          port in int i;
          a.b.B sub;
          varif (f) {
            i -> sub.i;
          }
          constraint (f);
        }
        """
      ),
      // conditionally connected output port
      arg("""
        component ValidCompWithVariability8 {
          feature f;
          port out int o;
          a.b.C sub;
          varif (f) {
            sub.o -> o;
          }
          constraint (f);
        }
        """
      ),
      // conditionally connected input and output port
      arg("""
        component ValidCompWithVariability9 {
          feature f;
          port in int i;
          port out int o;
          a.b.D sub;
          varif (f) {
            i -> sub.i;
            sub.o -> o;
          }
          constraint (f);
        }
        """
      ),
      // conditional input port with conditional connector
      arg("""
        component ValidCompWithVariability10 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int i;
          }
          a.b.B sub;
          varif (f2) {
            i -> sub.i;
          }
          constraint (f1 == f2);
        }
        """
      ),
      // conditional output port with conditional connector
      arg("""
        component ValidCompWithVariability11 {
          feature f1;
          feature f2;
          varif (f1) {
            port out int o;
          }
          a.b.C sub;
          varif (f2) {
            sub.o -> o;
          }
          constraint (f1 == f2);
        }
        """
      ),
      // conditional input and output port with conditional connector
      arg("""
        component ValidCompWithVariability12 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int i;
            port out int o;
          }
          a.b.D sub;
          varif (f2) {
            i -> sub.i;
            sub.o -> o;
          }
          constraint (f1 == f2);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // unconnected input port, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            varif (true) {
              port in int i;
            }
            a.b.A sub;
          }
          """,
        IN_PORT_UNUSED
      ),
      // unconnected output port, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            varif (true) {
              port out int o;
            }
            a.b.A sub;
          }
          """,
        OUT_PORT_UNUSED
      ),
      // unconnected input and output port, tautological variation point
      arg("""
          component InvalidCompWithVariability3 {
            varif (true) {
              port in int i;
              port out int o;
            }
            a.b.A sub;
          }
          """,
        IN_PORT_UNUSED,
        OUT_PORT_UNUSED
      ),
      // unconnected input port, core feature
      arg("""
          component InvalidCompWithVariability4 {
            feature f;
            varif (f) {
              port in int i;
            }
            a.b.A sub;
            constraint (f);
          }
          """,
        IN_PORT_UNUSED
      ),
      // unconnected output port, core feature
      arg("""
          component InvalidCompWithVariability5 {
            feature f;
            varif (f) {
              port out int o;
            }
            a.b.A sub;
            constraint (!f);
          }
          """,
        OUT_PORT_UNUSED
      ),
      // unconnected input and output port, core feature
      arg("""
          component InvalidCompWithVariability6 {
            feature f;
            varif (f) {
              port in int i;
              port out int o;
            }
            a.b.A sub;
            constraint (!f);
          }
          """,
        IN_PORT_UNUSED,
        OUT_PORT_UNUSED
      ),
      // conditionally unconnected input port
      arg("""
          component InvalidCompWithVariability7 {
            feature f;
            port in int i;
            a.b.B sub;
            varif (f) {
              i -> sub.i;
            }
          }
          """,
        IN_PORT_UNUSED
      ),
      // conditionally unconnected output port
      arg("""
          component InvalidCompWithVariability8 {
            feature f;
            port out int o;
            a.b.C sub;
            varif (f) {
              sub.o -> o;
            }
          }
          """,
        OUT_PORT_UNUSED
      ),
      // conditionally unconnected input and output port
      arg("""
          component InvalidCompWithVariability9 {
            feature f;
            port in int i;
            port out int o;
            a.b.D sub;
            varif (f) {
              i -> sub.i;
              sub.o -> o;
            }
          }
          """,
        IN_PORT_UNUSED,
        OUT_PORT_UNUSED
      ),
      // conditional input port with conditional connector
      arg("""
          component InvalidCompWithVariability10 {
            feature f1;
            feature f2;
            varif (f1) {
              port in int i;
            }
            a.b.B sub;
            varif (f2) {
              i -> sub.i;
            }
            constraint (!(!f1 && f2));
          }
          """,
        IN_PORT_UNUSED
      ),
      // conditional output port with conditional connector
      arg("""
          component InvalidCompWithVariability11 {
            feature f1;
            feature f2;
            varif (f1) {
              port out int o;
            }
            a.b.C sub;
            varif (f2) {
              sub.o -> o;
            }
            constraint (!(!f1 && f2));
          }
          """,
        OUT_PORT_UNUSED
      ),
      // conditional input and output port with conditional connector
      arg("""
          component InvalidCompWithVariability12 {
            feature f1;
            feature f2;
            varif (f1) {
              port in int i;
              port out int o;
            }
            a.b.D sub;
            varif (f2) {
              i -> sub.i;
              sub.o -> o;
            }
            constraint (!(!f1 && f2));
          }
          """,
        IN_PORT_UNUSED,
        OUT_PORT_UNUSED
      )
    );
  }

  @Test
  void shouldNotCrashOnUnresolvedSourcePort() {
    // Given
    ASTMACompilationUnit ast = compile("""
      component RegressionUnresolvedSourcePort {
        port in int i;
        a.b.B sub;
        i -> sub.i;
        unknownPort -> sub.i;
      }
      """);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortsConnected4Family());

    // Then
    assertDoesNotThrow(() -> checker.checkAll(ast));
  }
}
