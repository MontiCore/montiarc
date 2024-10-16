/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorTimingsFit;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
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
public class ConnectorTimingsFitTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // all timing match (sync)
    "component c1 {"
      + "port <<sync>> in int i;"
      + "port <<sync>> out int o;"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "i -> inner1.i;"
      + "inner1.o -> inner2.i;"
      + "inner2.o -> o;"
      + "}",
    // all timing match (timed)
    "component c2 {"
      + "port <<timed>> in int i;"
      + "port <<timed>> out int o;"
      + "component Inner {"
      + "port <<timed>> in int i;"
      + "port <<timed>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "i -> inner1.i;"
      + "inner1.o -> inner2.i;"
      + "inner2.o -> o;"
      + "}",
    // all timing match (untimed) - default explicit
    "component c3 {"
      + "port <<untimed>> in int i;"
      + "port <<untimed>> out int o;"
      + "component Inner {"
      + "port <<untimed>> in int i;"
      + "port <<untimed>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "i -> inner1.i;"
      + "inner1.o -> inner2.i;"
      + "inner2.o -> o;"
      + "}",
    // all timing match (untimed) - default implicit
    "component c4 {"
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
    // all timing match (untimed) - inner default implicit
    "component c5 {"
      + "port <<untimed>> in int i;"
      + "port <<untimed>> out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "inner.o -> o;"
      + "}",
    // all timing match (sync) - outer implicit
    "component c6 {"
      + "port in int i;"
      + "port out int o;"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "inner.o -> o;"
      + "}",
    // all timing match (timed) - outer implicit
    "component c7 {"
      + "port in int i;"
      + "port out int o;"
      + "component Inner {"
      + "port <<timed>> in int i;"
      + "port <<timed>> out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "inner.o -> o;"
      + "}",
    // all timing match (untimed) - outer implicit
    "component c8 {"
      + "port in int i;"
      + "port out int o;"
      + "component Inner {"
      + "port <<untimed>> in int i;"
      + "port <<untimed>> out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "inner.o -> o;"
      + "}",
    // all timing match (sync) - multiple targets
    "component c9 {"
      + "port <<sync>> in int i;"
      + "port <<sync>> out int o1;"
      + "port <<sync>> out int o2;"
      + "component Inner {"
      + "port <<sync>> in int i1;"
      + "port <<sync>> in int i2;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timing match (sync) - multiple targets, outer implicit
    "component c10 {"
      + "port in int i;"
      + "port out int o1;"
      + "port out int o2;"
      + "component Inner {"
      + "port <<sync>> in int i1;"
      + "port <<sync>> in int i2;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timing match (sync) - automaton
    "component c11 {"
      + "port <<sync>> in int i;"
      + "port <<sync>> out int o1;"
      + "port <<sync>> out int o2;"
      + "component Inner {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o;"
      + "<<sync>> automaton { }"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timing match - automaton override incoming port
    "component c12 {"
      + "port <<timed>> in int i;"
      + "port <<sync>> out int o1;"
      + "port <<sync>> out int o2;"
      + "component Inner {"
      + "port <<timed>> in int i1;"
      + "port <<timed>> in int i2;"
      + "port out int o;"
      + "<<sync>> automaton { }"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timing match - automaton override incoming port
    "component c13 {"
      + "port <<sync>> in int i;"
      + "port <<timed>> out int o1;"
      + "port <<timed>> out int o2;"
      + "component Inner {"
      + "port in int i1;"
      + "port in int i2;"
      + "port <<timed>> out int o;"
      + "<<sync>> automaton { }"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timings match - pass through connector and implicit timing
    "component c14 {"
      + "port in int i;"
      + "port out int o;"
      + "i -> o;"
      + "component Inner { }"
      + "Inner inner; "
      + "}",
    // all timings match for an input port forward (sync -> timed)
    "component c15 {"
      + "port <<sync>> in int i;"
      + "component Inner {"
      + "port <<timed>> in int i;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "}",
    // all timings match for an output port forward (sync -> timed)
    "component c16 {"
      + "port <<timed>> out int o;"
      + "component Inner {"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      + "}",
    // all timings match for a pass through connector (sync -> timed)
    "component c17 {"
      + "port <<sync>> in int i;"
      + "port <<timed>> out int o;"
      + "i -> o;"
      + "}",
    // all timings match for an input port forward (sync -> untimed) - default explicit
    "component c18 {"
      + "port <<sync>> in int i;"
      + "component Inner {"
      + "port <<untimed>> in int i;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "}",
    // all timings match for an input port forward (sync -> untimed) - default implicit
    "component c19 {"
      + "port <<sync>> in int i;"
      + "component Inner {"
      + "port in int i;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "}",
    // all timings match for a hidden connector (sync -> timed)
    "component c20 {"
      + "component Inner {"
      + "port <<timed>> in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // sync -> timed
      + "}",
    // all timings match for a hidden connector (sync -> untimed) - default explicit
    "component c21 {"
      + "component Inner {"
      + "port <<untimed>> in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // sync -> untimed
      + "}",
    // all timings match for a hidden connector (sync -> untimed) - default implicit
    "component c22 {"
      + "component Inner {"
      + "port in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // sync -> untimed
      + "}",
    // all timings match for an input port forward (sync -> timed) - multiple targets
    "component c23 {"
      + "port <<sync>> in int i;"
      + "component Inner {"
      + "port <<sync>> in int i1;"
      + "port <<timed>> in int i2;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "}",
    // all timings match for an output port forward (sync -> timed) - multiple targets
    "component c24 {"
      + "port <<sync>> out int o1;"
      + "port <<timed>> out int o2;"
      + "component Inner {"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      + "}",
    // all timings match for a hidden connector (sync -> timed) - multiple targets
    "component c25 {"
      + "component Source {"
      + "port <<sync>> out int o;"
      + "}"
      + "component Sink {"
      + "port <<sync>> in int i1;"
      + "port <<timed>> in int i2;"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "source.o -> sink.i1;"
      + "source.o -> sink.i2;"
      + "}",
    // mismatched timing for a hidden connector (sync -> untimed)
    // the automaton defines the timing for the outgoing port
    "component c26 {"
      + "component Source {"
      + "port <<sync>> out int o;"
      + "}"
      + "component Sink {"
      + "port in int i;"
      + "<<untimed>> automaton { }"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "source.o -> sink.i;"
      + "}",
    // mismatched timing for an output port forward (sync -> untimed)
    // automaton override for incoming port
    "component c27 {"
      + "port <<untimed>> out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port <<sync>> out int o;"
      + "<<untimed>> automaton { }"
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
    // mismatched timing for an output port forward (untimed -> sync) - default explicit
    "component c1 {"
      + "port <<sync>> out int o;"
      + "component Inner {"
      + "port <<untimed>> out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      + "}",
    // mismatched timing for an output port forward (untimed -> sync) - default implicit
    "component c2 {"
      + "port <<sync>> out int o;"
      + "component Inner {"
      + "port out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      + "}",
    // mismatched timing for a hidden connector (timed -> sync)
    "component c3 {"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port <<timed>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // timed -> sync
      + "}",
    // mismatched timing for a hidden connector (untimed -> sync) - default explicit
    "component c4 {"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port <<untimed>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // untimed -> sync
      + "}",
    // mismatched timing for a hidden connector (untimed -> sync) - default implicit
    "component c5 {"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // untimed -> sync
      + "}",
    // mismatched timing for a hidden connector (untimed -> sync)
    // the automaton defines the timing for the incoming port
    "component c6 {"
      + "component Source {"
      + "port <<untimed>> out int o;"
      + "}"
      + "component Sink {"
      + "port in int i;"
      + "<<sync>> automaton { }"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "source.o -> sink.i;"
      + "}",
    // mismatched timing for an input port forward (untimed -> sync)
    // automaton override for incoming port
    "component c7 {"
      + "port <<untimed>> in int i;"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port out int o;"
      + "<<untimed>> automaton { }"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      + "}",
    // mismatched timing for a pass through connector with default source timing (timed -> sync)
    "component c8 {"
      + "port in int i;"
      + "port <<sync>> out int o;"
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
    this.checkOnlyExpectedErrorsPresent(ArcError.CONNECTOR_TIMING_MISMATCH);
  }
}
