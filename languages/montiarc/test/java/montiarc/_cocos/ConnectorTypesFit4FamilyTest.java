/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;
import variablearc._cocos.ConnectorTypesFit4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.CONNECTOR_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorTypesFit4Family}.
 */
class ConnectorTypesFit4FamilyTest extends ConnectorTypesFitTest {

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
    compile("package a.b; component M { feature ff; varif (ff) { port in int i; } else { port in boolean i; } }");
    compile("package a.b; component N { feature ff; varif (ff) { port out int o; } else { port out boolean o; } }");
    compile("package a.b; component P<T> { port in T i; }");
    compile("package a.b; component CP extends a.b.P<int> { }");
    compile("package a.b; component GP<U> extends a.b.P<U> { }");
    compile("package a.b; component Q(int n) { varif (n > 3) { port in int i; } else { port in boolean i; } }");
    compile("package a.b; component QD(int n = 5) { varif (n > 3) { port in int i; } else { port in boolean i; } }");
    compile("package a.b; component WP { feature F; varif (F) { port in int i; } else { port in boolean i; } }");
  }

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorTypesFit4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @MethodSource("validModelsInContext")
  @Override
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @MethodSource("invalidModelsInContext")
  @DisableIfDisplayName(contains = "InvalidComp21 ")
  @Override
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // boolean -> int (input forward), dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in boolean i;
          varif (false) {
            i -> sub.i;
          }
          a.b.D sub;
        }"""
      ),
      // int -> boolean (output forward), dead variation point
      arg("""
        component ValidCompWithVariability2 {
          port out boolean o;
          varif (false) {
            sub.o -> o;
          }
          a.b.E sub;
        }"""
      ),
      // int -> boolean (hidden channel), dead variation point
      arg("""
        component ValidCompWithVariability3 {
          varif (false) {
            sub1.o -> sub2.i;
          }
          a.b.E sub1;
          a.b.B sub2;
        }"""
      ),
      // boolean -> int (input forward), dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          port in boolean i;
          varif (f) {
            i -> sub.i;
          }
          a.b.D sub;
          constraint (!f);
        }"""
      ),
      // int -> boolean (output forward), dead feature
      arg("""
        component ValidCompWithVariability5 {
          feature f;
          port out boolean o;
          varif (f) {
            sub.o -> o;
          }
          a.b.E sub;
          constraint (!f);
        }"""
      ),
      // int -> boolean (hidden channel), dead feature
      arg("""
        component ValidCompWithVariability6 {
          feature f;
          varif (f) {
            sub1.o -> sub2.i;
          }
          a.b.E sub1;
          a.b.B sub2;
          constraint (!f);
        }"""
      ),
      // input forward, subcomponent interface with variable typing
      arg("""
        component ValidCompWithVariability7 {
          port in int i;
          i -> sub.i;
          a.b.M sub;
          constraint (sub.ff);
        }"""
      ),
      // output forward, subcomponent interface with variable typing
      arg("""
        component ValidCompWithVariability8 {
          port out int o;
          sub.o -> o;
          a.b.N sub;
          constraint (sub.ff);
        }"""
      ),
      // hidden channel, subcomponent interfaces with variable typing
      arg("""
        component ValidCompWithVariability9 {
          a.b.N sub1;
          a.b.M sub2;
          sub1.o -> sub2.i;
          constraint (sub1.ff == sub2.ff);
        }"""
      ),
      // input forward, component interface with variable typing
      arg("""
        component ValidCompWithVariability10 {
          feature f;
          varif (f) {
            port in int i;
          } else {
            port in boolean i;
          }
          i -> sub.i;
          a.b.D sub;
          constraint (f);
        }"""
      ),
      // output forward, subcomponent interface with variable typing
      arg("""
        component ValidCompWithVariability11 {
          feature f;
          varif (f) {
            port out int o;
          } else {
            port out boolean o;
          }
          sub.o -> o;
          a.b.E sub;
          constraint (f);
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // boolean -> int (input forward), tautological variation point
      arg("""
        component InvalidCompWithVariability1 {
          port in boolean i;
          varif (true) {
            i -> sub.i;
          }
          a.b.D sub;
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // int -> boolean (output forward), tautological variation point
      arg("""
        component InvalidCompWithVariability2 {
          port out boolean o;
          varif (true) {
            sub.o -> o;
          }
          a.b.E sub;
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // int -> boolean (hidden channel), tautological variation point
      arg("""
        component InvalidCompWithVariability3 {
          varif (true) {
            sub1.o -> sub2.i;
          }
          a.b.E sub1;
          a.b.B sub2;
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // boolean -> int (input forward), core feature
      arg("""
        component InvalidCompWithVariability4 {
          feature f;
          port in boolean i;
          varif (f) {
            i -> sub.i;
          }
          a.b.D sub;
          constraint (f);
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // int -> boolean (output forward), core feature
      arg("""
        component InvalidCompWithVariability5 {
          feature f;
          port out boolean o;
          varif (f) {
            sub.o -> o;
          }
          a.b.E sub;
          constraint (f);
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // int -> boolean (hidden channel), core feature
      arg("""
        component InvalidCompWithVariability6 {
          feature f;
          varif (f) {
            sub1.o -> sub2.i;
          }
          a.b.E sub1;
          a.b.B sub2;
          constraint (f);
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // input forward, subcomponent interface with variable typing
      arg("""
        component InvalidCompWithVariability7 {
          port in int i;
          i -> sub.i;
          a.b.M sub;
          constraint (!sub.ff);
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // output forward, subcomponent interface with variable typing
      arg("""
        component InvalidCompWithVariability8 {
          port out int o;
          sub.o -> o;
          a.b.N sub;
          constraint (!sub.ff);
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // hidden channel, subcomponent interfaces with variable typing
      arg("""
        component InvalidCompWithVariability9 {
          a.b.N sub1;
          a.b.M sub2;
          sub1.o -> sub2.i;
          constraint (sub1.ff == !sub2.ff);
        }""",
        CONNECTOR_TYPE_MISMATCH, CONNECTOR_TYPE_MISMATCH
      ),
      // input forward, component interface with variable typing
      arg("""
        component InvalidCompWithVariability10 {
          feature f;
          varif (f) {
            port in int i;
          } else {
            port in boolean i;
          }
          i -> sub.i;
          a.b.D sub;
          constraint (!f);
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // output forward, subcomponent interface with variable typing
      arg("""
        component InvalidCompWithVariability11 {
          feature f;
          varif (f) {
            port out int o;
          } else {
            port out boolean o;
          }
          sub.o -> o;
          a.b.E sub;
          constraint (!f);
        }""",
        CONNECTOR_TYPE_MISMATCH
      )
    );
  }

  static Stream<Arguments> validModelsInContext() {
    return Stream.of(
      arg("""
        component ValidGenericSubcomponentInVarIf {
          feature f;
          port in int x;
          port in boolean y;
          varif (f) { a.b.K<int> sub; } else { a.b.K<boolean> sub; }
          varif (f) { x -> sub.i; } else { y -> sub.i; }
        }"""
      ),
      arg("""
        component ValidGenericSubcomponentConstrained {
          feature f;
          port in int x;
          varif (f) { a.b.K<int> sub; } else { a.b.K<boolean> sub; }
          x -> sub.i;
          constraint (f);
        }"""
      ),
      arg("""
        component ValidInheritedGenericPort {
          port in int x;
          a.b.CP sub;
          x -> sub.i;
        }"""
      ),
      arg("""
        component ValidInheritedPortOfGenericChild {
          port in int x;
          a.b.GP<int> sub;
          x -> sub.i;
        }"""
      ),
      arg("""
        component ValidParameterDependentPort {
          port in int x;
          a.b.Q sub(5);
          x -> sub.i;
        }"""
      ),
      arg("""
        component ValidDefaultParameterDependentPort {
          port in int x;
          a.b.QD sub;
          x -> sub.i;
        }"""
      ),
      arg("""
        component ValidInheritedFeature extends a.b.WP {
          port out int x;
          i -> x;
          constraint (F);
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModelsInContext() {
    return Stream.of(
      arg("""
        component InvalidGenericSubcomponentInVarIf {
          feature f;
          port in int x;
          varif (f) { a.b.K<int> sub; } else { a.b.K<boolean> sub; }
          x -> sub.i;
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      arg("""
        component InvalidInheritedGenericPort {
          port in boolean x;
          a.b.CP sub;
          x -> sub.i;
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      arg("""
        component InvalidInheritedPortOfGenericChild {
          port in int x;
          a.b.GP<boolean> sub;
          x -> sub.i;
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      arg("""
        component InvalidParameterDependentPort {
          port in int x;
          a.b.Q sub(1);
          x -> sub.i;
        }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      arg("""
        component InvalidInheritedFeature extends a.b.WP {
          port out int x;
          i -> x;
          constraint (!F);
        }""",
        CONNECTOR_TYPE_MISMATCH
      )
    );
  }

  @Test
  void shouldReportErrorIfAnotherSourceMismatchesForUnknownValues() {
    // Given
    ASTMACompilationUnit ast = compile("""
      component UnknownComp1 {
        feature f;
        port out int o;
        varif (java.lang.Math.abs(-1) > 0) {
          port in boolean i;
        }
        varif (f) {
          port in boolean i;
        }
        i -> o;
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isError)
      .extracting(Finding::getMsg).asString()
      .startsWith(CONNECTOR_TYPE_MISMATCH.getErrorCode());
  }

  @Test
  void shouldReportErrorIfConstraintsDependOnUnknownValues() {
    // Given
    ASTMACompilationUnit ast = compile("""
      component UnknownComp2(int n) {
        port in int i;
        port out boolean o;
        i -> o;
        constraint (java.lang.Math.abs(n) > 3);
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isError)
      .extracting(Finding::getMsg).asString()
      .startsWith(CONNECTOR_TYPE_MISMATCH.getErrorCode());
  }
}
