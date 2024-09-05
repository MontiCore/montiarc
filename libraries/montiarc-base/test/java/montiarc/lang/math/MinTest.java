/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JSimTest
class MinTest {

  @ParameterizedTest
  @MethodSource("minIntegerTestProvider")
  void test(int a, int b, int r) {
    // Given
    MinIComp sut = new MinICompBuilder().setName("sut").build();
    PortObserver<Number> port_r = new PortObserver<>();

    sut.port_r().connect(port_r);

    // When
    sut.init();

    sut.port_a().receive(msg(a));
    sut.port_a().receive(tk());
    sut.port_b().receive(msg(b));
    sut.port_b().receive(tk());

    sut.run();

    // Then
    assertAll(
      () -> assertThat(port_r.getObservedValues()).containsExactly(r)
    );
  }

  static Stream<Arguments> minIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0),
      Arguments.of(0, 1, 0),
      Arguments.of(0, -1, -1),
      Arguments.of(0, Integer.MAX_VALUE, 0),
      Arguments.of(0, Integer.MIN_VALUE, Integer.MIN_VALUE),
      Arguments.of(1, 0, 0),
      Arguments.of(1, 1, 1),
      Arguments.of(1, -1, -1),
      Arguments.of(1, Integer.MAX_VALUE, 1),
      Arguments.of(1, Integer.MIN_VALUE, Integer.MIN_VALUE),
      Arguments.of(-1, 0, -1),
      Arguments.of(-1, 1, -1),
      Arguments.of(-1, -1, -1),
      Arguments.of(-1, Integer.MAX_VALUE, -1),
      Arguments.of(-1, Integer.MIN_VALUE, Integer.MIN_VALUE),
      Arguments.of(Integer.MAX_VALUE, 0, 0),
      Arguments.of(Integer.MAX_VALUE, 1, 1),
      Arguments.of(Integer.MAX_VALUE, -1, -1),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
      Arguments.of(Integer.MIN_VALUE, 0, Integer.MIN_VALUE),
      Arguments.of(Integer.MIN_VALUE, 1, Integer.MIN_VALUE),
      Arguments.of(Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE)
    );
  }

  @ParameterizedTest
  @MethodSource("minLongTestProvider")
  void test(long a, long b, long r) {
    // Given
    MinLComp sut = new MinLCompBuilder().setName("sut").build();
    PortObserver<Number> port_r = new PortObserver<>();

    sut.port_r().connect(port_r);

    // When
    sut.init();

    sut.port_a().receive(msg(a));
    sut.port_a().receive(tk());
    sut.port_b().receive(msg(b));
    sut.port_b().receive(tk());

    sut.run();

    // Then
    assertAll(
      () -> assertThat(port_r.getObservedValues()).containsExactly(r)
    );
  }

  static Stream<Arguments> minLongTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0),
      Arguments.of(0, 1, 0),
      Arguments.of(0, -1, -1),
      Arguments.of(0, Long.MAX_VALUE, 0),
      Arguments.of(0, Long.MIN_VALUE, Long.MIN_VALUE),
      Arguments.of(1, 0, 0),
      Arguments.of(1, 1, 1),
      Arguments.of(1, -1, -1),
      Arguments.of(1, Long.MAX_VALUE, 1),
      Arguments.of(1, Long.MIN_VALUE, Long.MIN_VALUE),
      Arguments.of(-1, 0, -1),
      Arguments.of(-1, 1, -1),
      Arguments.of(-1, -1, -1),
      Arguments.of(-1, Long.MAX_VALUE, -1),
      Arguments.of(-1, Long.MIN_VALUE, Long.MIN_VALUE),
      Arguments.of(Long.MAX_VALUE, 0, 0),
      Arguments.of(Long.MAX_VALUE, 1, 1),
      Arguments.of(Long.MAX_VALUE, -1, -1),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE),
      Arguments.of(Long.MIN_VALUE, 0, Long.MIN_VALUE),
      Arguments.of(Long.MIN_VALUE, 1, Long.MIN_VALUE),
      Arguments.of(Long.MIN_VALUE, -1, Long.MIN_VALUE),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE)
    );
  }
}
