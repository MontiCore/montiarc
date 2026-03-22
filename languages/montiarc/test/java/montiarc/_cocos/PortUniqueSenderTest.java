/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortUniqueSender;
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

import static montiarc.util.ArcError.PORT_MULTIPLE_SENDER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortUniqueSender}.
 */
class PortUniqueSenderTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { port in int i; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port in int i; port out int o; }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender());

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
    checker.addCoCo(new PortUniqueSender());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component without ports
      arg("""
        component ValidComp1 { }
        """
      ),
      // input forward
      arg("""
        component ValidComp2 {
          port in int i;
          a.b.A sub;
          i -> sub.i;
        }
        """
      ),
      // input forward to multiple different targets (multi-target connector)
      arg("""
        component ValidComp3 {
          port in int i;
          a.b.A sub1, sub2;
          i -> sub1.i, sub2.i;
        }
        """
      ),
      // input forward to multiple different targets (multiple connectors)
      arg("""
        component ValidComp4 {
          port in int i;
          a.b.A sub1, sub2;
          i -> sub1.i;
          i -> sub2.i;
        }
        """
      ),
      // output forward
      arg("""
        component ValidComp5 {
          port out int o;
          a.b.B sub;
          sub.o -> o;
        }
        """
      ),
      // output forward to multiple different targets (multi-target connector)
      arg("""
        component ValidComp6 {
          port out int o1, o2;
          a.b.B sub;
          sub.o -> o1, o2;
        }
        """
      ),
      // output forward to multiple different targets (multiple connectors)
      arg("""
        component ValidComp7 {
          port out int o1, o2;
          a.b.B sub1, sub2;
          sub1.o -> o1;
          sub2.o -> o2;
        }
        """
      ),
      // hidden channel between single source and target
      arg("""
        component ValidComp8 {
          a.b.A sub1;
          a.b.B sub2;
          sub2.o -> sub1.i;
        }
        """
      ),
      // hidden channel between single source and multiple targets (multi-target connector)
      arg("""
        component ValidComp9 {
          a.b.A sub1, sub2;
          a.b.B sub3;
          sub3.o -> sub1.i, sub2.i;
        }
        """
      ),
      // hidden channel between single source and multiple targets (multiple connectors)
      arg("""
        component ValidComp10 {
          a.b.A sub1, sub2;
          a.b.B sub3, sub4;
          sub3.o -> sub1.i;
          sub4.o -> sub2.i;
        }
        """
      ),
      // input and output forward
      arg("""
        component ValidComp11 {
          port in int i;
          port out int o;
          a.b.C sub;
          i -> sub.i;
          sub.o -> o;
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // multiple input forwards (same target)
      arg("""
          component InvalidComp1 {
            port in int i1, i2;
            a.b.A sub;
            i1 -> sub.i;
            i2 -> sub.i;
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // multiple output forwards (same target)
      arg("""
          component InvalidComp2 {
            port out int o;
            a.b.B sub1, sub2;
            sub1.o -> o;
            sub2.o -> o;
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // redundant input forward
      arg("""
          component InvalidComp3 {
            port in int i;
            a.b.A sub;
            i -> sub.i;
            i -> sub.i;
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // redundant output forward
      arg("""
          component InvalidComp4 {
            port out int o;
            a.b.B sub;
            sub.o -> o;
            sub.o -> o;
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // multi-target hidden connector (same target)
      arg("""
          component InvalidComp5 {
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i, sub1.i;
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // redundant hidden connectors (same target)
      arg("""
          component InvalidComp6 {
            a.b.A sub1;
            a.b.B sub2, sub3;
            sub2.o -> sub1.i;
            sub3.o -> sub1.i;
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // multiple input forwards (same target)
      arg("""
          component InvalidComp7 {
            port in int i1, i2, i3;
            a.b.A sub;
            i1 -> sub.i;
            i2 -> sub.i;
            i3 -> sub.i;
          }
          """,
        PORT_MULTIPLE_SENDER,
        PORT_MULTIPLE_SENDER
      ),
      // multiple output forwards (same target)
      arg("""
          component InvalidComp8 {
            port out int o;
            a.b.B sub1, sub2, sub3;
            sub1.o -> o;
            sub2.o -> o;
            sub3.o -> o;
          }
          """,
        PORT_MULTIPLE_SENDER,
        PORT_MULTIPLE_SENDER
      )
    );
  }
}
