/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.automata;

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
import static montiarc.types.OnOff.ON;
import static montiarc.types.OnOff.OFF;

@JSimTest
class MediumTest {

  /**
   * @param inA       the input stream on port 'inA'
   * @param inB       the input stream on port 'inB'
   * @param inY       the input stream on port 'inY'
   * @param inZ       the input stream on port 'inZ'
   * @param outA      the expected output stream on port 'outA'
   * @param outB      the expected output stream on port 'outB'                  
   * @param outY      the expected output stream on port 'outY'
   * @param outZ      the expected output stream on port 'outZ'
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> inA,
              @NotNull List<Message<OnOff>> inB,
              @NotNull List<Message<OnOff>> inY,
              @NotNull List<Message<OnOff>> inZ,
              @NotNull List<Message<OnOff>> outA,
              @NotNull List<Message<OnOff>> outB,
              @NotNull List<Message<OnOff>> outY,
              @NotNull List<Message<OnOff>> outZ) {
    Preconditions.checkNotNull(inA);
    Preconditions.checkNotNull(inB);
    Preconditions.checkNotNull(inY);
    Preconditions.checkNotNull(inZ);
    Preconditions.checkNotNull(outA);
    Preconditions.checkNotNull(outB);
    Preconditions.checkNotNull(outY);
    Preconditions.checkNotNull(outZ);

    // Given
    MediumComp sut = new MediumCompBuilder().setName("sut").build();

    PortObserver<OnOff> port_outA = new PortObserver<>();
    PortObserver<OnOff> port_outB = new PortObserver<>();
    PortObserver<OnOff> port_outY = new PortObserver<>();
    PortObserver<OnOff> port_outZ = new PortObserver<>();

    sut.port_outA().connect(port_outA);
    sut.port_outB().connect(port_outB);
    sut.port_outY().connect(port_outY);
    sut.port_outZ().connect(port_outZ);

    // When
    sut.init();

    inA.forEach(sut.port_inA()::receive);
    inB.forEach(sut.port_inB()::receive);
    inY.forEach(sut.port_inY()::receive);
    inZ.forEach(sut.port_inZ()::receive);

    sut.run();

    // Then
    org.junit.jupiter.api.Assertions.assertAll(
      () -> Assertions.assertThat(port_outA.getObservedMessages()).as("outA").containsExactlyElementsOf(outA),
      () -> Assertions.assertThat(port_outB.getObservedMessages()).as("outB").containsExactlyElementsOf(outB),
      () -> Assertions.assertThat(port_outY.getObservedMessages()).as("outY").containsExactlyElementsOf(outY),
      () -> Assertions.assertThat(port_outZ.getObservedMessages()).as("outZ").containsExactlyElementsOf(outZ)
    );
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        /*inA*/ List.of(tk()),
        /*inB*/ List.of(tk()),
        /*inY*/ List.of(msg(ON), tk()),
        /*inZ*/ List.of(msg(ON), tk()),
        /* oA*/ List.of(tk()),
        /* oB*/ List.of(tk()),
        /* oY*/ List.of(msg(ON), tk()),
        /* oZ*/ List.of(msg(ON), tk())
      ),
      Arguments.of(
        /*inA*/ List.of(tk()),
        /*inB*/ List.of(tk()),
        /*inY*/ List.of(msg(OFF), tk()),
        /*inZ*/ List.of(msg(ON), tk()),
        /* oA*/ List.of(tk()),
        /* oB*/ List.of(tk()),
        /* oY*/ List.of(msg(OFF), tk()),
        /* oZ*/ List.of(msg(ON), tk())
      ),
      Arguments.of(
        /*inA*/ List.of(tk()),
        /*inB*/ List.of(tk()),
        /*inY*/ List.of(msg(ON), tk()),
        /*inZ*/ List.of(msg(OFF), tk()),
        /* oA*/ List.of(tk()),
        /* oB*/ List.of(tk()),
        /* oY*/ List.of(msg(ON), tk()),
        /* oZ*/ List.of(msg(OFF), tk())
      ),
      Arguments.of(
        /*inA*/ List.of(tk()),
        /*inB*/ List.of(tk()),
        /*inY*/ List.of(msg(OFF), tk()),
        /*inZ*/ List.of(msg(OFF), tk()),
        /* oA*/ List.of(tk()),
        /* oB*/ List.of(tk()),
        /* oY*/ List.of(msg(OFF), tk()),
        /* oZ*/ List.of(msg(OFF), tk())
      ),
      Arguments.of(
        /*inA*/ List.of(msg(OFF), tk()),
        /*inB*/ List.of(tk()),
        /*inY*/ List.of(msg(ON), tk()),
        /*inZ*/ List.of(msg(ON), tk()),
        /* oA*/ List.of(msg(OFF), tk()),
        /* oB*/ List.of(tk()),
        /* oY*/ List.of(msg(ON), tk()),
        /* oZ*/ List.of(msg(ON), tk())
      ),
      Arguments.of(
        /*inA*/ List.of(msg(ON),  tk()),
        /*inB*/ List.of(tk()),
        /*inY*/ List.of(msg(OFF), tk()),
        /*inZ*/ List.of(msg(ON),  tk()),
        /* oA*/ List.of(msg(ON),  tk()),
        /* oB*/ List.of(tk()),
        /* oY*/ List.of(msg(OFF), tk()),
        /* oZ*/ List.of(msg(ON),  tk())
      ),
      Arguments.of(
        /*inA*/ List.of(tk()),
        /*inB*/ List.of(msg(OFF), tk()),
        /*inY*/ List.of(msg(OFF), tk()),
        /*inZ*/ List.of(msg(ON),  tk()),
        /* oA*/ List.of(tk()),
        /* oB*/ List.of(msg(OFF), tk()),
        /* oY*/ List.of(msg(OFF), tk()),
        /* oZ*/ List.of(msg(ON),  tk())
      ),
      Arguments.of(
        /*inA*/ List.of(msg(ON),  tk()),
        /*inB*/ List.of(msg(OFF), tk()),
        /*inY*/ List.of(msg(OFF), tk()),
        /*inZ*/ List.of(msg(ON),  tk()),
        /* oA*/ List.of(msg(ON),  tk()),
        /* oB*/ List.of(msg(OFF), tk()),
        /* oY*/ List.of(msg(OFF), tk()),
        /* oZ*/ List.of(msg(ON),  tk())
      ),
      Arguments.of(
        /*inA*/ List.of(msg(ON), msg(OFF), msg(OFF), tk()),
        /*inB*/ List.of(tk()),
        /*inY*/ List.of(msg(OFF), tk()),
        /*inZ*/ List.of(msg(ON), tk()),
        /* oA*/ List.of(msg(ON), msg(OFF), msg(OFF), tk()),
        /* oB*/ List.of(tk()),
        /* oY*/ List.of(msg(OFF), tk()),
        /* oZ*/ List.of(msg(ON), tk())
      ),
      Arguments.of(
        /*inA*/ List.of(tk(), tk()),
        /*inB*/ List.of(msg(ON), msg(OFF), msg(OFF), tk()),
        /*inY*/ List.of(msg(OFF), tk()),
        /*inZ*/ List.of(msg(ON), tk()),
        /* oA*/ List.of(tk()),
        /* oB*/ List.of(msg(ON), msg(OFF), msg(OFF), tk()),
        /* oY*/ List.of(msg(OFF), tk()),
        /* oZ*/ List.of(msg(ON), tk())
      ),
      Arguments.of(
        /*inA*/ List.of(msg(ON),  tk(),                   tk(), msg(OFF), msg(OFF), msg(ON), tk()),
        /*inB*/ List.of(          tk(), msg(ON), msg(ON), tk(), msg(ON), tk()),
        /*inY*/ List.of(msg(OFF), tk(), msg(OFF),         tk(), msg(ON), tk()),
        /*inZ*/ List.of(msg(ON),  tk(), msg(OFF),         tk(), msg(ON), tk()),
        /* oA*/ List.of(msg(ON),  tk(),                   tk(), msg(OFF), msg(OFF), msg(ON), tk()),
        /* oB*/ List.of(          tk(), msg(ON), msg(ON), tk(), msg(ON), tk()),
        /* oY*/ List.of(msg(OFF), tk(), msg(OFF),         tk(), msg(ON), tk()),
        /* oZ*/ List.of(msg(ON),  tk(), msg(OFF),         tk(), msg(ON), tk())
      )
    );
  }
}
