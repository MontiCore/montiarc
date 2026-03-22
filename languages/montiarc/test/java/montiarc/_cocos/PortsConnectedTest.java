/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortsConnected;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.IN_PORT_UNUSED;
import static montiarc.util.ArcError.OUT_PORT_UNUSED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortsConnected}.
 */
class PortsConnectedTest extends MontiArcTestBase {

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    this.setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; }");
    compile("package a.b; component C { port out int o; }");
    compile("package a.b; component D { port in int i; port out int o; }");
    compile("package a.b; component E { port in int i1, i2; }");
    compile("package a.b; component F { port out int o1, o2; }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortsConnected());

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
    checker.addCoCo(new PortsConnected());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // atomic component
      arg("""
        component ValidComp1 {
          port in int i;
          port out int o;
        }
        """
      ),
      // composed component without ports
      arg("""
        component ValidComp2 {
          a.b.A a;
        }
        """
      ),
      // input port is source of connector
      arg("""
        component ValidComp3 {
          port in int i;
          a.b.B b;
          i -> b.i;
        }
        """
      ),
      // output port is target of connector
      arg("""
        component ValidComp4 {
          port out int o;
          a.b.C c;
          c.o -> o;
        }
        """
      ),
      // input port and output port are source and target of connector
      arg("""
        component ValidComp5 {
          port in int i;
          port out int o;
          a.b.D d;
          i -> d.i;
          d.o -> o;
        }
        """
      ),
      // input ports are each a source of a connector
      arg("""
        component ValidComp6 {
          port in int i1, i2;
          a.b.E e;
          i1 -> e.i1;
          i2 -> e.i2;
        }
        """
      ),
      // output ports are each a target of a connector
      arg("""
        component ValidComp7 {
          port out int o1, o2;
          a.b.C c;
          c.o -> o1;
          c.o -> o2;
        }
        """
      ),
      // output ports are target of a multi-target connector
      arg("""
        component ValidComp8 {
          port out int o1, o2;
          a.b.C c;
          c.o -> o1, o2;
        }
        """
      ),
      // atomic inner component with same named ports
      arg("""
        component ValidComp9 {
          port in int i;
          port out int o;
          component Inner {
            port in int i;
            port out int o;
          }
          Inner sub;
          i -> sub.i;
          o -> sub.o;
        }
        """
      ),
      // composed inner component with connected input and output ports
      arg("""
        component ValidComp10 {
          component Inner {
            port in int i;
            port out int o;
            a.b.D d;
            i -> d.i;
            d.o -> o;
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // unused input port
      arg("""
          component InvalidComp1 {
            port in int i;
            a.b.A a;
          }
          """,
        IN_PORT_UNUSED
      ),
      // unused output port
      arg("""
          component InvalidComp2 {
            port out int o;
            a.b.A a;
          }
          """,
        OUT_PORT_UNUSED
      ),
      // unused input and output port
      arg("""
          component InvalidComp3 {
            port in int i;
            port out int o;
            a.b.A a;
          }
          """,
        IN_PORT_UNUSED,
        OUT_PORT_UNUSED
      ),
      // multiple unused input ports
      arg("""
          component InvalidComp4 {
            port in int i1, i2;
            a.b.A a;
          }
          """,
        IN_PORT_UNUSED,
        IN_PORT_UNUSED
      ),
      // multiple unused output ports
      arg("""
          component InvalidComp5 {
            port out int o1, o2;
            a.b.A a;
          }
          """,
        OUT_PORT_UNUSED,
        OUT_PORT_UNUSED
      ),
      // input and output of composed inner component are unused
      arg("""
          component InvalidComp6 {
            port in int i;
            port out int o;
            component Inner {
              port in int i;
              port out int o;
              a.b.A a;
            }
            Inner sub;
            i -> sub.i;
            sub.o -> o;
          }
          """,
        IN_PORT_UNUSED,
        OUT_PORT_UNUSED
      )
    );
  }
}
