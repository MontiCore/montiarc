/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class NestedTransitionsCrosscuttingTheHierarchyTest {
  /**
   * @param transition the transition to test (starting at state aaa_aaa)
   * @param expectedEndState the expected leaf state that should be entered by the transition
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull String transition,
              @NotNull String expectedEndState) {
    Preconditions.checkNotNull(transition);
    Preconditions.checkNotNull(expectedEndState);
    
    // With
    List<Message<String>> inputs = inputsForTransitionTest(transition);
    List<Message<String>> expectedOutputs = expectedResultForTransitionTest(transition, expectedEndState);
    
    // Given
    NestedTransitionsCrosscuttingTheHierarchyComp sut = new NestedTransitionsCrosscuttingTheHierarchyCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<String> msg : inputs) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expectedOutputs);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of("aaa_aaa -> aaa_aab", "aaa_aab"),
      Arguments.of("aaa_aaa -> aaa_aba", "aaa_aba"),
      Arguments.of("aaa_aaa -> aaa_baa", "aaa_baa"),
      Arguments.of("aaa_aa -> aaa_ab", "aaa_abb"),
      Arguments.of("aaa_aa -> aaa_ba", "aaa_bab"),
      Arguments.of("aaa_aa -> aab_aa", "aab_aaa"),
      Arguments.of("aaa_a -> aaa_b", "aaa_bba"),
      Arguments.of("aaa_a -> aab_a", "aab_aba"),
      Arguments.of("aaa_a -> aba_a", "aba_aaa"),
      Arguments.of("aa -> baa", "baa")
    );
  }

  static List<Message<String>> inputsForTransitionTest(String transitionToTest) {
    return List.of(
      msg("aaa_aaa"), tk(),
      msg("check"), tk(),
      msg(transitionToTest), tk(),
      msg("check"), tk()
    );
  }

  static List<Message<String>> expectedResultForTransitionTest(String transitionToTest, String resultingState) {
    return List.of(
      msg("-> aaa_aaa"), tk(),
      msg("aaa_aaa"), tk(),  // 1. Check of current state (before transition)
      msg(transitionToTest), tk(),
      msg(resultingState), tk()    // 2. Check of current state (after transition)
    );
  }
}
