/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.FeedbackLoopStrongCausality;
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
 * {@link FeedbackLoopStrongCausality} is the class and context-condition under test.
 */
public class FeedbackLoopStrongCausalityTest extends AbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // feedback loop without delay
    "component c1 {"
      + "port in int i;"
      + "port out int o;"
      + "component E {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "}"
      + "E e;"
      + "i -> e.i1;"
      + "e.o1 -> e.i2;"
      + "e.o2 -> o;"
      + "}",
    // feedback loop without delay, nested hierarchy
    "component c2 {"
      + "port in int i;"
      + "port out int o;"
      + "component E {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "component EE {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "}"
      + "EE ee;"
      + "i1 -> ee.i1;"
      + "i2 -> ee.i2;"
      + "ee.o1 -> o1;"
      + "ee.o2 -> o2;"
      + "}"
      + "E e;"
      + "i -> e.i1;"
      + "e.o1 -> e.i2;"
      + "e.o2 -> o;"
      + "}"
  })
  public void shouldReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackLoopStrongCausality());

    // When
    checker.checkAll(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent(
      ArcError.FEEDBACK_LOOP_TIMING_NOT_DELAYED,
      ArcError.FEEDBACK_LOOP_TIMING_NOT_DELAYED
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // feedback loop with explicit delay
    "component c1 {"
      + "port in int i;"
      + "port out int o;"
      + "component E {"
      + "port in int i1;"
      + "port in int i2;"
      + "port <<delayed>> out int o1;"
      + "port out int o2;"
      + "}"
      + "E e;"
      + "i -> e.i1;"
      + "e.o1 -> e.i2;"
      + "e.o2 -> o;"
      + "}",
    // feedback loop with delay inherited implicitly from port forward
    "component c2 {"
      + "port in int i;"
      + "port out int o;"
      + "component E {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "component EE {"
      + "port in int i1;"
      + "port in int i2;"
      + "port <<delayed>> out int o1;"
      + "port out int o2;"
      + "}"
      + "EE ee;"
      + "i1 -> ee.i1;"
      + "i2 -> ee.i2;"
      + "ee.o1 -> o1;"
      + "ee.o2 -> o2;"
      + "}"
      + "E e;"
      + "i -> e.i1;"
      + "e.o1 -> e.i2;"
      + "e.o2 -> o;"
      + "}",
    // feedback loop with delay inherited implicitly from composition
    "component c3 {"
      + "port in int i;"
      + "port out int o;"
      + "component E {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "component EE1 {"
      + "port in int i1;"
      + "port in int i2;"
      + "port <<delayed>> out int o;"
      + "}"
      + "component EE2 {"
      + "port in int i;"
      + "port out int o1;"
      + "port out int o2;"
      + "}"
      + "EE1 ee1;"
      + "EE2 ee2;"
      + "i1 -> ee1.i1;"
      + "i2 -> ee1.i2;"
      + "ee1.o -> ee2.i;"
      + "ee2.o1 -> o1;"
      + "ee2.o2 -> o2;"
      + "}"
      + "E e;"
      + "i -> e.i1;"
      + "e.o1 -> e.i2;"
      + "e.o2 -> o;"
      + "}",
    // feedback loop with delay, nested hierarchy
    // (unconnected output ports of composed components are considered as delayed)
    "component c4 {"
      + "port in int i;"
      + "port out int o;"
      + "component E {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "component EE {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "}"
      + "EE ee;"
      + "i1 -> ee.i1;"
      + "i2 -> ee.i2;"
      + "ee.o2 -> o2;"
      + "}"
      + "E e;"
      + "i -> e.i1;"
      + "e.o1 -> e.i2;"
      + "e.o2 -> o;"
      + "}",
    // feedback loop with delay, nested hierarchy
    // (components without input are considered strongly causal)
    "component c5 {"
      + "port in int i;"
      + "port out int o;"
      + "component E {"
      + "port in int i1;"
      + "port in int i2;"
      + "port out int o1;"
      + "port out int o2;"
      + "component EE {"
      + "port out int o1;"
      + "port out int o2;"
      + "}"
      + "EE ee;"
      + "ee.o1 -> o1;"
      + "ee.o2 -> o2;"
      + "}"
      + "E e;"
      + "i -> e.i1;"
      + "e.o1 -> e.i2;"
      + "e.o2 -> o;"
      + "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackLoopStrongCausality());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }
}
