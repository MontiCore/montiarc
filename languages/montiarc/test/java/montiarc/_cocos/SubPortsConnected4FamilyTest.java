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
import variablearc._cocos.SubPortsConnected4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.IN_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.OUT_PORT_NOT_CONNECTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SubPortsConnected4Family}
 */
class SubPortsConnected4FamilyTest extends SubPortsConnectedTest {

  @BeforeEach
  @Override
  protected void setUpComponents() {
    super.setUpComponents();
    compile("package a.b; component G { feature f; varif (f) { port in int i; } }");
    compile("package a.b; component H { feature f; varif (f) { port out int o; } }");
    compile("package a.b; component I { feature f; varif (f) { port in int i; port out int o; } }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability7",
    "ValidCompWithVariability8",
    "ValidCompWithVariability9",
    "ValidCompWithVariability13",
    "ValidCompWithVariability14",
    "ValidCompWithVariability15"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubPortsConnected4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability1",
    "InvalidCompWithVariability2",
    "InvalidCompWithVariability3",
    "InvalidCompWithVariability4",
    "InvalidCompWithVariability5",
    "InvalidCompWithVariability6"
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubPortsConnected4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // subcomponent with unconnected input port, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          varif (false) {
            a.b.B sub;
          }
        }
        """
      ),
      // subcomponent with unconnected output port, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          varif (false) {
            a.b.C sub;
          }
        }
        """
      ),
      // subcomponent with unconnected input and output port, dead variation point
      arg("""
        component ValidCompWithVariability3 {
          varif (false) {
            a.b.D sub;
          }
        }
        """
      ),
      // subcomponent with unconnected input port, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          varif (f) {
            a.b.B sub;
          }
          constraint (!f);
        }
        """
      ),
      // subcomponent with unconnected output port, dead feature
      arg("""
        component ValidCompWithVariability5 {
          feature f;
          varif (f) {
            a.b.C sub;
          }
          constraint (!f);
        }
        """
      ),
      // subcomponent with unconnected input and output port, dead feature
      arg("""
        component ValidCompWithVariability6 {
          feature f;
          varif (f) {
            a.b.D sub;
          }
          constraint (!f);
        }
        """
      ),
      // subcomponent with conditionally connected input port
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
      // subcomponent with conditionally connected output port
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
      // subcomponent with conditionally connected input and output port
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
      // conditional subcomponent with conditionally connected input port
      arg("""
        component ValidCompWithVariability10 {
          feature f1;
          feature f2;
          port in int i;
          varif (f1) {
            a.b.B sub;
          }
          varif (f2) {
            i -> sub.i;
          }
          constraint (f1 == f2);
        }
        """
      ),
      // conditional subcomponent with conditionally connected output port
      arg("""
        component ValidCompWithVariability11 {
          feature f1;
          feature f2;
          port in int i;
          varif (f1) {
            a.b.C sub;
          }
          varif (f2) {
            sub.o -> o;
          }
          constraint (f1 == f2);
        }
        """
      ),
      // conditional subcomponent with conditionally connected input and output port
      arg("""
        component ValidCompWithVariability12 {
          feature f1;
          feature f2;
          port in int i;
          port out int o;
          varif (f1) {
            a.b.D sub;
          }
          varif (f2) {
            i -> sub.i;
            sub.o -> o;
          }
          constraint (f1 == f2);
        }
        """
      ),
      // subcomponent with conditionally connected variable input port
      arg("""
        component ValidCompWithVariability13 {
          feature f;
          port in int i;
          a.b.G sub;
          varif (f) {
            i -> sub.i;
          }
          constraint (f == sub.f);
        }
        """
      ),
      // conditional subcomponent with conditionally connected variable output port
      arg("""
        component ValidCompWithVariability14 {
          feature f;
          port in int i;
          port out int o;
          a.b.H sub;
          varif (f) {
            sub.o -> o;
          }
          constraint (f == sub.f);
        }
        """
      ),
      // conditional subcomponent with conditionally connected variable input and output port
      arg("""
        component ValidCompWithVariability15 {
          feature f;
          port in int i;
          port out int o;
          a.b.I sub;
          varif (f) {
            i -> sub.i;
            sub.o -> o;
          }
          constraint (f == sub.f);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // subcomponent with unconnected input port, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            varif (true) {
              a.b.B sub;
            }
          }
          """,
        IN_PORT_NOT_CONNECTED
      ),
      // subcomponent with unconnected output port, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            varif (true) {
              a.b.C sub;
            }
          }
          """,
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with unconnected input and output port, tautological variation point
      arg("""
          component InvalidCompWithVariability3 {
            varif (true) {
              a.b.D sub;
            }
          }
          """,
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with unconnected input port, core feature
      arg("""
          component InvalidCompWithVariability4 {
            feature f;
            varif (f) {
              a.b.B sub;
            }
          }
          """,
        IN_PORT_NOT_CONNECTED
      ),
      // subcomponent with unconnected output port, core feature
      arg("""
          component InvalidCompWithVariability5 {
            feature f;
            varif (f) {
              a.b.C sub;
            }
          }
          """,
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with unconnected input and output port, core feature
      arg("""
          component InvalidCompWithVariability6 {
            feature f;
            varif (f) {
              a.b.D sub;
            }
          }
          """,
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with conditionally unconnected input port
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
        IN_PORT_NOT_CONNECTED
      ),
      // subcomponent with conditionally unconnected output port
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
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with conditionally unconnected input and output port
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
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // conditional subcomponent with conditionally unconnected input port
      arg("""
          component InvalidCompWithVariability10 {
            feature f1;
            feature f2;
            port in int i;
            varif (f1) {
              a.b.B sub;
            }
            varif (f2) {
              i -> sub.i;
            }
            constraint (!(f2 && !f1);
          }
          """,
        IN_PORT_NOT_CONNECTED
      ),
      // conditional subcomponent with conditionally unconnected output port
      arg("""
          component InvalidCompWithVariability11 {
            feature f1;
            feature f2;
            port in int i;
            varif (f1) {
              a.b.C sub;
            }
            varif (f2) {
              sub.o -> o;
            }
            constraint (!(f2 && !f1);
          }
          """,
        OUT_PORT_NOT_CONNECTED
      ),
      // conditional subcomponent with conditionally unconnected input and output port
      arg("""
          component InvalidCompWithVariability12 {
            feature f1;
            feature f2;
            port in int i;
            port out int o;
            varif (f1) {
              a.b.D sub;
            }
            varif (f2) {
              i -> sub.i;
              sub.o -> o;
            }
            constraint (!(f2 && !f1);
          }
          """,
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with conditionally unconnected variable input port
      arg("""
          component InvalidCompWithVariability13 {
            feature f;
            port in int i;
            a.b.G sub;
            varif (f) {
              i -> sub.i;
            }
            constraint (!(f && !sub.f));
          }
          """,
        IN_PORT_NOT_CONNECTED
      ),
      // conditional subcomponent with conditionally unconnected variable output port
      arg("""
          component InvalidCompWithVariability14 {
            feature f;
            port in int i;
            port out int o;
            a.b.H sub;
            varif (f) {
              sub.o -> o;
            }
            constraint (!(f && !sub.f));
          }
          """,
        OUT_PORT_NOT_CONNECTED
      ),
      // conditional subcomponent with conditionally unconnected variable input and output port
      arg("""
          component InvalidCompWithVariability15 {
            feature f;
            port in int i;
            port out int o;
            a.b.I sub;
            varif (f) {
              i -> sub.i;
              sub.o -> o;
            }
            constraint (!(f && !sub.f));
          }
          """,
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      )
    );
  }
}
