/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.tuples;

import de.monticore.rte.tuples.Tuple2;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class TupleAccessTest {

  @Test
  void shouldAccessTupleElementsByIndex() {
    // Given
    TupleAccessComp sut = new TupleAccessCompBuilder().setName("sut").build();
    PortObserver<Integer> first = new PortObserver<>();
    PortObserver<Boolean> flag = new PortObserver<>();
    PortObserver<Double> value = new PortObserver<>();
    PortObserver<Tuple2<Boolean, Integer>> swapped = new PortObserver<>();
    sut.port_first().connect(first);
    sut.port_flag().connect(flag);
    sut.port_value().connect(value);
    sut.port_swapped().connect(swapped);

    List<Tuple2<Tuple2<Integer, Boolean>, Double>> input = List.of(
      Tuple2.of(Tuple2.of(1, true), 1.5),
      Tuple2.of(Tuple2.of(-2, false), -0.5)
    );

    // When
    for (Tuple2<Tuple2<Integer, Boolean>, Double> tuple : input) {
      sut.port_input().receive(Message.of(tuple));
      sut.port_input().receive(tk());
    }
    sut.runToCompletion();

    // Then
    Assertions.assertThat(first.getObservedValues()).containsExactly(1, -2);
    Assertions.assertThat(flag.getObservedValues()).containsExactly(true, false);
    Assertions.assertThat(value.getObservedValues()).containsExactly(1.5, -0.5);
    Assertions.assertThat(swapped.getObservedValues()).containsExactly(
      Tuple2.of(true, 2),
      Tuple2.of(false, -1)
    );
  }
}
