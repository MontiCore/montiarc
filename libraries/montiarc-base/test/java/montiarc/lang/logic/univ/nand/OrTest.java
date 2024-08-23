/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nand;

import com.google.common.base.Preconditions;
import montiarc.lang.logic.gate.OrComp;
import montiarc.lang.logic.gate.OrCompBuilder;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

public class OrTest {

  @Order(1)
  @ParameterizedTest
  @CsvSource({
    // a     b     q
    "true, true, true",
    "true, false, true",
    "false, true, true",
    "false, false, false"
  })
  public void testBehavior(boolean a, boolean b, boolean q) {
    // Given
    montiarc.lang.logic.gate.OrComp sut = new montiarc.lang.logic.gate.OrCompBuilder().setName("sut").build();
    PortObserver<Boolean> port_q = new PortObserver<>();

    sut.port_q().connect(port_q);

    // When
    sut.init();

    sut.port_a().receive(msg(a));
    sut.port_a().receive(tk());
    sut.port_b().receive(msg(b));
    sut.port_b().receive(tk());

    sut.run();

    // Then
    Assertions.assertThat(port_q.getObservedValues()).containsExactly(q);
  }

  @Order(2)
  @ParameterizedTest
  @MethodSource("histories")
  public void testBehavior(@NotNull List<Message<Boolean>> a,
                           @NotNull List<Message<Boolean>> b,
                           @NotNull List<Message<Boolean>> q) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);
    Preconditions.checkNotNull(q);

    // Given
    OrComp sut = new OrCompBuilder().setName("sut").build();
    PortObserver<Boolean> actual_q = new PortObserver<>();

    sut.port_q().connect(actual_q);

    // When
    sut.init();

    for (Message<Boolean> msg : a) {
      sut.port_a().receive(msg);
    }

    for (Message<Boolean> msg : b) {
      sut.port_b().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(actual_q.getObservedMessages()).containsExactlyElementsOf(q);
  }

  public static Stream<Arguments> histories() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 2
      Arguments.of(
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 3
      Arguments.of(
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 4
      Arguments.of(
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 5
      Arguments.of(
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 6
      Arguments.of(
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(false), tk())),
      // 7
      Arguments.of(
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 8
      Arguments.of(
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(false), tk())),
      // 9
      Arguments.of(
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 10
      Arguments.of(
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 11
      Arguments.of(
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(false), tk(), msg(true), tk())),
      // 12
      Arguments.of(
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(false), tk(), msg(true), tk())),
      // 13
      Arguments.of(
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(true), tk()), 
        List.of(msg(true), tk(), msg(true), tk())),
      // 14
      Arguments.of(
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(false), tk()), 
        List.of(msg(true), tk(), msg(false), tk())),
      // 15
      Arguments.of(
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(false), tk(), msg(true), tk()), 
        List.of(msg(false), tk(), msg(true), tk())),
      // 16
      Arguments.of(
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(false), tk(), msg(false), tk()), 
        List.of(msg(false), tk(), msg(false), tk()))
    );
  }
}
