/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.ILogHook;
import de.se_rwth.commons.logging.Log;
import montiarc.lang.Simulation;
import montiarc.rte.msg.Message;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class DelayLogTest {

  @ParameterizedTest
  @MethodSource("io")
  void testLogOutput(@NotNull List<Message<OnOff>> input,
              @NotNull List<String> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    DelayComp sut = new DelayCompBuilder().setName("sut").build();
    List<String> logs = new ArrayList<>();
    Log.addLogHook(new ILogHook() {
      @Override
      public void doPrintln(String msg) {
        logs.add(msg);
      }

      @Override
      public void doErrPrint(String msg) {
        logs.add(msg);
      }

      @Override
      public void doPrintStackTrace(Throwable t) {
      }

      @Override
      public void doErrPrintStackTrace(Throwable t) {
      }

      @Override
      public void doPrint(String msg) {
        logs.add(msg);
      }
    });

    // When
    Simulation.ticks = 0;

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(logs).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of("[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 1 ---")
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk()),
        List.of("[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 1 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send ON",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 2 ---")
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk()),
        List.of("[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 1 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send ON",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 2 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send ON",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 3 ---")
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of("[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 1 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 2 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 3 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 4 ---")
      ),
      Arguments.of(
        List.of(tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of("[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 1 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 2 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 3 ---",
          "[INFO]  sut#receive Tick",
          "[INFO]  sut.o#send OFF",
          "[INFO]  sut#enter_state S",
          "[INFO]  Scheduler --- Tick 4 ---")
      )
    );
  }
}
