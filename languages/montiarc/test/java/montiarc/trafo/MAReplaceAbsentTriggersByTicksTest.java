/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTMsgEvent;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class MAReplaceAbsentTriggersByTicksTest extends MontiArcAbstractTest {
  @Test
  void shouldAddTickToUntriggeredEventTransition() throws IOException {
    // Given
    String model =
      "component Comp {" +
        "  port in int i;" +
        "  port out int o;" +
        "  " +
        "  <<timed>> automaton {" +
        "    initial state S;" +
        "    S -> S / { o = i; };" +
        "  }" +
        "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAReplaceAbsentTriggersByTicks trafo = new MAReplaceAbsentTriggersByTicks();

    // When
    trafo.apply(ast);

    // Then
    ASTArcStatechart automaton = getAutomatonOf(ast.getComponentType());
    ASTTransitionBody transitionBody = bodyOfFirstTransitionOf(automaton);

    Assertions.assertThat(transitionBody.isPresentSCEvent()).as("trigger presence").isTrue();
    Assertions.assertThat(transitionBody.getSCEvent()).as("trigger").isInstanceOf(ASTMsgEvent.class);
    Assertions.assertThat(((ASTMsgEvent) transitionBody.getSCEvent()).getName()).as("trigger").isEqualTo("Tick");
  }

  @Test
  void shouldNotAddTickToTriggeredEventTransition() throws IOException {
    // Given
    String model =
      "component Comp {" +
        "  port in int i;" +
        "  port out int o;" +
        "  " +
        "  <<timed>> automaton {" +
        "    initial state S;" +
        "    S -> S i / { o = i; };" +
        "  }" +
        "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAReplaceAbsentTriggersByTicks trafo = new MAReplaceAbsentTriggersByTicks();

    // When
    trafo.apply(ast);

    // Then
    ASTArcStatechart automaton = getAutomatonOf(ast.getComponentType());
    ASTTransitionBody transitionBody = bodyOfFirstTransitionOf(automaton);

    Assertions.assertThat(transitionBody.isPresentSCEvent()).as("trigger presence").isTrue();
    Assertions.assertThat(transitionBody.getSCEvent()).as("trigger").isInstanceOf(ASTMsgEvent.class);
    Assertions.assertThat(((ASTMsgEvent) transitionBody.getSCEvent()).getName()).as("trigger").isEqualTo("i");
  }

  @Test
  void shouldNotAddTickToSyncTransition() throws IOException {
    // Given
    String model =
      "component Comp {" +
        "  port in int i;" +
        "  port out int o;" +
        "  " +
        "  <<sync>> automaton {" +
        "    initial state S;" +
        "    S -> S / { o = i; };" +
        "  }" +
        "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAReplaceAbsentTriggersByTicks trafo = new MAReplaceAbsentTriggersByTicks();

    // When
    trafo.apply(ast);

    // Then
    ASTArcStatechart automaton = getAutomatonOf(ast.getComponentType());
    ASTTransitionBody transitionBody = bodyOfFirstTransitionOf(automaton);

    Assertions.assertThat(transitionBody.isPresentSCEvent()).as("trigger presence").isFalse();
  }

  @Test
  void shouldAddTickInNestedEventTransition() throws IOException {
    // Given
    String model =
      "component Comp {" +
        "  port in int i;" +
        "  port out int o;" +
        "  " +
        "  <<timed>> automaton {" +
        "    initial state S {" +
        "      initial state SI;" +
        "      SI -> SI / { o = i; };" +
        "    };" +
        "    S -> S / { o = i; };" +
        "  }" +
        "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAReplaceAbsentTriggersByTicks trafo = new MAReplaceAbsentTriggersByTicks();

    // When
    trafo.apply(ast);

    // Then
    ASTArcStatechart automaton = getAutomatonOf(ast.getComponentType());
    ASTTransitionBody transitionBody = bodyOfFirstTransitionOf(firstStateOf(automaton));

    Assertions.assertThat(transitionBody.isPresentSCEvent()).as("trigger presence").isTrue();
    Assertions.assertThat(transitionBody.getSCEvent()).as("trigger").isInstanceOf(ASTMsgEvent.class);
    Assertions.assertThat(((ASTMsgEvent) transitionBody.getSCEvent()).getName()).as("trigger").isEqualTo("Tick");
  }

  @Test
  void shouldNotAddTickToTriggeredEventInNestedTransition() throws IOException {
    // Given
    String model =
      "component Comp {" +
        "  port in int i;" +
        "  port out int o;" +
        "  " +
        "  <<timed>> automaton {" +
        "    initial state S {" +
        "      initial state SI;" +
        "      SI -> SI i / { o = i; };" +
        "    };" +
        "    S -> S / { o = i; };" +
        "  }" +
        "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAReplaceAbsentTriggersByTicks trafo = new MAReplaceAbsentTriggersByTicks();

    // When
    trafo.apply(ast);

    // Then
    ASTArcStatechart automaton = getAutomatonOf(ast.getComponentType());
    ASTTransitionBody transitionBody = bodyOfFirstTransitionOf(firstStateOf(automaton));

    Assertions.assertThat(transitionBody.isPresentSCEvent()).as("trigger presence").isTrue();
    Assertions.assertThat(transitionBody.getSCEvent()).as("trigger").isInstanceOf(ASTMsgEvent.class);
    Assertions.assertThat(((ASTMsgEvent) transitionBody.getSCEvent()).getName()).as("trigger").isEqualTo("i");
  }

  @Test
  void shouldNotAddTickInNestedSyncTransition() throws IOException {
    // Given
    String model =
      "component Comp {" +
        "  port in int i;" +
        "  port out int o;" +
        "  " +
        "  <<sync>> automaton {" +
        "    initial state S {" +
        "      initial state SI;" +
        "      SI -> SI / { o = i; };" +
        "    };" +
        "    S -> S / { o = i; };" +
        "  }" +
        "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAReplaceAbsentTriggersByTicks trafo = new MAReplaceAbsentTriggersByTicks();

    // When
    trafo.apply(ast);

    // Then
    ASTArcStatechart automaton = getAutomatonOf(ast.getComponentType());
    ASTTransitionBody transitionBody = bodyOfFirstTransitionOf(firstStateOf(automaton));

    Assertions.assertThat(transitionBody.isPresentSCEvent()).as("trigger presence").isFalse();
  }

  private ASTArcStatechart getAutomatonOf(@NotNull ASTComponentType componentType) {
    Preconditions.checkNotNull(componentType);

    return componentType.getBody().streamArcElements()
      .filter(ASTArcStatechart.class::isInstance)
      .map(ASTArcStatechart.class::cast)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Component does not contain an automaton"));
  }

  private ASTTransitionBody bodyOfFirstTransitionOf(@NotNull ASTArcStatechart automaton) {
    Preconditions.checkNotNull(automaton);

    return automaton.streamTransitions()
      .map(ASTSCTransition::getSCTBody)
      .filter(ASTTransitionBody.class::isInstance)
      .map(ASTTransitionBody.class::cast)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Automaton does not contain a transition"));
  }

  private ASTSCState firstStateOf(@NotNull ASTArcStatechart automaton) {
    Preconditions.checkNotNull(automaton);

    return automaton.streamSCStatechartElements()
      .filter(ASTSCState.class::isInstance)
      .map(ASTSCState.class::cast)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Automaton does not contain a state"));
  }

  private ASTTransitionBody bodyOfFirstTransitionOf(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    Preconditions.checkArgument(state.getSCSBody() instanceof ASTSCHierarchyBody);

    return ((ASTSCHierarchyBody) state.getSCSBody()).streamSCStateElements()
      .filter(ASTSCTransition.class::isInstance)
      .map(ASTSCTransition.class::cast)
      .map(ASTSCTransition::getSCTBody)
      .map(ASTTransitionBody.class::cast)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("State does not contain a transition"));
  }
}
