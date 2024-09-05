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
class AbsTest {

  @ParameterizedTest
  @MethodSource("absIntegerTestProvider")
  void test(int a, int r, boolean of) {
    // Given
    AbsIComp sut = new AbsICompBuilder().setName("sut").build();
    PortObserver<Number> port_r = new PortObserver<>();
    PortObserver<Boolean> port_of = new PortObserver<>();

    sut.port_r().connect(port_r);
    sut.port_of().connect(port_of);

    // When
    sut.init();

    sut.port_a().receive(msg(a));
    sut.port_a().receive(tk());

    sut.run();

    // Then
    assertAll(
      () -> assertThat(port_r.getObservedValues()).containsExactly(r),
      () -> assertThat(port_of.getObservedValues()).containsExactly(of)
    );
  }

  static Stream<Arguments> absIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, false),
      Arguments.of(1, 1, false),
      Arguments.of(-1, 1, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, true)
    );
  }

  @ParameterizedTest
  @MethodSource("absLongTestProvider")
  void test(long a, long r, boolean of) {
    // Given
    AbsLComp sut = new AbsLCompBuilder().setName("sut").build();
    PortObserver<Number> port_r = new PortObserver<>();
    PortObserver<Boolean> port_of = new PortObserver<>();

    sut.port_r().connect(port_r);
    sut.port_of().connect(port_of);

    // When
    sut.init();

    sut.port_a().receive(msg(a));
    sut.port_a().receive(tk());

    sut.run();

    // Then
    assertAll(
      () -> assertThat(port_r.getObservedValues()).containsExactly(r),
      () -> assertThat(port_of.getObservedValues()).containsExactly(of)
    );
  }

  static Stream<Arguments> absLongTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, false),
      Arguments.of(1, 1, false),
      Arguments.of(-1, 1, false),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, false),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, true)
    );
  }
}
