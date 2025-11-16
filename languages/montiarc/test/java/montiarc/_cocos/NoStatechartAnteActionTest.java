/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoStatechartAnteAction;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcAutomataError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NoStatechartAnteAction}
 */
public class NoStatechartAnteActionTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNotReportError(@NotNull String model) throws IOException {
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoStatechartAnteAction());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error[] expectedErrors) throws IOException {
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoStatechartAnteAction());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of("component Comp1 { automaton { initial state Init; state Ready; Init -> Ready; } }"),
      Arguments.of("component Comp2 { automaton { initial state Init { entry / { x = 0; } } state S; Init -> S; } }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      Arguments.of(
        "component Comp1 { port out int x; automaton { initial { x = 0; } state Init;} }",
        new Error[]{ArcAutomataError.STATECHART_ANTE_ACTION_NOT_SUPPORTED}
      ),
      Arguments.of(
        "component Comp2 { automaton { initial { }  state A;} }",
        new Error[]{ArcAutomataError.STATECHART_ANTE_ACTION_NOT_SUPPORTED}
      ),
      Arguments.of(
        "component Comp3 { component Comp6 { automaton {  { x = 1; } state Init; } } }",
        new Error[]{ArcAutomataError.STATECHART_ANTE_ACTION_NOT_SUPPORTED}
      )
    );
  }
}
