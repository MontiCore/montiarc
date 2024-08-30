/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import montiarc.rte.port.PortObserver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MultiplyTest {

  @ParameterizedTest
  @MethodSource("multiplyIntegerTestProvider")
  void test(int a, int b, int r, boolean of) {
    // Given
    MultiplyIComp sut = new MultiplyICompBuilder().setName("sut").build();
    PortObserver<Number> port_r = new PortObserver<>();
    PortObserver<Boolean> port_of = new PortObserver<>();

    sut.port_r().connect(port_r);
    sut.port_of().connect(port_of);

    // When
    sut.init();

    sut.port_a().receive(msg(a));
    sut.port_a().receive(tk());
    sut.port_b().receive(msg(b));
    sut.port_b().receive(tk());

    sut.run();

    // Then
    assertAll(
      () -> assertThat(port_r.getObservedValues()).containsExactly(r),
      () -> assertThat(port_of.getObservedValues()).containsExactly(of)
    );
  }

  static Stream<Arguments> multiplyIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, 0, false),
      Arguments.of(0, -1, 0, false),
      Arguments.of(0, Integer.MAX_VALUE, 0, false),
      Arguments.of(0, Integer.MIN_VALUE, 0, false),
      Arguments.of(1, 0, 0, false),
      Arguments.of(1, 1, 1, false),
      Arguments.of(1, -1, -1, false),
      Arguments.of(1, Integer.MAX_VALUE, Integer.MAX_VALUE, false),
      Arguments.of(1, Integer.MIN_VALUE, Integer.MIN_VALUE, false),
      Arguments.of(-1, 0, 0, false),
      Arguments.of(-1, 1, -1, false),
      Arguments.of(-1, -1, 1, false),
      Arguments.of(-1, Integer.MAX_VALUE, -Integer.MAX_VALUE, false),
      Arguments.of(-1, Integer.MIN_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MAX_VALUE, 0, 0, false),
      Arguments.of(Integer.MAX_VALUE, 1, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, -1, -Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, 1, true),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, 0, 0, false),
      Arguments.of(Integer.MIN_VALUE, 1, Integer.MIN_VALUE, false),
      Arguments.of(Integer.MIN_VALUE, -1, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, 0, true)
    );
  }

  @ParameterizedTest
  @MethodSource("multiplyLongTestProvider")
  void test(long a, long b, long r, boolean of) {
    // Given
    MultiplyLComp sut = new MultiplyLCompBuilder().setName("sut").build();
    PortObserver<Number> port_r = new PortObserver<>();
    PortObserver<Boolean> port_of = new PortObserver<>();

    sut.port_r().connect(port_r);
    sut.port_of().connect(port_of);

    // When
    sut.init();

    sut.port_a().receive(msg(a));
    sut.port_a().receive(tk());
    sut.port_b().receive(msg(b));
    sut.port_b().receive(tk());

    sut.run();

    // Then
    assertAll(
      () -> assertThat(port_r.getObservedValues()).containsExactly(r),
      () -> assertThat(port_of.getObservedValues()).containsExactly(of)
    );
  }

  static Stream<Arguments> multiplyLongTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, 0, false),
      Arguments.of(0, -1, 0, false),
      Arguments.of(0, Long.MAX_VALUE, 0, false),
      Arguments.of(0, Long.MIN_VALUE, 0, false),
      Arguments.of(1, 0, 0, false),
      Arguments.of(1, 1, 1, false),
      Arguments.of(1, -1, -1, false),
      Arguments.of(1, Long.MAX_VALUE, Long.MAX_VALUE, false),
      Arguments.of(1, Long.MIN_VALUE, Long.MIN_VALUE, false),
      Arguments.of(-1, 0, 0, false),
      Arguments.of(-1, 1, -1, false),
      Arguments.of(-1, -1, 1, false),
      Arguments.of(-1, Long.MAX_VALUE, -Long.MAX_VALUE, false),
      Arguments.of(-1, Long.MIN_VALUE, Long.MIN_VALUE, true),
      Arguments.of(Long.MAX_VALUE, 0, 0, false),
      Arguments.of(Long.MAX_VALUE, 1, Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, -1, -Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, 1, true),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, true),
      Arguments.of(Long.MIN_VALUE, 0, 0, false),
      Arguments.of(Long.MIN_VALUE, 1, Long.MIN_VALUE, false),
      Arguments.of(Long.MIN_VALUE, -1, Long.MIN_VALUE, true),
      Arguments.of(Long.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, true),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, 0, true)
    );
  }
}
