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
import variablearc._cocos.ConnectorDirectionsFit4Family;
import variablearc._cocos.ConnectorPortsExist4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.SOURCE_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.TARGET_DIRECTION_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorDirectionsFit4Family}.
 */
class ConnectorDirectionsFit4FamilyTest extends ConnectorDirectionsFitTest {

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
    compile("package a.b; component E { feature ff; varif (ff) { port in int io; } else { port out int io; } }");
    compile("package a.b; component F { port in int io; }");
    compile("package a.b; component G { port out int io; }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability19 ",
    "ValidCompWithVariability20 ",
    "ValidCompWithVariability21 ",
    "ValidCompWithVariability22 ",
    "ValidCompWithVariability23 "
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());
    checker.get4FullVariant().addCoCo(new ConnectorDirectionsFit4Family());

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
    "InvalidCompWithVariability16 ",
    "InvalidCompWithVariability17 ",
    "InvalidCompWithVariability18 ",
    "InvalidCompWithVariability19 ",
    "InvalidCompWithVariability20 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorDirectionsFit4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // in port forward, single variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          feature f;
          varif (f) {
            a.b.B sub;
            i -> sub.i;
          }
          constraint (f);
        }"""
      ),
      // out port forward, single variation point
      arg("""
        component ValidCompWithVariability2 {
          port out int o;
          feature f;
          varif (f) {
            a.b.C sub;
            sub.o -> o;
          }
          constraint (f);
        }"""
      ),
      // hidden channel, single variation point
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          varif (f) {
            a.b.B sub1;
            a.b.C sub2;
            sub2.o -> sub1.i;
          }
        }"""
      ),
      // in port forward, source direction mismatch, dead variation point
      arg("""
        component ValidCompWithVariability4 {
          varif (false) {
            port out int o;
            a.b.B sub;
            o -> sub.i;
          }
        }"""
      ),
      // in port forward, target direction mismatch, dead variation point
      arg("""
        component ValidCompWithVariability5 {
          varif (false) {
            port in int i;
            a.b.C sub;
            i -> sub.o;
          }
        }"""
      ),
      // out port forward, source direction mismatch, dead variation point
      arg("""
        component ValidCompWithVariability6 {
          varif (false) {
            port out int o;
            a.b.B sub;
            sub.i -> o;
          }
        }"""
      ),
      // out port forward, target direction mismatch, dead variation point
      arg("""
        component ValidCompWithVariability7 {
          varif (false) {
            port in int i;
            a.b.C sub;
            sub.o -> i;
          }
        }"""
      ),
      // hidden channel, source direction mismatch, dead variation point
      arg("""
        component ValidCompWithVariability8 {
          varif (false) {
            a.b.B sub1, sub2;
            sub2.i -> sub1.i;
          }
        }"""
      ),
      // hidden channel, target direction mismatch, dead variation point
      arg("""
        component ValidCompWithVariability9 {
          varif (false) {
            a.b.C sub1, sub2;
            sub2.o -> sub1.o;
          }
        }"""
      ),
      // in port forward, source direction mismatch, dead feature
      arg("""
        component ValidCompWithVariability10 {
          feature f;
          varif (f) {
            port out int o;
            a.b.B sub;
            o -> sub.i;
          }
          constraint (!f);
        }"""
      ),
      // in port forward, target direction mismatch, dead feature
      arg("""
        component ValidCompWithVariability11 {
          feature f;
          varif (f) {
            port in int i;
            a.b.C sub;
            i -> sub.o;
          }
          constraint (!f);
        }"""
      ),
      // out port forward, source direction mismatch, dead feature
      arg("""
        component ValidCompWithVariability12 {
          feature f;
          varif (f) {
            port out int o;
            a.b.B sub;
            sub.i -> o;
          }
          constraint (!f);
        }"""
      ),
      // out port forward, target direction mismatch, dead feature
      arg("""
        component ValidCompWithVariability13 {
          feature f;
          varif (f) {
            port in int i;
            a.b.C sub;
            sub.o -> i;
          }
          constraint (!f);
        }"""
      ),
      // hidden channel, source direction mismatch, dead feature
      arg("""
        component ValidCompWithVariability14 {
          feature f;
          varif (f) {
            a.b.B sub1, sub2;
            sub2.i -> sub1.i;
          }
          constraint (!f);
        }"""
      ),
      // hidden channel, target direction mismatch, dead feature
      arg("""
        component ValidCompWithVariability15 {
          feature f;
          varif (f) {
            a.b.C sub1, sub2;
            sub2.o -> sub1.o;
          }
          constraint (!f);
        }"""
      ),
      // in port forward, subcomponent interface with variable direction
      arg("""
        component ValidCompWithVariability16 {
          port in int i;
          a.b.E sub;
          i -> sub.io;
          constraint (sub.ff);
        }"""
      ),
      // out port forward, subcomponent interface with variable direction
      arg("""
        component ValidCompWithVariability17 {
          port out int o;
          a.b.E sub;
          sub.io -> o;
          constraint (!sub.ff);
        }"""
      ),
      // hidden channel, subcomponent interface with variable direction
      arg("""
        component ValidCompWithVariability18 {
          a.b.E sub1;
          a.b.E sub2;
          sub2.io -> sub1.io;
          constraint (sub1.ff && !sub2.ff);
        }"""
      ),
      // in port forward, component interface with variable direction
      arg("""
        component ValidCompWithVariability19 {
          feature f;
          varif (f) {
            port in int io;
          } else {
            port out int io;
          }
          a.b.F sub;
          io -> sub.io;
          constraint (f);
        }"""
      ),
      // out port forward, subcomponent interface with variable direction
      arg("""
        component ValidCompWithVariability20 {
          feature f;
          varif (f) {
            port in int io;
          } else {
            port out int io;
          }
          a.b.G sub;
          sub.io -> io;
          constraint (!f);
        }"""
      ),
      // port forward, component & subcomponent interfaces with variable direction
      arg("""
        component ValidCompWithVariability21 {
          feature f;
          varif (f) {
            port in int io;
            io -> sub.io;
          } else {
            port out int io;
            sub.io -> io;
          }
          a.b.E sub;
          constraint (sub.ff == f);
        }"""
      ),
      // in port forward, subcomponent with variable type
      arg("""
        component ValidCompWithVariability22 {
          feature f;
          port in int io;
          varif (f) {
            a.b.F sub;
          } else {
            a.b.G sub;
          }
          io -> sub.io;
          constraint (f);
        }"""
      ),
      // out port forward, subcomponent interface with variable direction
      arg("""
        component ValidCompWithVariability23 {
          feature f;
          port out int io;
          varif (f) {
            a.b.F sub;
          } else {
            a.b.G sub;
          }
          sub.io -> io;
          constraint (!f);
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // in port forward, source direction mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            varif (true) {
              port out int o;
              a.b.B sub;
              o -> sub.i;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            varif (true) {
              port in int i;
              a.b.C sub;
              i -> sub.o;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability3 {
            varif (true) {
              port out int o;
              a.b.B sub;
              sub.i -> o;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability4 {
            varif (true) {
              port in int i;
              a.b.C sub;
              sub.o -> i;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability5 {
            varif (true) {
              a.b.B sub1, sub2;
              sub2.i -> sub1.i;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch, tautological variation point
      arg("""
          component InvalidCompWithVariability6 {
            varif (true) {
              a.b.C sub1, sub2;
              sub2.o -> sub1.o;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, source direction mismatch, core feature
      arg("""
          component InvalidCompWithVariability7 {
            feature f;
            varif (f) {
              port out int o;
              a.b.B sub;
              o -> sub.i;
            }
            constraint (f);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, core feature
      arg("""
          component InvalidCompWithVariability8 {
            feature f;
            varif (f) {
              port in int i;
              a.b.C sub;
              i -> sub.o;
            }
            constraint (f);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, core feature
      arg("""
          component InvalidCompWithVariability9 {
            feature f;
            varif (f) {
              port out int o;
              a.b.B sub;
              sub.i -> o;
            }
            constraint (f);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch, core feature
      arg("""
          component InvalidCompWithVariability10 {
            feature f;
            varif (f) {
              port in int i;
              a.b.C sub;
              sub.o -> i;
            }
            constraint (f);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch, core feature
      arg("""
          component InvalidCompWithVariability11 {
            feature f;
            varif (f) {
              a.b.B sub1, sub2;
              sub2.i -> sub1.i;
            }
            constraint (f);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch, core feature
      arg("""
          component InvalidCompWithVariability12 {
            feature f;
            varif (f) {
              a.b.C sub1, sub2;
              sub2.o -> sub1.o;
            }
            constraint (f);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, subcomponent interface with variable direction
      arg("""
          component InvalidCompWithVariability13 {
            port in int i;
            a.b.E sub;
            i -> sub.io;
            constraint (!sub.ff);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, subcomponent interface with variable direction
      arg("""
          component InvalidCompWithVariability14 {
            port out int o;
            a.b.E sub;
            sub.io -> o;
            constraint (!sub.ff);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, source and target direction mismatch, subcomponent interface with variable direction
      arg("""
          component InvalidCompWithVariability15 {
            a.b.E sub1;
            a.b.E sub2;
            sub2.io -> sub1.io;
            constraint (!sub1.ff && sub2.ff);
          }""",
        SOURCE_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, source direction mismatch, component interface with variable direction
      arg("""
          component InvalidCompWithVariability16 {
            feature f;
            varif (f) {
              port in int io;
            } else {
              port out int io;
            }
            a.b.F sub;
            io -> sub.io;
            constraint (!f);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch, subcomponent interface with variable direction
      arg("""
          component InvalidCompWithVariability17 {
            feature f;
            varif (f) {
              port in int io;
            } else {
              port out int io;
            }
            a.b.G sub;
            sub.io -> io;
            constraint (f);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // port forward, source and target direction mismatch, component & subcomponent interfaces with variable direction
      arg("""
          component InvalidCompWithVariability18 {
            feature f;
            varif (f) {
              port in int io;
              io -> sub.io;
            } else {
              port out int io;
              sub.io -> io;
            }
            a.b.E sub;
            constraint (sub.ff == !f);
          }""",
        SOURCE_DIRECTION_MISMATCH, TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, subcomponent with variable type
      arg("""
          component InvalidCompWithVariability19 {
            feature f;
            port in int io;
            varif (f) {
              a.b.F sub;
            } else {
              a.b.G sub;
            }
            io -> sub.io;
            constraint (!f);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, subcomponent interface with variable direction
      arg("""
          component InvalidCompWithVariability20 {
            feature f;
            port out int io;
            varif (f) {
              a.b.F sub;
            } else {
              a.b.G sub;
            }
            sub.io -> io;
            constraint (f);
          }""",
        SOURCE_DIRECTION_MISMATCH
      )
    );
  }
}
