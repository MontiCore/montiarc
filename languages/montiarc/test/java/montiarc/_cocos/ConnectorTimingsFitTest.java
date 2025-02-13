/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorTimingsFit;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorTimingsFit}.
 */
public class ConnectorTimingsFitTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // all timing match (sync)
    "component c1 {"
      + "port sync in int i;"
      + "port sync out int o;"
      + "component Inner {"
      + "port sync in int i;"
      + "port sync out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "i -> inner1.i;"
      + "inner1.o -> inner2.i;"
      + "inner2.o -> o;"
      + "}",
    // all timing match (timed)
    "component c2 {"
      + "port in int i;"
      + "port out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "i -> inner1.i;"
      + "inner1.o -> inner2.i;"
      + "inner2.o -> o;"
      + "}",
    // all timing match (timed)
    "component c3 {"
      + "port in int i;"
      + "port out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "inner.o -> o;"
      + "}",
    // all timing match (sync)
    "component c4 {"
      + "port sync in int i;"
      + "port sync out int o;"
      + "component Inner {"
      + "port sync in int i;"
      + "port sync out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "inner.o -> o;"
      + "}",
    // all timing match (sync) - multiple targets
    "component c5 {"
      + "port sync in int i;"
      + "port sync out int o1;"
      + "port sync out int o2;"
      + "component Inner {"
      + "port sync in int i1;"
      + "port sync in int i2;"
      + "port sync out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timings match - pass through connector
    "component c6 {"
      + "port in int i;"
      + "port out int o;"
      + "i -> o;"
      + "component Inner { }"
      + "Inner inner; "
      + "}",
    // all timings match for an input port forward (sync -> timed)
    "component c7 {"
      + "port sync in int i;"
      + "component Inner {"
      + "port in int i;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "}",
    // all timings match for an output port forward (sync -> timed)
    "component c8 {"
      + "port out int o;"
      + "component Inner {"
      + "port sync out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      + "}",
    // all timings match for a pass through connector (sync -> timed)
    "component c9 {"
      + "port sync in int i;"
      + "port out int o;"
      + "i -> o;"
      + "}",
    // all timings match for a hidden connector (sync -> timed)
    "component c10 {"
      + "component Inner {"
      + "port in int i;"
      + "port sync out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // sync -> timed
      + "}",
    // all timings match for an input port forward (sync -> timed) - multiple targets
    "component c11 {"
      + "port sync in int i;"
      + "component Inner {"
      + "port sync in int i1;"
      + "port in int i2;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "}",
    // all timings match for an output port forward (sync -> timed) - multiple targets
    "component c12 {"
      + "port sync out int o1;"
      + "port out int o2;"
      + "component Inner {"
      + "port sync out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timings match for a hidden connector (sync -> timed) - multiple targets
    "component c13 {"
      + "component Source {"
      + "port sync out int o;"
      + "}"
      + "component Sink {"
      + "port sync in int i1;"
      + "port in int i2;"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "source.o -> sink.i1;"
      + "source.o -> sink.i2;"
      + "}",
    // mismatched timing for a hidden connector (sync -> untimed)
    // the automaton defines the timing for the outgoing port
    "component c14 {"
      + "component Source {"
      + "port sync out int o;"
      + "}"
      + "component Sink {"
      + "port in int i;"
      + "automaton { }"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "source.o -> sink.i;"
      + "}",
    // mismatched timing for an output port forward (sync -> untimed)
    // automaton override for incoming port
    "component c15 {"
      + "port out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port sync out int o;"
      + "automaton { }"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      + "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorTimingsFit());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // mismatched timing for an output port forward (timed -> sync)
    "component c1 {"
      + "port sync out int o;"
      + "component Inner {"
      + "port out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      + "}",
    // mismatched timing for a hidden connector (timed -> sync)
    "component c2 {"
      + "component Inner {"
      + "port sync in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // timed -> sync
      + "}",
    // mismatched timing for a hidden connector (untimed -> sync)
    "component c3 {"
      + "component Source {"
      + "port out int o;"
      + "}"
      + "component Sink {"
      + "port sync in int i;"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "source.o -> sink.i;"
      + "}",
    // mismatched timing for an input port forward (untimed -> sync)
    "component c4 {"
      + "port in int i;"
      + "component Inner {"
      + "port sync in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "}",
    // mismatched timing for a pass through connector with default source timing (timed -> sync)
    "component c5 {"
      + "port in int i;"
      + "port sync out int o;"
      + "i -> o;"
      + "component Inner { }"
      + "Inner inner; "
      + "}"
  })
  public void shouldReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElse(null);
    Preconditions.checkNotNull(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorTimingsFit());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.CONNECTOR_TIMING_MISMATCH));
  }
}
