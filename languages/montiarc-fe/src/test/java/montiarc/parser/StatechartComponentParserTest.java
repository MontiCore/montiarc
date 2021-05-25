/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

import montiarc.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

public class StatechartComponentParserTest extends AbstractTest {
  private static final String VALID_MODELS = "montiarc/parser/statecharts/valid";
  private static final String FALSE_MODELS = "montiarc/parser/statecharts/invalid";

  @ParameterizedTest
  @ValueSource(strings = {"A_EmptyStateChart.arc",
                          "B_JustSomeStates.arc",
                          "C_StatesAndTransitions.arc",
                          "D_GuardedTransitions.arc",
                          "E_TransitionsWithReactions.arc",
                          "F_HierarchicalStates.arc",
                          "G_SubStates.arc",
                          "H_Actions.arc",
                          "I_SelfTransitions.arc"})
  public void shouldParseValidStateCharts(String fileName) {
    ParserTest.parse(Paths.get(RELATIVE_MODEL_PATH, VALID_MODELS, fileName).toString(), false);
  }

  @ParameterizedTest
  @ValueSource(strings ={"ConfusedTransition.arc",
                         "DeclareStatesWithoutStatechart.arc",
                         "EmptyGuard.arc",
                         "EmptyReaction.arc",
                         "InvalidAction.arc",
                         "MissedSlash.arc",
                         "MissedSlashButGuard.arc",
                         "PortsInStatechart.arc"})
  public void shouldParseWithErrors(String fileName) {
    ParserTest.parse(Paths.get(RELATIVE_MODEL_PATH, FALSE_MODELS, fileName).toString(), true);
  }
}