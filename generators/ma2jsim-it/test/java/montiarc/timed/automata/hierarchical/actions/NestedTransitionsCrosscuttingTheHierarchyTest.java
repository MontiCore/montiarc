/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class NestedTransitionsCrosscuttingTheHierarchyTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<String>> inputs,
              @NotNull List<Message<String>> expectedOutputs) {
    Preconditions.checkNotNull(inputs);
    Preconditions.checkNotNull(expectedOutputs);
    
    // Given
    NestedTransitionsCrosscuttingTheHierarchyComp sut = new NestedTransitionsCrosscuttingTheHierarchyCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<String> msg : inputs) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expectedOutputs);
  }

  static Stream<Arguments> io() {
    // Building two variants of every input, one without and one with a tick in between the two input messages.
    Stream.Builder<Arguments> argBuilder = Stream.builder();
    addInputsToArgBuilder(argBuilder, true);
    addInputsToArgBuilder(argBuilder, false);
    return argBuilder.build();
  }

  private static void addInputsToArgBuilder(Stream.Builder<Arguments> argBuilder, boolean doTick) {
    // aaa_aaa -> aaa_aab
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_aaa -> aaa_aab", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"),
          msg("aaa_aaa -> aaa_aab"),
          enter("aaa_aab"),
          doo("a"), doo("aa"), doo("aaa"), doo("aaa_a"), doo("aaa_aa"), doo("aaa_aab"), tk()
        )).collect(Collectors.toList())
    ));
    // aaa_aaa -> aaa_aba
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_aaa -> aaa_aba", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"),
          msg("aaa_aaa -> aaa_aba"),
          enter("aaa_ab"), enter("aaa_aba"),
          doo("a"), doo("aa"), doo("aaa"), doo("aaa_a"), doo("aaa_ab"), doo("aaa_aba"), tk()
      )).collect(Collectors.toList())
    ));
    // aaa_aaa -> aaa_baa
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_aaa -> aaa_baa", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"), exit("aaa_a"),
          msg("aaa_aaa -> aaa_baa"),
          enter("aaa_b"), enter("aaa_ba"), enter("aaa_baa"),
          doo("a"), doo("aa"), doo("aaa"), doo("aaa_b"), doo("aaa_ba"), doo("aaa_baa"), tk()
      )).collect(Collectors.toList())
    ));
    // aaa_aa -> aaa_ab
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_aa -> aaa_ab", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"),
          msg("aaa_aa -> aaa_ab"),
          enter("aaa_ab"), enter("aaa_abb"),
          doo("a"), doo("aa"), doo("aaa"), doo("aaa_a"), doo("aaa_ab"), doo("aaa_abb"), tk()
      )).collect(Collectors.toList())
    ));
    // aaa_aa -> aaa_ba
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_aa -> aaa_ba", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"), exit("aaa_a"),
          msg("aaa_aa -> aaa_ba"),
          enter("aaa_b"), enter("aaa_ba"), enter("aaa_bab"),
          doo("a"), doo("aa"), doo("aaa"), doo("aaa_b"), doo("aaa_ba"), doo("aaa_bab"), tk()
      )).collect(Collectors.toList())
    ));
    // aaa_aa -> aab_aa
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_aa -> aab_aa", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"), exit("aaa_a"), exit("aaa"),
          msg("aaa_aa -> aab_aa"),
          enter("aab"), enter("aab_a"), enter("aab_aa"), enter("aab_aaa"),
          doo("a"), doo("aa"), doo("aab"), doo("aab_a"), doo("aab_aa"), doo("aab_aaa"), tk()
      )).collect(Collectors.toList())
    ));
    // aaa_a -> aaa_b
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_a -> aaa_b", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"), exit("aaa_a"),
          msg("aaa_a -> aaa_b"),
          enter("aaa_b"), enter("aaa_bb"), enter("aaa_bba"),
          doo("a"), doo("aa"), doo("aaa"), doo("aaa_b"), doo("aaa_bb"), doo("aaa_bba"), tk()
      )).collect(Collectors.toList())
    ));
    // aaa_a -> aab_a
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_a -> aab_a", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"), exit("aaa_a"), exit("aaa"),
          msg("aaa_a -> aab_a"),
          enter("aab"), enter("aab_a"), enter("aab_ab"), enter("aab_aba"),
          doo("a"), doo("aa"), doo("aab"), doo("aab_a"), doo("aab_ab"), doo("aab_aba"), tk()
      )).collect(Collectors.toList())
    ));
    // aaa_a -> aba_a
    argBuilder.add(Arguments.of(
      inputsForTransition("aaa_a -> aba_a", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"), exit("aaa_a"), exit("aaa"), exit("aa"),
          msg("aaa_a -> aba_a"),
          enter("ab"), enter("aba"), enter("aba_a"), enter("aba_aa"), enter("aba_aaa"),
          doo("a"), doo("ab"), doo("aba"), doo("aba_a"), doo("aba_aa"), doo("aba_aaa"), tk()
      )).collect(Collectors.toList())
    ));
    // aa -> baa
    argBuilder.add(Arguments.of(
      inputsForTransition("aa -> baa", doTick),
      Streams.concat(expectedInitialOutputs(doTick), Stream.of(
        exit("aaa_aaa"), exit("aaa_aa"), exit("aaa_a"), exit("aaa"), exit("aa"), exit("a"),
          msg("aa -> baa"),
          enter("b"), enter("ba"), enter("baa"),
          doo("b"), doo("ba"), doo("baa"), tk()
      )).collect(Collectors.toList())
    ));
  }

  private static List<Message<String>> inputsForTransition(String transition, boolean doTick) {
    if (doTick) {
      return List.of(msg("aaa_aaa"), tk(), msg(transition), tk());
    } else {
      return List.of(msg("aaa_aaa"), msg(transition), tk());
    }
  }

  private static Stream<Message<String>> expectedInitialOutputs(boolean doTick) {
    Stream<Message<String>> initialMessages = Stream.of(
      msg("INIT -> aaa_aaa"), enter("a"), enter("aa"), enter("aaa"), enter("aaa_a"), enter("aaa_aa"), enter("aaa_aaa")
    );

    if (!doTick) {
      return initialMessages;
    } else {
      return Streams.concat(initialMessages, Stream.of(
        doo("a"), doo("aa"), doo("aaa"), doo("aaa_a"), doo("aaa_aa"), doo("aaa_aaa"), tk()
      ));
    }
  }

  private static Message<String> enter(String stateName) {
    return msg("-> " + stateName);
  }

  private static Message<String> doo(String stateName) {
    return msg("~ " + stateName);
  }

  private static Message<String> exit(String stateName) {
    return msg(stateName + " ->");
  }
}
