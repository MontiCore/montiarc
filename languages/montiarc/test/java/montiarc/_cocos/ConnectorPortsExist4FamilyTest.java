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

import static montiarc.util.ArcError.MISSING_PORT;
import static montiarc.util.ArcError.MISSING_SUBCOMPONENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorPortsExist4Family}.
 */
class ConnectorPortsExist4FamilyTest extends ConnectorPortsExistTest {

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
    compile("package a.b; component F { feature ff; varif (ff) { port in int i; }}");
    compile("package a.b; component G { feature ff; varif (ff) { port out int o; }}");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
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
    "InvalidCompWithVariability14 ",
    "InvalidCompWithVariability15 ",
    "InvalidCompWithVariability17 ",
    "InvalidCompWithVariability18 ",
    "InvalidCompWithVariability19 "
  })
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // input forward, single variation point
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          port in int i;
          varif (f) {
            i -> sub.i;
          }
          a.b.B sub;
          constraint (f);
        }"""
      ),
      // output forward, single variation point
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          port out int o;
          varif (f) {
            sub.o -> o;
          }
          a.b.C sub;
          constraint (f);
        }"""
      ),
      // hidden channel, single variation point
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          varif (f) {
            sub2.o -> sub1.i;
          }
          a.b.B sub1;
          a.b.C sub2;
        }"""
      ),
      // input forward, missing source, dead variation point
      arg("""
        component ValidCompWithVariability4 {
          varif (false) {
            i -> sub.i;
          }
          a.b.B sub;
        }"""
      ),
      // input forward, missing target (subcomponent), dead variation point
      arg("""
        component ValidCompWithVariability5 {
          port in int i;
          varif (false) {
            i -> sub.i;
          }
        }"""
      ),
      // input forward, missing target (port), dead variation point
      arg("""
        component ValidCompWithVariability6 {
          port in int i;
          varif (false) {
            i -> sub.i;
          }
          a.b.A sub;
        }"""
      ),
      // output forward, missing source (subcomponent), dead variation point
      arg("""
        component ValidCompWithVariability7 {
          port out int o;
          varif (false) {
            sub.o -> o;
          }
        }"""
      ),
      // output forward, missing source (port), dead variation point
      arg("""
        component ValidCompWithVariability8 {
          port out int o;
          varif (false) {
            sub.o -> o;
          }
          a.b.A sub;
        }"""
      ),
      // output forward, missing target, dead variation point
      arg("""
        component ValidCompWithVariability9 {
          varif (false) {
            sub.o -> o;
          }
          a.b.C sub;
        }"""
      ),      // input forward, missing source, dead feature
      arg("""
        component ValidCompWithVariability10 {
          feature f;
          varif (f) {
            a.b.B sub;
            i -> sub.i;
          }
          constraint (!f);
        }"""
      ),
      // input forward, missing target (subcomponent), dead feature
      arg("""
        component ValidCompWithVariability11 {
          feature f;
          varif (f) {
            port in int i;
            i -> sub.i;
          }
          constraint (!f);
        }"""
      ),
      // input forward, missing target (port), dead feature
      arg("""
        component ValidCompWithVariability12 {
          feature f;
          varif (f) {
            port in int i;
            a.b.A sub;
            i -> sub.i;
          }
          constraint (!f);
        }"""
      ),
      // output forward, missing source (subcomponent), dead feature
      arg("""
        component ValidCompWithVariability13 {
          feature f;
          varif (f) {
            port out int o;
            sub.o -> o;
          }
          constraint (!f);
        }"""
      ),
      // output forward, missing source (port), dead feature
      arg("""
        component ValidCompWithVariability14 {
          feature f;
          varif (f) {
            port out int o;
            a.b.A sub;
            sub.o -> o;
          }
          constraint (!f);
        }"""
      ),
      // output forward, missing target, dead feature
      arg("""
        component ValidCompWithVariability15 {
          feature f;
          varif (f) {
            sub.o -> o;
          }
          a.b.A sub;
          constraint (!f);
        }"""
      ),
      // input forward, component with conditional interface
      arg("""
        component ValidCompWithVariability16 {
          feature f;
          varif (f) {
            port in int i;
          }
          a.b.B sub;
          i -> sub.i;
          constraint (f);
        }"""
      ),
      // input forward, subcomponent with conditional interface
      arg("""
        component ValidCompWithVariability17 {
          port in int i;
          a.b.F sub;
          i -> sub.i;
          constraint (sub.ff);
        }"""
      ),
      // input forward, component and subcomponent with conditional interfaces
      arg("""
        component ValidCompWithVariability18 {
          feature f;
          varif (f) {
            port in int i;
          }
          a.b.F sub;
          constraint (sub.ff == f);
        }"""
      ),
      // output forward, component with conditional interface
      arg("""
        component ValidCompWithVariability19 {
          feature f;
          varif (f) {
            port out int o;
          }
          a.b.C sub;
          sub.o -> o;
          constraint (f);
        }"""
      ),
      // output forward, subcomponent with conditional interface
      arg("""
        component ValidCompWithVariability20 {
          port out int o;
          a.b.G sub;
          sub.o -> o;
          constraint (sub.ff);
        }"""
      ),
      // output forward, component and subcomponent with conditional interfaces
      arg("""
        component ValidCompWithVariability21 {
          feature f;
          varif (f) {
            port out int o;
          }
          a.b.G sub;
          constraint (sub.ff == f);
        }"""
      ),
      // hidden channel, subcomponents with conditional interfaces
      arg("""
        component ValidCompWithVariability22 {
          a.b.F sub1;
          a.b.G sub2;
          sub2.o -> sub1.i;
          constraint (sub1.ff && sub2.ff);
        }"""
      ),
      // input forward, conditional subcomponent
      arg("""
        component ValidCompWithVariability23 {
          feature f;
          port in int i;
          varif (f) {
            a.b.B sub;
          }
          i -> sub.i;
          constraint (f);
        }"""
      ),
      // output forward, conditional subcomponent
      arg("""
        component ValidCompWithVariability24 {
          feature f;
          port out int o;
          varif (f) {
            a.b.C sub;
          }
          sub.o -> o;
          constraint (f);
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // input forward, missing source, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            varif (true) {
              i -> sub.i;
            }
            a.b.B sub;
          }""",
        MISSING_PORT
      ),
      // input forward, missing target (subcomponent), tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            port in int i;
            varif (true) {
              i -> sub.i;
            }
          }""",
        MISSING_SUBCOMPONENT
      ),
      // input forward, missing target (port), tautological variation point
      arg("""
          component InvalidCompWithVariability3 {
            port in int i;
            varif (true) {
              i -> sub.i;
            }
            a.b.A sub;
          }""",
        MISSING_PORT
      ),
      // output forward, missing source (subcomponent), tautological variation point
      arg("""
          component InvalidCompWithVariability4 {
            port out int o;
            varif (true) {
              sub.o -> o;
            }
          }""",
        MISSING_SUBCOMPONENT
      ),
      // output forward, missing source (port), tautological variation point
      arg("""
          component InvalidCompWithVariability5 {
            port out int o;
            varif (true) {
              sub.o -> o;
            }
            a.b.A sub;
          }""",
        MISSING_PORT
      ),
      // output forward, missing target, tautological variation point
      arg("""
          component InvalidCompWithVariability6 {
            a.b.C sub;
            varif (true) {
              sub.o -> o;
            }
          }""",
        MISSING_PORT
      ),
      // input forward, missing source, core feature
      arg("""
          component InvalidCompWithVariability7 {
            feature f;
            varif (f) {
              i -> sub.i;
            }
            a.b.B sub;
            constraint (f);
          }""",
        MISSING_PORT
      ),
      // input forward, missing target (subcomponent), core feature
      arg("""
          component InvalidCompWithVariability8 {
            feature f;
            port in int i;
            varif (f) {
              i -> sub.i;
            }
            constraint (f);
          }""",
        MISSING_SUBCOMPONENT
      ),
      // input forward, missing target (port), core feature
      arg("""
          component InvalidCompWithVariability9 {
            feature f;
            port in int i;
            varif (f) {
              i -> sub.i;
            }
            a.b.A sub;
            constraint (f);
          }""",
        MISSING_PORT
      ),
      // output forward, missing source (subcomponent), core feature
      arg("""
          component InvalidCompWithVariability10 {
            feature f;
            port out int o;
            varif (f) {
              sub.o -> o;
            }
            constraint (f);
          }""",
        MISSING_SUBCOMPONENT
      ),
      // output forward, missing source (port), core feature
      arg("""
          component InvalidCompWithVariability11 {
            feature f;
            port out int o;
            varif (f) {
              sub.o -> o;
            }
            a.b.A sub;
            constraint (f);
          }""",
        MISSING_PORT
      ),
      // output forward, missing target, core feature
      arg("""
          component InvalidCompWithVariability12 {
            feature f;
            varif (f) {
              sub.o -> o;
            }
            a.b.C sub;
            constraint (f);
          }""",
        MISSING_PORT
      ),
      // input forward, missing source, component with conditional interface
      arg("""
          component InvalidCompWithVariability13 {
            feature f;
            varif (f) {
              port in int i;
            }
            a.b.B sub;
            i -> sub.i;
            constraint (!f);
          }""",
        MISSING_PORT
      ),
      // input forward, missing target, subcomponent with conditional interface
      arg("""
          component InvalidCompWithVariability14 {
            port in int i;
            a.b.F sub;
            i -> sub.i;
            constraint (!sub.ff);
          }""",
        MISSING_PORT
      ),
      // input forward, missing source and target, component and subcomponent with conditional interfaces
      arg("""
          component InvalidCompWithVariability15 {
            feature f;
            varif (f) {
              port in int i;
            }
            a.b.F sub;
            constraint (sub.ff == !f);
          }""",
        MISSING_PORT, MISSING_PORT
      ),
      // output forward, missing target, component with conditional interface
      arg("""
          component InvalidCompWithVariability16 {
            feature f;
            varif (f) {
              port out int o;
            }
            a.b.C sub;
            sub.o -> o;
            constraint (!f);
          }""",
        MISSING_PORT
      ),
      // output forward, missing source, subcomponent with conditional interface
      arg("""
          component InvalidCompWithVariability17 {
            port out int o;
            a.b.G sub;
            sub.o -> o;
            constraint (!sub.ff);
          }""",
        MISSING_PORT
      ),
      // output forward, missing source and target, component and subcomponent with conditional interfaces
      arg("""
          component InvalidCompWithVariability18 {
            feature f;
            varif (f) {
              port out int o;
            }
            a.b.G sub;
            constraint (sub.ff == !f);
          }""",
        MISSING_PORT, MISSING_PORT
      ),
      // hidden channel, missing source and target, subcomponents with conditional interfaces
      arg("""
          component InvalidCompWithVariability19 {
            a.b.F sub1;
            a.b.G sub2;
            sub2.o -> sub1.i;
            constraint (sub1.ff && !sub2.ff);
          }""",
        MISSING_PORT, MISSING_PORT
      ),
      // input forward, missing source (subcomponent), conditional subcomponent
      arg("""
          component InvalidCompWithVariability20 {
            feature f;
            port in int i;
            varif (f) {
              a.b.B sub;
            }
            i -> sub.i;
            constraint (!f);
          }""",
        MISSING_SUBCOMPONENT
      ),
      // output forward, missing source (subcomponent), conditional subcomponent
      arg("""
          component InvalidCompWithVariability21 {
            feature f;
            port out int o;
            varif (f) {
              a.b.C sub;
            }
            sub.o -> o;
            constraint (!f);
          }""",
        MISSING_SUBCOMPONENT
      )
    );
  }
}
