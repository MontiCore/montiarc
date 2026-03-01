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
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability7 ",
    "ValidCompWithVariability8 ",
    "ValidCompWithVariability9 "
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorTypesFit4Family());
    checker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorTypesFit4Family());
    checker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
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
}
