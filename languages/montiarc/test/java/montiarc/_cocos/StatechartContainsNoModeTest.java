/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTArcStatechart;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.StatechartContainsNoMode;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcScope;
import montiarc.util.Error;
import montiarc.util.ModesError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Tests for {@link StatechartContainsNoMode}
 */
public class StatechartContainsNoModeTest extends MontiArcAbstractTest {

  protected static Stream<Arguments> numberOfModesAndStatesWithErrorProvider() {
    return Stream.of(
      Arguments.of(0, 0, new ModesError[]{}),
      Arguments.of(1, 0, new ModesError[]{}),
      Arguments.of(2, 0, new ModesError[]{}),
      Arguments.of(3, 0, new ModesError[]{}),
      Arguments.of(0, 1, new ModesError[]{ModesError.STATECHART_CONTAINS_MODE}),
      Arguments.of(1, 1, new ModesError[]{ModesError.STATECHART_CONTAINS_MODE}),
      Arguments.of(2, 1, new ModesError[]{ModesError.STATECHART_CONTAINS_MODE}),
      Arguments.of(3, 1, new ModesError[]{ModesError.STATECHART_CONTAINS_MODE}),
      Arguments.of(0, 2, new ModesError[]{ModesError.STATECHART_CONTAINS_MODE}),
      Arguments.of(1, 2, new ModesError[]{ModesError.STATECHART_CONTAINS_MODE})
    );
  }

  @ParameterizedTest
  @MethodSource("numberOfModesAndStatesWithErrorProvider")
  public void testCocoWithNModeAutomata(int numberOfStates, int numberOfModes, @NotNull ModesError... expectedErrors) {
    Preconditions.checkArgument(numberOfModes >= 0);
    Preconditions.checkArgument(numberOfStates >= 0);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTArcStatechart statechart = MontiArcMill.arcStatechartBuilder().build();
    statechart.setSpannedScope(MontiArcMill.scope());
    for (int i = 0; i < numberOfModes; i++) {
      ((IMontiArcScope) statechart.getSpannedScope()).add(MontiArcMill.arcModeSymbolBuilder().setName("m" + i).build());
    }
    for (int i = 0; i < numberOfStates; i++) {
      statechart.getSpannedScope().add(MontiArcMill.sCStateSymbolBuilder().setName("s" + i).build());
    }

    // When
    StatechartContainsNoMode coco = new StatechartContainsNoMode();
    coco.check(statechart);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new StatechartContainsNoMode());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // automaton with a mode
      arg("component Comp1 { " +
          "automaton { mode m1 { } } " +
          "}",
        ModesError.STATECHART_CONTAINS_MODE),
      // automaton with multiple states and modes
      arg("component Comp2 { " +
          "automaton { " +
          "state s1; " +
          "mode m1 { }" +
          "state s2; " +
          "} " +
          "}",
        ModesError.STATECHART_CONTAINS_MODE),
      // inner with two automata where one has a mode
      arg("component Comp3 { " +
          "component Inner { " +
          "automaton { state s1; } " +
          "automaton { state s1; mode m1 { } } " +
          "} " +
          "}",
        ModesError.STATECHART_CONTAINS_MODE)
    );
  }
}
