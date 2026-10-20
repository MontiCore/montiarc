/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.tuples;

import de.monticore.rte.tuples.Tuple2;
import de.monticore.rte.tuples.Tuple3;
import de.monticore.rte.tuples.Tuple12;
import montiarc.rte.deploy.util.DeSerializer;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class TupleForwardTest {

  @Test
  void shouldForwardTuples() {
    // Given
    TupleForwardComp sut = new TupleForwardCompBuilder().setName("sut").build();
    PortObserver<Tuple2<Tuple2<Integer, Boolean>, Double>> output = new PortObserver<>();
    sut.port_output().connect(output);

    List<Tuple2<Integer, Boolean>> input = List.of(
      Tuple2.of(1, true),
      Tuple2.of(-2, false)
    );

    // When
    for (Tuple2<Integer, Boolean> tuple : input) {
      sut.port_input().receive(Message.of(tuple));
      sut.port_input().receive(tk());
    }
    sut.runToCompletion();

    // Then
    Assertions.assertThat(output.getObservedValues()).containsExactly(
      Tuple2.of(Tuple2.of(1, true), 1.0),
      Tuple2.of(Tuple2.of(-2, false), 1.0)
    );
  }

  @Test
  void shouldDeserializeTuplePorts() {
    // Given
    DeSerializer deSerializer = new DeSerializer();
    Tuple2<Tuple2<Integer, Boolean>, Double> tuple = Tuple2.of(Tuple2.of(1, true), 1.0);

    // When
    String serialized = deSerializer.serialize(tuple);

    // Then
    Assertions.assertThat(deSerializer.deserialize(
      serialized,
      new TypeReference<Tuple2<Tuple2<Integer, Boolean>, Double>>() { }
    )).contains(tuple);
  }

  @Test
  void shouldDeserializeTuplesOfSupportedArities() {
    // Given
    DeSerializer deSerializer = new DeSerializer();
    Tuple3<Integer, Boolean, Double> tuple3 = Tuple3.of(1, true, 1.0);
    Tuple12<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple12 =
      Tuple12.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

    // Then
    Assertions.assertThat(deSerializer.deserialize(
      deSerializer.serialize(tuple3),
      new TypeReference<Tuple3<Integer, Boolean, Double>>() { }
    )).contains(tuple3);
    Assertions.assertThat(deSerializer.deserialize(
      deSerializer.serialize(tuple12),
      new TypeReference<Tuple12<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>() { }
    )).contains(tuple12);
  }
}
