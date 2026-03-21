/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.SubPortsConnected;
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

import static montiarc.util.ArcError.IN_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.OUT_PORT_NOT_CONNECTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SubPortsConnected}.
 */
class SubPortsConnectedTest extends MontiArcTestBase {

  @BeforeEach
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
    checker.addCoCo(new SubPortsConnected());

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
    checker.addCoCo(new SubPortsConnected());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // no subcomponents
      arg("""
        component ValidComp1 { }
        """
      ),
      // subcomponent without ports
      arg("""
        component ValidComp2 {
          a.b.A a;
        }
        """
      ),
      // input port of subcomponent is target of connector
      arg("""
        component ValidComp3 {
          port in int i;
          a.b.B b;
          i -> b.i;
        }
        """
      ),
      // output port of subcomponent is source of connector
      arg("""
        component ValidComp4 {
          port out int o;
          a.b.C c;
          c.o -> o;
        }
        """
      ),
      // input and output port of subcomponent are source and target of connector
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
      // input ports of subcomponent are each a target of a connector
      arg("""
        component ValidComp6 {
          port in int i;
          a.b.E e;
          i -> e.i1;
          i -> e.i2;
        }
        """
      ),
      // input ports of subcomponent are target of a multi-target connector
      arg("""
        component ValidComp7 {
          port in int i;
          a.b.E e;
          i -> e.i1, e.i2;
        }
        """
      ),
      // output ports of subcomponent are each a target of a connector
      arg("""
        component ValidComp8 {
          port out int o1, o2;
          a.b.F f;
          f.o1 -> o1;
          f.o2 -> o2;
        }
        """
      ),
      // input and output port of subcomponent in inner component are source and target of a connector
      arg("""
        component ValidComp9 {
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
      // subcomponent with unconnected input port
      arg("""
          component InvalidComp1 {
            a.b.B b;
          }
          """,
        IN_PORT_NOT_CONNECTED
      ),
      // subcomponent with unconnected output port
      arg("""
          component InvalidComp2 {
            a.b.C c;
          }
          """,
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with unconnected input and output port
      arg("""
          component InvalidComp3 {
            a.b.D d;
          }
          """,
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // two subcomponents with unconnected ports
      arg("""
          component InvalidComp4 {
            a.b.B b;
            a.b.C c;
          }
          """,
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // subcomponent with two unconnected input ports
      arg("""
          component InvalidComp5 {
            a.b.E e;
          }
          """,
        IN_PORT_NOT_CONNECTED,
        IN_PORT_NOT_CONNECTED
      ),
      // subcomponent with two unconnected output ports
      arg("""
          component InvalidComp6 {
            a.b.F f;
          }
          """,
        OUT_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // two subcomponents with unconnected input ports
      arg("""
          component InvalidComp7 {
            a.b.B b1, b2;
          }
          """,
        IN_PORT_NOT_CONNECTED,
        IN_PORT_NOT_CONNECTED
      ),
      // two subcomponents with unconnected output ports
      arg("""
          component InvalidComp8 {
            a.b.C c1, c2;
          }
          """,
        OUT_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      )
    );
  }
}
