/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoNonSyncInputPortInEpsilonTransition;
import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
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
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

class NoNonSyncInputPortInEpsilonTransitionTest extends MontiArcTestBase {

  final static String SYMBOLS_DIR = "symbols";

  @BeforeEach
  public void setUp() {
    MontiArcMill.globalScope().setSymbolPath(
      new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR))
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - Write literal to output port in timed-event triggered transition
    "component Comp1 { " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = 0; } " +
      "  } " +
      "}",
    // 2 - Write literal to synchronous output port in timed-event triggered transition
    "component Comp2 { " +
      "  port sync out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = 0; } " +
      "  } " +
      "}",
    // 3 - Write value of component variable to output port in timed-event triggered transition
    "component Comp3 { " +
      "  port out int o; " +
      "  int v = 0; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = v; } " +
      "  } " +
      "}",
    // 5 - Write value of synchronous port to output port in timed-event triggered transition
    "component Comp4 { " +
      "  port sync in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = i; } " +
      "  } " +
      "}",
    // 5 - Write value of synchronous ports to output port in timed-event triggered transition
    "component Comp5 { " +
      "  port sync in int i1, i2; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = i1 + i2; } " +
      "  } " +
      "}",
    // 6 - Read value from and write to field in timed-event triggered transition
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp6 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    } " +
      "  } " +
      "}",
    // 7 - Read value from method call in timed-event triggered transition
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp7 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { " +
      "      o = v.i(); o = v.o(); " +
      "    } " +
      "  } " +
      "}",
    // 8 - Read value from and write to static field in timed-event triggered transition
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp8 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { " +
      "      o = OOTypeWithStaticFieldIO.i; " +
      "      o = OOTypeWithStaticFieldIO.o; " +
      "    } " +
      "  } " +
      "}",
    // 9 - Read value from static method call in timed-event triggered transition
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { " +
      "      o = OOTypeWithStaticFunctionIO.i(); " +
      "      o = OOTypeWithStaticFunctionIO.o(); " +
      "    } " +
      "  } " +
      "}",
    // 10 - Variable declaration shadows port in timed-event triggered transition
    "component Comp10 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { " +
      "      int i = 0; " +
      "      o = i; " +
      "    } " +
      "  } " +
      "}",
    // 11 - For control shadows port in timed-event triggered transition
    "component Comp11 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { " +
      "      for (int i = 0; i < 10; i++) { " +
      "        o = i; " +
      "      } " +
      "    } " +
      "  } " +
      "}",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoNonSyncInputPortInEpsilonTransition());

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
    checker.addCoCo(new NoNonSyncInputPortInEpsilonTransition());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - No non-synchronous input port in var declaration statement in timed-event triggered transition
      arg("component Comp1 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 2 - No non-synchronous input port in assignment expressions in timed-event triggered transition
      arg("component Comp2 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 3 - No non-synchronous input port in inc prefix expressions in timed-event triggered transition
      arg("component Comp3 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      ++i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 4 - No non-synchronous input port in dec prefix expressions in timed-event triggered transition
      arg("component Comp4 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      --i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 5 - No non-synchronous input port in inc suffix expressions in timed-event triggered transition
      arg("component Comp5 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      i++; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 6 - No non-synchronous input port in dec suffix expressions in timed-event triggered transition
      arg("component Comp6 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      i--; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 7 - No non-synchronous input port in boolean not expressions in timed-event triggered transition
      arg("component Comp7 { " +
          "  port in boolean i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      boolean x = true; " +
          "      x = ~i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 8 - No non-synchronous input port in logical not expressions in timed-event triggered transition
      arg("component Comp8 { " +
          "  port in boolean i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      boolean x = true; " +
          "      x = !i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 9 - No non-synchronous input port in multiply expressions (right) in timed-event triggered transition
      arg("component Comp9 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = 2 * i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 10 - No non-synchronous input port in multiply expressions (left) in timed-event triggered transition
      arg("component Comp10 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = i * 2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 11 - No non-synchronous input port in multiply expressions (both) in timed-event triggered transition
      arg("component Comp11 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = i * i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 12 - No non-synchronous input port in multiply expressions (both) in timed-event triggered transition
      arg("component Comp12 { " +
          "  port in int i1; " +
          "  port sync in int i2; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = i1 * i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 13 - No non-synchronous input port in infix expressions in timed-event triggered transition
      arg("component Comp13 { " +
          "  port in int i1; " +
          "  port in boolean i2; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      boolean y = true; " +
          "      x = 2 / i1;  x = i1 / 2; " +
          "      x = 2 % i1;  x = i1 % 2; " +
          "      x = 2 + i1;  x = i1 + 2; " +
          "      x = 2 - i1;  x = i1 - 2; " +
          "      y = i1 <= 2; y = 2 <= i1; " +
          "      y = i1 >= 2; y = 2 >= i1; " +
          "      y = i1 < 2;  y = 2 < i1; " +
          "      y = i1 > 2;  y = 2 > i1; " +
          "      y = i1 == 2; y = 2 == i1; " +
          "      y = i1 != 2; y = 2 != i1; " +
          "      y = i2 && 2; y = 2 && i2; " +
          "      y = i2 || 2; y = 2 || i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 14 - No non-synchronous input port in conditional expressions (condition) in timed-event triggered transition
      arg("component Comp14 { " +
          "  port in boolean i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = i ? -2 : 2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 15 - No non-synchronous input port in conditional expressions (then) in timed-event triggered transition
      arg("component Comp15 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = true ? i : 2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 16 - No non-synchronous input port in conditional expressions (else) in timed-event triggered transition
      arg("component Comp16 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = true ? -2 : i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 17 - No non-synchronous input port in bracket expressions in timed-event triggered transition
      arg("component Comp17 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = (i); " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 18 - No non-synchronous input port in shift expressions in timed-event triggered transition
      arg("component Comp18 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int x = 0; " +
          "      x = i << 1;  x = 1 << i; " +
          "      x = i >> 1;  x = 1 >> i; " +
          "      x = i >>> 1; x = 1 >>> i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 19 - No non-synchronous input port in binary expressions in timed-event triggered transition
      arg("component Comp19 { " +
          "  port in boolean i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      boolean x = true; " +
          "      x = i & true; x = true & i; " +
          "      x = i ^ true; x = true ^ i; " +
          "      x = i | true; x = true | i; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 20 - No non-synchronous input port in if statement (condition) in timed-event triggered transition
      arg("component Comp20 { " +
          "  port in boolean i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      if (i) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 21 - No non-synchronous input port in if statement (then) in timed-event triggered transition
      arg("component Comp21 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      if (true) { int x = i; } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 22 - No non-synchronous input port in if statement (else) in timed-event triggered transition
      arg("component Comp22 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      if (true) { } else { int x = i; } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 23 - No non-synchronous input port in common for control (var dec) in timed-event triggered transition
      arg("component Comp23 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      for (int j = i; j > 10; j++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 24 - No non-synchronous input port in common for control (var assignment) in timed-event triggered transition
      arg("component Comp24 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      int j = 0; " +
          "      for (j = i; j > 10; j++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 25 - No non-synchronous input port in common for control (condition) in timed-event triggered transition
      arg("component Comp25 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      for (int j = 1; i > 10; j++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 26 - No non-synchronous input port in common for control (expression) in timed-event triggered transition
      arg("component Comp26 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      for (int j = 0; j > 10; i++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 27 - No non-synchronous input port in common for control (expression) in timed-event triggered transition
      arg("component Comp27 { " +
          "  port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { " +
          "      for (int j = 0; j > 10; j++, i++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      )
    );
  }
}
