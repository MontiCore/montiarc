/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTypesFit;
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

import static montiarc.util.ArcError.CONNECTOR_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorTypesFit}.
 */
class ConnectorTypesFitTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUp() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    this.setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b;component A { }");
    compile("package a.b;component B { port in boolean i;}");
    compile("package a.b;component C { port out boolean o;}");
    compile("package a.b;component D { port in int i;}");
    compile("package a.b;component E { port out int o;}");
    compile("package a.b;component F { port in java.lang.Integer i;}");
    compile("package a.b;component G { port in java.util.List<java.lang.Integer> i;}");
    compile("package a.b;component H { port out java.util.List<java.lang.Integer> o;}");
    compile("package a.b;component I { port in java.lang.Comparable<java.lang.Integer> i;}");
    compile("package a.b;component J { port out java.lang.Comparable<java.lang.Integer> o;}");
    compile("package a.b;component K<T> { port in T i;} ");
    compile("package a.b;component L<T> { port out T o;}");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorTypesFit());
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
    checker.addCoCo(new ConnectorTypesFit());
    checker.addCoCo(new ConnectorPortsExist());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // decomposed no connectors
      arg("""
        component ValidComp1 {
          a.b.A sub;
        }
        """
      ),
      // boolean -> boolean (input forward)
      arg("""
        component ValidComp2 {
          port in boolean i;
          a.b.B sub;
          i -> sub.i;
        }
        """
      ),
      // boolean -> boolean (output forward)
      arg("""
        component ValidComp3 {
          port out boolean o;
          a.b.C sub;
          sub.o -> o;
        }
        """
      ),
      // boolean -> boolean (hidden channel)
      arg("""
        component ValidComp4 {
          a.b.B sub1;
          a.b.C sub2;
          sub2.o -> sub1.i;
        }
        """
      ),
      // int -> int (input forward)
      arg("""
        component ValidComp5 {
          port in int i;
          a.b.D sub;
          i -> sub.i;
        }
        """
      ),
      // int -> int (output forward)
      arg("""
        component ValidComp6 {
          port out int o;
          a.b.E sub;
          sub.o -> o;
        }
        """
      ),
      // int -> int (hidden channel)
      arg("""
        component ValidComp7 {
          a.b.D sub1;
          a.b.E sub2;
          sub2.o -> sub1.i;
        }
        """
      ),
      // java.lang.Integer -> java.lang.Integer (input forward)
      arg("""
        component ValidComp8 {
          port in java.lang.Integer i;
          a.b.F sub;
          i -> sub.i;
        }
        """
      ),
      // java.util.List<java.lang.Integer> -> java.util.List<java.lang.Integer> (input forward)
      arg("""
        component ValidComp9 {
          port in java.util.List<java.lang.Integer> i;
          a.b.G sub;
          i -> sub.i;
        }
        """
      ),
      // java.util.List<java.lang.Integer> -> java.util.List<java.lang.Integer> (output forward)
      arg("""
        component ValidComp10 {
          port in java.util.List<java.lang.Integer> o;
          a.b.H sub;
          sub.o -> o;
        }
        """
      ),
      // java.lang.Integer -> java.lang.Comparable<java.lang.Integer> (input forward, super type conversion)
      arg("""
        component ValidComp11 {
          port in java.lang.Integer i;
          a.b.I sub;
          i -> sub.i;
        }
        """
      ),
      // java.lang.Comparable<java.lang.Integer> -> java.lang.Comparable<java.lang.Integer> (input forward)
      arg("""
        component ValidComp12 {
          port in java.lang.Comparable<java.lang.Integer> i;
          a.b.I sub;
          i -> sub.i;
        }
        """
      ),
      // java.lang.Comparable<java.lang.Integer> -> java.lang.Comparable<java.lang.Integer> (output forward)
      arg("""
        component ValidComp13 {
          port in java.lang.Comparable<java.lang.Integer> o;
          a.b.J sub;
          sub.o -> o;
        }
        """
      ),
      // java.lang.Integer -> java.lang.Integer (input forward, generic subcomponent)
      arg("""
        component ValidComp14 {
          port in java.lang.Integer i;
          a.b.K<java.lang.Integer> sub;
          i -> sub.i;
        }
        """
      ),
      // java.lang.Integer -> java.lang.Integer (output forward, generic subcomponent)
      arg("""
        component ValidComp15 {
          port in java.lang.Integer o;
          a.b.L<java.lang.Integer> sub;
          o -> sub.o;
        }
        """
      ),
      // S -> S (input forward, generic components)
      arg("""
        component ValidComp16<S> {
          port in S i;
          a.b.K<S> sub;
          i -> sub.i;
        }
        """
      ),
      // S -> S (output forward, generic components)
      arg("""
        component ValidComp17<S> {
          port out S o;
          a.b.L<S> sub;
          sub.o -> o;
        }
        """
      ),
      // T -> T (input forward, generic components)
      arg("""
        component ValidComp18<T> {
          port in T i;
          a.b.K<T> sub;
          i -> sub.i;
        }
        """
      ),
      // T -> T (input forward, generic components)
      arg("""
        component ValidComp19<T> {
          port out T o;
          a.b.L<T> sub;
          sub.o -> o;
        }
        """
      ),
      // S -> S (input forward, generic subcomponents)
      arg("""
        component ValidComp20<S> {
          a.b.K<S> sub1;
          a.b.L<S> sub2;
          sub2.o -> sub1.i;
        }
        """
      ),
      // int -> int, int (input forward, multiple targets)
      arg("""
        component ValidComp21 {
          port in int i;
          a.b.D sub1, sub2;
          i -> sub1.i, sub2.i;
        }
        """
      ),
      // int -> int, int (output forward, multiple targets)
      arg("""
        component ValidComp22 {
          port out int o1, o2;
          a.b.E sub;
          sub.o -> o1, o2;
        }
        """
      ),
      // int -> int, int (hidden channel, multiple targets)
      arg("""
        component ValidComp23 {
          a.b.D sub1;
          a.b.E sub2;
          sub2.o -> sub1.i;
        }
        """
      ),
      // T -> T (generic inner component)
      arg("""
        component ValidComp24<T> {
          port in T i;
          component Inner<T> {
            port in T i;
          }
          Inner<T> sub;
          i -> sub.i;
        }
        """
      ),
      // S -> S (generic inner component)
      arg("""
        component ValidComp25<S> {
          port in S i;
          component Inner<T> {
            port in T i;
          }
          Inner<S> sub;
          i -> sub.i;
        }
        """
      ),
      // int -> int (generic super component)
      arg("""
        component ValidComp26 extends a.b.K<int> {
          port in int i;
          a.b.D sub;
          i -> sub.i;
        }
        """
      ),
      // T -> T (upper bound, generic super component)
      arg("""
        component ValidComp27<T extends int> {
          port in T i;
          a.b.K<T> sub;
          i -> sub.i;
        }
        """
      ),
      // java.lang.Boolean -> boolean (input forward)
      arg("""
        component ValidComp28 {
          port in java.lang.Boolean i;
          a.b.B sub;
          i -> sub.i;
        }
        """
      ),
      // boolean -> java.lang.Boolean (output forward)
      arg("""
        component ValidComp29 {
          port out java.lang.Boolean o;
          a.b.C sub;
          sub.o -> o;
        }
        """
      ),
      // java.lang.Integer -> int (input forward)
      arg("""
        component ValidComp30 {
          port in java.lang.Integer i;
          a.b.D sub;
          i -> sub.i;
        }
        """
      ),
      // int -> java.lang.Integer (output forward)
      arg("""
        component ValidComp31 {
          port out java.lang.Integer o;
          a.b.E sub;
          sub.o -> o;
        }
        """
      )
      ,
      // int -> java.lang.Integer (hidden channel)
      arg("""
        component ValidComp32 {
          port in int i;
          a.b.F sub;
          i -> sub.i;
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // int -> boolean (input forward)
      arg("""
        component InvalidComp1 {
          port in int i;
          a.b.B sub;
          i -> sub.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // boolean -> int (output forward)
      arg("""
        component InvalidComp2 {
          port out int o;
          a.b.C sub;
          sub.o -> o;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // boolean -> int (input forward)
      arg("""
        component InvalidComp3 {
          port in boolean i;
          a.b.D sub;
          i -> sub.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // int -> boolean (output forward)
      arg("""
        component InvalidComp4 {
          port out boolean o;
          a.b.E sub;
          sub.o -> o;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // java.lang.String -> java.lang.Integer (input forward)
      arg("""
        component InvalidComp5 {
          port in java.lang.String i;
          a.b.F sub;
          i -> sub.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // java.util.List<java.lang.String> -> java.util.List<java.lang.Integer> (input forward)
      arg("""
        component InvalidComp6 {
          port in java.util.List<java.lang.String> i;
          a.b.G sub;
          i -> sub.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // java.lang.Integer -> java.util.List<java.lang.Integer> (input forward)
      arg("""
        component InvalidComp7 {
          port in java.lang.Integer i;
          a.b.G sub;
          i -> sub.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // java.util.List<java.lang.Integer> -> java.util.List<java.lang.String> (output forward)
      arg("""
        component InvalidComp8 {
          port out java.util.List<java.lang.String> o;
          a.b.H sub;
          sub.o -> o;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // java.util.List<java.lang.Integer> -> java.lang.Integer (output forward)
      arg("""
        component InvalidComp9 {
          port out java.lang.Integer o;
          a.b.H sub;
          sub.o -> o;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // java.lang.Comparable<java.lang.Integer> -> java.lang.Integer (output forward)
      arg("""
        component InvalidComp10 {
          port out java.lang.Integer o;
          a.b.J sub;
          sub.o -> o;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // int -> T (input forward, type parameter)
      arg("""
        component InvalidComp11 {
          port in int i;
          a.b.K sub;
          i -> sub.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // T -> int (output forward, type parameter)
      arg("""
        component InvalidComp12 {
          port out int o;
          a.b.L sub;
          sub.o -> o;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // T -> T (hidden channel, type parameter)
      arg("""
        component InvalidComp13 {
          a.b.K sub1;
          a.b.L sub2;
          sub2.o -> sub1.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // V -> U (hidden channel, type parameter)
      arg("""
        component InvalidComp14<U, V> {
          a.b.K<U> sub1;
          a.b.L<V> sub2;
          sub2.o -> sub1.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      ),
      // boolean -> int, int (input forward, multiple targets)
      arg("""
        component InvalidComp15 {
          port in boolean i;
          a.b.D sub1, sub2;
          i -> sub1.i, sub2.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH,
        CONNECTOR_TYPE_MISMATCH
      ),
      // boolean -> int, int (output forward, multiple targets)
      arg("""
        component InvalidComp16 {
          port out int o1, o2;
          a.b.C sub;
          sub.o -> o1, o2;
        }
        """,
        CONNECTOR_TYPE_MISMATCH,
        CONNECTOR_TYPE_MISMATCH
      ),
      // boolean -> int, int (hidden channel, multiple targets)
      arg("""
        component InvalidComp17 {
          a.b.C sub1;
          a.b.D sub2, sub3;
          sub1.o -> sub2.i, sub3.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH,
        CONNECTOR_TYPE_MISMATCH
      ),
      // T -> T (inner component, type parameter)
      arg("""
        component InvalidComp18<T> {
          port in T i;
          component Inner<T> {
            port in T i;
          }
          Inner sub;
          i -> sub.i;
        }
        """,
        CONNECTOR_TYPE_MISMATCH
      )
    );
  }
}
