---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->
# JUnit
If you want to write tests with the simulator in Java directly, you can use JUnit and the extensions we provide.

- `montiarc.rte.tests.JSimTest`: This class annotation can be used to hook the tests into the simulation log output.
- `montiarc.rte.msg.MessageFactory`: The message factory allows you to quickly create messages that can be sent and received by ports.
- `montiarc.rte.port.PortObserver`: A special port that stores all messages it receives. It can be hooked up to existing ports to collect their output streams.

!!! info
    Be aware that for the simulation to progress over a [time interval](../Concepts/Timing.md), **all** incoming ports must have received a tick.

    Messages supplied to ports are only processed once the simulation has been started. The simulation runs until no component can process any more messages. This is indefinitely for closed-loop systems.

## Example Test

Given the following MontiArc model, which acts as a medium and forwards all messages:
```montiarc title="Medium.arc"
component Medium {

  port sync in boolean i;
  port sync out boolean o;

  automaton {
    initial state S;

    S -> S / {
      o = i;
    };
  }
}

```

An input-output test for it looks like this:

```java title="MediumTest.java" linenums="1"
import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
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
class MediumTest {

  /**
   * @param input    the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Boolean>> input,
              @NotNull List<Message<Boolean>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given (set up component and listen to outgoing port) 
    MediumComp sut = new MediumCompBuilder().setName("sut").build();
    PortObserver<Boolean> port_o = new PortObserver<>();
    
    sut.port_o().connect(port_o);

    // When (inject provided messages and run simulation)
    for (Message<Boolean> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions
      .assertThat(port_o.getObservedMessages())
      .containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(msg(true), tk()),
        List.of(msg(true), tk())
      ),
      Arguments.of(
        List.of(msg(false), tk()),
        List.of(msg(false), tk())
      ),
      Arguments.of(
        List.of(tk(), msg(true), tk()),
        List.of(tk(), msg(true), tk())
      ),
      Arguments.of(
        List.of(tk(), msg(false), tk()),
        List.of(tk(), msg(false), tk())
      ),
      Arguments.of(
        List.of(msg(true), tk(), msg(true), tk()),
        List.of(msg(true), tk(), msg(true), tk())
      ),
      Arguments.of(
        List.of(msg(true), tk(), msg(false), tk()),
        List.of(msg(true), tk(), msg(false), tk())
      ),
      Arguments.of(
        List.of(msg(false), tk(), msg(true), tk()),
        List.of(msg(false), tk(), msg(true), tk())
      ),
      Arguments.of(
        List.of(msg(false), tk(), msg(false), tk()),
        List.of(msg(false), tk(), msg(false), tk())
      ),
      Arguments.of(
        List.of(msg(false), tk(), msg(true), tk(), msg(true), tk()),
        List.of(msg(false), tk(), msg(true), tk(), msg(true), tk())
      ),
      Arguments.of(
        List.of(tk(), tk()),
        List.of(tk(), tk())
      ));
  }
}

```
