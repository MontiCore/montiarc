/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorSourceAndTargetTimingsFit;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ConnectorSourceAndTargetTimingsFit} is the class and context-condition under test.
 */
public class ConnectorSourceAndTargetTimingsFitTest extends AbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // mismatched timing for an input port forward (sync -> timed)
    "component c1 {"
    + "port <<sync>> in int i;"
    + "component Inner {"
    + "port <<timed>> in int i;"
    + "}"
    + "Inner inner;"
    + "i -> inner.i;"
    +"}",
    // mismatched timing for an output port forward (sync -> timed)
    "component c2 {"
      + "port <<timed>> out int o;"
      + "component Inner {"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      +"}",
    // mismatched timing for a pass through connector (sync -> timed)
    "component c3 {"
      + "port <<sync>> in int i;"
      + "port <<timed>> out int o;"
      + "i -> o;"
      +"}",
    // mismatched timing for an input port forward (sync -> untimed) - default explicit
    "component c4 {"
      + "port <<sync>> in int i;"
      + "component Inner {"
      + "port <<untimed>> in int i;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      +"}",
    // mismatched timing for an input port forward (sync -> untimed) - default implicit
    "component c5 {"
      + "port <<sync>> in int i;"
      + "component Inner {"
      + "port in int i;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      +"}",
    // mismatched timing for an output port forward (untimed -> sync) - default explicit
    "component c6 {"
      + "port <<sync>> out int o;"
      + "component Inner {"
      + "port <<untimed>> out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      +"}",
    // mismatched timing for an output port forward (untimed -> sync) - default implicit
    "component c7 {"
      + "port <<sync>> out int o;"
      + "component Inner {"
      + "port out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      +"}",
    // mismatched timing for a hidden connector (sync -> timed)
    "component c8 {"
      + "component Inner {"
      + "port <<timed>> in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // sync -> timed
      +"}",
    // mismatched timing for a hidden connector (sync -> untimed) - default explicit
    "component c9 {"
      + "component Inner {"
      + "port <<untimed>> in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // sync -> untimed
      +"}",
    // mismatched timing for a hidden connector (sync -> untimed) - default implicit
    "component c10 {"
      + "component Inner {"
      + "port in int i;"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // sync -> untimed
      +"}",
    // mismatched timing for a hidden connector (timed -> sync)
    "component c11 {"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port <<timed>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // timed -> sync
      +"}",
    // mismatched timing for a hidden connector (untimed -> sync) - default explicit
    "component c12 {"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port <<untimed>> out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // untimed -> sync
      +"}",
    // mismatched timing for a hidden connector (untimed -> sync) - default implicit
    "component c13 {"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "inner1.o -> inner2.i;" // untimed -> sync
      +"}",
    // mismatched timing for an input port forward (sync -> timed) - multiple targets
    "component c14 {"
      + "port <<sync>> in int i;"
      + "component Inner {"
      + "port <<sync>> in int i1;"
      + "port <<timed>> in int i2;"
      + "}"
      + "Inner inner;"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      +"}",
    // mismatched timing for an output port forward (sync -> timed) - multiple targets
    "component c15 {"
      + "port <<sync>> out int o1;"
      + "port <<timed>> out int o2;"
      + "component Inner {"
      + "port <<sync>> out int o;"
      + "}"
      + "Inner inner;"
      + "inner.o -> o1;"
      + "inner.o -> o2;"
      +"}",
    // mismatched timing for a hidden connector (sync -> timed) - multiple targets
    "component c16 {"
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
      +"}",
    // mismatched timing for a hidden connector (untimed -> sync)
    // the automaton defines the timing for the incoming port
    "component c17 {"
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
      +"}",
    // mismatched timing for a hidden connector (sync -> untimed)
    // the automaton defines the timing for the outgoing port
    "component c18 {"
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
      +"}",
    // mismatched timing for an input port forward (untimed -> sync)
    // automaton override for incoming port
    "component c19 {"
      + "port <<untimed>> in int i;"
      + "component Inner {"
      + "port <<sync>> in int i;"
      + "port out int o;"
      + "<<untimed>> automaton { }"
      + "}"
      + "Inner inner;"
      + "i -> inner.i;"
      +"}",
    // mismatched timing for an output port forward (sync -> untimed)
    // automaton override for incoming port
    "component c20 {"
      + "port <<untimed>> out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port <<sync>> out int o;"
      + "<<untimed>> automaton { }"
      + "}"
      + "Inner inner;"
      + "inner.o -> o;"
      +"}",
  })
  public void shouldReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElse(null);
    Preconditions.checkNotNull(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorSourceAndTargetTimingsFit());

    // When
    checker.checkAll(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH);
  }

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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
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
      +"}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorSourceAndTargetTimingsFit());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }
}
