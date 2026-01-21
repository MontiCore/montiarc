/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.io.IOException;
import java.util.stream.Stream;

import static montiarc.MontiArcMill.globalScope;
import static montiarc.MontiArcMill.scope;
import static montiarc.MontiArcMillTOP.fieldSymbolBuilder;
import static montiarc.MontiArcMillTOP.oOTypeSymbolBuilder;
import static montiarc.util.ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL;
import static montiarc.util.ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE;
import static montiarc.util.ArcComputeError.MULTIPLE_INIT;
import static montiarc.util.ArcError.CONNECTORS_IN_ATOMIC;
import static montiarc.util.ArcError.CONNECTOR_TIMING_MISMATCH;
import static montiarc.util.ArcError.CONNECTOR_TYPE_MISMATCH;
import static montiarc.util.ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR;
import static montiarc.util.ArcError.FEEDBACK_CAUSALITY;
import static montiarc.util.ArcError.IN_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.IN_PORT_UNUSED;
import static montiarc.util.ArcError.MISSING_PORT;
import static montiarc.util.ArcError.MULTIPLE_BEHAVIOR;
import static montiarc.util.ArcError.OUT_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.OUT_PORT_UNUSED;
import static montiarc.util.ArcError.SOURCE_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.TARGET_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.UNIQUE_IDENTIFIER_NAMES;
import static montiarc.util.MCError.CANT_FIND_SYMBOL_IN_EXPRESSION;
import static montiarc.util.MCError.EXPR_EQUAL_OP_NOT_APPLICABLE;
import static montiarc.util.SCError.PRECONDITION_NOT_BOOLEAN;
import static org.assertj.core.api.Assertions.assertThat;

class VariantCoCosTest extends MontiArcTestBase {

  @BeforeEach
  void initSymbols() {
    globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpEnums();
    setUpComponents();
  }

  void setUpComponents() {
    compile("package a.b; component A { port in int i; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port in int i1, i2; port out int o; }");
    compile("package a.b; component D { feature ff; varif (ff) { port in int io; } else { port out int io; } }");
    compile("package a.b; component E { port in boolean i; }");
    compile("package a.b; component F { port out boolean o; }");
    compile("package a.b; component G { feature ff; varif (ff) { port in boolean i, out boolean o; } else { port in int i, out int o; } }");
    compile("package a.b; component H { feature ff; varif (ff) { port in int io; } else { port out boolean io; } }");
    compile("package a.b; component I { feature ff; varif (ff) { port sync in int i; } else { port in int i; } }");
    compile("package a.b; component J { feature ff; varif (ff) { port sync out int o; } else { port out int o; } }");
    compile("package a.b; component K { port in int i; port out int o1, out int o2; <<delayed>> compute {}} ");
    compile("package a.b; component L { port in int i; port out int o; feature ff; varif (!ff) { <<delayed>> compute {}} }");
    compile("package a.b; component M { feature ff; varif (ff) { port in int i; } }");
    compile("package a.b; component N { feature ff; varif (ff) { port out int o; } }");
    compile("package a.b; component O { feature ff; port sync out int o; a.b.J sub; sub.o -> o; constraint(ff == sub.ff); }");
    compile("package a.b; component P<A,B> { feature ff; varif (ff) { port out A o; } else { port out B o; } }");
  }

  void setUpEnums() {
    OOTypeSymbol onOffEnumType = oOTypeSymbolBuilder()
      .setName("OnOff").setIsEnum(true).setIsPublic(true)
      .setSpannedScope(scope()).build();
    onOffEnumType.getSpannedScope().add(fieldSymbolBuilder()
      .setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true)
      .setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    onOffEnumType.getSpannedScope().add(fieldSymbolBuilder()
      .setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true)
      .setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    globalScope().add(onOffEnumType);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic component, no variability
    "component ValidComp1 { }",
    // in port forward, connector type mismatch, excluded variation point
    """
      component ValidComp24 {
        varif (false) {
          port in int i;
          a.b.E sub;
          i -> sub.i;
        }
      }""",
    // out port forward, connector type mismatch, excluded variation point
    """
      component ValidComp25 {
        varif (false) {
          port out int o;
          a.b.F sub;
          sub.o -> o;
        }
      }""",
    // hidden channel, connector type mismatch, excluded variation point
    """
      component ValidComp26 {
        varif (false) {
        a.b.E sub1;
        a.b.B sub2;
        sub2.o -> sub1.i;
        a.b.A sub3;
        a.b.F sub4;
        sub4.o -> sub3.i;
        }
      }""",
    // port forward, subcomponent with variable interface types (deselect feature)
    """
      component ValidComp27 {
        port in int i;
        port out int o;
        a.b.G sub;
        i -> sub.i;
        sub.o -> o;
        constraint (!sub.ff);
      }""",
    // port forward, subcomponent with variable interface types (select feature)
    """
      component ValidComp28 {
        port in boolean i;
        port out boolean o;
        a.b.G sub;
        i -> sub.i;
        sub.o -> o;
        constraint (sub.ff);
      }""",
    // port forward, component and subcomponent with variable interface types
    """
      component ValidComp29 {
        feature f;
        varif (f) {
          port in boolean i;
          port out boolean o;
        } else {
          port in int i;
          port out int o;
        }
        a.b.G sub;
        i -> sub.i;
        sub.o -> o;
        constraint (sub.ff == f);
      }""",
    // in port forward, subcomponent with variable interface timing (deselect feature)
    """
      component ValidComp30 {
        port in int i;
        a.b.I sub;
        i -> sub.i;
        constraint (!sub.ff);
      }""",
    // in port forward, subcomponent with variable interface timing (select feature)
    """
      component ValidComp31 {
        port sync in int i;
        a.b.I sub;
        i -> sub.i;
        constraint (sub.ff);
      }""",
    // out port forward, subcomponent with variable interface timing (deselect feature)
    """
      component ValidComp32 {
        port out int o;
        a.b.J sub;
        sub.o -> o;
        constraint (!sub.ff);
      }""",
    // out port forward, subcomponent with variable interface timing (select feature)
    """
      component ValidComp33 {
        port sync out int o;
        a.b.J sub;
        sub.o -> o;
        constraint (sub.ff);
      }""",
    // in port forward, component and subcomponent with variable interface timing
    """
      component ValidComp34 {
        feature f;
        varif (f) {
          port sync in int i;
        } else {
          port in int i;
        }
        a.b.I sub;
        i -> sub.i;
        constraint (sub.ff == f);
      }""",
    // out port forward, component and subcomponent with variable interface timing
    """
      component ValidComp35 {
        feature f;
        varif (f) {
        port sync out int o;
        } else {
          port out int o;
        }
        a.b.J sub;
        sub.o -> o;
        constraint (sub.ff == f);
      }""",
    // feedback loop
    """
      component ValidComp36 {
        port in int i;
        port out int o;
        a.b.C sub1;
        a.b.K sub2;
        i -> sub1.i1;
        sub1.o -> sub2.i;
        sub2.o1 -> o;
        sub2.o2 -> sub1.i2;
      }""",
    // feedback loop, subcomponent with variable interface delay (deselect feature)
    """
      component ValidComp37 {
        port in int i;
        port out int o;
        a.b.C sub1;
        a.b.L sub2;
        i -> sub1.i1;
        sub1.o -> sub2.i;
        sub2.o -> o;
        sub2.o -> sub1.i2;
        constraint (!sub2.ff);
      }""",
    // feedback loop, component with variable configuration and subcomponent with variable interface delay
    """
      component ValidComp38 {
        port in int i;
        port out int o;
        feature f;
        a.b.C sub1;
        a.b.L sub2;
        i -> sub1.i1;
        sub1.o -> sub2.i;
        sub2.o -> o;
        varif (!f) {
          sub2.o -> sub1.i2;
        } else {
          i -> sub1.i2;
        }
        constraint (sub2.ff == f);
      }""",
    // in port unused, component with variable configuration, excluded variation point
    """
      component ValidComp39 {
        varif (false) {
          port in int i;
        }
        a.b.A sub1;
        a.b.B sub2;
        sub2.o -> sub1.i;
      }""",
    // out port unused, component with variable configuration, excluded variation point
    """
      component ValidComp40 {
        varif (false) {
          port out int o;
        }
        a.b.A sub1;
        a.b.B sub2;
        sub2.o -> sub1.i;
      }""",
    // in port not connected, component with variable configuration, excluded variation point
    """
      component ValidComp41 {
        varif (false) {
          a.b.A sub;
        }
      }""",
    // out port not connected, component with variable configuration, excluded variation point
    """
      component ValidComp42 {
        varif (false) {
          a.b.B sub;
        }
      }""",
    // in port not connected, subcomponent with variable configuration (deselected feature)
    """
      component ValidComp43 {
        a.b.M sub;
        constraint (!sub.ff);
      }""",
    // out port not connected, subcomponent with variable configuration (deselected feature)
    """
      component ValidComp44 {
        a.b.N sub;
        constraint (!sub.ff);
      }""",
    // out port forward, subcomponent with variable generic interface type (selected feature)

    """
      component ValidComp45<T> {
        port out T o;
        a.b.P<T, java.lang.Integer> sub;
        sub.o -> o;
        constraint(sub.ff);
      }""",
    // out port forward, subcomponent with variable generic interface type
    """
      component ValidComp46<A, B> {
        feature f;
        varif (f) {
          port out A o;
        }
        else {
          port out B o;
        }
        a.b.P<A, B> sub;
        sub.o -> o;
        constraint(sub.ff == f);
      }""",
    // in port forward with inherited port
    """
      component ValidComp47 extends a.b.M {
        varif (ff) {
          a.b.A sub;
          i -> sub.i;
        }
      }""",
    // inherited port that switches direction
    """
      component ValidComp48 extends a.b.D {
        varif (ff) {
          a.b.A sub;
          io -> sub.i;
        } else {
          a.b.B sub;
          sub.o -> io;
        }
      }""",
    // inherited generic port that switches between int and boolean
    """
      component ValidComp49 extends a.b.P<int, boolean> {
        varif (ff) {
          a.b.B sub;
        } else {
          a.b.F sub;
        }
        sub.o -> o;
      }""",
    // atomic component with port that switches types
    """
      component ValidComp50 {
        feature ff;
        varif (ff) {
        port out int p;
        } else {
          port out double p;
        }
        compute {
          p = 5;
        }
      }""",
    // Switches between behaviors
    """
      component ValidComp51 {
        feature f;
        varif (f) {
          compute { }
        } else {
          compute { }
        }
      }""",
    // Switches between field initial values
    """
      component ValidComp52 {
        feature f;
        port out int o;
        varif (f) {
          int i = 0;
        } else {
          int i = 5;
        }
        compute {
          o = i;
        }
      }""",
    // Switches between automaton behaviors with preconditions
    """
      component ValidComp53 {
        feature f;
        varif (f) {
          port in int i;
          automaton {
            initial state A;
            A -> A [i > 1];
          }
        } else {
          port in boolean i;
          automaton {
            initial state A;
            A -> A [i];
          }
        }
      }""",
    // Switches between field types
    """
      component ValidComp54 {
        feature f;
        port out double o;
        varif (f) {
          double i = 2.5;
        } else {
          int i = 2;
        }
        compute {
          o = i;
        }
      }""",
    // Enum constants map to different values
    """
      component ValidComp55(OnOff onOff) {
        varif(onOff == OnOff.OFF) {
          automaton {
            initial state S;
          }
        }
        varif(onOff == OnOff.ON) {
          automaton {
            initial state S;
          }
        }
      }""",
    // in port forward, timing match, subcomponent with variable interface timing (deselect feature)
    """
      component ValidComp56 {
        port sync in int i;
        a.b.I sub;
        i -> sub.i;
        constraint (!sub.ff);
      }""",
    // out port forward, timing mismatch, subcomponent with variable interface timing (select feature)
    """
      component ValidComp57 {
        port out int o;
        a.b.J sub;
        sub.o -> o;
        constraint (sub.ff);
      }""",
    // Event trigger constraint always required
    """
      component ValidComp58 {
        feature f;
        varif (f) {
          port in boolean i;
        }
        automaton {
          initial state A;
          A -> A i;
        }
        constraint (f);
      }""",
  })
  @DisableIfDisplayName(contains = {
    "ValidComp23",
    "ValidComp27",
    "ValidComp28",
    "ValidComp29",
    "ValidComp33",
    "ValidComp35",
    "ValidComp37",
    "ValidComp38",
    "ValidComp45",
    "ValidComp46",
    "ValidComp47",
    "ValidComp48",
    "ValidComp49",
    "ValidComp53",
  })
  void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = MontiArcCoCos.afterSymTab2(true);

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @DisableIfDisplayName(contains = {
    "InvalidComp26",
    "InvalidComp27",
    "InvalidComp29",
    "InvalidComp34",
    "InvalidComp35",
    "InvalidComp37",
    "InvalidComp38",
    "InvalidComp41",
    "InvalidComp60",
    "InvalidComp61",
    "InvalidComp62",
    "InvalidComp63",
    "InvalidComp64",
    "InvalidComp69",
    "InvalidComp74",
    "InvalidComp75",
    "InvalidComp76",
    "InvalidComp77"
  })
  void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = MontiArcCoCos.afterSymTab2(true);

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // in port forward, source direction mismatch
      arg("""
          component InvalidComp1 {
            port out int o;
            a.b.A sub;
            o -> sub.i;
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch
      arg("""
          component InvalidComp2 {
            port in int i;
            a.b.B sub;
            i -> sub.o;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch
      arg("""
          component InvalidComp3 {
            port out int o;
            a.b.A sub;
            sub.i -> o;
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch
      arg("""
          component InvalidComp4 {
            port in int i;
            a.b.B sub;
            sub.o -> i;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch
      arg("""
          component InvalidComp5 {
            a.b.A sub1, sub2;
            sub2.i -> sub1.i;
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch
      arg("""
          component InvalidComp6 {
            a.b.B sub1, sub2;
            sub2.o -> sub1.o;
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, source direction mismatch, single variation point
      arg("""
          component InvalidComp7 {
            port out int o;
            feature f;
            varif (f) {
              a.b.A sub;
              o -> sub.i;
            }
            constraint (f);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, single variation point
      arg("""
          component InvalidComp8 {
            port in int i;
            feature f;
            varif (f) {
              a.b.B sub;
              i -> sub.o;
            }
            constraint (f);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, single variation point
      arg("""
          component InvalidComp9 {
            port out int o;
            feature f;
            varif (f) {
              a.b.A sub;
              sub.i -> o;
            }
            constraint (f);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch, single variation point
      arg("""
          component InvalidComp10 {
            port in int i;
            feature f;
            varif (f) {
              a.b.B sub;
              sub.o -> i;
            }
            constraint (f);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, single variation point, source direction mismatch
      arg("""
          component InvalidComp11 {
            feature f;
            varif (f) {
              a.b.A sub1, sub2;
              sub2.i -> sub1.i;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, single variation point, target direction mismatch
      arg("""
          component InvalidComp12 {
            feature f;
            varif (f) {
              a.b.B sub1, sub2;
              sub2.o -> sub1.o;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, source direction mismatch, included variation point
      arg("""
          component InvalidComp13 {
            varif (true) {
              port out int o;
              a.b.A sub;
              o -> sub.i;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, included variation point
      arg("""
          component InvalidComp14 {
            varif (true) {
              port in int i;
              a.b.B sub;
              i -> sub.o;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, included variation point
      arg("""
          component InvalidComp15 {
            varif (true) {
              port out int o;
              a.b.A sub;
              sub.i -> o;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch, included variation point
      arg("""
          component InvalidComp16 {
            varif (true) {
              port in int i;
              a.b.B sub;
              sub.o -> i;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch, included variation point
      arg("""
          component InvalidComp17 {
            varif (true) {
              a.b.A sub1, sub2;
              sub2.i -> sub1.i;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch, included variation point
      arg("""
          component InvalidComp18 {
            varif (true) {
              a.b.B sub1, sub2;
              sub2.o -> sub1.o;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, source direction mismatch, unconstrained feature
      arg("""
          component InvalidComp19 {
            feature f;
            constraint(f);
            varif (f) {
              port out int o;
              a.b.A sub;
              o -> sub.i;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, unconstrained feature
      arg("""
          component InvalidComp20 {
            feature f;
            constraint(f);
            varif (f) {
              port in int i;
              a.b.B sub;
              i -> sub.o;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, unconstrained feature
      arg("""
          component InvalidComp21 {
            feature f;
            constraint(f);
            varif (f) {
              port out int o;
              a.b.A sub;
              sub.i -> o;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // out port forward, target direction mismatch, unconstrained feature
      arg("""
          component InvalidComp22 {
            feature f;
            constraint(f);
            varif (f) {
              port in int i;
              a.b.B sub;
              sub.o -> i;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch, unconstrained feature
      arg("""
          component InvalidComp23 {
            feature f;
            constraint(f);
            varif (f) {
              a.b.A sub1, sub2;
              sub2.i -> sub1.i;
            }
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch, unconstrained feature
      arg("""
          component InvalidComp24 {
            feature f;
            constraint(f);
            varif (f) {
              a.b.B sub1, sub2;
              sub2.o -> sub1.o;
            }
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, target direction mismatch, subcomponent with variable interface direction
      arg("""
          component InvalidComp25 {
            port in int i;
            a.b.D sub;
            i -> sub.io;
            constraint (!sub.ff);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // out port forward, source direction mismatch, subcomponent with variable interface direction
      arg("""
          component InvalidComp26 {
            port out int o;
            a.b.D sub;
            sub.io -> o;
            constraint (sub.ff);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, source direction mismatch, subcomponent with variable interface direction
      arg("""
          component InvalidComp27 {
            a.b.D sub1;
            a.b.D sub2;
            sub2.io -> sub1.io;
            constraint (sub1.ff && sub2.ff);
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // hidden channel, target direction mismatch, subcomponent with variable interface direction
      arg("""
          component InvalidComp28 {
            a.b.D sub1;
            a.b.D sub2;
            sub2.io -> sub1.io;
            constraint (!sub1.ff && !sub2.ff);
          }""",
        TARGET_DIRECTION_MISMATCH
      ),
      // hidden channel, source and target direction mismatch, subcomponent with variable interface direction
      arg("""
          component InvalidComp29 {
            a.b.D sub1;
            a.b.D sub2;
            sub2.io -> sub1.io;
            constraint (!sub1.ff && sub2.ff);
          }""",
        SOURCE_DIRECTION_MISMATCH,
        TARGET_DIRECTION_MISMATCH
      ),
      // in port forward, connector type mismatch, included variation point
      arg("""
          component InvalidComp30 {
            varif (true) {
              port in int i;
              a.b.E sub;
              i -> sub.i;
            }
          }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // out port forward, connector type mismatch, included variation point
      arg("""
          component InvalidComp31 {
            varif (true) {
            port out int o;
              a.b.F sub;
              sub.o -> o;
            }
          }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // hidden channel, connector type mismatch, included variation point
      arg("""
          component InvalidComp32 {
            varif (true) {
              a.b.E sub1;
              a.b.B sub2;
              sub2.o -> sub1.i;
              a.b.A sub3;
              a.b.F sub4;
              sub4.o -> sub3.i;
            }
          }""",
        CONNECTOR_TYPE_MISMATCH,
        CONNECTOR_TYPE_MISMATCH
      ),
      // port forward, connector type mismatch, component and subcomponent with variable interface types
      arg("""
          component InvalidComp33 {
            feature f;
            varif (f) {
              port in boolean i;
              port out boolean o;
            } else {
              port in int i;
              port out int o;
            }
            a.b.G sub;
            i -> sub.i;
            sub.o -> o;
            constraint (sub.ff == !f);
          }""",
        CONNECTOR_TYPE_MISMATCH,
        CONNECTOR_TYPE_MISMATCH,
        CONNECTOR_TYPE_MISMATCH,
        CONNECTOR_TYPE_MISMATCH
      ),
      // port forward, connector direction and type mismatch, component and subcomponent with variable interface types
      arg("""
          component InvalidComp34 {
            feature f;
            a.b.H sub;
            varif (f) {
              port out boolean o;
              sub.io -> o;
            } else {
              port in int i;
              i -> sub.io;
            }
            constraint (sub.ff == f);
          }""",
        SOURCE_DIRECTION_MISMATCH,
        CONNECTOR_TYPE_MISMATCH,
        TARGET_DIRECTION_MISMATCH,
        CONNECTOR_TYPE_MISMATCH
      ),
      // in port forward, timing mismatch, subcomponent with variable interface timing (select feature)
      arg("""
          component InvalidComp35 {
            port in int i;
            a.b.I sub;
            i -> sub.i;
            constraint (sub.ff);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // out port forward, timing mismatch, subcomponent with variable interface timing (deselect feature)
      arg("""
          component InvalidComp36 {
            port sync out int o;
            a.b.J sub;
            sub.o -> o;
            constraint (!sub.ff);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // in port forward, timing mismatch, component and subcomponent with variable interface timing
      arg("""
          component InvalidComp37 {
            feature f;
            varif (f) {
              port sync in int i;
            } else {
              port in int i;
            }
            a.b.I sub;
            i -> sub.i;
            constraint (sub.ff == !f);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // out port forward, timing mismatch, component and subcomponent with variable interface timing
      arg("""
          component InvalidComp38 {
            feature f;
            varif (f) {
              port sync out int o;
            } else {
              port out int o;
            }
            a.b.J sub;
            sub.o -> o;
            constraint (sub.ff == !f);
          }""",
        CONNECTOR_TIMING_MISMATCH
      ),
      // feedback loop, weakly-causal feedback
      arg("""
          component InvalidComp39 {
            feature f;
            port in int i;
            port out int o;
            a.b.C sub1;
            a.b.L sub2;
            i -> sub1.i1;
            sub1.o -> sub2.i;
            sub2.o -> sub1.i2;
            sub2.o -> o;
            constraint(sub2.ff == f);
          }""",
        FEEDBACK_CAUSALITY
      ),
      // feedback loop, weakly-causal feedback, subcomponent with variable interface delay (select feature)
      arg("""
          component InvalidComp40 {
            port in int i;
            port out int o;
            a.b.C sub1;
            a.b.L sub2;
            i -> sub1.i1;
            sub1.o -> sub2.i;
            sub2.o -> o;
            sub2.o -> sub1.i2;
            constraint (sub2.ff);
          }""",
        FEEDBACK_CAUSALITY
      ),
      // feedback loop, weakly-causal feedback, component with variable configuration and subcomponent with variable interface delay
      arg("""
          component InvalidComp41 {
            port in int i;
            port out int o;
            feature f;
            a.b.C sub1;
            a.b.L sub2;
            i -> sub1.i1;
            sub1.o -> sub2.i;
            sub2.o -> o;
            varif (f) {
              sub2.o -> sub1.i2;
            } else {
              i -> sub1.i2;
            }
            constraint (sub2.ff == f);
          }""",
        FEEDBACK_CAUSALITY
      ),
      // in port unused
      arg("""
          component InvalidComp42 {
            port in int i;
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i;
          }""",
        IN_PORT_UNUSED
      ),
      // out port unused
      arg("""
          component InvalidComp43 {
            port out int o;
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i;
          }""",
        OUT_PORT_UNUSED
      ),
      // ports unused
      arg("""
          component InvalidComp44 {
            port in int i;
            port out int o;
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i;
          }""",
        IN_PORT_UNUSED,
        OUT_PORT_UNUSED
      ),
      // in port unused, component with variable configuration, included variation point
      arg("""
          component InvalidComp45 {
            varif (true) {
              port in int i;
            }
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i;
          }""",
        IN_PORT_UNUSED
      ),
      // out port unused, component with variable configuration, included variation point
      arg("""
          component InvalidComp46 {
            varif (true) {
              port out int o;
            }
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i;
          }""",
        OUT_PORT_UNUSED
      ),
      // in port unused, component with variable configuration
      arg("""
          component InvalidComp47 {
            feature f;
            port in int i;
            varif (f) {
              a.b.A sub;
              i -> sub.i;
            }
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i;
          }""",
        IN_PORT_UNUSED
      ),
      // out port unused, component with variable configuration
      arg("""
          component InvalidComp48 {
            feature f;
            port out int o;
            varif (f) {
              a.b.B sub;
              sub.o -> o;
            }
            a.b.A sub1;
            a.b.B sub2;
            sub2.o -> sub1.i;
          }""",
        OUT_PORT_UNUSED
      ),
      // in port not connected
      arg("""
          component InvalidComp49 {
            a.b.A sub;
          }""",
        IN_PORT_NOT_CONNECTED
      ),
      // out port not connected
      arg("""
          component InvalidComp50 {
            a.b.B sub;
          }""",
        OUT_PORT_NOT_CONNECTED
      ),
      // ports not connected
      arg("""
          component InvalidComp51 {
            a.b.C sub;
          }""",
        IN_PORT_NOT_CONNECTED,
        IN_PORT_NOT_CONNECTED,
        OUT_PORT_NOT_CONNECTED
      ),
      // in port not connected, component with variable configuration, included variation point
      arg("""
          component InvalidComp52 {
            varif (true) {
              a.b.A sub;
            }
          }""",
        IN_PORT_NOT_CONNECTED
      ),
      // out port not connected, component with variable configuration, included variation point
      arg("""
          component InvalidComp53 {
            varif (true) {
              a.b.B sub;
            }
          }""",
        OUT_PORT_NOT_CONNECTED
      ),
      // in port not connected, component with variable configuration
      arg("""
          component InvalidComp54 {
            feature f;
            a.b.A sub;
            varif (f) {
              port in int i;
              i -> sub.i;
            }
          }""",
        IN_PORT_NOT_CONNECTED
      ),
      // out port not connected, component with variable configuration
      arg("""
          component InvalidComp55 {
            feature f;
            a.b.B sub;
            varif (f) {
              port out int o;
              sub.o -> o;
            }
          }""",
        OUT_PORT_NOT_CONNECTED
      ),
      // in port unused, in port not connected, component with variable configuration
      arg("""
          component InvalidComp56 {
            feature f;
            a.b.A sub;
            port in int i;
            varif (f) {
              i -> sub.i;
            }
          }""",
        IN_PORT_UNUSED,
        IN_PORT_NOT_CONNECTED
      ),
      // out port unused, out port not connected, component with variable configuration
      arg("""
          component InvalidComp57 {
            feature f;
            a.b.B sub;
            port out int o;
            varif (f) {
              sub.o -> o;
            }
          }""",
        OUT_PORT_UNUSED,
        OUT_PORT_NOT_CONNECTED
      ),
      // in port not connected, subcomponent with variable configuration (selected feature)
      arg("""
          component InvalidComp58 {
            a.b.M sub;
            constraint (sub.ff);
          }""",
        IN_PORT_NOT_CONNECTED
      ),
      // out port not connected, subcomponent with variable configuration (selected feature)
      arg("""
          component InvalidComp59 {
            a.b.N sub;
            constraint (sub.ff);
          }""",
        OUT_PORT_NOT_CONNECTED
      ),
      // Multiple behaviors if feature is selected
      arg("""
          component InvalidComp60 {
            feature f;
            varif (f) {
              compute { }
              compute { }
            } else {
              a.b.N sub;
              constraint (!sub.ff);
            }
          }""",
        MULTIPLE_BEHAVIOR,
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      ),
      // out port forward, subcomponent with variable generic interface type (deselected feature)
      arg("""
          component InvalidComp61<T> {
            port out T o;
            a.b.P<T, java.lang.Integer> sub;
            sub.o -> o;
            constraint(!sub.ff);
          }""",
        CONNECTOR_TYPE_MISMATCH
      ),
      // in port forward with inherited port
      arg("""
          component InvalidComp62 extends a.b.M {
            a.b.A sub;
            i -> sub.i;
          }""",
        MISSING_PORT
      ),
      // inherited port that switches direction
      arg("""
          component InvalidComp63 extends a.b.D {
            a.b.A sub;
            io -> sub.i;
          }""",
        SOURCE_DIRECTION_MISMATCH
      ),
      // atomic component with port that switches existence
      arg(
        """
          component InvalidComp64 {
            feature ff;
            varif (ff) {
              port out int p;
            }
            compute {
              p = 5;
            }
          }""", CANT_FIND_SYMBOL_IN_EXPRESSION
      ),
      // Multiple behaviors if feature is selected
      arg("""
          component InvalidComp65 {
            feature f;
            varif (f) {
              compute { }
            }
            compute { }
          }""",
        MULTIPLE_BEHAVIOR
      ),
      // Multiple behaviors if both features are selected
      arg("""
          component InvalidComp66 {
            feature f1, f2;
            varif (f1) {
              compute { }
            }
            varif (f2) {
              compute { }
            }
          }""",
        MULTIPLE_BEHAVIOR
      ),
      // Multiple fields if both features are selected
      arg("""
          component InvalidComp67 {
            feature f1, f2;
            varif (f1) {
              int i = 0;
            }
            varif (f2) {
              int i = 1;
            }
          }""",
        UNIQUE_IDENTIFIER_NAMES
      ),
      // component that switches between atomic and decomposed with connector
      arg("""
          component InvalidComp68 {
            feature f;
            port in int i;
            port out int o;
            varif (f) {
              port in int i2;
              a.b.C sub;
              i -> sub.i1;
              i2 -> sub.i2;
              sub.o -> o;
            } else {
              i -> o;
            }
          }""",
        CONNECTORS_IN_ATOMIC
      ),
      // Switches between automaton behaviors with preconditions
      arg(
        """
          component InvalidComp69 {
            feature f;
            varif(f){
              port in int i;
              automaton {
                initial state B;
                B -> B [i];
              }
            } else {
              port in boolean i;
              automaton {
                initial state A;
                A -> A [i == "a"];
              }
            }
          }""",
        EXPR_EQUAL_OP_NOT_APPLICABLE,
        PRECONDITION_NOT_BOOLEAN
      ),
      // Enum constants map to same value
      arg("""
          component InvalidComp70(OnOff onOff) {
            varif(onOff == OnOff.OFF) {
              automaton {
                initial state S;
              }
            }
            varif(onOff == OnOff.OFF) {
              automaton {
                initial state S;
              }
            }
          }""",
        MULTIPLE_BEHAVIOR
      ),
      // Multiple identifier if feature is selected
      arg("""
          component InvalidComp71(boolean i) {
            feature f1;
            varif (f1) {
              int i = 0;
            }
          }""",
        UNIQUE_IDENTIFIER_NAMES
      ),
      // Multiple identifier if feature is selected
      arg("""
          component InvalidComp72(boolean i) {
            feature f1;
            varif (f1) {
              port in int i;
            }
          }""",
        UNIQUE_IDENTIFIER_NAMES
      ),
      // Multiple identifier feature should not throw exception
      arg("""
          component InvalidComp73 {
            feature f;
            feature f;
            varif (f) { }
          }""",
        UNIQUE_IDENTIFIER_NAMES
      ),
      // Automaton with missing trigger symbol in one variant
      arg("""
          component InvalidComp74 {
            feature f;
            varif(f) {
              port in boolean i;
            }
            automaton {
              initial state A;
              A -> A i;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // Automaton in decomposed component variant
      arg("""
          component InvalidComp75 {
            feature f;
            varif(f) {
              a.b.B sub;
              port out int o;
              sub.o -> o;
            }
            automaton {
              initial state A;
              A -> A;
            }
          }""",
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      ),
      // Two Initial for compute
      arg("""
          component InvalidComp76 {
            feature f1, f2;
            varif(f1) {
              init { }
            }
            varif(f2) {
              init { }
            }
            compute { }
          }""",
        MULTIPLE_INIT
      ),
      // Init without compute
      arg("""
          component InvalidComp77 {
            feature f;
            varif(f) {
              compute { }
            }
            init { }
          }""",
        INIT_BLOCK_WITHOUT_COMPUTE
      )
    );
  }
}
